/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.transaction.TransactionCallback
 *  com.atlassian.sal.api.transaction.TransactionTemplate
 */
package com.atlassian.sal.core.transaction;

import com.atlassian.sal.api.transaction.TransactionCallback;
import com.atlassian.sal.api.transaction.TransactionTemplate;

public class NoOpTransactionTemplate
implements TransactionTemplate {
    public Object execute(TransactionCallback action) {
        return action.doInTransaction();
    }
}

