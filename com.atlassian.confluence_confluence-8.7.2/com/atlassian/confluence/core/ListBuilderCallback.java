/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.core;

import java.util.List;

public interface ListBuilderCallback<T> {
    public List<T> getElements(int var1, int var2);

    public int getAvailableSize();
}

