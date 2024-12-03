/*
 * Decompiled with CFR 0.152.
 */
package com.sun.media.jai.opimage;

import com.sun.media.jai.opimage.DilateBinaryOpImage;
import com.sun.media.jai.opimage.DilateOpImage;
import com.sun.media.jai.opimage.RIFUtil;
import java.awt.RenderingHints;
import java.awt.image.MultiPixelPackedSampleModel;
import java.awt.image.RenderedImage;
import java.awt.image.SampleModel;
import java.awt.image.renderable.ParameterBlock;
import java.awt.image.renderable.RenderedImageFactory;
import javax.media.jai.BorderExtender;
import javax.media.jai.ImageLayout;
import javax.media.jai.KernelJAI;

public class DilateRIF
implements RenderedImageFactory {
    public RenderedImage create(ParameterBlock paramBlock, RenderingHints renderHints) {
        boolean isBinary;
        ImageLayout layout = RIFUtil.getImageLayoutHint(renderHints);
        BorderExtender extender = RIFUtil.getBorderExtenderHint(renderHints);
        KernelJAI unRotatedKernel = (KernelJAI)paramBlock.getObjectParameter(0);
        KernelJAI kJAI = unRotatedKernel.getRotatedKernel();
        RenderedImage source = paramBlock.getRenderedSource(0);
        SampleModel sm = source.getSampleModel();
        int dataType = sm.getDataType();
        boolean bl = isBinary = sm instanceof MultiPixelPackedSampleModel && sm.getSampleSize(0) == 1 && (dataType == 0 || dataType == 1 || dataType == 3);
        if (isBinary) {
            return new DilateBinaryOpImage(source, extender, renderHints, layout, kJAI);
        }
        return new DilateOpImage(source, extender, renderHints, layout, kJAI);
    }
}

