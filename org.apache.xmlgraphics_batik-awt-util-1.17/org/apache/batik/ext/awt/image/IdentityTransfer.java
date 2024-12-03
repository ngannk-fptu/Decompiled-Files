/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.ext.awt.image;

import org.apache.batik.ext.awt.image.TransferFunction;

public class IdentityTransfer
implements TransferFunction {
    public static byte[] lutData = new byte[256];

    @Override
    public byte[] getLookupTable() {
        return lutData;
    }

    static {
        for (int j = 0; j <= 255; ++j) {
            IdentityTransfer.lutData[j] = (byte)j;
        }
    }
}

