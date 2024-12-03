/*
 * Decompiled with CFR 0.152.
 */
package com.sun.media.jai.mlib;

import com.sun.media.jai.mlib.MediaLibAccessor;
import com.sun.media.jai.mlib.MlibAffineBicubicOpImage;
import com.sun.media.jai.mlib.MlibAffineBilinearOpImage;
import com.sun.media.jai.mlib.MlibAffineNearestOpImage;
import com.sun.media.jai.mlib.MlibAffineTableOpImage;
import com.sun.media.jai.opimage.RIFUtil;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.ParameterBlock;
import java.awt.image.renderable.RenderedImageFactory;
import java.util.Map;
import javax.media.jai.BorderExtender;
import javax.media.jai.EnumeratedParameter;
import javax.media.jai.ImageLayout;
import javax.media.jai.Interpolation;
import javax.media.jai.InterpolationBicubic;
import javax.media.jai.InterpolationBicubic2;
import javax.media.jai.InterpolationBilinear;
import javax.media.jai.InterpolationNearest;
import javax.media.jai.InterpolationTable;
import javax.media.jai.operator.ShearDescriptor;

public class MlibShearRIF
implements RenderedImageFactory {
    public RenderedImage create(ParameterBlock args, RenderingHints hints) {
        ImageLayout layout = RIFUtil.getImageLayoutHint(hints);
        Interpolation interp = (Interpolation)args.getObjectParameter(4);
        RenderedImage source = args.getRenderedSource(0);
        if (!MediaLibAccessor.isMediaLibCompatible(args, layout) || !MediaLibAccessor.hasSameNumBands(args, layout) || source.getTileWidth() >= 32768 || source.getTileHeight() >= 32768) {
            return null;
        }
        BorderExtender extender = RIFUtil.getBorderExtenderHint(hints);
        float shear_amt = args.getFloatParameter(0);
        EnumeratedParameter shear_dir = (EnumeratedParameter)args.getObjectParameter(1);
        float xTrans = args.getFloatParameter(2);
        float yTrans = args.getFloatParameter(3);
        double[] backgroundValues = (double[])args.getObjectParameter(5);
        AffineTransform tr = new AffineTransform();
        if (shear_dir.equals(ShearDescriptor.SHEAR_HORIZONTAL)) {
            tr.setTransform(1.0, 0.0, shear_amt, 1.0, xTrans, 0.0);
        } else {
            tr.setTransform(1.0, shear_amt, 0.0, 1.0, 0.0, yTrans);
        }
        if (interp instanceof InterpolationNearest) {
            return new MlibAffineNearestOpImage(source, extender, (Map)hints, layout, tr, interp, backgroundValues);
        }
        if (interp instanceof InterpolationBilinear) {
            return new MlibAffineBilinearOpImage(source, extender, (Map)hints, layout, tr, interp, backgroundValues);
        }
        if (interp instanceof InterpolationBicubic || interp instanceof InterpolationBicubic2) {
            return new MlibAffineBicubicOpImage(source, extender, (Map)hints, layout, tr, interp, backgroundValues);
        }
        if (interp instanceof InterpolationTable) {
            return new MlibAffineTableOpImage(source, extender, (Map)hints, layout, tr, interp, backgroundValues);
        }
        return null;
    }
}

