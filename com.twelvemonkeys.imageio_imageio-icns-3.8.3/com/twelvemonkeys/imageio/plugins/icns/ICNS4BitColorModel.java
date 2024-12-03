/*
 * Decompiled with CFR 0.152.
 */
package com.twelvemonkeys.imageio.plugins.icns;

import java.awt.image.IndexColorModel;

final class ICNS4BitColorModel
extends IndexColorModel {
    private static final int[] CMAP = new int[]{-1, -199931, -39934, -2291706, -915324, -12189531, -16777004, -16602134, -14698732, -16751599, -11129851, -7311046, -4144960, -8355712, -12566464, -16777216};
    static final IndexColorModel INSTANCE = new ICNS4BitColorModel();

    private ICNS4BitColorModel() {
        super(4, 16, CMAP, 0, false, -1, 0);
    }
}

