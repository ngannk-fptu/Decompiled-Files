/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.action.spi;

import java.io.Serializable;
import org.hibernate.HibernateException;
import org.hibernate.action.spi.AfterTransactionCompletionProcess;
import org.hibernate.action.spi.BeforeTransactionCompletionProcess;
import org.hibernate.engine.spi.SharedSessionContractImplementor;

public interface Executable {
    public Serializable[] getPropertySpaces();

    public void beforeExecutions() throws HibernateException;

    public void execute() throws HibernateException;

    public AfterTransactionCompletionProcess getAfterTransactionCompletionProcess();

    public BeforeTransactionCompletionProcess getBeforeTransactionCompletionProcess();

    public void afterDeserialize(SharedSessionContractImplementor var1);
}

