/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.crowd.model.page;

public interface Page<T> {
    public Iterable<T> getResults();

    public int getSize();

    public int getStart();

    public int getLimit();

    public boolean isLastPage();
}

