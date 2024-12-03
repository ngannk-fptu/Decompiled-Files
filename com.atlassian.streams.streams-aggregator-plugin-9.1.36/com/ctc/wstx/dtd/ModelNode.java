/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.dtd;

import java.util.BitSet;
import java.util.List;

public abstract class ModelNode {
    public abstract ModelNode cloneModel();

    public abstract boolean isNullable();

    public abstract void indexTokens(List var1);

    public abstract void addFirstPos(BitSet var1);

    public abstract void addLastPos(BitSet var1);

    public abstract void calcFollowPos(BitSet[] var1);
}

