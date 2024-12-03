/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.sal.spi;

import java.util.Map;

public interface HostContextAccessor {
    public <T> Map<String, T> getComponentsOfType(Class<T> var1);

    public <T> T doInTransaction(HostTransactionCallback<T> var1);

    public static interface HostTransactionCallback<T> {
        public T doInTransaction();
    }
}

