/*
 * MIT License
 *
 * Copyright (c) 2022-2023 Jannis Weis
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
import java.net.URI;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jetbrains.annotations.NotNull;

import com.github.weisj.jsvg.parser.resources.RenderableResource;
import com.github.weisj.jsvg.util.ResourceUtil;


public final class AsynchronousResourceLoader implements ResourceLoader {
    private static final Logger LOGGER = Logger.getLogger(AsynchronousResourceLoader.class.getName());

    @Override
    public @NotNull UIFuture<RenderableResource> loadImage(@NotNull URI uri) {
        return new SwingUIFuture<>(() -> {
            try {
                return ResourceUtil.loadImage(uri);
            } catch (IOException e) {
                LOGGER.log(Level.SEVERE, e.getMessage(), e);
                return null;
            }
        });
    }
}
