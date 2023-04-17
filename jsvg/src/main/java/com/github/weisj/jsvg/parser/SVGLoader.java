/*
 * MIT License
 *
 * Copyright (c) 2021-2023 Jannis Weis
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */
package com.github.weisj.jsvg.parser;

import com.github.weisj.jsvg.SVGDocument;
import com.github.weisj.jsvg.attributes.AttributeParser;
import com.github.weisj.jsvg.nodes.SVG;
import com.github.weisj.jsvg.nodes.SVGNode;
import com.github.weisj.jsvg.nodes.Style;
import com.github.weisj.jsvg.parser.css.CssParser;
import com.github.weisj.jsvg.parser.css.StyleSheet;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.xml.sax.*;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.XMLConstants;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.*;
import java.net.URL;
import java.util.*;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.GZIPInputStream;

/**
 * Class for loading svg files as an {@link SVGDocument}.
 * Note that this class isn't guaranteed to be thread safe and hence shouldn't be used across multiple threads.
 */
public final class SVGLoader {

    private static final Logger LOGGER = Logger.getLogger(SVGLoader.class.getName());
    private static final @NotNull Map<String, Supplier<SVGNode>> NODE_CONSTRUCTOR_MAP = NodeMap.createNodeConstructorMap(new TreeMap<>(String.CASE_INSENSITIVE_ORDER));

    private final @NotNull SAXParser saxParser;

    private SVGLoader() {
        this(createSaxParser());
    }

    private SVGLoader(@NotNull SAXParser saxParser) {
        this.saxParser = saxParser;
    }

    private static @NotNull SAXParser createSaxParser() {
        SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
        saxParserFactory.setNamespaceAware(true);
        try {
            SAXParser parser = saxParserFactory.newSAXParser();
            setParserProperty(parser, XMLConstants.ACCESS_EXTERNAL_DTD);
            setParserProperty(parser, XMLConstants.ACCESS_EXTERNAL_SCHEMA);
            return parser;
        } catch (ParserConfigurationException | SAXException e) {
            throw new RuntimeException(e);
        }
    }

    private static void setParserProperty(@NotNull SAXParser parser, @NotNull String property) {
        try {
            parser.setProperty(property, "");
        } catch (SAXNotRecognizedException | SAXNotSupportedException e) {
            // We don't care if when the properties aren't recognized or supported.
        }
    }

    public @Nullable SVGDocument load(@NotNull URL xmlBase) {
        return load(xmlBase, new DefaultParserProvider());
    }


    @Nullable
    private SVGDocument load(@NotNull URL xmlBase, @NotNull ParserProvider parserProvider) {
        try {
            return load(xmlBase.openStream(), parserProvider);
        } catch (IOException e) {
            LOGGER.log(Level.WARNING, "Could not read " + xmlBase, e);
        }
        return null;
    }

    public @Nullable SVGDocument load(@NotNull InputStream inputStream) {
        return load(inputStream, new DefaultParserProvider());
    }

    @Nullable
    private SVGDocument load(@NotNull InputStream inputStream, @NotNull ParserProvider parserProvider) {
        return load(inputStream, parserProvider, new SynchronousResourceLoader());
    }


    @Nullable
    private SVGDocument load(@NotNull InputStream inputStream,
                             @NotNull ParserProvider parserProvider,
                             @NotNull ResourceLoader resourceLoader) {
        try {
            XMLReader xmlReader = saxParser.getXMLReader();
            xmlReader.setEntityResolver(
                    (publicId, systemId) -> {
                        // Ignore all DTDs
                        return new InputSource(new ByteArrayInputStream(new byte[0]));
                    });
            SVGLoadHandler handler = new SVGLoadHandler(parserProvider, resourceLoader);
            xmlReader.setContentHandler(handler);
            xmlReader.parse(new InputSource(createDocumentInputStream(inputStream)));
            return handler.getDocument();
        } catch (SAXParseException e) {
            LOGGER.log(Level.WARNING, "Error processing ", e);
        } catch (Throwable e) {
            LOGGER.log(Level.WARNING, "Could not load SVG ", e);
        }
        return null;
    }

    private static InputStream createDocumentInputStream(@NotNull InputStream is) throws IOException {
        BufferedInputStream bin = new BufferedInputStream(is);
        bin.mark(2);
        int b0 = bin.read();
        int b1 = bin.read();
        bin.reset();

        // Check for gzip magic number
        if ((b1 << 8 | b0) == GZIPInputStream.GZIP_MAGIC) {
            return new GZIPInputStream(bin);
        } else {
            // Plain text
            return bin;
        }
    }

    private static final class SVGLoadHandler extends DefaultHandler implements LoadHelper {

        private static final boolean DEBUG_PRINT = false;
        private final PrintStream printer = System.out;
        private int nestingLevel = 0;
        private String ident = "";

