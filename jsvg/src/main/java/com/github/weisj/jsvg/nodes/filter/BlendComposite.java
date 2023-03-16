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
package com.github.weisj.jsvg.nodes.filter;

/*
 * $Id: BlendComposite.java,v 1.9 2007/02/28 01:21:29 gfx Exp $
 *
 * Dual-licensed under LGPL (Sun and Romain Guy) and BSD (Romain Guy).
 *
 * Copyright 2005 Sun Microsystems, Inc., 4150 Network Circle, Santa Clara, California 95054, U.S.A.
 * All rights reserved.
 *
 * Copyright (c) 2006 Romain Guy <romain.guy@mac.com> All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted
 * provided that the following conditions are met: 1. Redistributions of source code must retain the
 * above copyright notice, this list of conditions and the following disclaimer. 2. Redistributions
 * in binary form must reproduce the above copyright notice, this list of conditions and the
 * following disclaimer in the documentation and/or other materials provided with the distribution.
 * 3. The name of the author may not be used to endorse or promote products derived from this
 * software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR IMPLIED WARRANTIES,
 * INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A
 * PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED
 * TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT
 * LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

import java.awt.*;
import java.awt.image.*;

import org.jetbrains.annotations.NotNull;

import com.github.weisj.jsvg.attributes.filter.BlendMode;
import com.github.weisj.jsvg.util.ColorUtil;

/**
 * <p>A blend composite defines the rule according to which a drawing primitive
 * (known as the source) is mixed with existing graphics (know as the
 * destination.)</p>
 * <p><code>BlendComposite</code> is an implementation of the
 * {@link java.awt.Composite} interface and must therefore be set as a state on
 * a {@link java.awt.Graphics2D} surface.</p>
 * <p>Please refer to {@link java.awt.Graphics2D#setComposite(java.awt.Composite)}
 * for more information on how to use this class with a graphics surface.</p>
 * <h2>Blending Modes</h2>
 * <p>This class offers a certain number of blending modes, or compositing
 * rules. These rules are inspired from graphics editing software packages,
 * like <em>Adobe Photoshop</em> or <em>The GIMP</em>.</p>
 * <p>Given the wide variety of implemented blending modes and the difficulty
 * to describe them with words, please refer to those tools to visually see
 * the result of these blending modes.</p>
 *
 * @see java.awt.Graphics2D
 * @see java.awt.Composite
 * @see java.awt.AlphaComposite
 * @author Romain Guy <romain.guy@mac.com>
 */
public final class BlendComposite implements Composite {

    private final BlendMode mode;

    public BlendComposite(BlendMode mode) {
        this.mode = mode;
    }

    /**
     * <p>Returns the blending mode of this composite.</p>
     *
     * @return the blending mode used by this object
     */
    private BlendMode blendMode() {
        return mode;
    }

