/*
 * Decompiled with CFR 0.152.
 */
package com.sun.media.jai.opimage;

import com.sun.media.jai.opimage.CopyOpImage;
import com.sun.media.jai.opimage.RIFUtil;
import com.sun.media.jai.opimage.ScaleBicubicOpImage;
import com.sun.media.jai.opimage.ScaleBilinearBinaryOpImage;
import com.sun.media.jai.opimage.ScaleBilinearOpImage;
import com.sun.media.jai.opimage.ScaleGeneralOpImage;
import com.sun.media.jai.opimage.ScaleNearestBinaryOpImage;
import com.sun.media.jai.opimage.ScaleNearestOpImage;
import com.sun.media.jai.opimage.TranslateIntOpImage;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.MultiPixelPackedSampleModel;
import java.awt.image.RenderedImage;
import java.awt.image.SampleModel;
import java.awt.image.renderable.ParameterBlock;
import java.awt.image.renderable.RenderContext;
import java.awt.image.renderable.RenderableImage;
import javax.media.jai.BorderExtender;
import javax.media.jai.CRIFImpl;
import javax.media.jai.ImageLayout;
import javax.media.jai.Interpolation;
import javax.media.jai.InterpolationBicubic;
import javax.media.jai.InterpolationBicubic2;
import javax.media.jai.InterpolationBilinear;
import javax.media.jai.InterpolationNearest;
import javax.media.jai.TileCache;

public class ScaleCRIF
extends CRIFImpl {
    private static final float TOLERANCE = 0.01f;

    public ScaleCRIF() {
        super("scale");
    }

    public RenderedImage create(ParameterBlock paramBlock, RenderingHints renderHints) {
        ImageLayout layout = RIFUtil.getImageLayoutHint(renderHints);
        TileCache cache = RIFUtil.getTileCacheHint(renderHints);
        BorderExtender extender = RIFUtil.getBorderExtenderHint(renderHints);
        RenderedImage source = paramBlock.getRenderedSource(0);
        float xScale = paramBlock.getFloatParameter(0);
        float yScale = paramBlock.getFloatParameter(1);
        float xTrans = paramBlock.getFloatParameter(2);
        float yTrans = paramBlock.getFloatParameter(3);
        Interpolation interp = (Interpolation)paramBlock.getObjectParameter(4);
        if (xScale == 1.0f && yScale == 1.0f && xTrans == 0.0f && yTrans == 0.0f) {
            return new CopyOpImage(source, renderHints, layout);
        }
        if (xScale == 1.0f && yScale == 1.0f && Math.abs(xTrans - (float)((int)xTrans)) < 0.01f && Math.abs(yTrans - (float)((int)yTrans)) < 0.01f && layout == null) {
            return new TranslateIntOpImage(source, renderHints, (int)xTrans, (int)yTrans);
        }
        if (interp instanceof InterpolationNearest) {
            SampleModel sm = source.getSampleModel();
            if (sm instanceof MultiPixelPackedSampleModel && sm.getSampleSize(0) == 1 && (sm.getDataType() == 0 || sm.getDataType() == 1 || sm.getDataType() == 3)) {
                return new ScaleNearestBinaryOpImage(source, extender, renderHints, layout, xScale, yScale, xTrans, yTrans, interp);
            }
            return new ScaleNearestOpImage(source, extender, renderHints, layout, xScale, yScale, xTrans, yTrans, interp);
        }
        if (interp instanceof InterpolationBilinear) {
            SampleModel sm = source.getSampleModel();
            if (sm instanceof MultiPixelPackedSampleModel && sm.getSampleSize(0) == 1 && (sm.getDataType() == 0 || sm.getDataType() == 1 || sm.getDataType() == 3)) {
                return new ScaleBilinearBinaryOpImage(source, extender, renderHints, layout, xScale, yScale, xTrans, yTrans, interp);
            }
            return new ScaleBilinearOpImage(source, extender, renderHints, layout, xScale, yScale, xTrans, yTrans, interp);
        }
        if (interp instanceof InterpolationBicubic || interp instanceof InterpolationBicubic2) {
            return new ScaleBicubicOpImage(source, extender, renderHints, layout, xScale, yScale, xTrans, yTrans, interp);
        }
        return new ScaleGeneralOpImage(source, extender, renderHints, layout, xScale, yScale, xTrans, yTrans, interp);
    }

    public RenderedImage create(RenderContext renderContext, ParameterBlock paramBlock) {
        return paramBlock.getRenderedSource(0);
    }

    public RenderContext mapRenderContext(int i, RenderContext renderContext, ParameterBlock paramBlock, RenderableImage image) {
        float scale_x = paramBlock.getFloatParameter(0);
        float scale_y = paramBlock.getFloatParameter(1);
        float trans_x = paramBlock.getFloatParameter(2);
        float trans_y = paramBlock.getFloatParameter(3);
        AffineTransform scale = new AffineTransform((double)scale_x, 0.0, 0.0, (double)scale_y, (double)trans_x, (double)trans_y);
        RenderContext RC = (RenderContext)renderContext.clone();
        AffineTransform usr2dev = RC.getTransform();
        usr2dev.concatenate(scale);
        RC.setTransform(usr2dev);
        return RC;
    }

    public Rectangle2D getBounds2D(ParameterBlock paramBlock) {
        RenderableImage source = paramBlock.getRenderableSource(0);
        float scale_x = paramBlock.getFloatParameter(0);
        float scale_y = paramBlock.getFloatParameter(1);
        float trans_x = paramBlock.getFloatParameter(2);
        float trans_y = paramBlock.getFloatParameter(3);
        Interpolation interp = (Interpolation)paramBlock.getObjectParameter(4);
        float x0 = source.getMinX();
        float y0 = source.getMinY();
        float w = source.getWidth();
        float h = source.getHeight();
        float d_x0 = x0 * scale_x + trans_x;
        float d_y0 = y0 * scale_y + trans_y;
        float d_w = w * scale_x;
        float d_h = h * scale_y;
        return new Rectangle2D.Float(d_x0, d_y0, d_w, d_h);
    }
}

