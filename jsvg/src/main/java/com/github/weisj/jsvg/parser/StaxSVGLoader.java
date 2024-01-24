/*
 * MIT License
 *
 * Copyright (c) 2023 Jannis Weis
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

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.github.weisj.jsvg.SVGDocument;

public final class StaxSVGLoader {
    private static final Logger LOGGER = Logger.getLogger(StaxSVGLoader.class.getName());
    private static final String SVG_NAMESPACE_URI = "http://www.w3.org/2000/svg";
    private static final String XLINK_NAMESPACE_URI = "http://www.w3.org/1999/xlink";

    private final @NotNull NodeSupplier nodeSupplier;
    private final @NotNull XMLInputFactory xmlInputFactory;

    public StaxSVGLoader(@NotNull NodeSupplier nodeSupplier) {
        this(nodeSupplier, createDefaultFactory());
    }

    private static @NotNull XMLInputFactory createDefaultFactory() {
        XMLInputFactory factory = XMLInputFactory.newFactory();
        factory.setProperty(XMLInputFactory.SUPPORT_DTD, false);
        factory.setProperty(XMLInputFactory.IS_REPLACING_ENTITY_REFERENCES, false);
        factory.setProperty(XMLInputFactory.IS_SUPPORTING_EXTERNAL_ENTITIES, false);
        return factory;
    }

    public StaxSVGLoader(@NotNull NodeSupplier nodeSupplier, @NotNull XMLInputFactory factory) {
        this.nodeSupplier = nodeSupplier;
        this.xmlInputFactory = factory;
    }

    private @Nullable XMLEventReader createReader(@Nullable InputStream inputStream) {
        try {
            return xmlInputFactory.createXMLEventReader(inputStream);
        } catch (XMLStreamException e) {
            LOGGER.log(Level.SEVERE, "Error while creating XMLEventReader.", e);
            return null;
        }
    }

    public @Nullable SVGDocument load(
            @Nullable InputStream inputStream,
            @NotNull ParserProvider parserProvider,
            @NotNull ResourceLoader resourceLoader) throws IOException, XMLStreamException {
        if (inputStream == null) return null;
        XMLEventReader reader = createReader(inputStream);
        if (reader == null) return null;
        try {
            SVGDocumentBuilder builder = new SVGDocumentBuilder(parserProvider, resourceLoader, nodeSupplier);
            while (reader.hasNext()) {
                XMLEvent event = reader.nextEvent();
                switch (event.getEventType()) {
                    case XMLStreamConstants.START_DOCUMENT:
                        builder.startDocument();
                        break;
                    case XMLStreamConstants.END_DOCUMENT:
                        builder.endDocument();
                        break;

                    case XMLStreamConstants.START_ELEMENT:
                        StartElement element = event.asStartElement();
                        String uri = element.getName().getNamespaceURI();
                        if (uri != null && !uri.isEmpty() && !SVG_NAMESPACE_URI.equals(uri)) {
                            skipElement(reader);
                            break;
                        }
                        Map<String, String> attributes = new HashMap<>();
                        element.getAttributes().forEachRemaining(
                                attr -> attributes.put(qualifiedName(attr.getName()), attr.getValue().trim()));
                        if (!builder.startElement(qualifiedName(element.getName()), attributes)) {
                            skipElement(reader);
                        }
                        break;

                    case XMLStreamConstants.END_ELEMENT:
                        builder.endElement(qualifiedName(event.asEndElement().getName()));
                        break;

                    case XMLStreamConstants.CDATA:
                    case XMLStreamConstants.CHARACTERS:
                        char[] data = event.asCharacters().getData().toCharArray();
                        builder.addTextContent(data, 0, data.length);
                        break;

                    case XMLStreamConstants.SPACE:
                        // This is ignorable whitespace.
                    case XMLStreamConstants.COMMENT:
                    case XMLStreamConstants.PROCESSING_INSTRUCTION:
                    case XMLStreamConstants.ENTITY_REFERENCE:
                    case XMLStreamConstants.ATTRIBUTE:
                    case XMLStreamConstants.DTD:
                    case XMLStreamConstants.NAMESPACE:
                    case XMLStreamConstants.NOTATION_DECLARATION:
                    case XMLStreamConstants.ENTITY_DECLARATION:
                        break;
                }
            }
            return builder.build();
        } catch (XMLStreamException e) {
            LOGGER.log(Level.SEVERE, "Error while parsing SVG.", e);
        } finally {
            reader.close();
            inputStream.close();
        }
        return null;
    }

    private static void skipElement(@NotNull XMLEventReader reader) throws XMLStreamException {
        int elementCount = 1;
        while (reader.hasNext()) {
            XMLEvent event = reader.nextEvent();
            if (event.isStartElement()) {
                elementCount++;
            } else if (event.isEndElement()) {
                elementCount--;
            }
            if (elementCount == 0) return;
        }
    }

    private static String qualifiedName(@NotNull QName name) {
        String prefix = name.getPrefix();
        String localName = name.getLocalPart();
        if (prefix == null) return localName;
        if (prefix.isEmpty()) return localName;
        if (SVG_NAMESPACE_URI.equals(name.getNamespaceURI())) return localName;
        if (XLINK_NAMESPACE_URI.equals(name.getNamespaceURI())) return "xlink:" + localName;
        return prefix + ":" + localName;
    }
}
