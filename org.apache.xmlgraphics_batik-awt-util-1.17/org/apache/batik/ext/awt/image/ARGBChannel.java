/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.ext.awt.image;

import java.io.Serializable;

public final class ARGBChannel
implements Serializable {
    public static final int CHANNEL_A = 3;
    public static final int CHANNEL_R = 2;
    public static final int CHANNEL_G = 1;
    public static final int CHANNEL_B = 0;
    public static final String RED = "Red";
    public static final String GREEN = "Green";
    public static final String BLUE = "Blue";
    public static final String ALPHA = "Alpha";
    public static final ARGBChannel R = new ARGBChannel(2, "Red");
    public static final ARGBChannel G = new ARGBChannel(1, "Green");
    public static final ARGBChannel B = new ARGBChannel(0, "Blue");
    public static final ARGBChannel A = new ARGBChannel(3, "Alpha");
    private String desc;
    private int val;

    private ARGBChannel(int val, String desc) {
        this.desc = desc;
        this.val = val;
    }

    public String toString() {
        return this.desc;
    }

    public int toInt() {
        return this.val;
    }

    public Object readResolve() {
        switch (this.val) {
            case 2: {
                return R;
            }
            case 1: {
                return G;
            }
            case 0: {
                return B;
            }
            case 3: {
                return A;
            }
        }
        throw new RuntimeException("Unknown ARGBChannel value");
    }
}

