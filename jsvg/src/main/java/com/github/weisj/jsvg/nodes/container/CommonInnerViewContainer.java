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
package com.github.weisj.jsvg.nodes.container;

import com.github.weisj.jsvg.geometry.size.FloatSize;
import com.github.weisj.jsvg.geometry.size.Length;
import com.github.weisj.jsvg.geometry.size.MeasureContext;
import com.github.weisj.jsvg.nodes.SVGNode;
import com.github.weisj.jsvg.nodes.prototype.ShapedContainer;
import com.github.weisj.jsvg.parser.AttributeNode;
import com.github.weisj.jsvg.renderer.RenderContext;
import org.jetbrains.annotations.MustBeInvokedByOverriders;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.geom.Point2D;

public abstract class CommonInnerViewContainer extends BaseInnerViewContainer implements ShapedContainer<SVGNode> {
    private Length x;
    private Length y;
    protected Length width;
    protected Length height;

    @Override
    protected @NotNull Point2D outerLocation(@NotNull MeasureContext context) {
        return new Point2D.Float(x.resolveWidth(context), y.resolveHeight(context));
    }

    @Override
    protected @Nullable Point2D anchorLocation(@NotNull MeasureContext context) {
        // By default, we aren't anchored.
        return null;
    }

    @Override
    public @NotNull FloatSize size(@NotNull RenderContext context) {
        MeasureContext measure = context.measureContext();
        return new FloatSize(
                width.orElseIfUnspecified(measure.viewWidth()).resolveWidth(measure),
                height.orElseIfUnspecified(measure.viewHeight()).resolveHeight(measure));
    }

    @Override
    public boolean isVisible(@NotNull RenderContext context) {
        return !width.isZero() && !height.isZero() && super.isVisible(context);
    }

    @Override
    @MustBeInvokedByOverriders
    public void build(@NotNull AttributeNode attributeNode) {
        super.build(attributeNode);
        x = attributeNode.getLength("x", 0);
        y = attributeNode.getLength("y", 0);
        width = attributeNode.getLength("width", Length.UNSPECIFIED);
        height = attributeNode.getLength("height", Length.UNSPECIFIED);
    }
}
