/*
 * Decompiled with CFR 0.152.
 */
package com.sun.media.jai.opimage;

import java.awt.RenderingHints;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.ParameterBlock;
import java.util.Arrays;
import javax.media.jai.CRIFImpl;
import javax.media.jai.JAI;
import javax.media.jai.KernelJAI;

final class FilterCRIF
extends CRIFImpl {
    private static final int STEPSIZE = 5;

    private static final KernelJAI createKernel(double p) {
        float[] data;
        int size;
        int STEPSIZE = 5;
        if (p == 0.0) {
            return null;
        }
        double pAbs = Math.abs(p);
        int idx = (int)pAbs / STEPSIZE;
        double frac = (double)(10.0f / (float)STEPSIZE) * (pAbs - (double)(idx * STEPSIZE));
        double blend = 0.010101010101010102 * (Math.pow(10.0, 0.2 * frac) - 1.0);
        if ((double)(idx * STEPSIZE) == pAbs) {
            size = 2 * idx + 1;
            data = new float[size * size];
            float val = 1.0f / (float)(size * size);
            Arrays.fill(data, val);
        } else {
            int i;
            int size1 = 2 * idx + 1;
            size = size1 + 2;
            data = new float[size * size];
            float val1 = 1.0f / (float)(size1 * size1) * (1.0f - (float)blend);
            int row = size;
            for (int j = 1; j < size - 1; ++j) {
                for (i = 1; i < size - 1; ++i) {
                    data[row + i] = val1;
                }
                row += size;
            }
            float val2 = 1.0f / (float)(size * size) * (float)blend;
            i = 0;
            while (i < data.length) {
                int n = i++;
                data[n] = data[n] + val2;
            }
        }
        if (p > 0.0) {
            int i = 0;
            while (i < data.length) {
                int n = i++;
                data[n] = (float)((double)data[n] * -1.0);
            }
            int n = data.length / 2;
            data[n] = data[n] + 2.0f;
        }
        return new KernelJAI(size, size, data);
    }

    public RenderedImage create(ParameterBlock paramBlock, RenderingHints renderHints) {
        KernelJAI kernel = FilterCRIF.createKernel(paramBlock.getFloatParameter(0));
        return kernel == null ? paramBlock.getRenderedSource(0) : JAI.create("convolve", paramBlock.getRenderedSource(0), (Object)kernel);
    }
}

