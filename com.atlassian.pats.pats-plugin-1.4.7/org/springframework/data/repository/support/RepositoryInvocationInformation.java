/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.data.repository.support;

public interface RepositoryInvocationInformation {
    public boolean hasSaveMethod();

    public boolean hasDeleteMethod();

    public boolean hasFindOneMethod();

    public boolean hasFindAllMethod();
}

