/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.impl.hibernate.extras.ExportHibernateHandle
 *  com.atlassian.confluence.impl.hibernate.extras.HibernateTranslator
 *  com.google.common.base.Function
 *  org.checkerframework.checker.nullness.qual.Nullable
 *  org.hibernate.Hibernate
 *  org.hibernate.LockMode
 *  org.hibernate.Session
 *  org.hibernate.engine.spi.SessionFactoryImplementor
 *  org.hibernate.engine.spi.SharedSessionContractImplementor
 *  org.hibernate.persister.entity.EntityPersister
 */
package com.atlassian.confluence.importexport.impl;

import com.atlassian.confluence.core.persistence.hibernate.TransientHibernateHandle;
import com.atlassian.confluence.impl.hibernate.extras.ExportHibernateHandle;
import com.atlassian.confluence.impl.hibernate.extras.HibernateTranslator;
import com.atlassian.confluence.mail.notification.Notification;
import com.google.common.base.Function;
import java.io.Serializable;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.hibernate.Hibernate;
import org.hibernate.LockMode;
import org.hibernate.Session;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.persister.entity.EntityPersister;

public class HibernateObjectHandleTranslator
implements HibernateTranslator {
    private final Session session;

    public HibernateObjectHandleTranslator(Session session) {
        this.session = session;
    }

    public TransientHibernateHandle objectOrHandleToHandle(Object object) {
        return object instanceof TransientHibernateHandle ? (TransientHibernateHandle)object : this.objectToHandle(object);
    }

    public Object objectOrHandleToObject(Object object) {
        return object instanceof TransientHibernateHandle ? this.handleToObject((TransientHibernateHandle)object) : object;
    }

    public TransientHibernateHandle objectToHandle(Object object) {
        Class clazz = Hibernate.getClass((Object)object);
        EntityPersister persister = ((SessionFactoryImplementor)this.session.getSessionFactory()).getMetamodel().entityPersister(clazz.getName());
        return TransientHibernateHandle.create(clazz, persister.getIdentifier(object, (SharedSessionContractImplementor)this.session));
    }

    @Deprecated
    public Function<Object, TransientHibernateHandle> objectToHandle() {
        return this::objectToHandle;
    }

    public Object handleToObject(ExportHibernateHandle handle) {
        return this.session.get(handle.getClazz(), ((TransientHibernateHandle)handle).getId(), LockMode.NONE);
    }

    @Deprecated
    public Function<Long, TransientHibernateHandle> idToHandleFunction(Class<Notification> notificationClass) {
        return this.idToHandle(notificationClass);
    }

    @Deprecated
    public <T> Function<T, TransientHibernateHandle> idToHandle(Class<?> clazz) {
        return new IdToHandleFunction(clazz);
    }

    public <T> java.util.function.Function<T, TransientHibernateHandle> idToHandleTransformer(Class<?> clazz) {
        return arg_0 -> this.idToHandle(clazz).apply(arg_0);
    }

    private static class IdToHandleFunction<T>
    implements Function<T, TransientHibernateHandle> {
        private final Class<?> clazz;

        public IdToHandleFunction(Class<?> clazz) {
            this.clazz = clazz;
        }

        public TransientHibernateHandle apply(@Nullable T id) {
            if (id == null) {
                return null;
            }
            return TransientHibernateHandle.create(this.clazz, (Serializable)id);
        }
    }
}

