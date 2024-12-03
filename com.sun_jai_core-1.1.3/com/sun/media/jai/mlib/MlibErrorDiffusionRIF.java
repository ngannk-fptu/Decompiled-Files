/*
 * Decompiled with CFR 0.152.
 */
package com.sun.media.jai.mlib;

import com.sun.media.jai.mlib.MediaLibAccessor;
import com.sun.media.jai.mlib.MlibErrorDiffusionOpImage;
import com.sun.media.jai.opimage.RIFUtil;
import com.sun.media.jai.util.ImageUtil;
import java.awt.RenderingHints;
import java.awt.image.RenderedImage;
import java.awt.image.SampleModel;
import java.awt.image.renderable.ParameterBlock;
import java.awt.image.renderable.RenderedImageFactory;
import javax.media.jai.ImageLayout;
import javax.media.jai.KernelJAI;
import javax.media.jai.LookupTableJAI;

public class MlibErrorDiffusionRIF
implements RenderedImageFactory {
    public RenderedImage create(ParameterBlock args, RenderingHints hints) {
        RenderedImage source = args.getRenderedSource(0);
        LookupTableJAI colorMap = (LookupTableJAI)args.getObjectParameter(0);
        KernelJAI errorKernel = (KernelJAI)args.getObjectParameter(1);
        if (colorMap.getNumBands() != 1 && colorMap.getNumBands() != 3) {
            return null;
        }
        if (colorMap.getDataType() != 0) {
            return null;
        }
        SampleModel sourceSM = source.getSampleModel();
        if (sourceSM.getDataType() != 0) {
            return null;
        }
        if (sourceSM.getNumBands() != colorMap.getNumBands()) {
            return null;
        }
        ImageLayout layoutHint = RIFUtil.getImageLayoutHint(hints);
        ImageLayout layout = MlibErrorDiffusionOpImage.layoutHelper(layoutHint, source, colorMap);
        SampleModel destSM = layout.getSampleModel(null);
        if (!MediaLibAccessor.isMediaLibCompatible(args) || !MediaLibAccessor.isMediaLibCompatible(destSM, null) && !ImageUtil.isBinary(destSM)) {
            return null;
        }
        return new MlibErrorDiffusionOpImage(source, hints, layout, colorMap, errorKernel);
    }
}

