/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.migration.agent.store.jpa;

public interface Pageable {
    public int getPageNumber();

    public int getPageSize();

    public Pageable next();
}

