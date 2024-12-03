/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate;

import java.io.Serializable;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Transaction;
import org.hibernate.jdbc.ReturningWork;
import org.hibernate.jdbc.Work;
import org.hibernate.procedure.ProcedureCall;
import org.hibernate.query.Query;
import org.hibernate.query.QueryProducer;

public interface SharedSessionContract
extends QueryProducer,
Serializable {
    public String getTenantIdentifier();

    public void close() throws HibernateException;

    public boolean isOpen();

    public boolean isConnected();

    public Transaction beginTransaction();

    public Transaction getTransaction();

    @Override
    public Query createQuery(String var1);

    @Override
    public Query getNamedQuery(String var1);

    public ProcedureCall getNamedProcedureCall(String var1);

    public ProcedureCall createStoredProcedureCall(String var1);

    public ProcedureCall createStoredProcedureCall(String var1, Class ... var2);

    public ProcedureCall createStoredProcedureCall(String var1, String ... var2);

    @Deprecated
    public Criteria createCriteria(Class var1);

    @Deprecated
    public Criteria createCriteria(Class var1, String var2);

    @Deprecated
    public Criteria createCriteria(String var1);

    @Deprecated
    public Criteria createCriteria(String var1, String var2);

    public Integer getJdbcBatchSize();

    public void setJdbcBatchSize(Integer var1);

    default public void doWork(Work work) throws HibernateException {
        throw new UnsupportedOperationException("The doWork method has not been implemented in this implementation of org.hibernate.engine.spi.SharedSessionContractImplemento");
    }

    default public <T> T doReturningWork(ReturningWork<T> work) throws HibernateException {
        throw new UnsupportedOperationException("The doReturningWork method has not been implemented in this implementation of org.hibernate.engine.spi.SharedSessionContractImplemento");
    }
}

