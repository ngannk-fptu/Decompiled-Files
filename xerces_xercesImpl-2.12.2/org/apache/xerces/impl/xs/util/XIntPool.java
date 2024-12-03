/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xerces.impl.xs.util;

import org.apache.xerces.impl.xs.util.XInt;

public final class XIntPool {
    private static final short POOL_SIZE = 10;
    private static final XInt[] fXIntPool = new XInt[10];

    public final XInt getXInt(int n) {
        if (n >= 0 && n < fXIntPool.length) {
            return fXIntPool[n];
        }
        return new XInt(n);
    }

    static {
        for (int i = 0; i < 10; ++i) {
            XIntPool.fXIntPool[i] = new XInt(i);
        }
    }
}

