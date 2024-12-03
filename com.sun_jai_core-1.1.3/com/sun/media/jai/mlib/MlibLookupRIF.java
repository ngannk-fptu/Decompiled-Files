/*
 * Decompiled with CFR 0.152.
 */
package com.sun.media.jai.mlib;

import com.sun.media.jai.mlib.MediaLibAccessor;
import com.sun.media.jai.mlib.MlibLookupOpImage;
import com.sun.media.jai.opimage.RIFUtil;
import java.awt.RenderingHints;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.ParameterBlock;
import java.awt.image.renderable.RenderedImageFactory;
import javax.media.jai.ImageLayout;
import javax.media.jai.LookupTableJAI;

public class MlibLookupRIF
implements RenderedImageFactory {
    public RenderedImage create(ParameterBlock args, RenderingHints hints) {
        ImageLayout layout = RIFUtil.getImageLayoutHint(hints);
        if (!MediaLibAccessor.isMediaLibCompatible(args)) {
            return null;
        }
        LookupTableJAI table = (LookupTableJAI)args.getObjectParameter(0);
        if (table.getNumBands() > 4 || table.getDataType() == 1) {
            return null;
        }
        return new MlibLookupOpImage(args.getRenderedSource(0), hints, layout, table);
    }
}

