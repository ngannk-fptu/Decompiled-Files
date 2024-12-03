/*
 * Decompiled with CFR 0.152.
 */
package com.sun.media.jai.opimage;

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

public class TranslateCRIF
extends CRIFImpl {
    private static final float TOLERANCE = 0.01f;

    public TranslateCRIF() {
        super("translate");
    }

    public RenderedImage create(ParameterBlock paramBlock, RenderingHints renderHints) {
        boolean isBinary;
        RenderedImage source = paramBlock.getRenderedSource(0);
        float xTrans = paramBlock.getFloatParameter(0);
        float yTrans = paramBlock.getFloatParameter(1);
        Interpolation interp = (Interpolation)paramBlock.getObjectParameter(2);
        ImageLayout layout = RIFUtil.getImageLayoutHint(renderHints);
        if (Math.abs(xTrans - (float)((int)xTrans)) < 0.01f && Math.abs(yTrans - (float)((int)yTrans)) < 0.01f && layout == null) {
            return new TranslateIntOpImage(source, renderHints, (int)xTrans, (int)yTrans);
        }
        TileCache cache = RIFUtil.getTileCacheHint(renderHints);
        BorderExtender extender = RIFUtil.getBorderExtenderHint(renderHints);
        float xScale = 1.0f;
        float yScale = 1.0f;
        SampleModel sm = source.getSampleModel();
        boolean bl = isBinary = sm instanceof MultiPixelPackedSampleModel && sm.getSampleSize(0) == 1 && (sm.getDataType() == 0 || sm.getDataType() == 1 || sm.getDataType() == 3);
        if (interp instanceof InterpolationNearest) {
            if (isBinary) {
                return new ScaleNearestBinaryOpImage(source, extender, renderHints, layout, xScale, yScale, xTrans, yTrans, interp);
            }
            return new ScaleNearestOpImage(source, extender, renderHints, layout, xScale, yScale, xTrans, yTrans, interp);
        }
        if (interp instanceof InterpolationBilinear) {
            if (isBinary) {
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
        AffineTransform translate = new AffineTransform();
        translate.setToTranslation(paramBlock.getFloatParameter(0), paramBlock.getFloatParameter(1));
        RenderContext RC = (RenderContext)renderContext.clone();
        AffineTransform usr2dev = RC.getTransform();
        usr2dev.concatenate(translate);
        RC.setTransform(usr2dev);
        return RC;
    }

    public Rectangle2D getBounds2D(ParameterBlock paramBlock) {
        RenderableImage source = paramBlock.getRenderableSource(0);
        float xTrans = paramBlock.getFloatParameter(0);
        float yTrans = paramBlock.getFloatParameter(1);
        return new Rectangle2D.Float(source.getMinX() + xTrans, source.getMinY() + yTrans, source.getWidth(), source.getHeight());
    }
}

