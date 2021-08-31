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
package com.github.weisj.jsvg.attributes.font;

import java.awt.*;
import java.awt.font.GlyphVector;

import org.jetbrains.annotations.NotNull;

import com.github.weisj.jsvg.attributes.Percentage;

public class AWTSVGFont implements SVGFont {
    private final @NotNull Font font;
    // Todo: Do something with stretch
    private final @Percentage float stretch;

    public AWTSVGFont(@NotNull Font font, @Percentage float stretch) {
        this.font = font;
        this.stretch = stretch;
    }

    @Override
    public @NotNull GlyphVector unicodeGlyphVector(@NotNull Graphics2D g, char[] codepoints) {
        return font.createGlyphVector(g.getFontRenderContext(), codepoints);
    }

    public @NotNull Font font() {
        return font;
    }
}