    @Override
    public int hashCode() {
        return mode.ordinal();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof BlendComposite)) return false;
        BlendComposite bc = (BlendComposite) obj;
        return mode == bc.mode;
    }

    private static boolean isColorModelInvalid(ColorModel cm) {
        if ((cm instanceof DirectColorModel) && (cm.getTransferType() == DataBuffer.TYPE_INT)) {
            DirectColorModel directCM = (DirectColorModel) cm;

            return !((directCM.getRedMask() == 0x00FF0000)
                    && (directCM.getGreenMask() == 0x0000FF00)
                    && (directCM.getBlueMask() == 0x000000FF)
                    && ((directCM.getNumComponents() != 4) || (directCM.getAlphaMask() == 0xFF000000)));
        }
        return true;
    }

    @Override
    public CompositeContext createContext(ColorModel srcColorModel, ColorModel dstColorModel, RenderingHints hints) {
        if (isColorModelInvalid(srcColorModel) || isColorModelInvalid(dstColorModel)) {
            throw new RasterFormatException("Incompatible color models");
        }
        return new BlendingContext(blendMode());
    }

    private static final class BlendingContext implements CompositeContext {
        private final @NotNull Blender blender;

        private BlendingContext(BlendMode blendMode) {
            this.blender = Blender.forBlendMode(blendMode);
        }

        @Override
        public void dispose() {}

        @Override
        public void compose(@NotNull Raster src, @NotNull Raster dstIn, @NotNull WritableRaster dstOut) {
            int width = Math.min(src.getWidth(), dstIn.getWidth());
            int height = Math.min(src.getHeight(), dstIn.getHeight());

            int[] result = new int[4];
            int[] srcPixel = new int[4];
            int[] dstPixel = new int[4];
            int[] srcPixels = new int[width];
            int[] dstPixels = new int[width];

            for (int y = 0; y < height; y++) {
                src.getDataElements(0, y, width, 1, srcPixels);
                dstIn.getDataElements(0, y, width, 1, dstPixels);

                for (int x = 0; x < width; x++) {
                    // pixels are stored as INT_ARGB
                    // our arrays are [R, G, B, A]
                    int pixel = srcPixels[x];
                    srcPixel[0] = (pixel >> 16) & 0xFF;
                    srcPixel[1] = (pixel >> 8) & 0xFF;
                    srcPixel[2] = pixel & 0xFF;
                    srcPixel[3] = (pixel >> 24) & 0xFF;

                    pixel = dstPixels[x];
                    dstPixel[0] = (pixel >> 16) & 0xFF;
                    dstPixel[1] = (pixel >> 8) & 0xFF;
                    dstPixel[2] = pixel & 0xFF;
                    dstPixel[3] = (pixel >> 24) & 0xFF;

                    blender.blend(srcPixel, dstPixel, result);

                    dstPixels[x] = ((result[3] & 0xFF) << 24)
                            | ((result[0] & 0xFF) << 16)
                            | ((result[1] & 0xFF) << 8)
                            | (result[2] & 0xFF);
                }
                dstOut.setDataElements(0, y, width, 1, dstPixels);
            }
        }
    }

    private static abstract class Blender {
        protected abstract void blend(int[] src, int[] dst, int[] result);

        static Blender forBlendMode(BlendMode blendMode) {
            switch (blendMode) {
                case Normal:
                    return new Blender() {

                        @Override
                        public void blend(int[] src, int[] dst, int[] result) {
                            if (src[3] == 0) {
                                result[0] = dst[0];
                                result[1] = dst[1];
                                result[2] = dst[2];
                                result[3] = dst[3];
                            } else {
                                result[0] = src[0];
                                result[1] = src[1];
                                result[2] = src[2];
                                result[3] = src[3];
                            }
                        }
                    };
                case Multiply:
                    return new Blender() {
                        @Override
                        public void blend(int[] src, int[] dst, int[] result) {
                            result[0] = (src[0] * dst[0]) >> 8;
                            result[1] = (src[1] * dst[1]) >> 8;
                            result[2] = (src[2] * dst[2]) >> 8;
                            result[3] = (src[3] * dst[3]) >> 8;
                        }
                    };
                case Screen:
                    return new Blender() {
                        @Override
                        public void blend(int[] src, int[] dst, int[] result) {
                            result[0] = 255 - (((255 - src[0]) * (255 - dst[0])) >> 8);
                            result[1] = 255 - (((255 - src[1]) * (255 - dst[1])) >> 8);
                            result[2] = 255 - (((255 - src[2]) * (255 - dst[2])) >> 8);
                            result[3] = 255 - (((255 - src[3]) * (255 - dst[3])) >> 8);
                        }
                    };
                case Overlay:
                    return new Blender() {
                        @Override
                        public void blend(int[] src, int[] dst, int[] result) {
                            result[0] = (dst[0] < 128)
                                    ? ((dst[0] * src[0]) >> 7)
                                    : (255 - (((255 - dst[0]) * (255 - src[0])) >> 7));
                            result[1] = (dst[1] < 128)
                                    ? ((dst[1] * src[1]) >> 7)
                                    : (255 - (((255 - dst[1]) * (255 - src[1])) >> 7));
                            result[2] = (dst[2] < 128)
                                    ? ((dst[2] * src[2]) >> 7)
                                    : (255 - (((255 - dst[2]) * (255 - src[2])) >> 7));
                            result[3] = (dst[3] < 128)
                                    ? ((dst[3] * src[3]) >> 7)
                                    : (255 - (((255 - dst[3]) * (255 - src[3])) >> 7));
                        }
                    };
                case Darken:
                    return new Blender() {
                        @Override
                        public void blend(int[] src, int[] dst, int[] result) {
                            result[0] = Math.min(src[0], dst[0]);
                            result[1] = Math.min(src[1], dst[1]);
                            result[2] = Math.min(src[2], dst[2]);
                            result[3] = Math.min(255, (src[3] + dst[3]) - ((src[3] * dst[3]) / 255));
                        }
                    };
                case Lighten:
                    return new Blender() {
                        @Override
                        public void blend(int[] src, int[] dst, int[] result) {
                            result[0] = Math.max(src[0], dst[0]);
                            result[1] = Math.max(src[1], dst[1]);
                            result[2] = Math.max(src[2], dst[2]);
                            result[3] = Math.min(255, (src[3] + dst[3]) - ((src[3] * dst[3]) / 255));
                        }
                    };
                case ColorDodge:
                    return new Blender() {
                        @Override
                        public void blend(int[] src, int[] dst, int[] result) {
                            result[0] = (src[0] == 255) ? 255 : Math.min((dst[0] << 8) / (255 - src[0]), 255);
                            result[1] = (src[1] == 255) ? 255 : Math.min((dst[1] << 8) / (255 - src[1]), 255);
                            result[2] = (src[2] == 255) ? 255 : Math.min((dst[2] << 8) / (255 - src[2]), 255);
                            result[3] = Math.min(255, (src[3] + dst[3]) - ((src[3] * dst[3]) / 255));
                        }
                    };
                case ColorBurn:
                    return new Blender() {
                        @Override
                        public void blend(int[] src, int[] dst, int[] result) {
                            result[0] = (src[0] == 0) ? 0 : Math.max(0, 255 - (((255 - dst[0]) << 8) / src[0]));
                            result[1] = (src[1] == 0) ? 0 : Math.max(0, 255 - (((255 - dst[1]) << 8) / src[1]));
                            result[2] = (src[2] == 0) ? 0 : Math.max(0, 255 - (((255 - dst[2]) << 8) / src[2]));
                            result[3] = Math.min(255, (src[3] + dst[3]) - ((src[3] * dst[3]) / 255));
                        }
                    };
                case HardLight:
                    return new Blender() {
                        @Override
                        public void blend(int[] src, int[] dst, int[] result) {
                            result[0] = (src[0] < 128)
                                    ? ((dst[0] * src[0]) >> 7)
                                    : (255 - (((255 - src[0]) * (255 - dst[0])) >> 7));
                            result[1] = (src[1] < 128)
                                    ? ((dst[1] * src[1]) >> 7)
                                    : (255 - (((255 - src[1]) * (255 - dst[1])) >> 7));
                            result[2] = (src[2] < 128)
                                    ? ((dst[2] * src[2]) >> 7)
                                    : (255 - (((255 - src[2]) * (255 - dst[2])) >> 7));
                            result[3] = (src[3] < 128)
                                    ? ((dst[3] * src[3]) >> 7)
                                    : (255 - (((255 - src[3]) * (255 - dst[3])) >> 7));
                        }
                    };
                case SoftLight:
                    return new Blender() {
                        @Override
                        public void blend(int[] src, int[] dst, int[] result) {
                            int mRed = (src[0] * dst[0]) / 255;
                            int mGreen = (src[1] * dst[1]) / 255;
                            int mBlue = (src[2] * dst[2]) / 255;
                            result[0] = mRed
                                    + ((src[0] * (255 - (((255 - src[0]) * (255 - dst[0])) / 255) - mRed)) / 255);
                            result[1] = mGreen
                                    + ((src[1] * (255 - (((255 - src[1]) * (255 - dst[1])) / 255) - mGreen)) / 255);
                            result[2] = mBlue
                                    + ((src[2] * (255 - (((255 - src[2]) * (255 - dst[2])) / 255) - mBlue)) / 255);
                            result[3] = Math.min(255, (src[3] + dst[3]) - ((src[3] * dst[3]) / 255));
                        }
                    };
                case Difference:
                    return new Blender() {
                        @Override
                        public void blend(int[] src, int[] dst, int[] result) {
                            result[0] = Math.abs(dst[0] - src[0]);
                            result[1] = Math.abs(dst[1] - src[1]);
                            result[2] = Math.abs(dst[2] - src[2]);
                            result[3] = Math.min(255, (src[3] + dst[3]) - ((src[3] * dst[3]) / 255));
                        }
                    };
                case Exclusion:
                    return new Blender() {
                        @Override
                        public void blend(int[] src, int[] dst, int[] result) {
                            result[0] = (dst[0] + src[0]) - ((dst[0] * src[0]) >> 7);
                            result[1] = (dst[1] + src[1]) - ((dst[1] * src[1]) >> 7);
                            result[2] = (dst[2] + src[2]) - ((dst[2] * src[2]) >> 7);
                            result[3] = Math.min(255, (src[3] + dst[3]) - ((src[3] * dst[3]) / 255));
                        }
                    };
                case Hue:
                    return new Blender() {
                        @Override
                        public void blend(int[] src, int[] dst, int[] result) {
                            float[] srcHSL = new float[3];
                            ColorUtil.RGBtoHSL(src[0], src[1], src[2], srcHSL);
                            float[] dstHSL = new float[3];
                            ColorUtil.RGBtoHSL(dst[0], dst[1], dst[2], dstHSL);

                            ColorUtil.HSLtoRGB(srcHSL[0], dstHSL[1], dstHSL[2], result);
                            result[3] = Math.min(255, (src[3] + dst[3]) - ((src[3] * dst[3]) / 255));
                        }
                    };
                case Saturation:
                    return new Blender() {
                        @Override
                        public void blend(int[] src, int[] dst, int[] result) {
                            float[] srcHSL = new float[3];
                            ColorUtil.RGBtoHSL(src[0], src[1], src[2], srcHSL);
                            float[] dstHSL = new float[3];
                            ColorUtil.RGBtoHSL(dst[0], dst[1], dst[2], dstHSL);

                            ColorUtil.HSLtoRGB(dstHSL[0], srcHSL[1], dstHSL[2], result);
                            result[3] = Math.min(255, (src[3] + dst[3]) - ((src[3] * dst[3]) / 255));
                        }
                    };
                case Color:
                    return new Blender() {
                        @Override
                        public void blend(int[] src, int[] dst, int[] result) {
                            float[] srcHSL = new float[3];
                            ColorUtil.RGBtoHSL(src[0], src[1], src[2], srcHSL);
                            float[] dstHSL = new float[3];
                            ColorUtil.RGBtoHSL(dst[0], dst[1], dst[2], dstHSL);

                            ColorUtil.HSLtoRGB(srcHSL[0], srcHSL[1], dstHSL[2], result);
                            result[3] = Math.min(255, (src[3] + dst[3]) - ((src[3] * dst[3]) / 255));
                        }
                    };
                case Luminosity:
                    return new Blender() {
                        @Override
                        public void blend(int[] src, int[] dst, int[] result) {
                            float[] srcHSL = new float[3];
                            ColorUtil.RGBtoHSL(src[0], src[1], src[2], srcHSL);
                            float[] dstHSL = new float[3];
                            ColorUtil.RGBtoHSL(dst[0], dst[1], dst[2], dstHSL);

                            ColorUtil.HSLtoRGB(dstHSL[0], dstHSL[1], srcHSL[2], result);
                            result[3] = Math.min(255, (src[3] + dst[3]) - ((src[3] * dst[3]) / 255));
                        }
                    };
            }
            throw new IllegalStateException("Mode not recognized " + blendMode);
        }


    }
}
