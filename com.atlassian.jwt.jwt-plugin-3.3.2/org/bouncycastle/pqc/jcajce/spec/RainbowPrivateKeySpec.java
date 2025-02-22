/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.pqc.jcajce.spec;

import java.security.spec.KeySpec;
import org.bouncycastle.pqc.crypto.rainbow.Layer;

public class RainbowPrivateKeySpec
implements KeySpec {
    private short[][] A1inv;
    private short[] b1;
    private short[][] A2inv;
    private short[] b2;
    private int[] vi;
    private Layer[] layers;

    public RainbowPrivateKeySpec(short[][] sArray, short[] sArray2, short[][] sArray3, short[] sArray4, int[] nArray, Layer[] layerArray) {
        this.A1inv = sArray;
        this.b1 = sArray2;
        this.A2inv = sArray3;
        this.b2 = sArray4;
        this.vi = nArray;
        this.layers = layerArray;
    }

    public short[] getB1() {
        return this.b1;
    }

    public short[][] getInvA1() {
        return this.A1inv;
    }

    public short[] getB2() {
        return this.b2;
    }

    public short[][] getInvA2() {
        return this.A2inv;
    }

    public Layer[] getLayers() {
        return this.layers;
    }

    public int[] getVi() {
        return this.vi;
    }
}

