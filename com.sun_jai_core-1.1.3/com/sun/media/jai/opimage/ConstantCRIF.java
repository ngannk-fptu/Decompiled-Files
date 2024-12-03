/*
 * Decompiled with CFR 0.152.
 */
package com.sun.media.jai.opimage;

import com.sun.media.jai.opimage.ConstantOpImage;
import com.sun.media.jai.opimage.RIFUtil;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.ParameterBlock;
import java.awt.image.renderable.RenderContext;
import javax.media.jai.CRIFImpl;
import javax.media.jai.ImageLayout;

public class ConstantCRIF
extends CRIFImpl {
    private static final int DEFAULT_TILE_SIZE = 128;

    public ConstantCRIF() {
        super("constant");
    }

    public RenderedImage create(ParameterBlock paramBlock, RenderingHints renderHints) {
        ImageLayout layout = RIFUtil.getImageLayoutHint(renderHints);
        int width = Math.round(paramBlock.getFloatParameter(0));
        int height = Math.round(paramBlock.getFloatParameter(1));
        Number[] bandValues = (Number[])paramBlock.getObjectParameter(2);
        int minX = 0;
        int minY = 0;
        int tileWidth = Math.min(width, 128);
        int tileHeight = Math.min(height, 128);
        if (layout != null) {
            if (layout.isValid(1)) {
                minX = layout.getMinX(null);
            }
            if (layout.isValid(2)) {
                minY = layout.getMinY(null);
            }
            if (layout.isValid(64)) {
                tileWidth = layout.getTileWidth(null);
            }
            if (layout.isValid(128)) {
                tileHeight = layout.getTileHeight(null);
            }
        }
        return new ConstantOpImage(minX, minY, width, height, tileWidth, tileHeight, bandValues);
    }

    public RenderedImage create(RenderContext renderContext, ParameterBlock paramBlock) {
        float minX = 0.0f;
        float minY = 0.0f;
        float width = paramBlock.getFloatParameter(0);
        float height = paramBlock.getFloatParameter(1);
        Number[] bandValues = (Number[])paramBlock.getObjectParameter(2);
        AffineTransform trans = renderContext.getTransform();
        float[] ptSrc = new float[8];
        float[] ptDst = new float[8];
        ptSrc[0] = minX;
        ptSrc[1] = minY;
        ptSrc[2] = minX + width;
        ptSrc[3] = minY;
        ptSrc[4] = minX + width;
        ptSrc[5] = minY + height;
        ptSrc[6] = minX;
        ptSrc[7] = minY + height;
        trans.transform(ptSrc, 0, ptDst, 0, 4);
        minX = Math.min(ptDst[0], ptDst[2]);
        minX = Math.min(minX, ptDst[4]);
        minX = Math.min(minX, ptDst[6]);
        float maxX = Math.max(ptDst[0], ptDst[2]);
        maxX = Math.max(maxX, ptDst[4]);
        maxX = Math.max(maxX, ptDst[6]);
        minY = Math.min(ptDst[1], ptDst[3]);
        minY = Math.min(minY, ptDst[5]);
        minY = Math.min(minY, ptDst[7]);
        float maxY = Math.max(ptDst[1], ptDst[3]);
        maxY = Math.max(maxY, ptDst[5]);
        maxY = Math.max(maxY, ptDst[7]);
        int iMinX = (int)minX;
        int iMinY = (int)minY;
        int iWidth = (int)maxX - iMinX;
        int iHeight = (int)maxY - iMinY;
        return new ConstantOpImage(iMinX, iMinY, iWidth, iHeight, Math.min(iWidth, 128), Math.min(iHeight, 128), bandValues);
    }

    public Rectangle2D getBounds2D(ParameterBlock paramBlock) {
        return new Rectangle2D.Float(0.0f, 0.0f, paramBlock.getFloatParameter(0), paramBlock.getFloatParameter(1));
    }
}

