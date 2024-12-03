/*
 * Decompiled with CFR 0.152.
 */
package com.sun.media.jai.opimage;

import com.sun.media.jai.opimage.ImageFunctionOpImage;
import com.sun.media.jai.opimage.JaiI18N;
import com.sun.media.jai.opimage.RIFUtil;
import java.awt.RenderingHints;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.ParameterBlock;
import java.awt.image.renderable.RenderedImageFactory;
import javax.media.jai.ImageFunction;
import javax.media.jai.ImageLayout;

public class ImageFunctionRIF
implements RenderedImageFactory {
    public RenderedImage create(ParameterBlock paramBlock, RenderingHints renderHints) {
        int numBandsRequired;
        ImageLayout layout = RIFUtil.getImageLayoutHint(renderHints);
        ImageFunction function = (ImageFunction)paramBlock.getObjectParameter(0);
        int n = numBandsRequired = function.isComplex() ? function.getNumElements() * 2 : function.getNumElements();
        if (layout != null && layout.isValid(256) && layout.getSampleModel(null).getNumBands() != numBandsRequired) {
            throw new RuntimeException(JaiI18N.getString("ImageFunctionRIF0"));
        }
        int minX = 0;
        int minY = 0;
        if (layout != null) {
            if (layout.isValid(1)) {
                minX = layout.getMinX(null);
            }
            if (layout.isValid(2)) {
                minY = layout.getMinX(null);
            }
        }
        int width = paramBlock.getIntParameter(1);
        int height = paramBlock.getIntParameter(2);
        float xScale = paramBlock.getFloatParameter(3);
        float yScale = paramBlock.getFloatParameter(4);
        float xTrans = paramBlock.getFloatParameter(5);
        float yTrans = paramBlock.getFloatParameter(6);
        return new ImageFunctionOpImage(function, minX, minY, width, height, xScale, yScale, xTrans, yTrans, renderHints, layout);
    }
}

