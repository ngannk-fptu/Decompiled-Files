/*
 * Decompiled with CFR 0.152.
 */
package com.lowagie.text.pdf.crypto;

import com.lowagie.text.pdf.crypto.ARCFOUREncryption;

public final class IVGenerator {
    private static ARCFOUREncryption arcfour = new ARCFOUREncryption();

    private IVGenerator() {
    }

    public static byte[] getIV() {
        return IVGenerator.getIV(16);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static byte[] getIV(int len) {
        byte[] b = new byte[len];
        ARCFOUREncryption aRCFOUREncryption = arcfour;
        synchronized (aRCFOUREncryption) {
            arcfour.encryptARCFOUR(b);
        }
        return b;
    }

    static {
        long time = System.currentTimeMillis();
        long mem = Runtime.getRuntime().freeMemory();
        String s = time + "+" + mem;
        arcfour.prepareARCFOURKey(s.getBytes());
    }
}

