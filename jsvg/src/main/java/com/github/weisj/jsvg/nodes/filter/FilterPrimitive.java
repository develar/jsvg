/*
 * MIT License
 *
 * Copyright (c) 2021-2022 Jannis Weis
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
package com.github.weisj.jsvg.nodes.filter;

import java.awt.*;

import org.jetbrains.annotations.MustBeInvokedByOverriders;
import org.jetbrains.annotations.NotNull;

import com.github.weisj.jsvg.attributes.filter.DefaultFilterChannel;
import com.github.weisj.jsvg.geometry.size.Length;
import com.github.weisj.jsvg.geometry.size.Unit;
import com.github.weisj.jsvg.nodes.AbstractSVGNode;
import com.github.weisj.jsvg.parser.AttributeNode;
import com.github.weisj.jsvg.renderer.RenderContext;

public abstract class FilterPrimitive extends AbstractSVGNode {

    Length x;
    Length y;
    Length width;
    Length height;

    private Object inputChannel;
    private Object resultChannel;

    @NotNull Channel channel(@NotNull Object channelName, @NotNull FilterContext context) {
        Channel input = context.getChannel(channelName);
        if (input == null) throw new IllegalStateException("Input channel [" + channelName + "] doesn't exist.");
        return input;
    }

    @NotNull Channel inputChannel(@NotNull FilterContext context) {
        return channel(inputChannel, context);
    }

    void saveResult(@NotNull Channel output, @NotNull FilterContext filterContext) {
        filterContext.addResult(resultChannel, output);
        if (resultChannel != DefaultFilterChannel.LastResult) {
            filterContext.addResult(DefaultFilterChannel.LastResult, output);
        }
    }

    @Override
    @MustBeInvokedByOverriders
    public void build(@NotNull AttributeNode attributeNode) {
        super.build(attributeNode);
        x = attributeNode.getLength("x", Unit.PERCENTAGE.valueOf(0));
        y = attributeNode.getLength("y", Unit.PERCENTAGE.valueOf(0));
        width = attributeNode.getLength("width", Unit.PERCENTAGE.valueOf(100));
        height = attributeNode.getLength("height", Unit.PERCENTAGE.valueOf(100));

        inputChannel = attributeNode.getValue("in");
        if (inputChannel == null) inputChannel = DefaultFilterChannel.LastResult;
        resultChannel = attributeNode.getValue("result");
        if (resultChannel == null) resultChannel = DefaultFilterChannel.LastResult;
    }

    public abstract void applyFilter(@NotNull Graphics2D g, @NotNull RenderContext context,
            @NotNull FilterContext filterContext);
}
