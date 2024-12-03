/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.pqc.crypto.xmss;

import org.bouncycastle.pqc.crypto.xmss.WOTSPlusParameters;
import org.bouncycastle.pqc.crypto.xmss.XMSSUtil;

final class WOTSPlusPrivateKeyParameters {
    private final byte[][] privateKey;

    protected WOTSPlusPrivateKeyParameters(WOTSPlusParameters wOTSPlusParameters, byte[][] byArray) {
        if (wOTSPlusParameters == null) {
            throw new NullPointerException("params == null");
        }
        if (byArray == null) {
            throw new NullPointerException("privateKey == null");
        }
        if (XMSSUtil.hasNullPointer(byArray)) {
            throw new NullPointerException("privateKey byte array == null");
        }
        if (byArray.length != wOTSPlusParameters.getLen()) {
            throw new IllegalArgumentException("wrong privateKey format");
        }
        for (int i = 0; i < byArray.length; ++i) {
            if (byArray[i].length == wOTSPlusParameters.getTreeDigestSize()) continue;
            throw new IllegalArgumentException("wrong privateKey format");
        }
        this.privateKey = XMSSUtil.cloneArray(byArray);
    }

    protected byte[][] toByteArray() {
        return XMSSUtil.cloneArray(this.privateKey);
    }
}

