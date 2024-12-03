/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.bonnie.Handle
 *  com.atlassian.confluence.impl.hibernate.extras.ExportHibernateHandle
 *  org.apache.commons.lang3.tuple.Pair
 *  org.hibernate.Hibernate
 *  org.hibernate.LockMode
 *  org.hibernate.Session
 *  org.hibernate.engine.spi.SessionFactoryImplementor
 *  org.hibernate.engine.spi.SharedSessionContractImplementor
 *  org.hibernate.persister.entity.EntityPersister
 */
package com.atlassian.confluence.core.persistence.hibernate;

import com.atlassian.bonnie.Handle;
import com.atlassian.confluence.impl.hibernate.extras.ExportHibernateHandle;
import java.io.Serializable;
import java.util.Arrays;
import org.apache.commons.lang3.tuple.Pair;
import org.hibernate.Hibernate;
import org.hibernate.LockMode;
import org.hibernate.Session;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.persister.entity.EntityPersister;

@Deprecated
public class TransientHibernateHandle
implements Handle,
ExportHibernateHandle {
    protected final Object storage;
    private int hashCode = -1;

    private TransientHibernateHandle(Object storage) {
        this.storage = storage;
    }

    public static TransientHibernateHandle create(Class clazz, Serializable id) {
        return id instanceof Long ? new LongTransientHibernateHandle(clazz, (Long)id) : new ObjectTransientHibernateHandle(clazz, id);
    }

    public Class getClazz() {
        return (Class)((Pair)this.storage).getLeft();
    }

    public Serializable getId() {
        return (Serializable)((Pair)this.storage).getRight();
    }

    public Object get(Session session) {
        return session.get(this.getClazz(), this.getId(), LockMode.NONE);
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof TransientHibernateHandle)) {
            return false;
        }
        TransientHibernateHandle theOtherObj = (TransientHibernateHandle)obj;
        if (!this.getClazz().equals(theOtherObj.getClazz())) {
            return false;
        }
        if (this.getId() instanceof Object[] && theOtherObj.getId() instanceof Object[]) {
            return Arrays.equals((Object[])this.getId(), (Object[])theOtherObj.getId());
        }
        return this.getId() == null ? theOtherObj.getId() == null : this.getId().equals(theOtherObj.getId());
    }

    public int hashCode() {
        if (this.hashCode == -1) {
            int result = this.getClazz().hashCode();
            this.hashCode = 29 * result + (this.getId() != null ? TransientHibernateHandle.computeObjectArrayHashcode(this.getId()) : 0);
        }
        return this.hashCode;
    }

    public String toString() {
        return "class = " + this.getClazz().getName() + ", id = " + this.getId();
    }

    private static int computeObjectArrayHashcode(Object obj) {
        if (obj instanceof Object[]) {
            Object[] objectArray = (Object[])obj;
            int result = 29 * objectArray.getClass().hashCode();
            for (int i = 0; i < objectArray.length; ++i) {
                Object element = objectArray[i];
                result += element != null ? element.hashCode() : 0;
            }
            return result;
        }
        return obj.hashCode();
    }

    private static Object getId(Session session, Object object) {
        Class clazz = Hibernate.getClass((Object)object);
        EntityPersister persister = ((SessionFactoryImplementor)session.getSessionFactory()).getMetamodel().entityPersister(clazz.getName());
        return persister.getIdentifier(object, (SharedSessionContractImplementor)session);
    }

    @Deprecated
    public TransientHibernateHandle(Class clazz, Object id) {
        this.storage = Pair.of((Object)clazz, (Object)((Serializable)id));
    }

    @Deprecated
    public TransientHibernateHandle(Session session, Object object) {
        this(Hibernate.getClass((Object)object), TransientHibernateHandle.getId(session, object));
    }

    private static class ObjectTransientHibernateHandle
    extends TransientHibernateHandle {
        private final Serializable id;

        public ObjectTransientHibernateHandle(Class clazz, Serializable id) {
            super(clazz);
            this.id = id;
        }

        @Override
        public Class getClazz() {
            return (Class)this.storage;
        }

        @Override
        public Serializable getId() {
            return this.id;
        }

        @Override
        public boolean equals(Object obj) {
            return super.equals(obj);
        }

        @Override
        public int hashCode() {
            return super.hashCode();
        }
    }

    private static class LongTransientHibernateHandle
    extends TransientHibernateHandle {
        private final long id;

        public LongTransientHibernateHandle(Class clazz, Long id) {
            super(clazz);
            this.id = id;
        }

        @Override
        public Class getClazz() {
            return (Class)this.storage;
        }

        @Override
        public Serializable getId() {
            return Long.valueOf(this.id);
        }

        @Override
        public boolean equals(Object obj) {
            return super.equals(obj);
        }

        @Override
        public int hashCode() {
            return super.hashCode();
        }
    }
}

