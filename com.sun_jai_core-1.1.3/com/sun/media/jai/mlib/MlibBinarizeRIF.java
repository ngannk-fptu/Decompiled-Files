/*
 * Decompiled with CFR 0.152.
 */
package com.sun.media.jai.mlib;

import com.sun.media.jai.mlib.MediaLibAccessor;
import com.sun.media.jai.mlib.MlibBinarizeOpImage;
import com.sun.media.jai.opimage.RIFUtil;
import java.awt.RenderingHints;
import java.awt.image.RenderedImage;
import java.awt.image.SampleModel;
import java.awt.image.renderable.ParameterBlock;
import java.awt.image.renderable.RenderedImageFactory;
import java.util.Map;
import javax.media.jai.ImageLayout;

public class MlibBinarizeRIF
implements RenderedImageFactory {
    public RenderedImage create(ParameterBlock args, RenderingHints hints) {
        RenderedImage source = args.getRenderedSource(0);
        SampleModel sm = source.getSampleModel();
        if (!MediaLibAccessor.isMediaLibCompatible(args) || sm.getNumBands() > 1) {
            return null;
        }
        double thresh = args.getDoubleParameter(0);
        if ((thresh > 255.0 || thresh <= 0.0) && sm.getDataType() == 0 || (thresh > 32767.0 || thresh <= 0.0) && sm.getDataType() == 2 || (thresh > 2.147483647E9 || thresh <= 0.0) && sm.getDataType() == 3) {
            return null;
        }
        ImageLayout layout = RIFUtil.getImageLayoutHint(hints);
        return new MlibBinarizeOpImage(source, layout, (Map)hints, thresh);
    }
}

