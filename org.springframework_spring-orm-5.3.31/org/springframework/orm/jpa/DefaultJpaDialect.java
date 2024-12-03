/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.EntityManager
 *  javax.persistence.PersistenceException
 *  org.springframework.dao.DataAccessException
 *  org.springframework.jdbc.datasource.ConnectionHandle
 *  org.springframework.lang.Nullable
 *  org.springframework.transaction.InvalidIsolationLevelException
 *  org.springframework.transaction.TransactionDefinition
 *  org.springframework.transaction.TransactionException
 */
package org.springframework.orm.jpa;

import java.io.Serializable;
import java.sql.SQLException;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.datasource.ConnectionHandle;
import org.springframework.lang.Nullable;
import org.springframework.orm.jpa.EntityManagerFactoryUtils;
import org.springframework.orm.jpa.JpaDialect;
import org.springframework.transaction.InvalidIsolationLevelException;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionException;

public class DefaultJpaDialect
implements JpaDialect,
Serializable {
    @Override
    @Nullable
    public Object beginTransaction(EntityManager entityManager, TransactionDefinition definition) throws PersistenceException, SQLException, TransactionException {
        if (definition.getIsolationLevel() != -1) {
            throw new InvalidIsolationLevelException(this.getClass().getSimpleName() + " does not support custom isolation levels due to limitations in standard JPA. Specific arrangements may be implemented in custom JpaDialect variants.");
        }
        entityManager.getTransaction().begin();
        return null;
    }

    @Override
    @Nullable
    public Object prepareTransaction(EntityManager entityManager, boolean readOnly, @Nullable String name) throws PersistenceException {
        return null;
    }

    @Override
    public void cleanupTransaction(@Nullable Object transactionData) {
    }

    @Override
    @Nullable
    public ConnectionHandle getJdbcConnection(EntityManager entityManager, boolean readOnly) throws PersistenceException, SQLException {
        return null;
    }

    @Override
    public void releaseJdbcConnection(ConnectionHandle conHandle, EntityManager em) throws PersistenceException, SQLException {
    }

    @Nullable
    public DataAccessException translateExceptionIfPossible(RuntimeException ex) {
        return EntityManagerFactoryUtils.convertJpaAccessExceptionIfPossible(ex);
    }
}

