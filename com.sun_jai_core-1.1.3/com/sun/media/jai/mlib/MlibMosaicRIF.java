/*
 * Decompiled with CFR 0.152.
 */
package com.sun.media.jai.mlib;

import com.sun.media.jai.mlib.MediaLibAccessor;
import com.sun.media.jai.mlib.MlibMosaicOpImage;
import com.sun.media.jai.opimage.RIFUtil;
import java.awt.RenderingHints;
import java.awt.image.RenderedImage;
import java.awt.image.SampleModel;
import java.awt.image.renderable.ParameterBlock;
import java.awt.image.renderable.RenderedImageFactory;
import java.util.Vector;
import javax.media.jai.ImageLayout;
import javax.media.jai.PlanarImage;
import javax.media.jai.ROI;
import javax.media.jai.operator.MosaicType;

public class MlibMosaicRIF
implements RenderedImageFactory {
    public RenderedImage create(ParameterBlock paramBlock, RenderingHints renderHints) {
        int dataType;
        ImageLayout layout = RIFUtil.getImageLayoutHint(renderHints);
        if (!MediaLibAccessor.isMediaLibCompatible(paramBlock, layout) || !MediaLibAccessor.hasSameNumBands(paramBlock, layout)) {
            return null;
        }
        Vector<Object> sources = paramBlock.getSources();
        SampleModel targetSM = null;
        if (sources.size() > 0) {
            targetSM = ((RenderedImage)sources.get(0)).getSampleModel();
        } else if (layout != null && layout.isValid(256)) {
            targetSM = layout.getSampleModel(null);
        }
        if (targetSM != null && ((dataType = targetSM.getDataType()) == 4 || dataType == 5)) {
            return null;
        }
        return new MlibMosaicOpImage(sources, layout, renderHints, (MosaicType)paramBlock.getObjectParameter(0), (PlanarImage[])paramBlock.getObjectParameter(1), (ROI[])paramBlock.getObjectParameter(2), (double[][])paramBlock.getObjectParameter(3), (double[])paramBlock.getObjectParameter(4));
    }
}

