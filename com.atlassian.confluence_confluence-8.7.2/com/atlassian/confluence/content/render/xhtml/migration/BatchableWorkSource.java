/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.content.render.xhtml.migration;

import java.util.List;

public interface BatchableWorkSource<T> {
    public List<T> getBatch();

    public boolean hasMoreBatches();

    public int numberOfBatches();

    public void reset(int var1);

    public int getTotalSize();
}

