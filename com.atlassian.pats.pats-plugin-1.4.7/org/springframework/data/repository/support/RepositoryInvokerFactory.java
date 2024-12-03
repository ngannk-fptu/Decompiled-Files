/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.data.repository.support;

import org.springframework.data.repository.support.RepositoryInvoker;

public interface RepositoryInvokerFactory {
    public RepositoryInvoker getInvokerFor(Class<?> var1);
}

