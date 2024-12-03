/*
 * Decompiled with CFR 0.152.
 */
package com.sun.media.jai.opimage;

import com.sun.media.jai.opimage.MaxFilterPlusOpImage;
import com.sun.media.jai.opimage.MaxFilterSeparableOpImage;
import com.sun.media.jai.opimage.MaxFilterSquareOpImage;
import com.sun.media.jai.opimage.MaxFilterXOpImage;
import com.sun.media.jai.opimage.RIFUtil;
import java.awt.RenderingHints;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.ParameterBlock;
import java.awt.image.renderable.RenderedImageFactory;
import javax.media.jai.BorderExtender;
import javax.media.jai.ImageLayout;
import javax.media.jai.operator.MaxFilterDescriptor;
import javax.media.jai.operator.MaxFilterShape;

public class MaxFilterRIF
implements RenderedImageFactory {
    public RenderedImage create(ParameterBlock paramBlock, RenderingHints renderHints) {
        ImageLayout layout = RIFUtil.getImageLayoutHint(renderHints);
        BorderExtender extender = RIFUtil.getBorderExtenderHint(renderHints);
        MaxFilterShape maskType = (MaxFilterShape)paramBlock.getObjectParameter(0);
        int maskSize = paramBlock.getIntParameter(1);
        RenderedImage ri = paramBlock.getRenderedSource(0);
        if (maskType.equals(MaxFilterDescriptor.MAX_MASK_SQUARE)) {
            return new MaxFilterSquareOpImage(ri, extender, renderHints, layout, maskSize);
        }
        if (maskType.equals(MaxFilterDescriptor.MAX_MASK_PLUS)) {
            return new MaxFilterPlusOpImage(ri, extender, renderHints, layout, maskSize);
        }
        if (maskType.equals(MaxFilterDescriptor.MAX_MASK_X)) {
            return new MaxFilterXOpImage(ri, extender, renderHints, layout, maskSize);
        }
        if (maskType.equals(MaxFilterDescriptor.MAX_MASK_SQUARE_SEPARABLE)) {
            return new MaxFilterSeparableOpImage(ri, extender, renderHints, layout, maskSize);
        }
        return null;
    }
}

