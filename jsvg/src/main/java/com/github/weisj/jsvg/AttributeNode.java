/*
 * MIT License
 *
 * Copyright (c) 2021 Jannis Weis
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
package com.github.weisj.jsvg;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.util.Map;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.github.weisj.jsvg.attributes.*;
import com.github.weisj.jsvg.attributes.paint.PaintParser;
import com.github.weisj.jsvg.attributes.paint.SVGPaint;
import com.github.weisj.jsvg.geometry.size.Length;
import com.github.weisj.jsvg.geometry.size.Unit;
import com.github.weisj.jsvg.nodes.ClipPath;
import com.github.weisj.jsvg.nodes.SVGNode;

public class AttributeNode {
    private final @NotNull String tagName;
    private final @NotNull Map<String, String> attributes;
    private final @Nullable AttributeNode parent;
    private final @NotNull Map<@NotNull String, @NotNull SVGNode> namedElements;

    public AttributeNode(@NotNull String tagName, @NotNull Map<String, String> attributes,
            @Nullable AttributeNode parent,
            @NotNull Map<@NotNull String, @NotNull SVGNode> namedElements) {
        this.tagName = tagName;
        this.attributes = attributes;
        this.parent = parent;
        this.namedElements = namedElements;
        if (attributes.containsKey("style")) {
            SVGLoader.LOGGER.warning("<style> not yet implemented.");
        }
    }

    public <T> @Nullable T getElementById(@NotNull Class<T> type, @Nullable String id) {
        if (id == null) return null;
        // Todo: Look up in spec how elements should be resolved if multiple elements have the same id.
        SVGNode node = namedElements.get(id);
        return type.isInstance(node) ? type.cast(node) : null;
    }

    private <T> @Nullable T getElementByUrl(@NotNull Class<T> type, @Nullable String value) {
        return getElementById(type, AttributeParser.parseUrl(value));
    }

    public <T> @Nullable T getElementByHref(@NotNull Class<T> type, @Nullable String value) {
        if (value == null) return null;
        if (!value.startsWith("#")) return null;
        return getElementById(type, value.substring(1));
    }

    public @NotNull Map<String, String> attributes() {
        return attributes;
    }

    public @NotNull String tagName() {
        return tagName;
    }

    public @Nullable AttributeNode parent() {
        return parent;
    }

    public @Nullable String getValue(@NotNull String key) {
        return getValue(key, Inherit.No);
    }

    public @Nullable String getValue(@NotNull String key, Inherit inherited) {
        // Search through hierarchy if any parent declares the attribute.
        AttributeNode node = this;
        String value = null;
        int depth = 0;
        while (value == null && node != null && depth <= inherited.maxDepth()) {
            value = node.attributes.get(key);
            node = node.parent;
            depth++;
        }
        return value != null ? value.trim() : null;
    }

    public @NotNull Color getColor(@NotNull String key) {
        Color c = PaintParser.parseColor(getValue(key));
        return c != null ? c : PaintParser.DEFAULT_COLOR;
    }

    public @Nullable SVGPaint getPaint(@NotNull String key) {
        String value = getValue(key);
        SVGPaint paint = getElementByUrl(SVGPaint.class, value);
        if (paint != null) return paint;
        return AttributeParser.parsePaint(value);
    }

    public @Nullable Length getLength(@NotNull String key) {
        return getLengthInternal(key, null);
    }

    public @NotNull Length getLength(@NotNull String key, float fallback) {
        return getLength(key, Unit.Raw.valueOf(fallback));
    }

    public @NotNull Length getLength(@NotNull String key, @NotNull Length fallback) {
        return getLengthInternal(key, fallback);
    }

    @Contract("_,!null -> !null")
    private @Nullable Length getLengthInternal(@NotNull String key, @Nullable Length fallback) {
        return AttributeParser.parseLength(getValue(key), fallback);
    }

    public @Percentage float getPercentage(@NotNull String key, @Percentage float fallback) {
        return AttributeParser.parsePercentage(getValue(key), fallback);
    }

    public Length[] getLengthList(@NotNull String key) {
        return AttributeParser.parseLengthList(getValue(key));
    }

    public float[] getFloatList(@NotNull String key) {
        return AttributeParser.parseFloatList(getValue(key));
    }

    public <E extends Enum<E>> @NotNull E getEnum(@NotNull String key, @NotNull E fallback) {
        return AttributeParser.parseEnum(getValue(key), fallback);
    }

    public @Nullable ClipPath getClipPath() {
        return getElementByUrl(ClipPath.class, getValue("clip-path"));
    }

    public @Nullable AffineTransform parseTransform(@NotNull String key) {
        return AttributeParser.parseTransform(getValue(key));
    }

    public boolean hasAttribute(@NotNull String name) {
        return attributes.containsKey(name);
    }

    public @NotNull String[] getStringList(@NotNull String name) {
        return getStringList(name, false);
    }


    public @NotNull String[] getStringList(@NotNull String name, boolean requireComma) {
        return AttributeParser.parseStringList(getValue(name), requireComma);
    }

    public float getFloat(@NotNull String name, float fallback) {
        return AttributeParser.parseFloat(getValue(name), fallback);
    }
}
