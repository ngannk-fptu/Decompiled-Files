/*
 * Decompiled with CFR 0.152.
 */
package com.sun.media.jai.opimage;

import com.sun.media.jai.opimage.MedianCutOpImage;
import com.sun.media.jai.opimage.NeuQuantOpImage;
import com.sun.media.jai.opimage.OctTreeOpImage;
import java.awt.RenderingHints;
import java.awt.image.RenderedImage;
import java.awt.image.SampleModel;
import java.awt.image.renderable.ParameterBlock;
import java.awt.image.renderable.RenderedImageFactory;
import javax.media.jai.ImageLayout;
import javax.media.jai.JAI;
import javax.media.jai.ROI;
import javax.media.jai.operator.ColorQuantizerDescriptor;
import javax.media.jai.operator.ColorQuantizerType;

public class ColorQuantizerRIF
implements RenderedImageFactory {
    public RenderedImage create(ParameterBlock paramBlock, RenderingHints renderHints) {
        RenderedImage source = paramBlock.getRenderedSource(0);
        ImageLayout layout = renderHints == null ? null : (ImageLayout)renderHints.get(JAI.KEY_IMAGE_LAYOUT);
        ColorQuantizerType algorithm = (ColorQuantizerType)paramBlock.getObjectParameter(0);
        int maxColorNum = paramBlock.getIntParameter(1);
        int upperBound = paramBlock.getIntParameter(2);
        ROI roi = (ROI)paramBlock.getObjectParameter(3);
        int xPeriod = paramBlock.getIntParameter(4);
        int yPeriod = paramBlock.getIntParameter(5);
        SampleModel sm = source.getSampleModel();
        if (sm.getNumBands() != 3 && sm.getDataType() == 0) {
            throw new IllegalArgumentException("ColorQuantizerRIF0");
        }
        if (algorithm.equals(ColorQuantizerDescriptor.NEUQUANT)) {
            return new NeuQuantOpImage(source, renderHints, layout, maxColorNum, upperBound, roi, xPeriod, yPeriod);
        }
        if (algorithm.equals(ColorQuantizerDescriptor.OCTTREE)) {
            return new OctTreeOpImage(source, renderHints, layout, maxColorNum, upperBound, roi, xPeriod, yPeriod);
        }
        return new MedianCutOpImage(source, renderHints, layout, maxColorNum, upperBound, roi, xPeriod, yPeriod);
    }
}

