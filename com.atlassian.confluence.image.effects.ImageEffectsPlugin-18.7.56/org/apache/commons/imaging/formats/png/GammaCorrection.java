/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.imaging.formats.png;

import java.util.logging.Level;
import java.util.logging.Logger;

public class GammaCorrection {
    private static final Logger LOGGER = Logger.getLogger(GammaCorrection.class.getName());
    private final int[] lookupTable;

    public GammaCorrection(double srcGamma, double dstGamma) {
        if (LOGGER.isLoggable(Level.FINEST)) {
            LOGGER.finest("src_gamma: " + srcGamma);
            LOGGER.finest("dst_gamma: " + dstGamma);
        }
        this.lookupTable = new int[256];
        for (int i = 0; i < 256; ++i) {
            this.lookupTable[i] = this.correctSample(i, srcGamma, dstGamma);
            if (!LOGGER.isLoggable(Level.FINEST)) continue;
            LOGGER.finest("lookup_table[" + i + "]: " + this.lookupTable[i]);
        }
    }

    public int correctSample(int sample) {
        return this.lookupTable[sample];
    }

    public int correctARGB(int pixel) {
        int alpha = 0xFF000000 & pixel;
        int red = pixel >> 16 & 0xFF;
        int green = pixel >> 8 & 0xFF;
        int blue = pixel >> 0 & 0xFF;
        red = this.correctSample(red);
        green = this.correctSample(green);
        blue = this.correctSample(blue);
        return alpha | (0xFF & red) << 16 | (0xFF & green) << 8 | (0xFF & blue) << 0;
    }

    private int correctSample(int sample, double srcGamma, double dstGamma) {
        return (int)Math.round(255.0 * Math.pow((double)sample / 255.0, srcGamma / dstGamma));
    }
}

