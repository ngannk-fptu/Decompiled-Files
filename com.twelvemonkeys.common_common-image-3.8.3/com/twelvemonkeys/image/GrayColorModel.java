/*
 * Decompiled with CFR 0.152.
 */
package com.twelvemonkeys.image;

import java.awt.image.IndexColorModel;

public class GrayColorModel
extends IndexColorModel {
    private static final byte[] sGrays = GrayColorModel.createGrayScale();

    public GrayColorModel() {
        super(8, sGrays.length, sGrays, sGrays, sGrays);
    }

    private static byte[] createGrayScale() {
        byte[] byArray = new byte[256];
        for (int i = 0; i < 256; ++i) {
            byArray[i] = (byte)i;
        }
        return byArray;
    }
}

