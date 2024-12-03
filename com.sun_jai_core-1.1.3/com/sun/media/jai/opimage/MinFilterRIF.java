/*
 * Decompiled with CFR 0.152.
 */
package com.sun.media.jai.opimage;

import com.sun.media.jai.opimage.MinFilterPlusOpImage;
import com.sun.media.jai.opimage.MinFilterSeparableOpImage;
import com.sun.media.jai.opimage.MinFilterSquareOpImage;
import com.sun.media.jai.opimage.MinFilterXOpImage;
import com.sun.media.jai.opimage.RIFUtil;
import java.awt.RenderingHints;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.ParameterBlock;
import java.awt.image.renderable.RenderedImageFactory;
import javax.media.jai.BorderExtender;
import javax.media.jai.ImageLayout;
import javax.media.jai.operator.MinFilterDescriptor;
import javax.media.jai.operator.MinFilterShape;

public class MinFilterRIF
implements RenderedImageFactory {
    public RenderedImage create(ParameterBlock paramBlock, RenderingHints renderHints) {
        ImageLayout layout = RIFUtil.getImageLayoutHint(renderHints);
        BorderExtender extender = RIFUtil.getBorderExtenderHint(renderHints);
        MinFilterShape maskType = (MinFilterShape)paramBlock.getObjectParameter(0);
        int maskSize = paramBlock.getIntParameter(1);
        RenderedImage ri = paramBlock.getRenderedSource(0);
        if (maskType.equals(MinFilterDescriptor.MIN_MASK_SQUARE)) {
            return new MinFilterSquareOpImage(ri, extender, renderHints, layout, maskSize);
        }
        if (maskType.equals(MinFilterDescriptor.MIN_MASK_PLUS)) {
            return new MinFilterPlusOpImage(ri, extender, renderHints, layout, maskSize);
        }
        if (maskType.equals(MinFilterDescriptor.MIN_MASK_X)) {
            return new MinFilterXOpImage(ri, extender, renderHints, layout, maskSize);
        }
        if (maskType.equals(MinFilterDescriptor.MIN_MASK_SQUARE_SEPARABLE)) {
            return new MinFilterSeparableOpImage(ri, extender, renderHints, layout, maskSize);
        }
        return null;
    }
}

