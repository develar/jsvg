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

import com.github.weisj.jsvg.attributes.Percentage;
import com.github.weisj.jsvg.geometry.size.Length;
import com.google.errorprone.annotations.Immutable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;

@Immutable
// Can only be constructed by internal code upholding that the families array isn't modified.
@SuppressWarnings("Immutable")
class FontSpec {
    final @NotNull List<String> families;
    final @Nullable FontStyle style;
    final @Nullable Length sizeAdjust;
    final @Percentage float stretch;

    FontSpec(@NotNull List<String> families, @Nullable FontStyle style, @Nullable Length sizeAdjust,
             @Percentage float stretch) {
        this.families = families;
        this.style = style;
        this.sizeAdjust = sizeAdjust;
        this.stretch = stretch;
    }

    @Override
    public String toString() {
        return "FontSpec{" +
                "families=" + families +
                ", style=" + style +
                ", sizeAdjust=" + sizeAdjust +
                ", stretch=" + stretch +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FontSpec fontSpec)) return false;
        return Float.compare(fontSpec.stretch, stretch) == 0
                && families.equals(fontSpec.families)
                && Objects.equals(style, fontSpec.style)
                && Objects.equals(sizeAdjust, fontSpec.sizeAdjust);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(style, sizeAdjust, stretch);
        result = 31 * result + families.hashCode();
        return result;
    }
}
