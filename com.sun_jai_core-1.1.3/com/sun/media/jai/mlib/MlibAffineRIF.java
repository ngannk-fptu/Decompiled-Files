/*
 * Decompiled with CFR 0.152.
 */
package com.sun.media.jai.mlib;

import com.sun.media.jai.mlib.MediaLibAccessor;
import com.sun.media.jai.mlib.MlibAffineBicubicOpImage;
import com.sun.media.jai.mlib.MlibAffineBilinearOpImage;
import com.sun.media.jai.mlib.MlibAffineNearestOpImage;
import com.sun.media.jai.mlib.MlibAffineTableOpImage;
import com.sun.media.jai.mlib.MlibCopyOpImage;
import com.sun.media.jai.mlib.MlibScaleBicubicOpImage;
import com.sun.media.jai.mlib.MlibScaleBilinearOpImage;
import com.sun.media.jai.mlib.MlibScaleNearestOpImage;
import com.sun.media.jai.mlib.MlibScaleTableOpImage;
import com.sun.media.jai.opimage.RIFUtil;
import com.sun.media.jai.opimage.TranslateIntOpImage;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.image.MultiPixelPackedSampleModel;
import java.awt.image.RenderedImage;
import java.awt.image.SampleModel;
import java.awt.image.renderable.ParameterBlock;
import java.awt.image.renderable.RenderedImageFactory;
import java.util.Map;
import javax.media.jai.BorderExtender;
import javax.media.jai.ImageLayout;
import javax.media.jai.Interpolation;
import javax.media.jai.InterpolationBicubic;
import javax.media.jai.InterpolationBicubic2;
import javax.media.jai.InterpolationBilinear;
import javax.media.jai.InterpolationNearest;
import javax.media.jai.InterpolationTable;

public class MlibAffineRIF
implements RenderedImageFactory {
    private static final float TOLERANCE = 0.01f;

    public RenderedImage create(ParameterBlock args, RenderingHints hints) {
        boolean isBilevel;
        ImageLayout layout = RIFUtil.getImageLayoutHint(hints);
        AffineTransform transform = (AffineTransform)args.getObjectParameter(0);
        Interpolation interp = (Interpolation)args.getObjectParameter(1);
        double[] backgroundValues = (double[])args.getObjectParameter(2);
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
        double[] tr = new double[6];
        transform.getMatrix(tr);
        if (tr[0] == 1.0 && tr[3] == 1.0 && tr[2] == 0.0 && tr[1] == 0.0 && tr[4] == 0.0 && tr[5] == 0.0) {
            return new MlibCopyOpImage(source, hints, layout);
        }
        if (tr[0] == 1.0 && tr[3] == 1.0 && tr[2] == 0.0 && tr[1] == 0.0 && Math.abs(tr[4] - (double)((int)tr[4])) < (double)0.01f && Math.abs(tr[5] - (double)((int)tr[5])) < (double)0.01f && layout == null) {
            return new TranslateIntOpImage(source, hints, (int)tr[4], (int)tr[5]);
        }
        if (tr[0] > 0.0 && tr[2] == 0.0 && tr[1] == 0.0 && tr[3] > 0.0) {
            if (interp instanceof InterpolationNearest) {
                return new MlibScaleNearestOpImage(source, extender, hints, layout, (float)tr[0], (float)tr[3], (float)tr[4], (float)tr[5], interp);
            }
            if (interp instanceof InterpolationBilinear) {
                return new MlibScaleBilinearOpImage(source, extender, hints, layout, (float)tr[0], (float)tr[3], (float)tr[4], (float)tr[5], interp);
            }
            if (interp instanceof InterpolationBicubic || interp instanceof InterpolationBicubic2) {
                return new MlibScaleBicubicOpImage(source, extender, hints, layout, (float)tr[0], (float)tr[3], (float)tr[4], (float)tr[5], interp);
            }
            if (interp instanceof InterpolationTable) {
                return new MlibScaleTableOpImage(source, extender, hints, layout, (float)tr[0], (float)tr[3], (float)tr[4], (float)tr[5], interp);
            }
            return null;
        }
        if (interp instanceof InterpolationNearest) {
            return new MlibAffineNearestOpImage(source, extender, (Map)hints, layout, transform, interp, backgroundValues);
        }
        if (interp instanceof InterpolationBilinear) {
            return new MlibAffineBilinearOpImage(source, extender, (Map)hints, layout, transform, interp, backgroundValues);
        }
        if (interp instanceof InterpolationBicubic || interp instanceof InterpolationBicubic2) {
            return new MlibAffineBicubicOpImage(source, extender, (Map)hints, layout, transform, interp, backgroundValues);
        }
        if (interp instanceof InterpolationTable) {
            return new MlibAffineTableOpImage(source, extender, (Map)hints, layout, transform, interp, backgroundValues);
        }
        return null;
    }
}

