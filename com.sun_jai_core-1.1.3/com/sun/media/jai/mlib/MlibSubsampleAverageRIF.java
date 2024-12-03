/*
 * Decompiled with CFR 0.152.
 */
package com.sun.media.jai.mlib;

import com.sun.media.jai.mlib.MediaLibAccessor;
import com.sun.media.jai.mlib.MlibSubsampleAverageOpImage;
import com.sun.media.jai.opimage.RIFUtil;
import java.awt.RenderingHints;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.ParameterBlock;
import java.awt.image.renderable.RenderedImageFactory;
import javax.media.jai.ImageLayout;

public class MlibSubsampleAverageRIF
implements RenderedImageFactory {
    public RenderedImage create(ParameterBlock args, RenderingHints hints) {
        double scaleX = args.getDoubleParameter(0);
        double scaleY = args.getDoubleParameter(1);
        if (scaleX == 1.0 && scaleY == 1.0) {
            return args.getRenderedSource(0);
        }
        ImageLayout layout = RIFUtil.getImageLayoutHint(hints);
        if (!MediaLibAccessor.isMediaLibCompatible(args, layout) || !MediaLibAccessor.hasSameNumBands(args, layout)) {
            return null;
        }
        return new MlibSubsampleAverageOpImage(args.getRenderedSource(0), layout, hints, scaleX, scaleY);
    }
}

