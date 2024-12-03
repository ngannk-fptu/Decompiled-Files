/*
 * Decompiled with CFR 0.152.
 */
package com.sun.media.jai.mlib;

import com.sun.media.jai.mlib.MediaLibAccessor;
import com.sun.media.jai.mlib.MlibBandCombineOpImage;
import com.sun.media.jai.opimage.RIFUtil;
import java.awt.RenderingHints;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.ParameterBlock;
import java.awt.image.renderable.RenderedImageFactory;
import javax.media.jai.ImageLayout;

public class MlibBandCombineRIF
implements RenderedImageFactory {
    public RenderedImage create(ParameterBlock args, RenderingHints hints) {
        ImageLayout layout = RIFUtil.getImageLayoutHint(hints);
        if (!MediaLibAccessor.isMediaLibCompatible(args, layout)) {
            return null;
        }
        double[][] matrix = (double[][])args.getObjectParameter(0);
        if (matrix.length != 3) {
            return null;
        }
        for (int i = 0; i < 3; ++i) {
            if (matrix[i].length == 4) continue;
            return null;
        }
        return new MlibBandCombineOpImage(args.getRenderedSource(0), hints, layout, matrix);
    }
}

