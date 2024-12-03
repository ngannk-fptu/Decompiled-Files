/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.data.repository.history.support;

public interface RevisionEntityInformation {
    public Class<?> getRevisionNumberType();

    public boolean isDefaultRevisionEntity();

    public Class<?> getRevisionEntityClass();
}

