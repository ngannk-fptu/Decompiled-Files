/*
 * Decompiled with CFR 0.152.
 */
package com.sun.media.jai.mlib;

import com.sun.media.jai.mlib.MediaLibAccessor;
import com.sun.media.jai.mlib.MlibOrderedDitherOpImage;
import com.sun.media.jai.opimage.RIFUtil;
import com.sun.media.jai.util.ImageUtil;
import java.awt.RenderingHints;
import java.awt.image.RenderedImage;
import java.awt.image.SampleModel;
import java.awt.image.renderable.ParameterBlock;
import java.awt.image.renderable.RenderedImageFactory;
import javax.media.jai.ColorCube;
import javax.media.jai.ImageLayout;
import javax.media.jai.KernelJAI;

public class MlibOrderedDitherRIF
implements RenderedImageFactory {
    public RenderedImage create(ParameterBlock args, RenderingHints hints) {
        RenderedImage source = args.getRenderedSource(0);
        ColorCube colorMap = (ColorCube)args.getObjectParameter(0);
        KernelJAI[] ditherMask = (KernelJAI[])args.getObjectParameter(1);
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
        ImageLayout layout = MlibOrderedDitherOpImage.layoutHelper(layoutHint, source, colorMap);
        SampleModel destSM = layout.getSampleModel(null);
        if (!MediaLibAccessor.isMediaLibCompatible(args) || !MediaLibAccessor.isMediaLibCompatible(destSM, null) && !ImageUtil.isBinary(destSM)) {
            return null;
        }
        return new MlibOrderedDitherOpImage(source, hints, layout, colorMap, ditherMask);
    }
}

