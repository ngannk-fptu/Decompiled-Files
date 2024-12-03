/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.PersistenceException
 *  javax.transaction.SystemException
 */
package org.hibernate.jpa.internal;

import javax.persistence.PersistenceException;
import javax.transaction.SystemException;
import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.resource.transaction.backend.jta.internal.synchronization.ExceptionMapper;

public class ExceptionMapperLegacyJpaImpl
implements ExceptionMapper {
    public static final ExceptionMapperLegacyJpaImpl INSTANCE = new ExceptionMapperLegacyJpaImpl();

    @Override
    public RuntimeException mapStatusCheckFailure(String message, SystemException systemException, SessionImplementor session) {
        throw new PersistenceException(message, (Throwable)systemException);
    }

    @Override
    public RuntimeException mapManagedFlushFailure(String message, RuntimeException failure, SessionImplementor session) {
        if (HibernateException.class.isInstance(failure)) {
            throw session.getExceptionConverter().convert(failure);
        }
        if (PersistenceException.class.isInstance(failure)) {
            throw failure;
        }
        throw new PersistenceException(message, (Throwable)failure);
    }
}

