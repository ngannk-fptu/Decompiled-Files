/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.medialib.mlib.Image
 *  com.sun.medialib.mlib.mediaLibImage
 */
package com.sun.media.jai.mlib;

import com.sun.medialib.mlib.Image;
import com.sun.medialib.mlib.mediaLibImage;
import java.awt.image.ColorModel;

final class MlibUtils {
    MlibUtils() {
    }

    static final int[] initConstants(int[] constants, int numBands) {
        int[] c = null;
        if (constants.length < numBands) {
            c = new int[numBands];
            for (int i = 0; i < numBands; ++i) {
                c[i] = constants[0];
            }
        } else {
            c = (int[])constants.clone();
        }
        return c;
    }

    static final double[] initConstants(double[] constants, int numBands) {
        double[] c = null;
        if (constants.length < numBands) {
            c = new double[numBands];
            for (int i = 0; i < numBands; ++i) {
                c[i] = constants[0];
            }
        } else {
            c = (double[])constants.clone();
        }
        return c;
    }

    static void clampImage(mediaLibImage image, ColorModel colorModel) {
        if (image == null) {
            throw new IllegalArgumentException("image == null!");
        }
        if (colorModel != null) {
            int fullDepth = 0;
            switch (image.getType()) {
                case 1: {
                    fullDepth = 8;
                    break;
                }
                case 3: {
                    fullDepth = 32;
                    break;
                }
                default: {
                    fullDepth = 16;
                }
            }
            int[] numBits = colorModel.getComponentSize();
            int[] high = new int[numBits.length];
            int[] low = new int[numBits.length];
            boolean applyThreshold = false;
            for (int j = 0; j < numBits.length; ++j) {
                high[j] = (1 << numBits[j]) - 1;
                if (numBits[j] == fullDepth) continue;
                applyThreshold = true;
            }
            if (applyThreshold) {
                Image.Thresh4((mediaLibImage)image, (int[])high, (int[])low, (int[])high, (int[])low);
            }
        }
    }
}

