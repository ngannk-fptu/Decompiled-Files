/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.spi.HostContextAccessor
 *  com.atlassian.sal.spi.HostContextAccessor$HostTransactionCallback
 */
package com.atlassian.confluence.spring.transaction.interceptor;

import com.atlassian.sal.spi.HostContextAccessor;

public interface TransactionalHostContextAccessor
extends HostContextAccessor {
    public <T> T doInTransaction(Propagation var1, HostContextAccessor.HostTransactionCallback<T> var2);

    public <T> T doInTransaction(Permission var1, HostContextAccessor.HostTransactionCallback<T> var2);

    public <T> T doInTransaction(Propagation var1, Permission var2, HostContextAccessor.HostTransactionCallback<T> var3);

    public static enum Permission {
        READ_ONLY,
        READ_WRITE;

    }

    public static enum Propagation {
        REQUIRED,
        REQUIRES_NEW,
        MANDATORY;

    }
}

