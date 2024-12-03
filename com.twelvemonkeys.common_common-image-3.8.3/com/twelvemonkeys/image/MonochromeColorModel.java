/*
 * Decompiled with CFR 0.152.
 */
package com.twelvemonkeys.image;

import java.awt.image.IndexColorModel;

public class MonochromeColorModel
extends IndexColorModel {
    private static final int[] MONO_PALETTE = new int[]{0, 0xFFFFFF};
    private static MonochromeColorModel sInstance = new MonochromeColorModel();

    private MonochromeColorModel() {
        super(1, 2, MONO_PALETTE, 0, false, -1, 0);
    }

    public static IndexColorModel getInstance() {
        return sInstance;
    }

    @Override
    public synchronized Object getDataElements(int n, Object object) {
        int n2 = n >> 16 & 0xFF;
        int n3 = n >> 8 & 0xFF;
        int n4 = n & 0xFF;
        int n5 = (222 * n2 + 707 * n3 + 71 * n4) / 1000;
        byte[] byArray = object != null ? (byte[])object : new byte[1];
        byArray[0] = n5 <= 128 ? (byte)0 : 1;
        return byArray;
    }
}

