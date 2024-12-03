/*
 * Decompiled with CFR 0.152.
 */
package com.mchange.v2.encounter;

public interface EncounterCounter {
    public long encounter(Object var1);

    public long reset(Object var1);

    public void resetAll();
}

