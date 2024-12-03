/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.spring.transaction.interceptor.TransactionalHostContextAccessor
 *  com.atlassian.confluence.spring.transaction.interceptor.TransactionalHostContextAccessor$Permission
 *  com.atlassian.confluence.spring.transaction.interceptor.TransactionalHostContextAccessor$Propagation
 *  com.google.common.base.Throwables
 *  com.google.common.util.concurrent.UncheckedExecutionException
 */
package com.atlassian.confluence.plugins.createcontent.transaction;

import com.atlassian.confluence.plugins.createcontent.transaction.Throwing2TransactionCallback;
import com.atlassian.confluence.plugins.createcontent.transaction.ThrowingTransactionCallback;
import com.atlassian.confluence.spring.transaction.interceptor.TransactionalHostContextAccessor;
import com.google.common.base.Throwables;
import com.google.common.util.concurrent.UncheckedExecutionException;

public class ThrowingTransactionTemplate {
    private final TransactionalHostContextAccessor hostContextAccessor;

    public ThrowingTransactionTemplate(TransactionalHostContextAccessor hostContextAccessor) {
        this.hostContextAccessor = hostContextAccessor;
    }

    public <T, X extends Exception> T execute(Class<X> exceptionType, TransactionalHostContextAccessor.Propagation propagation, ThrowingTransactionCallback<T, X> callback) throws X {
        return this.execute(exceptionType, propagation, TransactionalHostContextAccessor.Permission.READ_ONLY, callback);
    }

    public <T, X extends Exception> T execute(Class<X> exceptionType, TransactionalHostContextAccessor.Permission permission, ThrowingTransactionCallback<T, X> callback) throws X {
        return this.execute(exceptionType, TransactionalHostContextAccessor.Propagation.REQUIRED, permission, callback);
    }

    public <T, X extends Exception> T execute(Class<X> exceptionType, TransactionalHostContextAccessor.Propagation propagation, TransactionalHostContextAccessor.Permission permission, ThrowingTransactionCallback<T, X> callback) throws X {
        try {
            return (T)this.hostContextAccessor.doInTransaction(propagation, permission, () -> {
                try {
                    return callback.doInTransaction();
                }
                catch (Exception e) {
                    if (exceptionType.isInstance(e)) {
                        throw new UncheckedExecutionException((Throwable)e);
                    }
                    Throwables.propagateIfPossible((Throwable)e);
                    return null;
                }
            });
        }
        catch (UncheckedExecutionException e) {
            if (exceptionType.isInstance(e.getCause())) {
                throw (Exception)exceptionType.cast(e.getCause());
            }
            Throwables.propagateIfPossible((Throwable)e.getCause());
            throw e;
        }
    }

    public <T, X1 extends Exception, X2 extends Exception> T execute(Class<X1> exceptionType, Class<X2> exceptionType2, TransactionalHostContextAccessor.Propagation propagation, Throwing2TransactionCallback<T, X1, X2> callback) throws X1, X2 {
        return this.execute(exceptionType, exceptionType2, propagation, TransactionalHostContextAccessor.Permission.READ_ONLY, callback);
    }

    public <T, X1 extends Exception, X2 extends Exception> T execute(Class<X1> exceptionType, Class<X2> exceptionType2, TransactionalHostContextAccessor.Permission permission, Throwing2TransactionCallback<T, X1, X2> callback) throws X1, X2 {
        return this.execute(exceptionType, exceptionType2, TransactionalHostContextAccessor.Propagation.REQUIRED, permission, callback);
    }

    public <T, X1 extends Exception, X2 extends Exception> T execute(Class<X1> exceptionType, Class<X2> exceptionType2, TransactionalHostContextAccessor.Propagation propagation, TransactionalHostContextAccessor.Permission permission, Throwing2TransactionCallback<T, X1, X2> callback) throws X1, X2 {
        try {
            return (T)this.hostContextAccessor.doInTransaction(propagation, permission, () -> {
                try {
                    return callback.doInTransaction();
                }
                catch (Exception e) {
                    if (exceptionType.isInstance(e)) {
                        throw new UncheckedExecutionException((Throwable)e);
                    }
                    Throwables.propagateIfPossible((Throwable)e);
                    return null;
                }
            });
        }
        catch (UncheckedExecutionException e) {
            Throwables.propagateIfPossible((Throwable)e.getCause(), exceptionType, exceptionType2);
            throw e;
        }
    }
}

