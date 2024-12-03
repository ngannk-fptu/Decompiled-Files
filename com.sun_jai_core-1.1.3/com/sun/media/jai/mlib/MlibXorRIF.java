/*
 * Decompiled with CFR 0.152.
 */
package com.sun.media.jai.mlib;

import com.sun.media.jai.mlib.MediaLibAccessor;
import com.sun.media.jai.mlib.MlibXorOpImage;
import com.sun.media.jai.opimage.RIFUtil;
import java.awt.RenderingHints;
import java.awt.image.RenderedImage;
import java.awt.image.SampleModel;
import java.awt.image.renderable.ParameterBlock;
import java.awt.image.renderable.RenderedImageFactory;
import java.util.Map;
import javax.media.jai.ImageLayout;

public class MlibXorRIF
implements RenderedImageFactory {
    public RenderedImage create(ParameterBlock args, RenderingHints hints) {
        int dtype;
        SampleModel sm;
        ImageLayout layout = RIFUtil.getImageLayoutHint(hints);
        if (!MediaLibAccessor.isMediaLibCompatible(args, layout) || !MediaLibAccessor.hasSameNumBands(args, layout)) {
            return null;
        }
        if (layout != null && (sm = layout.getSampleModel(null)) != null && ((dtype = sm.getDataType()) == 4 || dtype == 5)) {
            return null;
        }
        return new MlibXorOpImage(args.getRenderedSource(0), args.getRenderedSource(1), (Map)hints, layout);
    }
}

