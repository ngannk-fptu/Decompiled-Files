/*
 * Decompiled with CFR 0.152.
 */
package com.fasterxml.jackson.databind.cfg;

public interface ConfigFeature {
    public boolean enabledByDefault();

    public int getMask();

    public boolean enabledIn(int var1);
}

