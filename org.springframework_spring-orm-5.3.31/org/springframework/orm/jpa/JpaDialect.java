/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.EntityManager
 *  javax.persistence.PersistenceException
 *  org.springframework.dao.support.PersistenceExceptionTranslator
 *  org.springframework.jdbc.datasource.ConnectionHandle
 *  org.springframework.lang.Nullable
 *  org.springframework.transaction.TransactionDefinition
 *  org.springframework.transaction.TransactionException
 */
package org.springframework.orm.jpa;

import java.sql.SQLException;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;
import org.springframework.dao.support.PersistenceExceptionTranslator;
import org.springframework.jdbc.datasource.ConnectionHandle;
import org.springframework.lang.Nullable;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionException;

public interface JpaDialect
extends PersistenceExceptionTranslator {
    @Nullable
    public Object beginTransaction(EntityManager var1, TransactionDefinition var2) throws PersistenceException, SQLException, TransactionException;

    @Nullable
    public Object prepareTransaction(EntityManager var1, boolean var2, @Nullable String var3) throws PersistenceException;

    public void cleanupTransaction(@Nullable Object var1);

    @Nullable
    public ConnectionHandle getJdbcConnection(EntityManager var1, boolean var2) throws PersistenceException, SQLException;

    public void releaseJdbcConnection(ConnectionHandle var1, EntityManager var2) throws PersistenceException, SQLException;
}

