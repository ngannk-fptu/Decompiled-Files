/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package org.apache.pdfbox.pdmodel.graphics.blend;

import java.awt.AlphaComposite;
import java.awt.Composite;
import java.awt.CompositeContext;
import java.awt.RenderingHints;
import java.awt.color.ColorSpace;
import java.awt.image.ColorModel;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.pdfbox.pdmodel.graphics.blend.BlendMode;
import org.apache.pdfbox.pdmodel.graphics.blend.NonSeparableBlendMode;
import org.apache.pdfbox.pdmodel.graphics.blend.SeparableBlendMode;

public final class BlendComposite
implements Composite {
    private static final Log LOG = LogFactory.getLog(BlendComposite.class);
    private final BlendMode blendMode;
    private final float constantAlpha;

    public static Composite getInstance(BlendMode blendMode, float constantAlpha) {
        if (constantAlpha < 0.0f) {
            LOG.warn((Object)("using 0 instead of incorrect Alpha " + constantAlpha));
            constantAlpha = 0.0f;
        } else if (constantAlpha > 1.0f) {
            LOG.warn((Object)("using 1 instead of incorrect Alpha " + constantAlpha));
            constantAlpha = 1.0f;
        }
        if (blendMode == BlendMode.NORMAL) {
            return AlphaComposite.getInstance(3, constantAlpha);
        }
        return new BlendComposite(blendMode, constantAlpha);
    }

    private BlendComposite(BlendMode blendMode, float constantAlpha) {
        this.blendMode = blendMode;
        this.constantAlpha = constantAlpha;
    }

    @Override
    public CompositeContext createContext(ColorModel srcColorModel, ColorModel dstColorModel, RenderingHints hints) {
        return new BlendCompositeContext(srcColorModel, dstColorModel);
    }

    class BlendCompositeContext
    implements CompositeContext {
        private final ColorModel srcColorModel;
        private final ColorModel dstColorModel;

        BlendCompositeContext(ColorModel srcColorModel, ColorModel dstColorModel) {
            this.srcColorModel = srcColorModel;
            this.dstColorModel = dstColorModel;
        }

        @Override
        public void dispose() {
        }

        @Override
        public void compose(Raster src, Raster dstIn, WritableRaster dstOut) {
            int x0 = src.getMinX();
            int y0 = src.getMinY();
            int width = Math.min(Math.min(src.getWidth(), dstIn.getWidth()), dstOut.getWidth());
            int height = Math.min(Math.min(src.getHeight(), dstIn.getHeight()), dstOut.getHeight());
            int x1 = x0 + width;
            int y1 = y0 + height;
            int dstInXShift = dstIn.getMinX() - x0;
            int dstInYShift = dstIn.getMinY() - y0;
            int dstOutXShift = dstOut.getMinX() - x0;
            int dstOutYShift = dstOut.getMinY() - y0;
            ColorSpace srcColorSpace = this.srcColorModel.getColorSpace();
            int numSrcColorComponents = this.srcColorModel.getNumColorComponents();
            int numSrcComponents = src.getNumBands();
            boolean srcHasAlpha = numSrcComponents > numSrcColorComponents;
            ColorSpace dstColorSpace = this.dstColorModel.getColorSpace();
            int numDstColorComponents = this.dstColorModel.getNumColorComponents();
            int numDstComponents = dstIn.getNumBands();
            boolean dstHasAlpha = numDstComponents > numDstColorComponents;
            int srcColorSpaceType = srcColorSpace.getType();
            int dstColorSpaceType = dstColorSpace.getType();
            boolean subtractive = dstColorSpaceType != 5 && dstColorSpaceType != 6;
            boolean blendModeIsSeparable = BlendComposite.this.blendMode instanceof SeparableBlendMode;
            SeparableBlendMode separableBlendMode = blendModeIsSeparable ? (SeparableBlendMode)BlendComposite.this.blendMode : null;
            NonSeparableBlendMode nonSeparableBlendMode = !blendModeIsSeparable ? (NonSeparableBlendMode)BlendComposite.this.blendMode : null;
            boolean needsColorConversion = !srcColorSpace.equals(dstColorSpace);
            Object srcPixel = null;
            Object dstPixel = null;
            float[] srcComponents = new float[numSrcComponents];
            float[] dstComponents = null;
            float[] srcColor = new float[numSrcColorComponents];
            float[] rgbResult = blendModeIsSeparable ? null : new float[dstHasAlpha ? 4 : 3];
            for (int y = y0; y < y1; ++y) {
                for (int x = x0; x < x1; ++x) {
                    float value;
                    float dstValue;
                    float srcValue;
                    float[] srcConverted;
                    float srcAlphaRatio;
                    srcPixel = src.getDataElements(x, y, srcPixel);
                    dstPixel = dstIn.getDataElements(dstInXShift + x, dstInYShift + y, dstPixel);
                    srcComponents = this.srcColorModel.getNormalizedComponents(srcPixel, srcComponents, 0);
                    dstComponents = this.dstColorModel.getNormalizedComponents(dstPixel, dstComponents, 0);
                    float srcAlpha = srcHasAlpha ? srcComponents[numSrcColorComponents] : 1.0f;
                    float dstAlpha = dstHasAlpha ? dstComponents[numDstColorComponents] : 1.0f;
                    float resultAlpha = dstAlpha + (srcAlpha *= BlendComposite.this.constantAlpha) - srcAlpha * dstAlpha;
                    float f = srcAlphaRatio = resultAlpha > 0.0f ? srcAlpha / resultAlpha : 0.0f;
                    if (separableBlendMode != null) {
                        System.arraycopy(srcComponents, 0, srcColor, 0, numSrcColorComponents);
                        if (needsColorConversion) {
                            float[] cieXYZ = srcColorSpace.toCIEXYZ(srcColor);
                            srcConverted = dstColorSpace.fromCIEXYZ(cieXYZ);
                        } else {
                            srcConverted = srcColor;
                        }
                        for (int k = 0; k < numDstColorComponents; ++k) {
                            srcValue = srcConverted[k];
                            dstValue = dstComponents[k];
                            if (subtractive) {
                                srcValue = 1.0f - srcValue;
                                dstValue = 1.0f - dstValue;
                            }
                            value = separableBlendMode.blendChannel(srcValue, dstValue);
                            value = srcValue + dstAlpha * (value - srcValue);
                            value = dstValue + srcAlphaRatio * (value - dstValue);
                            if (subtractive) {
                                value = 1.0f - value;
                            }
                            dstComponents[k] = value;
                        }
                    } else {
                        srcConverted = srcColorSpaceType == 5 ? srcComponents : srcColorSpace.toRGB(srcComponents);
                        float[] dstConverted = dstColorSpaceType == 5 ? dstComponents : dstColorSpace.toRGB(dstComponents);
                        nonSeparableBlendMode.blend(srcConverted, dstConverted, rgbResult);
                        for (int k = 0; k < 3; ++k) {
                            srcValue = srcConverted[k];
                            dstValue = dstConverted[k];
                            value = rgbResult[k];
                            value = Math.max(Math.min(value, 1.0f), 0.0f);
                            value = srcValue + dstAlpha * (value - srcValue);
                            rgbResult[k] = value = dstValue + srcAlphaRatio * (value - dstValue);
                        }
                        if (dstColorSpaceType == 5) {
                            System.arraycopy(rgbResult, 0, dstComponents, 0, dstComponents.length);
                        } else {
                            float[] temp = dstColorSpace.fromRGB(rgbResult);
                            System.arraycopy(temp, 0, dstComponents, 0, Math.min(dstComponents.length, temp.length));
                        }
                    }
                    if (dstHasAlpha) {
                        dstComponents[numDstColorComponents] = resultAlpha;
                    }
                    dstPixel = this.dstColorModel.getDataElements(dstComponents, 0, dstPixel);
                    dstOut.setDataElements(dstOutXShift + x, dstOutYShift + y, dstPixel);
                }
            }
        }
    }
}

