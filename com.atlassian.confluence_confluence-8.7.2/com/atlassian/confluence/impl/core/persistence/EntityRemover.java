/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.impl.core.persistence;

public interface EntityRemover {
    public <T> int removeAllPersistentObjectsByType(Class<T> var1);
}

