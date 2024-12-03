/*
 * Decompiled with CFR 0.152.
 */
package com.sun.media.jai.opimage;

import com.sun.media.jai.opimage.OrderedDitherOpImage;
import com.sun.media.jai.opimage.RIFUtil;
import java.awt.RenderingHints;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.ParameterBlock;
import java.awt.image.renderable.RenderedImageFactory;
import javax.media.jai.ColorCube;
import javax.media.jai.ImageLayout;
import javax.media.jai.KernelJAI;

public class OrderedDitherRIF
implements RenderedImageFactory {
    public RenderedImage create(ParameterBlock paramBlock, RenderingHints renderHints) {
        ImageLayout layout = RIFUtil.getImageLayoutHint(renderHints);
        RenderedImage source = paramBlock.getRenderedSource(0);
        ColorCube colorMap = (ColorCube)paramBlock.getObjectParameter(0);
        KernelJAI[] ditherMask = (KernelJAI[])paramBlock.getObjectParameter(1);
        return new OrderedDitherOpImage(source, renderHints, layout, colorMap, ditherMask);
    }
}

