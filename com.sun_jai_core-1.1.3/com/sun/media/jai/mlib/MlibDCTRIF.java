/*
 * Decompiled with CFR 0.152.
 */
package com.sun.media.jai.mlib;

import com.sun.media.jai.mlib.FCTmediaLib;
import com.sun.media.jai.mlib.MediaLibAccessor;
import com.sun.media.jai.opimage.DCTOpImage;
import com.sun.media.jai.opimage.RIFUtil;
import java.awt.RenderingHints;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.ParameterBlock;
import java.awt.image.renderable.RenderedImageFactory;
import javax.media.jai.ImageLayout;

public class MlibDCTRIF
implements RenderedImageFactory {
    public RenderedImage create(ParameterBlock args, RenderingHints hints) {
        ImageLayout layout = RIFUtil.getImageLayoutHint(hints);
        if (!MediaLibAccessor.isMediaLibCompatible(new ParameterBlock())) {
            return null;
        }
        return new DCTOpImage(args.getRenderedSource(0), hints, layout, new FCTmediaLib(true, 2));
    }
}

