/*
 * Decompiled with CFR 0.152.
 */
package com.sun.media.jai.opimage;

import com.sun.media.jai.opimage.RIFUtil;
import com.sun.media.jai.opimage.SeparableConvolveOpImage;
import java.awt.RenderingHints;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.ParameterBlock;
import java.awt.image.renderable.RenderedImageFactory;
import java.util.Arrays;
import javax.media.jai.BorderExtender;
import javax.media.jai.ImageLayout;
import javax.media.jai.KernelJAI;

public class BoxFilterRIF
implements RenderedImageFactory {
    public RenderedImage create(ParameterBlock paramBlock, RenderingHints renderHints) {
        ImageLayout layout = RIFUtil.getImageLayoutHint(renderHints);
        BorderExtender extender = RIFUtil.getBorderExtenderHint(renderHints);
        int width = paramBlock.getIntParameter(0);
        int height = paramBlock.getIntParameter(1);
        int xOrigin = paramBlock.getIntParameter(2);
        int yOrigin = paramBlock.getIntParameter(3);
        float[] dataH = new float[width];
        Arrays.fill(dataH, 1.0f / (float)width);
        float[] dataV = null;
        if (height == width) {
            dataV = dataH;
        } else {
            dataV = new float[height];
            Arrays.fill(dataV, 1.0f / (float)height);
        }
        KernelJAI kernel = new KernelJAI(width, height, xOrigin, yOrigin, dataH, dataV);
        return new SeparableConvolveOpImage(paramBlock.getRenderedSource(0), extender, renderHints, layout, kernel);
    }
}

