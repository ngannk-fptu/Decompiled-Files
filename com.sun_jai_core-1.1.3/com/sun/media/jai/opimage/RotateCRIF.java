/*
 * Decompiled with CFR 0.152.
 */
package com.sun.media.jai.opimage;

import com.sun.media.jai.opimage.AffineBicubic2OpImage;
import com.sun.media.jai.opimage.AffineBicubicOpImage;
import com.sun.media.jai.opimage.AffineBilinearOpImage;
import com.sun.media.jai.opimage.AffineGeneralOpImage;
import com.sun.media.jai.opimage.AffineNearestBinaryOpImage;
import com.sun.media.jai.opimage.AffineNearestOpImage;
import com.sun.media.jai.opimage.CopyOpImage;
import com.sun.media.jai.opimage.PointMapperOpImage;
import com.sun.media.jai.opimage.RIFUtil;
import com.sun.media.jai.opimage.TranslateIntOpImage;
import com.sun.media.jai.opimage.TransposeBinaryOpImage;
import com.sun.media.jai.opimage.TransposeOpImage;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.MultiPixelPackedSampleModel;
import java.awt.image.RenderedImage;
import java.awt.image.SampleModel;
import java.awt.image.renderable.ParameterBlock;
import java.awt.image.renderable.RenderContext;
import java.awt.image.renderable.RenderableImage;
import java.util.Map;
import javax.media.jai.BorderExtender;
import javax.media.jai.CRIFImpl;
import javax.media.jai.ImageLayout;
import javax.media.jai.Interpolation;
import javax.media.jai.InterpolationBicubic;
import javax.media.jai.InterpolationBicubic2;
import javax.media.jai.InterpolationBilinear;
import javax.media.jai.InterpolationNearest;
import javax.media.jai.JAI;
import javax.media.jai.PlanarImage;

