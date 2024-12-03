/*
 * Decompiled with CFR 0.152.
 */
package com.sun.media.jai.opimage;

import com.sun.media.jai.opimage.MedianFilterPlusOpImage;
import com.sun.media.jai.opimage.MedianFilterSeparableOpImage;
import com.sun.media.jai.opimage.MedianFilterSquareOpImage;
import com.sun.media.jai.opimage.MedianFilterXOpImage;
import com.sun.media.jai.opimage.RIFUtil;
import java.awt.RenderingHints;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.ParameterBlock;
import java.awt.image.renderable.RenderedImageFactory;
import javax.media.jai.BorderExtender;
import javax.media.jai.ImageLayout;
import javax.media.jai.operator.MedianFilterDescriptor;
import javax.media.jai.operator.MedianFilterShape;

public class MedianFilterRIF
implements RenderedImageFactory {
    public RenderedImage create(ParameterBlock paramBlock, RenderingHints renderHints) {
        ImageLayout layout = RIFUtil.getImageLayoutHint(renderHints);
        BorderExtender extender = RIFUtil.getBorderExtenderHint(renderHints);
        MedianFilterShape maskType = (MedianFilterShape)paramBlock.getObjectParameter(0);
        int maskSize = paramBlock.getIntParameter(1);
        RenderedImage ri = paramBlock.getRenderedSource(0);
        if (maskType.equals(MedianFilterDescriptor.MEDIAN_MASK_SQUARE)) {
            return new MedianFilterSquareOpImage(ri, extender, renderHints, layout, maskSize);
        }
        if (maskType.equals(MedianFilterDescriptor.MEDIAN_MASK_PLUS)) {
            return new MedianFilterPlusOpImage(ri, extender, renderHints, layout, maskSize);
        }
        if (maskType.equals(MedianFilterDescriptor.MEDIAN_MASK_X)) {
            return new MedianFilterXOpImage(ri, extender, renderHints, layout, maskSize);
        }
        if (maskType.equals(MedianFilterDescriptor.MEDIAN_MASK_SQUARE_SEPARABLE)) {
            return new MedianFilterSeparableOpImage(ri, extender, renderHints, layout, maskSize);
        }
        return null;
    }
}

