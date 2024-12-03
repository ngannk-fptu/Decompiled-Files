/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.core;

import java.util.List;

public interface ListBuilder<T>
extends Iterable<List<T>> {
    public List<T> getRange(int var1, int var2);

    public List<T> getPage(int var1, int var2);

    public int getAvailableSize();
}

