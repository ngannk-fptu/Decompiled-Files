/*
 * Decompiled with CFR 0.152.
 */
package com.sun.media.jai.opimage;

import com.sun.media.jai.opimage.CopyOpImage;
import com.sun.media.jai.opimage.JaiI18N;
import com.sun.media.jai.opimage.RIFUtil;
import com.sun.media.jai.opimage.SubsampleBinaryToGray2x2OpImage;
import com.sun.media.jai.opimage.SubsampleBinaryToGray4x4OpImage;
import com.sun.media.jai.opimage.SubsampleBinaryToGrayOpImage;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
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

public class SubsampleBinaryToGrayCRIF
extends CRIFImpl {
    public SubsampleBinaryToGrayCRIF() {
        super("subsamplebinarytogray");
    }

    public RenderedImage create(ParameterBlock paramBlock, RenderingHints renderHints) {
        ImageLayout layout = RIFUtil.getImageLayoutHint(renderHints);
        BorderExtender extender = RIFUtil.getBorderExtenderHint(renderHints);
        RenderedImage source = paramBlock.getRenderedSource(0);
        float xScale = paramBlock.getFloatParameter(0);
        float yScale = paramBlock.getFloatParameter(1);
        if (xScale == 1.0f && yScale == 1.0f) {
            return new CopyOpImage(source, renderHints, layout);
        }
        SampleModel sm = source.getSampleModel();
        if (sm instanceof MultiPixelPackedSampleModel && sm.getSampleSize(0) == 1 && (sm.getDataType() == 0 || sm.getDataType() == 1 || sm.getDataType() == 3)) {
            int srcWidth = source.getWidth();
            int srcHeight = source.getHeight();
            float floatTol = 0.1f * Math.min(xScale / ((float)srcWidth * xScale + 1.0f), yScale / ((float)srcHeight * yScale + 1.0f));
            int invScale = Math.round(1.0f / xScale);
            if (Math.abs((float)invScale - 1.0f / xScale) < floatTol && Math.abs((float)invScale - 1.0f / yScale) < floatTol) {
                switch (invScale) {
                    case 2: {
                        return new SubsampleBinaryToGray2x2OpImage(source, layout, (Map)renderHints);
                    }
                    case 4: {
                        return new SubsampleBinaryToGray4x4OpImage(source, layout, (Map)renderHints);
                    }
                }
            }
            return new SubsampleBinaryToGrayOpImage(source, layout, renderHints, xScale, yScale);
        }
        throw new IllegalArgumentException(JaiI18N.getString("SubsampleBinaryToGray3"));
    }

    public RenderedImage create(RenderContext renderContext, ParameterBlock paramBlock) {
        return paramBlock.getRenderedSource(0);
    }

    public RenderContext mapRenderContext(int i, RenderContext renderContext, ParameterBlock paramBlock, RenderableImage image) {
        float scale_x = paramBlock.getFloatParameter(0);
        float scale_y = paramBlock.getFloatParameter(1);
        AffineTransform scale = new AffineTransform((double)scale_x, 0.0, 0.0, (double)scale_y, 0.0, 0.0);
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
        float x0 = source.getMinX();
        float y0 = source.getMinY();
        float w = source.getWidth();
        float h = source.getHeight();
        float d_x0 = x0 * scale_x;
        float d_y0 = y0 * scale_y;
        float d_w = w * scale_x;
        float d_h = h * scale_y;
        return new Rectangle2D.Float(d_x0, d_y0, d_w, d_h);
    }
}

