/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.instrumentation;

public interface Instrument
extends Comparable<Instrument> {
    public String getName();

    public long getValue();
}

