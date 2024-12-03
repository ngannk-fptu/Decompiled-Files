/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.engine.transaction.spi;

import java.util.concurrent.Callable;
import org.hibernate.HibernateException;
import org.hibernate.jdbc.WorkExecutorVisitable;

public interface IsolationDelegate {
    public <T> T delegateWork(WorkExecutorVisitable<T> var1, boolean var2) throws HibernateException;

    public <T> T delegateCallable(Callable<T> var1, boolean var2) throws HibernateException;
}