        private final Map<String, ParsedElement> namedElements = new HashMap<>();
        private final List<Style> styleElements = new ArrayList<>();
        private final List<StyleSheet> styleSheets = new ArrayList<>();
        private final Deque<ParsedElement> currentNodeStack = new ArrayDeque<>();

        private ParsedElement rootNode;

        private final @NotNull AttributeParser attributeParser;
        private final @NotNull ResourceLoader resourceLoader;
        private final @NotNull ParserProvider parserProvider;


        private SVGLoadHandler(@NotNull ParserProvider parserProvider, @NotNull ResourceLoader resourceLoader) {
            this.attributeParser = new AttributeParser(parserProvider.createPaintParser());
            this.resourceLoader = resourceLoader;
            this.parserProvider = parserProvider;
        }

        @Override
        public @NotNull AttributeParser attributeParser() {
            return attributeParser;
        }

        @Override
        public @NotNull ResourceLoader resourceLoader() {
            return resourceLoader;
        }

        private void setIdent(int level) {
            ident = " ".repeat(Math.max(0, level));
        }

        private static boolean isBlank(String text) {
            for (int i = 0; i < text.length(); i++) {
                if (text.charAt(i) > ' ') return false;
            }
            return true;
        }

        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes) {
            if (DEBUG_PRINT) {
                printer.print(ident);
                printer.print("<" + localName);
                for (int i = 0, end = attributes.getLength(); i < end; i++) {
                    printer.println();
                    printer.print(ident);
                    printer.print(" ");
                    printer.print(attributes.getQName(i));
                    printer.print(" = ");
                    printer.print(attributes.getValue(i));
                }
                printer.println(">");
                setIdent(++nestingLevel);
            }
            ParsedElement lastParsedElement = currentNodeStack.isEmpty()
                    ? null
                    : currentNodeStack.peek();

            if (lastParsedElement != null) flushText(lastParsedElement, true);

            Supplier<SVGNode> nodeSupplier = NODE_CONSTRUCTOR_MAP.get(localName);
            if (nodeSupplier != null) {
                SVGNode newNode = nodeSupplier.get();

                Map<String, String> attrs = new HashMap<>(attributes.getLength());
                for (int i = 0; i < attributes.getLength(); i++) {
                    attrs.put(attributes.getQName(i), attributes.getValue(i));
                }

                ParsedElement parsedElement = new ParsedElement(
                        attributes.getValue("id"),
                        new AttributeNode(qName, attrs, lastParsedElement != null
                                ? lastParsedElement.attributeNode()
                                : null, namedElements, styleSheets, this),
                        newNode);

                if (lastParsedElement != null) {
                    lastParsedElement.addChild(parsedElement);
                }
                if (rootNode == null) rootNode = parsedElement;

                if (parsedElement.node() instanceof Style) {
                    styleElements.add((Style) parsedElement.node());
                }

                currentNodeStack.push(parsedElement);
                String id = parsedElement.id();
                if (id != null && !namedElements.containsKey(id)) {
                    namedElements.put(id, parsedElement);
                }
            } else {
                LOGGER.warning("No node registered for tag " + localName);
            }
        }

        @Override
        public void endElement(String uri, String localName, String qName) {
            if (DEBUG_PRINT) {
                setIdent(--nestingLevel);
                printer.print(ident);
                printer.println("</" + localName + ">");
            }
            if (!currentNodeStack.isEmpty() && currentNodeStack.peek().attributeNode().tagName().equals(qName)) {
                flushText(currentNodeStack.pop(), false);
            }
        }

        private static void flushText(@NotNull ParsedElement element, boolean segmentBreak) {
            if (element.characterDataParser != null && element.characterDataParser.canFlush(segmentBreak)) {
                element.node().addContent(element.characterDataParser.flush(segmentBreak));
            }
        }

        @Override
        public void characters(char[] ch, int start, int length) {
            if (DEBUG_PRINT) {
                String text = new String(ch, start, length).replace("\n", "\\n");
                if (!isBlank(text)) {
                    printer.print(ident);
                    printer.print("__");
                    printer.print(text);
                    printer.println("__");
                }
            }
            if (!currentNodeStack.isEmpty() && currentNodeStack.peek().characterDataParser != null) {
                currentNodeStack.peek().characterDataParser.append(ch, start, length);
            }
        }

        @NotNull
        private SVGDocument getDocument() {
            DomProcessor preProcessor = parserProvider.createPreProcessor();

            if (!styleElements.isEmpty()) {
                CssParser cssParser = parserProvider.createCssParser();
                for (Style styleElement : styleElements) {
                    styleElement.parseStyleSheet(cssParser);
                    styleSheets.add(styleElement.styleSheet());
                }
            }

            if (preProcessor != null) preProcessor.process(rootNode);
            rootNode.build();
            DomProcessor postProcessor = parserProvider.createPostProcessor();
            if (postProcessor != null) postProcessor.process(rootNode);
            return new SVGDocument((SVG) rootNode.node());
        }
    }

}
