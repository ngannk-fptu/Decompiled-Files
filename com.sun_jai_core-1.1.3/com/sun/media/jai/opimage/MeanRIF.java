/*
 * Decompiled with CFR 0.152.
 */
package com.sun.media.jai.opimage;

import com.sun.media.jai.opimage.MeanOpImage;
import java.awt.RenderingHints;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.ParameterBlock;
import java.awt.image.renderable.RenderedImageFactory;
import javax.media.jai.ROI;

public class MeanRIF
implements RenderedImageFactory {
    public RenderedImage create(ParameterBlock paramBlock, RenderingHints renderHints) {
        RenderedImage src = paramBlock.getRenderedSource(0);
        int xStart = src.getMinX();
        int yStart = src.getMinY();
        int maxWidth = src.getWidth();
        int maxHeight = src.getHeight();
        return new MeanOpImage(src, (ROI)paramBlock.getObjectParameter(0), xStart, yStart, paramBlock.getIntParameter(1), paramBlock.getIntParameter(2));
    }
}

