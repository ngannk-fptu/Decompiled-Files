/*
 * Decompiled with CFR 0.152.
 */
package com.sun.media.jai.opimage;

import com.sun.media.jai.opimage.ErodeBinaryOpImage;
import com.sun.media.jai.opimage.ErodeOpImage;
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

public class ErodeRIF
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
            return new ErodeBinaryOpImage(source, extender, renderHints, layout, kJAI);
        }
        return new ErodeOpImage(source, extender, renderHints, layout, kJAI);
    }
}

