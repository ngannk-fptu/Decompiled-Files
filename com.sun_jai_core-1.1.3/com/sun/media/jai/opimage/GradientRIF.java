/*
 * Decompiled with CFR 0.152.
 */
package com.sun.media.jai.opimage;

import com.sun.media.jai.opimage.GradientOpImage;
import com.sun.media.jai.opimage.RIFUtil;
import java.awt.RenderingHints;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.ParameterBlock;
import java.awt.image.renderable.RenderedImageFactory;
import javax.media.jai.BorderExtender;
import javax.media.jai.ImageLayout;
import javax.media.jai.KernelJAI;

public class GradientRIF
implements RenderedImageFactory {
    public RenderedImage create(ParameterBlock paramBlock, RenderingHints renderHints) {
        ImageLayout layout = RIFUtil.getImageLayoutHint(renderHints);
        BorderExtender extender = RIFUtil.getBorderExtenderHint(renderHints);
        RenderedImage source = paramBlock.getRenderedSource(0);
        KernelJAI kern_h = (KernelJAI)paramBlock.getObjectParameter(0);
        KernelJAI kern_v = (KernelJAI)paramBlock.getObjectParameter(1);
        return new GradientOpImage(source, extender, renderHints, layout, kern_h, kern_v);
    }
}

