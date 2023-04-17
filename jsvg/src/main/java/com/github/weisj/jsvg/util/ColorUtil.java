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
package com.github.weisj.jsvg.util;

import java.awt.Color;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class ColorUtil {
    private ColorUtil() {}

    public static Color withAlpha(@NotNull Color c, float alpha) {
        int a = Math.max(Math.min(255, (int) (alpha * 255)), 0);
        return new Color(c.getRed(), c.getGreen(), c.getBlue(), a);
    }

    public static String toString(@Nullable Color c) {
        if (c == null) return "null";
        return String.format("Color[%d,%d,%d,%d]", c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha());
    }

    public static void RGBtoHSL(int r, int g, int b, float @NotNull [] hsl) {
        if (r < 0)
            r = 0;
        else if (r > 255) r = 255;
        if (g < 0)
            g = 0;
        else if (g > 255) g = 255;
        if (b < 0)
            b = 0;
        else if (b > 255) b = 255;

        float componentR = r / 255f;
        float componentG = g / 255f;
        float componentB = b / 255f;

        float minComponent;
        float maxComponent;

        if (componentR > componentG) {
            minComponent = componentG;
            maxComponent = componentR;
        } else {
            minComponent = componentR;
            maxComponent = componentG;
        }
        if (componentB > maxComponent) {
            maxComponent = componentB;
        }
        if (componentB < minComponent) {
            minComponent = componentB;
        }

        float deltaMax = maxComponent - minComponent;

        float h;
        float s;
        float l;
        l = (maxComponent + minComponent) / 2f;

        if (deltaMax - 0.01f <= 0.0f) {
            h = 0;
            s = 0;
        } else {
            if (l < 0.5f) {
                assert maxComponent + minComponent != 0;
                s = deltaMax / (maxComponent + minComponent);
            } else {
                s = deltaMax / (2 - maxComponent - minComponent);
            }

            assert deltaMax > 0;
            float deltaR = (((maxComponent - componentR) / 6f) + (deltaMax / 2f)) / deltaMax;
            float deltaG = (((maxComponent - componentG) / 6f) + (deltaMax / 2f)) / deltaMax;
            float deltaB = (((maxComponent - componentB) / 6f) + (deltaMax / 2f)) / deltaMax;

            if (componentR == maxComponent) {
                h = deltaB - deltaG;
            } else if (componentG == maxComponent) {
                h = (1 / 3f) + deltaR - deltaB;
            } else {
                h = (2 / 3f) + deltaG - deltaR;
            }
            if (h < 0) {
                h += 1;
            }
            if (h > 1) {
                h -= 1;
            }
        }

        hsl[0] = h;
        hsl[1] = s;
        hsl[2] = l;
    }

    public static void HSLtoRGB(float h, float s, float l, int @NotNull [] rgb) {
        if (h < 0)
            h = 0.0f;
        else if (h > 1.0f) h = 1.0f;
        if (s < 0)
            s = 0.0f;
        else if (s > 1.0f) s = 1.0f;
        if (l < 0)
            l = 0.0f;
        else if (l > 1.0f) l = 1.0f;

        int r;
        int g;
        int b;

        if (s - 0.01f <= 0.0f) {
            r = (int) (l * 255.0f);
            g = (int) (l * 255.0f);
            b = (int) (l * 255.0f);
        } else {
            float x;
            float y;
            if (l < 0.5f) {
                y = l * (1 + s);
            } else {
                y = (l + s) - (s * l);
            }
            x = 2 * l - y;

            r = (int) (255.0f * hue2RGB(x, y, h + (1.0f / 3.0f)));
            g = (int) (255.0f * hue2RGB(x, y, h));
            b = (int) (255.0f * hue2RGB(x, y, h - (1.0f / 3.0f)));
        }

        rgb[0] = r;
        rgb[1] = g;
        rgb[2] = b;
    }

    private static float hue2RGB(float v1, float v2, float vH) {
        if (vH < 0.0f) {
            vH += 1.0f;
        }
        if (vH > 1.0f) {
            vH -= 1.0f;
        }
        if ((6.0f * vH) < 1.0f) {
            return (v1 + (v2 - v1) * 6.0f * vH);
        }
        if ((2.0f * vH) < 1.0f) {
            return (v2);
        }
        if ((3.0f * vH) < 2.0f) {
            return (v1 + (v2 - v1) * ((2.0f / 3.0f) - vH) * 6.0f);
        }
        return (v1);
    }
}
