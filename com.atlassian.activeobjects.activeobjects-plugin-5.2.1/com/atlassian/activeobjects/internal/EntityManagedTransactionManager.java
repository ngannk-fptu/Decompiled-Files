/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.transaction.TransactionCallback
 *  com.google.common.base.Preconditions
 */
package com.atlassian.activeobjects.internal;

import com.atlassian.activeobjects.internal.AbstractLoggingTransactionManager;
import com.atlassian.sal.api.transaction.TransactionCallback;
import com.google.common.base.Preconditions;
import java.sql.SQLException;
import net.java.ao.ActiveObjectsException;
import net.java.ao.EntityManager;
import net.java.ao.Transaction;

final class EntityManagedTransactionManager
extends AbstractLoggingTransactionManager {
    private final EntityManager entityManager;

    EntityManagedTransactionManager(EntityManager entityManager) {
        this.entityManager = (EntityManager)Preconditions.checkNotNull((Object)entityManager);
    }

    @Override
    <T> T inTransaction(final TransactionCallback<T> callback) {
        try {
            return new Transaction<T>(this.entityManager){

                @Override
                public T run() {
                    return callback.doInTransaction();
                }
            }.execute();
        }
        catch (SQLException e) {
            throw new ActiveObjectsException(e);
        }
    }
}

