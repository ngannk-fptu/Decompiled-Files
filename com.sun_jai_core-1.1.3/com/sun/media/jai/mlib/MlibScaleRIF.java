/*
 * Decompiled with CFR 0.152.
 */
package com.sun.media.jai.mlib;

import com.sun.media.jai.mlib.MediaLibAccessor;
import com.sun.media.jai.mlib.MlibCopyOpImage;
import com.sun.media.jai.mlib.MlibScaleBicubicOpImage;
import com.sun.media.jai.mlib.MlibScaleBilinearOpImage;
import com.sun.media.jai.mlib.MlibScaleNearestOpImage;
import com.sun.media.jai.mlib.MlibScaleTableOpImage;
import com.sun.media.jai.opimage.RIFUtil;
import com.sun.media.jai.opimage.TranslateIntOpImage;
import java.awt.RenderingHints;
import java.awt.image.MultiPixelPackedSampleModel;
import java.awt.image.RenderedImage;
import java.awt.image.SampleModel;
import java.awt.image.renderable.ParameterBlock;
import java.awt.image.renderable.RenderedImageFactory;
import javax.media.jai.BorderExtender;
import javax.media.jai.ImageLayout;
import javax.media.jai.Interpolation;
import javax.media.jai.InterpolationBicubic;
import javax.media.jai.InterpolationBicubic2;
import javax.media.jai.InterpolationBilinear;
import javax.media.jai.InterpolationNearest;
import javax.media.jai.InterpolationTable;

public class MlibScaleRIF
implements RenderedImageFactory {
    private static final float TOLERANCE = 0.01f;

    public RenderedImage create(ParameterBlock args, RenderingHints hints) {
        boolean isBilevel;
        ImageLayout layout = RIFUtil.getImageLayoutHint(hints);
        Interpolation interp = (Interpolation)args.getObjectParameter(4);
        RenderedImage source = args.getRenderedSource(0);
        if (!MediaLibAccessor.isMediaLibCompatible(args, layout) || !MediaLibAccessor.hasSameNumBands(args, layout) || source.getTileWidth() >= 32768 || source.getTileHeight() >= 32768) {
            return null;
        }
        SampleModel sm = source.getSampleModel();
        boolean bl = isBilevel = sm instanceof MultiPixelPackedSampleModel && sm.getSampleSize(0) == 1 && (sm.getDataType() == 0 || sm.getDataType() == 1 || sm.getDataType() == 3);
        if (isBilevel) {
            return null;
        }
        BorderExtender extender = RIFUtil.getBorderExtenderHint(hints);
        float xScale = args.getFloatParameter(0);
        float yScale = args.getFloatParameter(1);
        float xTrans = args.getFloatParameter(2);
        float yTrans = args.getFloatParameter(3);
        if (xScale == 1.0f && yScale == 1.0f && xTrans == 0.0f && yTrans == 0.0f) {
            return new MlibCopyOpImage(source, hints, layout);
        }
        if (xScale == 1.0f && yScale == 1.0f && Math.abs(xTrans - (float)((int)xTrans)) < 0.01f && Math.abs(yTrans - (float)((int)yTrans)) < 0.01f && layout == null) {
            return new TranslateIntOpImage(source, hints, (int)xTrans, (int)yTrans);
        }
        if (interp instanceof InterpolationNearest) {
            return new MlibScaleNearestOpImage(source, extender, hints, layout, xScale, yScale, xTrans, yTrans, interp);
        }
        if (interp instanceof InterpolationBilinear) {
            return new MlibScaleBilinearOpImage(source, extender, hints, layout, xScale, yScale, xTrans, yTrans, interp);
        }
        if (interp instanceof InterpolationBicubic || interp instanceof InterpolationBicubic2) {
            return new MlibScaleBicubicOpImage(source, extender, hints, layout, xScale, yScale, xTrans, yTrans, interp);
        }
        if (interp instanceof InterpolationTable) {
            return new MlibScaleTableOpImage(source, extender, hints, layout, xScale, yScale, xTrans, yTrans, interp);
        }
        return null;
    }
}

