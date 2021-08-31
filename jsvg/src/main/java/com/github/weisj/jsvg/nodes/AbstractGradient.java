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
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.util.List;

import org.jetbrains.annotations.MustBeInvokedByOverriders;
import org.jetbrains.annotations.NotNull;

import com.github.weisj.jsvg.AttributeNode;
import com.github.weisj.jsvg.attributes.Percentage;
import com.github.weisj.jsvg.attributes.paint.GradientUnits;
import com.github.weisj.jsvg.attributes.paint.PaintParser;
import com.github.weisj.jsvg.attributes.paint.SVGPaint;
import com.github.weisj.jsvg.nodes.container.ContainerNode;

abstract class AbstractGradient extends ContainerNode implements SVGPaint {
    protected AffineTransform gradientTransform;
    protected GradientUnits gradientUnits;
    protected MultipleGradientPaint.CycleMethod spreadMethod;

    private @NotNull Color[] colors;
    private @Percentage float[] offsets;

    public @Percentage float[] offsets() {
        return offsets;
    }

    public @NotNull Color[] colors() {
        return colors;
    }

    @Override
    @MustBeInvokedByOverriders
    public void build(@NotNull AttributeNode attributeNode) {
        super.build(attributeNode);

        gradientUnits = attributeNode.getEnum("gradientUnits", GradientUnits.ObjectBoundingBox);
        gradientTransform = attributeNode.parseTransform("gradientTransform");

        spreadMethod = MultipleGradientPaint.CycleMethod.NO_CYCLE;
        String spreadMethodStr = attributeNode.getValue("spreadMethod");
        if ("repeat".equalsIgnoreCase(spreadMethodStr)) {
            spreadMethod = MultipleGradientPaint.CycleMethod.REPEAT;
        } else if ("reflect".equalsIgnoreCase(spreadMethodStr)) {
            spreadMethod = MultipleGradientPaint.CycleMethod.REFLECT;
        }

        List<Stop> stops = childrenOfType(Stop.class);

        boolean realGradient = false;
        colors = new Color[stops.size()];
        offsets = new float[stops.size()];
        for (int i = 0; i < offsets.length; i++) {
            Stop stop = stops.get(i);
            offsets[i] = stop.offset();
            colors[i] = stop.color();
            if (i > 0) {
                realGradient = realGradient || !colors[i].equals(colors[i - 1]);
            }
        }
        if (!realGradient && colors.length > 0) {
            colors = new Color[] {colors[0]};
            offsets = new float[] {0f};
        }
        // Todo: Sort gradients, throw away duplicate entries (by appearance).

        // Todo: href prototype
    }

    @Override
    public final @NotNull Paint paintForBounds(@NotNull Rectangle2D bounds) {
        Color[] gradColors = colors();
        if (gradColors.length == 0) return PaintParser.DEFAULT_COLOR;
        if (gradColors.length == 1) return gradColors[0];
        return gradientForBounds(bounds, offsets(), gradColors);
    }

    protected abstract @NotNull Paint gradientForBounds(@NotNull Rectangle2D bounds,
            @Percentage float[] gradOffsets, @NotNull Color[] gradColors);

    protected @NotNull AffineTransform computeViewTransform(@NotNull Rectangle2D bounds) {
        AffineTransform viewTransform = new AffineTransform();

        if (gradientUnits == GradientUnits.ObjectBoundingBox) {
            viewTransform.setToTranslation(bounds.getX(), bounds.getY());
            viewTransform.scale(bounds.getWidth(), bounds.getHeight());
        }
        if (gradientTransform != null) viewTransform.concatenate(gradientTransform);
        return viewTransform;
    }
}
