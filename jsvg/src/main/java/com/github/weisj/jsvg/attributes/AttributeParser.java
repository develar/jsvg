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
package com.github.weisj.jsvg.attributes;

import com.github.weisj.jsvg.attributes.paint.PaintParser;
import com.github.weisj.jsvg.attributes.paint.SVGPaint;
import com.github.weisj.jsvg.geometry.size.AngleUnit;
import com.github.weisj.jsvg.geometry.size.Length;
import com.github.weisj.jsvg.geometry.size.Unit;
import com.github.weisj.jsvg.parser.AttributeNode;
import com.github.weisj.jsvg.parser.SeparatorMode;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class AttributeParser {

    private final @NotNull PaintParser paintParser;

    public AttributeParser(@NotNull PaintParser paintParser) {
        this.paintParser = paintParser;
    }

    @Contract("_,!null -> !null")
    public @Nullable Length parseLength(@Nullable String value, @Nullable Length fallback) {
        if (value == null) return fallback;
        Unit unit = Unit.Raw;
        String lower = value.toLowerCase(Locale.ENGLISH);
        for (Unit u : Unit.units()) {
            if (lower.endsWith(u.suffix())) {
                unit = u;
                break;
            }
        }
        String str = lower.substring(0, lower.length() - unit.suffix().length());
        try {
            return unit.valueOf(Float.parseFloat(str));
        } catch (NumberFormatException e) {
            return fallback;
        }
    }

    public @Percentage float parsePercentage(@Nullable String value, float fallback) {
        return parsePercentage(value, fallback, 0, 1);
    }

    public @Percentage float parsePercentage(@Nullable String value, float fallback, float min, float max) {
        if (value == null) return fallback;
        try {
            float parsed;
            if (value.endsWith("%")) {
                parsed = Float.parseFloat(value.substring(0, value.length() - 1)) / 100f;
            } else {
                parsed = Float.parseFloat(value);
            }
            return Math.max(min, Math.min(max, parsed));
        } catch (NumberFormatException e) {
            return fallback;
        }
    }

    public int parseInt(@Nullable String value, int fallback) {
        if (value == null) return fallback;
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return fallback;
        }
    }

    public float parseFloat(@Nullable String value, float fallback) {
        if (value == null) return fallback;
        try {
            return Float.parseFloat(value);
        } catch (NumberFormatException e) {
            return fallback;
        }
    }

    private static double parseDouble(@Nullable String value, double fallback) {
        if (value == null) return fallback;
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException e) {
            return fallback;
        }
    }

    public @Radian float parseAngle(@Nullable String value, float fallback) {
        if (value == null) return fallback;
        AngleUnit unit = AngleUnit.Raw;
        String lower = value.toLowerCase(Locale.ENGLISH);
        for (AngleUnit u : AngleUnit.units()) {
            if (lower.endsWith(u.suffix())) {
                unit = u;
                break;
            }
        }
        String str = lower.substring(0, lower.length() - unit.suffix().length());
        try {
            return unit.toRadians(Float.parseFloat(str), AngleUnit.Deg);
        } catch (NumberFormatException e) {
            return fallback;
        }
    }

    public Length[] parseLengthList(@Nullable String value) {
        if (value != null && value.equalsIgnoreCase("none")) return new Length[0];
        List<String> values = parseStringList(value, SeparatorMode.COMMA_AND_WHITESPACE);
        Length[] ret = new Length[values.size()];
        for (int i = 0; i < ret.length; i++) {
            Length length = parseLength(values.get(i), null);
            if (length == null) return new Length[0];
            ret[i] = length;
        }
        return ret;
    }

    public float[] parseFloatList(@Nullable String value) {
        List<String> values = parseStringList(value, SeparatorMode.COMMA_AND_WHITESPACE);
        float[] ret = new float[values.size()];
        for (int i = 0; i < ret.length; i++) {
            ret[i] = parseFloat(values.get(i), 0);
        }
        return ret;
    }

    public double[] parseDoubleList(@Nullable String value) {
        List<String> values = parseStringList(value, SeparatorMode.COMMA_AND_WHITESPACE);
        double[] ret = new double[values.size()];
        for (int i = 0; i < ret.length; i++) {
            ret[i] = parseDouble(values.get(i), 0);
        }
        return ret;
    }

    public List<String> parseStringList(@Nullable String value, SeparatorMode separatorMode) {
        if (value == null || value.isEmpty()) return Collections.emptyList();
        List<String> list = new ArrayList<>();
        int max = value.length();
        int start = 0;
        int i = 0;
        boolean inWhiteSpace = false;
        for (; i < max; i++) {
            char c = value.charAt(i);
            if (Character.isWhitespace(c)) {
                if (!inWhiteSpace && separatorMode != SeparatorMode.COMMA_ONLY && i - start > 0) {
                    list.add(value.substring(start, i));
                    start = i + 1;
                }
                inWhiteSpace = true;
                continue;
            }
            inWhiteSpace = false;
            if (c == ',' && separatorMode != SeparatorMode.WHITESPACE_ONLY) {
                list.add(value.substring(start, i));
                start = i + 1;
            }
        }
        if (i - start > 0) list.add(value.substring(start, i));
        return list;
    }

    public @Nullable SVGPaint parsePaint(@Nullable String value, @NotNull AttributeNode attributeNode) {
        return paintParser.parsePaint(value, attributeNode);
    }

    public <E extends Enum<E>> @NotNull E parseEnum(@Nullable String value, @NotNull E fallback) {
        E e = parseEnum(value, fallback.getDeclaringClass());
        if (e == null) return fallback;
        return e;
    }

    public <E extends Enum<E>> @Nullable E parseEnum(@Nullable String value, @NotNull Class<E> enumType) {
        if (value == null) return null;
        for (E enumConstant : enumType.getEnumConstants()) {
            String name = enumConstant instanceof HasMatchName
                    ? ((HasMatchName) enumConstant).matchName()
                    : enumConstant.name();
            if (name.equalsIgnoreCase(value)) return enumConstant;
        }
        return null;
    }

    private static final Pattern WHITESPACE_PATTERN = Pattern.compile("\\s");

    private static @NotNull String removeWhiteSpace(@NotNull String value) {
        return WHITESPACE_PATTERN.matcher(value).replaceAll("");
    }

    public static @Nullable String parseUrl(@Nullable String value) {
        if (value == null) return null;
        if (!value.startsWith("url(") || !value.endsWith(")")) return removeWhiteSpace(value);
        return removeWhiteSpace(value.substring(4, value.length() - 1));
    }

    private static final Pattern TRANSFORM_PATTERN = Pattern.compile("\\w+\\([^)]*\\)");

    public @Nullable AffineTransform parseTransform(@Nullable String value) {
        if (value == null) return null;
        final Matcher transformMatcher = TRANSFORM_PATTERN.matcher(value);
        AffineTransform transform = new AffineTransform();
        while (transformMatcher.find()) {
            String group = transformMatcher.group();
            try {
                parseSingleTransform(group, transform);
            } catch (Exception e) {
                throw new IllegalArgumentException(
                        "Illegal transform definition '" + value + "' encountered error while parsing '" + group + "'",
                        e);
            }
        }
        return transform;
    }

    private void parseSingleTransform(@NotNull String value, @NotNull AffineTransform tx) {
        int first = value.indexOf('(');
        int last = value.lastIndexOf(')');
        String command = value.substring(0, value.indexOf('(')).toLowerCase(Locale.ENGLISH);
        double[] values = parseDoubleList(value.substring(first + 1, last));
        switch (command) {
            case "matrix" -> tx.concatenate(new AffineTransform(values));
            case "translate" -> {
                if (values.length == 1) {
                    tx.translate(values[0], 0);
                }
                else {
                    tx.translate(values[0], values[1]);
                }
            }
            case "translatex" -> tx.translate(values[0], 0);
            case "translatey" -> tx.translate(0, values[0]);
            case "scale" -> {
                if (values.length == 1) {
                    tx.scale(values[0], values[0]);
                }
                else {
                    tx.scale(values[0], values[1]);
                }
            }
            case "scalex" -> tx.scale(values[0], 1);
            case "scaley" -> tx.scale(1, values[0]);
            case "rotate" -> {
                if (values.length > 2) {
                    tx.rotate(Math.toRadians(values[0]), values[1], values[2]);
                }
                else {
                    tx.rotate(Math.toRadians(values[0]));
                }
            }
            case "skewx" -> tx.shear(Math.tan(Math.toRadians(values[0])), 0);
            case "skewy" -> tx.shear(0, Math.tan(Math.toRadians(values[0])));
            default -> throw new IllegalArgumentException("Unknown transform type: " + command);
        }
    }

    public @NotNull PaintParser paintParser() {
        return paintParser;
    }
}
