/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.vault.core;

import org.springframework.lang.Nullable;
import org.springframework.web.client.RestOperations;

@FunctionalInterface
public interface RestOperationsCallback<T> {
    @Nullable
    public T doWithRestOperations(RestOperations var1);
}

