/*
 * Decompiled with CFR 0.152.
 */
package com.sun.media.jai.mlib;

import com.sun.media.jai.mlib.MediaLibAccessor;
import com.sun.media.jai.mlib.MlibMedianFilterOpImage;
import com.sun.media.jai.opimage.RIFUtil;
import java.awt.RenderingHints;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.ParameterBlock;
import java.awt.image.renderable.RenderedImageFactory;
import javax.media.jai.BorderExtender;
import javax.media.jai.ImageLayout;
import javax.media.jai.operator.MedianFilterShape;

public class MlibMedianFilterRIF
implements RenderedImageFactory {
    public RenderedImage create(ParameterBlock paramBlock, RenderingHints renderHints) {
        ImageLayout layout = RIFUtil.getImageLayoutHint(renderHints);
        if (!MediaLibAccessor.isMediaLibCompatible(paramBlock, layout) || !MediaLibAccessor.hasSameNumBands(paramBlock, layout)) {
            return null;
        }
        BorderExtender extender = RIFUtil.getBorderExtenderHint(renderHints);
        MedianFilterShape maskType = (MedianFilterShape)paramBlock.getObjectParameter(0);
        int maskSize = paramBlock.getIntParameter(1);
        RenderedImage ri = paramBlock.getRenderedSource(0);
        return new MlibMedianFilterOpImage(ri, extender, renderHints, layout, maskType, maskSize);
    }
}

