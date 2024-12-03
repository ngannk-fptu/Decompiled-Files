/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.engines;

import org.bouncycastle.crypto.engines.Zuc256CoreEngine;
import org.bouncycastle.util.Memoable;

public final class Zuc256Engine
extends Zuc256CoreEngine {
    public Zuc256Engine() {
    }

    public Zuc256Engine(int pLength) {
        super(pLength);
    }

    private Zuc256Engine(Zuc256Engine pSource) {
        super(pSource);
    }

    @Override
    public Memoable copy() {
        return new Zuc256Engine(this);
    }
}

