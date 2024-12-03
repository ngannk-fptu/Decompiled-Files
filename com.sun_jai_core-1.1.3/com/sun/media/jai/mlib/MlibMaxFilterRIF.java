/*
 * Decompiled with CFR 0.152.
 */
package com.sun.media.jai.mlib;

import com.sun.media.jai.mlib.MediaLibAccessor;
import com.sun.media.jai.mlib.MlibMaxFilterOpImage;
import com.sun.media.jai.opimage.RIFUtil;
import java.awt.RenderingHints;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.ParameterBlock;
import java.awt.image.renderable.RenderedImageFactory;
import javax.media.jai.BorderExtender;
import javax.media.jai.ImageLayout;
import javax.media.jai.operator.MaxFilterDescriptor;
import javax.media.jai.operator.MaxFilterShape;

public class MlibMaxFilterRIF
implements RenderedImageFactory {
    public RenderedImage create(ParameterBlock paramBlock, RenderingHints renderHints) {
        ImageLayout layout = RIFUtil.getImageLayoutHint(renderHints);
        if (!MediaLibAccessor.isMediaLibCompatible(paramBlock, layout) || !MediaLibAccessor.hasSameNumBands(paramBlock, layout)) {
            return null;
        }
        BorderExtender extender = RIFUtil.getBorderExtenderHint(renderHints);
        MaxFilterShape maskType = (MaxFilterShape)paramBlock.getObjectParameter(0);
        int maskSize = paramBlock.getIntParameter(1);
        RenderedImage ri = paramBlock.getRenderedSource(0);
        if (maskType.equals(MaxFilterDescriptor.MAX_MASK_SQUARE) && (maskSize == 3 || maskSize == 5 || maskSize == 7) && ri.getSampleModel().getNumBands() == 1) {
            return new MlibMaxFilterOpImage(ri, extender, renderHints, layout, maskType, maskSize);
        }
        return null;
    }
}

