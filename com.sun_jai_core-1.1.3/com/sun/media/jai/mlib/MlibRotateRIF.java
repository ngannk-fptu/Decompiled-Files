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
import com.sun.media.jai.mlib.MlibTransposeOpImage;
import com.sun.media.jai.opimage.PointMapperOpImage;
import com.sun.media.jai.opimage.RIFUtil;
import com.sun.media.jai.opimage.TranslateIntOpImage;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.image.RenderedImage;
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
import javax.media.jai.JAI;
import javax.media.jai.PlanarImage;

public class MlibRotateRIF
implements RenderedImageFactory {
    public RenderedImage create(ParameterBlock args, RenderingHints hints) {
        ImageLayout layout = RIFUtil.getImageLayoutHint(hints);
        Interpolation interp = (Interpolation)args.getObjectParameter(3);
        double[] backgroundValues = (double[])args.getObjectParameter(4);
        RenderedImage source = args.getRenderedSource(0);
        if (!MediaLibAccessor.isMediaLibCompatible(args, layout) || !MediaLibAccessor.hasSameNumBands(args, layout) || source.getTileWidth() >= 32768 || source.getTileHeight() >= 32768) {
            return null;
        }
        BorderExtender extender = RIFUtil.getBorderExtenderHint(hints);
        float x_center = args.getFloatParameter(0);
        float y_center = args.getFloatParameter(1);
        float angle = args.getFloatParameter(2);
        double tmp_angle = (double)(180.0f * angle) / Math.PI;
        double rnd_angle = Math.round(tmp_angle);
        AffineTransform transform = AffineTransform.getRotateInstance(angle, x_center, y_center);
        if (Math.abs(rnd_angle - tmp_angle) < 1.0E-4) {
            int dangle = (int)rnd_angle % 360;
            if (dangle < 0) {
                dangle += 360;
            }
            if (dangle == 0) {
                return new MlibCopyOpImage(source, hints, layout);
            }
            int ix_center = Math.round(x_center);
            int iy_center = Math.round(y_center);
            if (dangle % 90 == 0 && (double)Math.abs(x_center - (float)ix_center) < 1.0E-4 && (double)Math.abs(y_center - (float)iy_center) < 1.0E-4) {
                int transType = -1;
                int rotMinX = 0;
                int rotMinY = 0;
                int sourceMinX = source.getMinX();
                int sourceMinY = source.getMinY();
                int sourceMaxX = sourceMinX + source.getWidth();
                int sourceMaxY = sourceMinY + source.getHeight();
                if (dangle == 90) {
                    transType = 4;
                    rotMinX = ix_center - (sourceMaxY - iy_center);
                    rotMinY = iy_center - (ix_center - sourceMinX);
                } else if (dangle == 180) {
                    transType = 5;
                    rotMinX = 2 * ix_center - sourceMaxX;
                    rotMinY = 2 * iy_center - sourceMaxY;
                } else {
                    transType = 6;
                    rotMinX = ix_center - (iy_center - sourceMinY);
                    rotMinY = iy_center - (sourceMaxX - ix_center);
                }
                MlibTransposeOpImage trans = new MlibTransposeOpImage(source, hints, layout, transType);
                int imMinX = trans.getMinX();
                int imMinY = trans.getMinY();
                if (layout == null) {
                    TranslateIntOpImage intermediateImage = new TranslateIntOpImage(trans, hints, rotMinX - imMinX, rotMinY - imMinY);
                    try {
                        return new PointMapperOpImage(intermediateImage, hints, transform);
                    }
                    catch (NoninvertibleTransformException nite) {
                        return intermediateImage;
                    }
                }
                ParameterBlock pbScale = new ParameterBlock();
                pbScale.addSource(trans);
                pbScale.add(0.0f);
                pbScale.add(0.0f);
                pbScale.add(rotMinX - imMinX);
                pbScale.add(rotMinY - imMinY);
                pbScale.add(interp);
                PlanarImage intermediateImage = JAI.create("scale", pbScale, hints).getRendering();
                try {
                    return new PointMapperOpImage(intermediateImage, hints, transform);
                }
                catch (NoninvertibleTransformException nite) {
                    return intermediateImage;
                }
            }
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

