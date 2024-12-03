/*
 * Decompiled with CFR 0.152.
 */
package com.sun.media.jai.mlib;

import com.sun.media.jai.mlib.MediaLibAccessor;
import com.sun.media.jai.mlib.MlibBandSelectOpImage;
import com.sun.media.jai.opimage.RIFUtil;
import java.awt.RenderingHints;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.ParameterBlock;
import java.awt.image.renderable.RenderedImageFactory;
import javax.media.jai.ImageLayout;

public class MlibBandSelectRIF
implements RenderedImageFactory {
    public RenderedImage create(ParameterBlock args, RenderingHints hints) {
        ImageLayout layout = RIFUtil.getImageLayoutHint(hints);
        if (!MediaLibAccessor.isMediaLibCompatible(args, layout)) {
            return null;
        }
        int[] bandIndices = (int[])args.getObjectParameter(0);
        for (int i = 1; i < bandIndices.length; ++i) {
            if (bandIndices[i] > bandIndices[i - 1]) continue;
            return null;
        }
        return new MlibBandSelectOpImage(args.getRenderedSource(0), hints, layout, bandIndices);
    }
}

