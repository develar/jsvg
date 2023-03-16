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
package com.github.weisj.jsvg;

import com.github.weisj.jsvg.attributes.ViewBox;
import com.github.weisj.jsvg.attributes.font.SVGFont;
import com.github.weisj.jsvg.geometry.size.FloatSize;
import com.github.weisj.jsvg.geometry.size.MeasureContext;
import com.github.weisj.jsvg.nodes.SVG;
import com.github.weisj.jsvg.renderer.NodeRenderer;
import com.github.weisj.jsvg.renderer.RenderContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.util.Objects;

public class SVGDocument {
    private static final boolean DEBUG = false;
    private final @NotNull SVG root;

    public SVGDocument(@NotNull SVG root) {
        this.root = root;
    }

    public @NotNull FloatSize size() {
        float em = SVGFont.defaultFontSize();
        return root.sizeForTopLevel(em, SVGFont.exFromEm(em));
    }

    public void render(@Nullable JComponent component, @NotNull Graphics2D g) {
        render(component, g, null);
    }

    private void render(@Nullable JComponent component, @NotNull Graphics2D graphics2D, @Nullable ViewBox bounds) {
        Graphics2D g = (Graphics2D) graphics2D.create();
        setupSVGRenderingHints(g);

        Font f = g.getFont();
        if (f == null && component != null) f = component.getFont();
        float defaultEm = f != null ? f.getSize2D() : SVGFont.defaultFontSize();
        float defaultEx = SVGFont.exFromEm(defaultEm);

        MeasureContext initialMeasure = bounds != null
                ? MeasureContext.createInitial(bounds.size(), defaultEm, defaultEx)
                : MeasureContext.createInitial(root.sizeForTopLevel(defaultEm, defaultEx), defaultEm, defaultEx);
        RenderContext context = RenderContext.createInitial(component, initialMeasure);

        if (bounds == null) bounds = new ViewBox(root.size(context));

        root.applyTransform(g, context);

        if (DEBUG) {
            Paint paint = g.getPaint();
            g.setColor(Color.MAGENTA);
            g.draw(bounds);
            g.setPaint(paint);
        }

        g.clip(bounds);
        g.translate(bounds.x, bounds.y);

        try (NodeRenderer.Info info = NodeRenderer.createRenderInfo(root, context, g, null)) {
            Objects.requireNonNull(info);
            root.renderWithSize(bounds.size(), root.viewBox(context), info.context, info.g);
        }

        g.dispose();
    }

    private static void setupSVGRenderingHints(@NotNull Graphics2D g) {
        Object aaHint = g.getRenderingHint(RenderingHints.KEY_ANTIALIASING);
        if (aaHint != RenderingHints.VALUE_ANTIALIAS_DEFAULT) setSVGRenderingHint(g,
                SVGRenderingHints.KEY_IMAGE_ANTIALIASING,
                aaHint == RenderingHints.VALUE_ANTIALIAS_ON
                        ? SVGRenderingHints.VALUE_IMAGE_ANTIALIASING_ON
                        : SVGRenderingHints.VALUE_IMAGE_ANTIALIASING_OFF);
    }

    private static void setSVGRenderingHint(@NotNull Graphics2D g, @NotNull RenderingHints.Key key, @NotNull Object o) {
        if (g.getRenderingHint(key) == null) {
            g.setRenderingHint(key, o);
        }
    }
}
