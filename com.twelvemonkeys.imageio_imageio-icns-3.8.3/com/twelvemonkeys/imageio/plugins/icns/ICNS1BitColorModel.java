/*
 * Decompiled with CFR 0.152.
 */
package com.twelvemonkeys.imageio.plugins.icns;

import java.awt.image.IndexColorModel;

final class ICNS1BitColorModel
extends IndexColorModel {
    private static final int[] CMAP = new int[]{-1, -16777216};
    static final IndexColorModel INSTANCE = new ICNS1BitColorModel();

    private ICNS1BitColorModel() {
        super(1, 2, CMAP, 0, false, -1, 0);
    }
}