public class RotateCRIF
extends CRIFImpl {
    public RotateCRIF() {
        super("rotate");
    }

    public RenderedImage create(ParameterBlock paramBlock, RenderingHints renderHints) {
        ImageLayout layout = RIFUtil.getImageLayoutHint(renderHints);
        BorderExtender extender = RIFUtil.getBorderExtenderHint(renderHints);
        RenderedImage source = paramBlock.getRenderedSource(0);
        float x_center = paramBlock.getFloatParameter(0);
        float y_center = paramBlock.getFloatParameter(1);
        float angle = paramBlock.getFloatParameter(2);
        Object arg1 = paramBlock.getObjectParameter(3);
        Interpolation interp = (Interpolation)arg1;
        double[] backgroundValues = (double[])paramBlock.getObjectParameter(4);
        SampleModel sm = source.getSampleModel();
        boolean isBinary = sm instanceof MultiPixelPackedSampleModel && sm.getSampleSize(0) == 1 && (sm.getDataType() == 0 || sm.getDataType() == 1 || sm.getDataType() == 3);
        double tmp_angle = 57.29577951308232 * (double)angle;
        double rnd_angle = Math.round(tmp_angle);
        AffineTransform transform = AffineTransform.getRotateInstance(angle, x_center, y_center);
        if (Math.abs(rnd_angle - tmp_angle) < 1.0E-4) {
            int dangle = (int)rnd_angle % 360;
            if (dangle < 0) {
                dangle += 360;
            }
            if (dangle == 0) {
                return new CopyOpImage(source, renderHints, layout);
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
                TransposeOpImage trans = isBinary ? new TransposeBinaryOpImage(source, renderHints, layout, transType) : new TransposeOpImage(source, renderHints, layout, transType);
                int imMinX = trans.getMinX();
                int imMinY = trans.getMinY();
                if (layout == null) {
                    TranslateIntOpImage intermediateImage = new TranslateIntOpImage(trans, renderHints, rotMinX - imMinX, rotMinY - imMinY);
                    try {
                        return new PointMapperOpImage(intermediateImage, renderHints, transform);
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
                PlanarImage intermediateImage = JAI.create("scale", pbScale, renderHints).getRendering();
                try {
                    return new PointMapperOpImage(intermediateImage, renderHints, transform);
                }
                catch (NoninvertibleTransformException nite) {
                    return intermediateImage;
                }
            }
        }
        if (interp instanceof InterpolationNearest) {
            if (isBinary) {
                return new AffineNearestBinaryOpImage(source, extender, (Map)renderHints, layout, transform, interp, backgroundValues);
            }
            return new AffineNearestOpImage(source, extender, (Map)renderHints, layout, transform, interp, backgroundValues);
        }
        if (interp instanceof InterpolationBilinear) {
            return new AffineBilinearOpImage(source, extender, (Map)renderHints, layout, transform, interp, backgroundValues);
        }
        if (interp instanceof InterpolationBicubic) {
            return new AffineBicubicOpImage(source, extender, (Map)renderHints, layout, transform, interp, backgroundValues);
        }
        if (interp instanceof InterpolationBicubic2) {
            return new AffineBicubic2OpImage(source, extender, (Map)renderHints, layout, transform, interp, backgroundValues);
        }
        return new AffineGeneralOpImage(source, extender, (Map)renderHints, layout, transform, interp, backgroundValues);
    }

    public RenderedImage create(RenderContext renderContext, ParameterBlock paramBlock) {
        return paramBlock.getRenderedSource(0);
    }

    public RenderContext mapRenderContext(int i, RenderContext renderContext, ParameterBlock paramBlock, RenderableImage image) {
        float x_center = paramBlock.getFloatParameter(0);
        float y_center = paramBlock.getFloatParameter(1);
        float angle = paramBlock.getFloatParameter(2);
        AffineTransform rotate = AffineTransform.getRotateInstance(angle, x_center, y_center);
        RenderContext RC = (RenderContext)renderContext.clone();
        AffineTransform usr2dev = RC.getTransform();
        usr2dev.concatenate(rotate);
        RC.setTransform(usr2dev);
        return RC;
    }

    public Rectangle2D getBounds2D(ParameterBlock paramBlock) {
        RenderableImage source = paramBlock.getRenderableSource(0);
        float x_center = paramBlock.getFloatParameter(0);
        float y_center = paramBlock.getFloatParameter(1);
        float angle = paramBlock.getFloatParameter(2);
        Interpolation interp = (Interpolation)paramBlock.getObjectParameter(3);
        int dangle = 0;
        double tmp_angle = (double)(180.0f * angle) / Math.PI;
        double rnd_angle = Math.round(tmp_angle);
        dangle = Math.abs(rnd_angle - tmp_angle) < 1.0E-4 ? (int)rnd_angle : (int)tmp_angle;
        if (dangle % 360 == 0) {
            return new Rectangle2D.Float(source.getMinX(), source.getMinY(), source.getWidth(), source.getHeight());
        }
        float x0 = source.getMinX();
        float y0 = source.getMinY();
        float s_width = source.getWidth();
        float s_height = source.getHeight();
        float x1 = x0 + s_width - 1.0f;
        float y1 = y0 + s_height - 1.0f;
        float tx0 = 0.0f;
        float ty0 = 0.0f;
        float tx1 = 0.0f;
        float ty1 = 0.0f;
        if (dangle % 270 == 0) {
            if (dangle < 0) {
                tx0 = s_height - y1 - 1.0f;
                ty0 = x0;
                tx1 = s_height - y0 - 1.0f;
                ty1 = x1;
                return new Rectangle2D.Float(tx0, ty0, tx1 - tx0 + 1.0f, ty1 - ty0 + 1.0f);
            }
            tx0 = y0;
            ty0 = s_width - x1 - 1.0f;
            tx1 = y1;
            ty1 = s_width - x0 - 1.0f;
            return new Rectangle2D.Float(tx0, ty0, tx1 - tx0 + 1.0f, ty1 - ty0 + 1.0f);
        }
        if (dangle % 180 == 0) {
            tx0 = s_width - x1 - 1.0f;
            ty0 = s_height - y1 - 1.0f;
            tx1 = s_width - x0 - 1.0f;
            ty1 = s_height - y0 - 1.0f;
            return new Rectangle2D.Float(tx0, ty0, tx1 - tx0 + 1.0f, ty1 - ty0 + 1.0f);
        }
        if (dangle % 90 == 0) {
            if (dangle < 0) {
                tx0 = y0;
                ty0 = s_width - x1 - 1.0f;
                tx1 = y1;
                ty1 = s_width - x0 - 1.0f;
                return new Rectangle2D.Float(tx0, ty0, tx1 - tx0 + 1.0f, ty1 - ty0 + 1.0f);
            }
            tx0 = s_height - y1 - 1.0f;
            ty0 = x0;
            tx1 = s_height - y0 - 1.0f;
            ty1 = x1;
            return new Rectangle2D.Float(tx0, ty0, tx1 - tx0 + 1.0f, ty1 - ty0 + 1.0f);
        }
        AffineTransform rotate = AffineTransform.getRotateInstance(angle, x_center, y_center);
        float sx0 = source.getMinX();
        float sy0 = source.getMinY();
        float sw = source.getWidth();
        float sh = source.getHeight();
        Point2D[] pts = new Point2D[]{new Point2D.Float(sx0, sy0), new Point2D.Float(sx0 + sw, sy0), new Point2D.Float(sx0 + sw, sy0 + sh), new Point2D.Float(sx0, sy0 + sh)};
        rotate.transform(pts, 0, pts, 0, 4);
        float dx0 = Float.MAX_VALUE;
        float dy0 = Float.MAX_VALUE;
        float dx1 = -3.4028235E38f;
        float dy1 = -3.4028235E38f;
        for (int i = 0; i < 4; ++i) {
            float px = (float)pts[i].getX();
            float py = (float)pts[i].getY();
            dx0 = Math.min(dx0, px);
            dy0 = Math.min(dy0, py);
            dx1 = Math.max(dx1, px);
            dy1 = Math.max(dy1, py);
        }
        float lw = dx1 - dx0;
        float lh = dy1 - dy0;
        return new Rectangle2D.Float(dx0, dy0, lw, lh);
    }
}

