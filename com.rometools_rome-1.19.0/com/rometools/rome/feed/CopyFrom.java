/*
 * Decompiled with CFR 0.152.
 */
package com.rometools.rome.feed;

public interface CopyFrom {
    public Class<? extends CopyFrom> getInterface();

    public void copyFrom(CopyFrom var1);
}

