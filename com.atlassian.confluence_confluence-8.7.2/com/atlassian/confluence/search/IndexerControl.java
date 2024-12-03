/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.search;

public interface IndexerControl {
    public boolean indexingEnabled();

    public boolean indexingDisabled();

    public void suspend();

    public void resume();
}

