/*
 * Decompiled with CFR 0.152.
 */
package com.sun.media.jai.opimage;

import com.sun.media.jai.opimage.ErrorDiffusionOpImage;
import com.sun.media.jai.opimage.RIFUtil;
import java.awt.RenderingHints;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.ParameterBlock;
import java.awt.image.renderable.RenderedImageFactory;
import javax.media.jai.ImageLayout;
import javax.media.jai.KernelJAI;
import javax.media.jai.LookupTableJAI;

public class ErrorDiffusionRIF
implements RenderedImageFactory {
    public RenderedImage create(ParameterBlock paramBlock, RenderingHints renderHints) {
        ImageLayout layout = RIFUtil.getImageLayoutHint(renderHints);
        RenderedImage source = paramBlock.getRenderedSource(0);
        LookupTableJAI lookupTable = (LookupTableJAI)paramBlock.getObjectParameter(0);
        KernelJAI kernel = (KernelJAI)paramBlock.getObjectParameter(1);
        return new ErrorDiffusionOpImage(source, renderHints, layout, lookupTable, kernel);
    }
}

