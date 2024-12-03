/*
 * Decompiled with CFR 0.152.
 */
package com.sun.media.jai.mlib;

import com.sun.media.jai.mlib.MediaLibAccessor;
import com.sun.media.jai.mlib.MlibTransposeOpImage;
import com.sun.media.jai.opimage.RIFUtil;
import java.awt.RenderingHints;
import java.awt.image.MultiPixelPackedSampleModel;
import java.awt.image.RenderedImage;
import java.awt.image.SampleModel;
import java.awt.image.renderable.ParameterBlock;
import java.awt.image.renderable.RenderedImageFactory;
import javax.media.jai.EnumeratedParameter;
import javax.media.jai.ImageLayout;

public class MlibTransposeRIF
implements RenderedImageFactory {
    public RenderedImage create(ParameterBlock args, RenderingHints hints) {
        boolean isBilevel;
        ImageLayout layout = RIFUtil.getImageLayoutHint(hints);
        if (!MediaLibAccessor.isMediaLibCompatible(args, layout) || !MediaLibAccessor.hasSameNumBands(args, layout)) {
            return null;
        }
        RenderedImage source = args.getRenderedSource(0);
        SampleModel sm = source.getSampleModel();
        boolean bl = isBilevel = sm instanceof MultiPixelPackedSampleModel && sm.getSampleSize(0) == 1 && (sm.getDataType() == 0 || sm.getDataType() == 1 || sm.getDataType() == 3);
        if (isBilevel) {
            return null;
        }
        EnumeratedParameter transposeType = (EnumeratedParameter)args.getObjectParameter(0);
        return new MlibTransposeOpImage(args.getRenderedSource(0), hints, layout, transposeType.getValue());
    }
}

