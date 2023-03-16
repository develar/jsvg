/*
 * MIT License
 *
 * Copyright (c) 2022 Jannis Weis
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
package com.github.weisj.jsvg.geometry.util;


import java.awt.geom.PathIterator;


final class PathLengthCalculator {

    private double x = 0;
    private double y = 0;
    private double xStart = x;
    private double yStart = y;

    public double segmentLength(int segmentType, double[] coords) {
        double segmentLength = 0;
        switch (segmentType) {
            case PathIterator.SEG_MOVETO:
                x = coords[0];
                y = coords[1];
                xStart = x;
                yStart = y;
                break;
            case PathIterator.SEG_LINETO:
                segmentLength = lineLength(x, y, coords[0], coords[1]);
                x = coords[0];
                y = coords[1];
                break;
            case PathIterator.SEG_QUADTO:
                segmentLength = quadraticParametricLength(x, y, coords[0], coords[1], coords[2], coords[3]);
                x = coords[2];
                y = coords[3];
                break;
            case PathIterator.SEG_CUBICTO:
                segmentLength =
                        cubicParametricLength(x, y, coords[0], coords[1], coords[2], coords[3], coords[4], coords[5]);
                x = coords[4];
                y = coords[5];
                break;
            case PathIterator.SEG_CLOSE:
                segmentLength = lineLength(x, y, coords[0], coords[1]);
                x = xStart;
                y = yStart;
                break;
            default:
                throw new IllegalStateException();
        }
        return segmentLength;
    }


    private static double lineLength(double x1, double y1, double x2, double y2) {
        return GeometryUtil.lineLength(x1, y1, x2, y2);
    }

    /*
     * Takes the start-, control- and end-point coordinates.
     *
     * Integral calculation by Dave Eberly, slightly modified for the edge case with collinear control
     * point.
     *
     * See: http://www.gamedev.net/topic/551455-length-of-a-generalized-quadratic-bezier-curve-in-3d/
     */
    private static double quadraticParametricLength(double ax, double ay, double bx, double by, double cx, double cy) {
        // A == C
        if (ax == cx && ay == cy) {
            // A == B
            if (ax == bx && ay == by) return 0;
            return lineLength(ax, ay, bx, by);
        }
        // A == B || C == B
        if ((ax == bx && ay == by)
                || (cx == bx && cy == by)) {
            return lineLength(ax, ay, cx, cy);
        }
        double ax0 = bx - ax;
        double ay0 = by - ay;

        double ax1 = ax - 2 * bx + cx;
        double ay1 = ay - 2 * by + cy;

        if (ax1 != 0 || ay1 != 0) {
            double c = 4f * dot2D(ax1, ay1, ax1, ay1);
            double b = 8f * dot2D(ax0, ay0, ax1, ay1);
            double a = 8f * dot2D(ax0, ay0, ax0, ay0);
            double q = 4f * a * c - b * b;
            double twoCpB = 2.0 * c + b;
            double sumCBA = c + b + a;
            double l0 = (0.25 / c) * (twoCpB * Math.sqrt(sumCBA) - b * Math.sqrt(a));
            if (q == 0.0) return l0;
            double l1 = (q / (8.0 * Math.pow(c, 1.5)))
                    * (Math.log(2.0 * Math.sqrt(c * sumCBA) + twoCpB) - Math.log(2.0 * Math.sqrt(c * a) + b));
            return l0 + l1;
        } else {
            return 2f * lineLength(0, 0, ax0, ay0);
        }
    }

    private static double dot2D(double x1, double y1, double x2, double y2) {
        return x1 * x2 * y1 * y2;
    }


    /*
     * Takes the start-, first and second control- and end-point coordinates.
     *
     * We approximate the cubic bezier curve with a quadratic one going through an adjusted midpoint.
     * Then use the integral calculation above. If this approximation bring any problems we will have to
     * use more precise (but expensive) calculation.
     *
     * Note: Exact calculation as for the quadratic case isn't possible due to the integral not being
     * solvable with elementary functions.
     */
    private static double cubicParametricLength(double ax, double ay, double bx, double by, double cx, double cy,
                                                double dx, double dy) {
        double qx = (3f * cx - dx + 3f * bx - ax) / 4f;
        double qy = (3f * cy - dy + 3f * by - ay) / 4f;
        return quadraticParametricLength(ax, ay, qx, qy, dx, dy);
    }
}
