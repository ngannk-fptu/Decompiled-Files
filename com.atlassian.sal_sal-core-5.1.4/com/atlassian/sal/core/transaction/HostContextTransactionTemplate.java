/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.transaction.TransactionCallback
 *  com.atlassian.sal.api.transaction.TransactionTemplate
 *  com.atlassian.sal.spi.HostContextAccessor
 *  com.atlassian.sal.spi.HostContextAccessor$HostTransactionCallback
 */
package com.atlassian.sal.core.transaction;

import com.atlassian.sal.api.transaction.TransactionCallback;
import com.atlassian.sal.api.transaction.TransactionTemplate;
import com.atlassian.sal.spi.HostContextAccessor;

public class HostContextTransactionTemplate
implements TransactionTemplate {
    private final HostContextAccessor hostContentAccessor;

    public HostContextTransactionTemplate(HostContextAccessor hostContentAccessor) {
        this.hostContentAccessor = hostContentAccessor;
    }

    public Object execute(final TransactionCallback action) {
        return this.hostContentAccessor.doInTransaction(new HostContextAccessor.HostTransactionCallback(){

            public Object doInTransaction() {
                return action.doInTransaction();
            }
        });
    }
}

