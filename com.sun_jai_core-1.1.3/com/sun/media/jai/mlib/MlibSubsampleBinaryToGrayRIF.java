/*
 * Decompiled with CFR 0.152.
 */
package com.sun.media.jai.mlib;

import com.sun.media.jai.mlib.MediaLibAccessor;
import com.sun.media.jai.mlib.MlibSubsampleBinaryToGrayOpImage;
import com.sun.media.jai.opimage.CopyOpImage;
import com.sun.media.jai.opimage.RIFUtil;
import java.awt.RenderingHints;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.ParameterBlock;
import java.awt.image.renderable.RenderedImageFactory;
import javax.media.jai.ImageLayout;

public class MlibSubsampleBinaryToGrayRIF
implements RenderedImageFactory {
    private int blockX;
    private int blockY;

    public RenderedImage create(ParameterBlock args, RenderingHints hints) {
        RenderedImage source = args.getRenderedSource(0);
        if (!MediaLibAccessor.isMediaLibBinaryCompatible(args, null)) {
            return null;
        }
        ImageLayout layout = RIFUtil.getImageLayoutHint(hints);
        if (layout != null && layout.isValid(256) && !MediaLibAccessor.isMediaLibCompatible(layout.getSampleModel(null), layout.getColorModel(null)) || !MediaLibAccessor.hasSameNumBands(args, layout)) {
            return null;
        }
        float xScale = args.getFloatParameter(0);
        float yScale = args.getFloatParameter(1);
        if (xScale == 1.0f && yScale == 1.0f) {
            return new CopyOpImage(source, hints, layout);
        }
        return new MlibSubsampleBinaryToGrayOpImage(source, layout, hints, xScale, yScale);
    }
}

