/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.lang.Nullable
 *  org.springframework.web.client.RestOperations
 */
package org.springframework.vault.core;

import org.springframework.lang.Nullable;
import org.springframework.web.client.RestOperations;

@FunctionalInterface
public interface RestOperationsCallback<T> {
    @Nullable
    public T doWithRestOperations(RestOperations var1);
}

