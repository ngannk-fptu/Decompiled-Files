/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.engines;

import org.bouncycastle.crypto.engines.VMPCEngine;

public class VMPCKSA3Engine
extends VMPCEngine {
    @Override
    public String getAlgorithmName() {
        return "VMPC-KSA3";
    }

    @Override
    protected void initKey(byte[] keyBytes, byte[] ivBytes) {
        byte temp;
        int m;
        this.s = 0;
        this.P = new byte[256];
        for (int i = 0; i < 256; ++i) {
            this.P[i] = (byte)i;
        }
        for (m = 0; m < 768; ++m) {
            this.s = this.P[this.s + this.P[m & 0xFF] + keyBytes[m % keyBytes.length] & 0xFF];
            temp = this.P[m & 0xFF];
            this.P[m & 0xFF] = this.P[this.s & 0xFF];
            this.P[this.s & 0xFF] = temp;
        }
        for (m = 0; m < 768; ++m) {
            this.s = this.P[this.s + this.P[m & 0xFF] + ivBytes[m % ivBytes.length] & 0xFF];
            temp = this.P[m & 0xFF];
            this.P[m & 0xFF] = this.P[this.s & 0xFF];
            this.P[this.s & 0xFF] = temp;
        }
        for (m = 0; m < 768; ++m) {
            this.s = this.P[this.s + this.P[m & 0xFF] + keyBytes[m % keyBytes.length] & 0xFF];
            temp = this.P[m & 0xFF];
            this.P[m & 0xFF] = this.P[this.s & 0xFF];
            this.P[this.s & 0xFF] = temp;
        }
        this.n = 0;
    }
}

