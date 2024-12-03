/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.ParametersAreNonnullByDefault
 *  org.hibernate.Session
 *  org.hibernate.SessionFactory
 *  org.hibernate.Transaction
 *  org.hibernate.resource.transaction.spi.TransactionStatus
 */
package com.atlassian.migration.agent.store.jpa.impl;

import com.atlassian.migration.agent.store.jpa.SessionFactorySupplier;
import com.atlassian.migration.agent.store.jpa.impl.ThreadBoundSessionContext;
import com.atlassian.migration.agent.store.tx.PluginTransactionTemplate;
import com.atlassian.migration.agent.store.tx.TransactionException;
import java.util.function.Supplier;
import javax.annotation.ParametersAreNonnullByDefault;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.resource.transaction.spi.TransactionStatus;

@ParametersAreNonnullByDefault
public final class DefaultPluginTransactionTemplate
implements PluginTransactionTemplate {
    private final SessionFactorySupplier sessionFactorySupplier;

    public DefaultPluginTransactionTemplate(SessionFactorySupplier sessionFactorySupplier) {
        this.sessionFactorySupplier = sessionFactorySupplier;
    }

    @Override
    public <T> T on(boolean readonly, Supplier<T> action) {
        Session session = ((SessionFactory)this.sessionFactorySupplier.get()).getCurrentSession();
        Transaction transaction = session.getTransaction();
        if (transaction.getStatus() == TransactionStatus.ACTIVE) {
            return action.get();
        }
        try {
            transaction.begin();
            T result = action.get();
            transaction.commit();
            T t = result;
            return t;
        }
        catch (RuntimeException e) {
            if (transaction.getStatus() == TransactionStatus.ACTIVE) {
                transaction.rollback();
            }
            throw e;
        }
        catch (Exception e) {
            if (transaction.getStatus() == TransactionStatus.ACTIVE) {
                transaction.rollback();
            }
            throw new TransactionException("Error occurred when executing transaction", e);
        }
        finally {
            ThreadBoundSessionContext.unbind(session.getSessionFactory());
            session.close();
        }
    }
}

