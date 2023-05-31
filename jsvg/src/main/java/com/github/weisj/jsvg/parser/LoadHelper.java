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

import com.github.weisj.jsvg.attributes.AttributeParser;
import org.jetbrains.annotations.NotNull;

public final class LoadHelper {
    private final @NotNull AttributeParser attributeParser;
    private final @NotNull ResourceLoader resourceLoader;

    public LoadHelper(@NotNull AttributeParser attributeParser, @NotNull ResourceLoader resourceLoader) {
        this.attributeParser = attributeParser;
        this.resourceLoader = resourceLoader;
    }

    public @NotNull AttributeParser attributeParser() {
        return attributeParser;
    }

    public @NotNull ResourceLoader resourceLoader() {
        return resourceLoader;
    }
}
