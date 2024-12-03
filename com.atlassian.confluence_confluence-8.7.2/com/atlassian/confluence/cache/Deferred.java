/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.cache;

interface Deferred {
    public String getName();

    public String getType();

    public boolean hasDeferredOperations();

    public void sync();

    public void clear();
}

