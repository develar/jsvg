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
package com.github.weisj.jsvg.nodes.path;

import java.awt.geom.Point2D;

/**
 * When building a path from command segments, most need to cache information
 * (such as the point finished at) for future commands. This structure allows
 * that
 *
 * @author Mark McKay
 * @author <a href="mailto:mark@kitfox.com">Mark McKay</a>
 */
public class BuildHistory {

    Point2D.Float startPoint = new Point2D.Float();
    Point2D.Float lastPoint = new Point2D.Float();
    Point2D.Float lastKnot = new Point2D.Float();

    public void setStartPoint(float x, float y) {
        startPoint.setLocation(x, y);
    }

    public void setLastPoint(float x, float y) {
        lastPoint.setLocation(x, y);
    }

    public void setLastKnot(float x, float y) {
        lastKnot.setLocation(x, y);
    }
}
