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
package com.github.weisj.jsvg.geometry.path;

import java.awt.geom.Point2D;

import org.jetbrains.annotations.NotNull;

import com.github.weisj.jsvg.geometry.mesh.Bezier;

final class CubicBezierCommand implements BezierPathCommand {

    private final boolean relative;
    private final float bx;
    private final float by;
    private final float cx;
    private final float cy;
    private final float dx;
    private final float dy;

    public CubicBezierCommand(boolean relative, float bx, float by, float cx, float cy, float dx, float dy) {
        this.relative = relative;
        this.bx = bx;
        this.by = by;
        this.cx = cx;
        this.cy = cy;
        this.dx = dx;
        this.dy = dy;
    }

    @Override
    public @NotNull Bezier createBezier(@NotNull Point2D.Float start) {
        if (relative) {
            return new Bezier(start,
                    new Point2D.Float(start.x + bx, start.y + by),
                    new Point2D.Float(start.x + cx, start.y + cy),
                    new Point2D.Float(start.x + dx, start.y + dy));
        } else {
            return new Bezier(start,
                    new Point2D.Float(bx, by),
                    new Point2D.Float(cx, cy),
                    new Point2D.Float(dx, dy));
        }
    }

    @Override
    public String toString() {
        return "CubicBezierCommand{" +
                "relative=" + relative +
                ", bx=" + bx +
                ", by=" + by +
                ", cx=" + cx +
                ", cy=" + cy +
                ", dx=" + dx +
                ", dy=" + dy +
                '}';
    }
}
