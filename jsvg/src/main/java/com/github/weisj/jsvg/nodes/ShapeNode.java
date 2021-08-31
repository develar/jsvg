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
package com.github.weisj.jsvg.nodes;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.util.Arrays;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.github.weisj.jsvg.AttributeNode;
import com.github.weisj.jsvg.geometry.SVGShape;
import com.github.weisj.jsvg.geometry.size.MeasureContext;
import com.github.weisj.jsvg.nodes.prototype.HasShape;
import com.github.weisj.jsvg.renderer.PaintContext;
import com.github.weisj.jsvg.renderer.RenderContext;
import com.github.weisj.jsvg.renderer.ShapeRenderer;

public abstract class ShapeNode extends RenderableSVGNode implements HasShape {
    private PaintContext paintContext;
    private @Nullable Stroke stroke;

    private SVGShape shape;

    @Override
    public final void build(@NotNull AttributeNode attributeNode) {
        super.build(attributeNode);
        paintContext = PaintContext.parse(attributeNode);

        // Todo: Support pathLength
        // Todo: Support stroke (similar to how font is handles, as it is a compound property)
        float[] dash = attributeNode.getFloatList("stroke-dasharray");
        if (dash.length > 0) {
            System.out.println("Dash Array:" + Arrays.toString(dash));
        }

        shape = buildShape(attributeNode);
    }

    protected abstract @NotNull SVGShape buildShape(@NotNull AttributeNode attributeNode);

    @Override
    public @NotNull Shape computeShape(@NotNull MeasureContext context) {
        return shape.shape(context);
    }

    @Override
    public boolean isVisible(@NotNull RenderContext context) {
        return super.isVisible(context);
    }

    @Override
    public void render(@NotNull RenderContext context, @NotNull Graphics2D g) {
        MeasureContext measureContext = context.measureContext();
        Shape paintShape = shape.shape(measureContext);
        Rectangle2D bounds = shape.bounds(measureContext, false);
        ShapeRenderer.renderShape(context, paintContext, g, paintShape, bounds, shape.canBeFilled(), true);
    }
}
