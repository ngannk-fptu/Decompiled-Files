/*
 * Decompiled with CFR 0.152.
 */
package com.sun.media.jai.mlib;

import com.sun.media.jai.mlib.MlibConvolveRIF;
import java.awt.RenderingHints;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.ParameterBlock;
import java.util.Arrays;
import javax.media.jai.KernelJAI;

public class MlibBoxFilterRIF
extends MlibConvolveRIF {
    public RenderedImage create(ParameterBlock paramBlock, RenderingHints renderHints) {
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
        ParameterBlock args = new ParameterBlock(paramBlock.getSources());
        args.add(kernel);
        return super.create(args, renderHints);
    }
}

