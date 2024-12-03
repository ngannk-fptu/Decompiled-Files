/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate;

import java.util.Iterator;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.bytecode.enhance.spi.interceptor.BytecodeLazyAttributeInterceptor;
import org.hibernate.bytecode.enhance.spi.interceptor.EnhancementAsProxyLazinessInterceptor;
import org.hibernate.collection.spi.LazyInitializable;
import org.hibernate.engine.HibernateIterator;
import org.hibernate.engine.internal.ManagedTypeHelper;
import org.hibernate.engine.jdbc.LobCreator;
import org.hibernate.engine.jdbc.spi.JdbcServices;
import org.hibernate.engine.spi.PersistentAttributeInterceptor;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.proxy.HibernateProxy;
import org.hibernate.proxy.LazyInitializer;

public final class Hibernate {
    private Hibernate() {
        throw new UnsupportedOperationException();
    }

    public static void initialize(Object proxy) throws HibernateException {
        PersistentAttributeInterceptor interceptor;
        if (proxy == null) {
            return;
        }
        if (proxy instanceof HibernateProxy) {
            ((HibernateProxy)proxy).getHibernateLazyInitializer().initialize();
        } else if (proxy instanceof LazyInitializable) {
            ((LazyInitializable)proxy).forceInitialization();
        } else if (ManagedTypeHelper.isPersistentAttributeInterceptable(proxy) && (interceptor = ManagedTypeHelper.asPersistentAttributeInterceptable(proxy).$$_hibernate_getInterceptor()) instanceof EnhancementAsProxyLazinessInterceptor) {
            ((EnhancementAsProxyLazinessInterceptor)interceptor).forceInitialize(proxy, null);
        }
    }

    public static boolean isInitialized(Object proxy) {
        if (proxy instanceof HibernateProxy) {
            return !((HibernateProxy)proxy).getHibernateLazyInitializer().isUninitialized();
        }
        if (ManagedTypeHelper.isPersistentAttributeInterceptable(proxy)) {
            PersistentAttributeInterceptor interceptor = ManagedTypeHelper.asPersistentAttributeInterceptable(proxy).$$_hibernate_getInterceptor();
            return !(interceptor instanceof EnhancementAsProxyLazinessInterceptor);
        }
        if (proxy instanceof LazyInitializable) {
            return ((LazyInitializable)proxy).wasInitialized();
        }
        return true;
    }

    public static Class getClass(Object proxy) {
        if (proxy instanceof HibernateProxy) {
            return ((HibernateProxy)proxy).getHibernateLazyInitializer().getImplementation().getClass();
        }
        return proxy.getClass();
    }

    public static LobCreator getLobCreator(Session session) {
        return Hibernate.getLobCreator((SessionImplementor)session);
    }

    public static LobCreator getLobCreator(SharedSessionContractImplementor session) {
        return session.getFactory().getServiceRegistry().getService(JdbcServices.class).getLobCreator(session);
    }

    public static LobCreator getLobCreator(SessionImplementor session) {
        return session.getFactory().getServiceRegistry().getService(JdbcServices.class).getLobCreator(session);
    }

    public static void close(Iterator iterator) throws HibernateException {
        if (!(iterator instanceof HibernateIterator)) {
            throw new IllegalArgumentException("not a Hibernate iterator");
        }
        ((HibernateIterator)iterator).close();
    }

    public static boolean isPropertyInitialized(Object proxy, String propertyName) {
        PersistentAttributeInterceptor interceptor;
        Object entity;
        if (proxy instanceof HibernateProxy) {
            LazyInitializer li = ((HibernateProxy)proxy).getHibernateLazyInitializer();
            if (li.isUninitialized()) {
                return false;
            }
            entity = li.getImplementation();
        } else {
            entity = proxy;
        }
        if (ManagedTypeHelper.isPersistentAttributeInterceptable(entity) && (interceptor = ManagedTypeHelper.asPersistentAttributeInterceptable(entity).$$_hibernate_getInterceptor()) instanceof BytecodeLazyAttributeInterceptor) {
            return ((BytecodeLazyAttributeInterceptor)interceptor).isAttributeLoaded(propertyName);
        }
        return true;
    }

    public static Object unproxy(Object proxy) {
        if (proxy instanceof HibernateProxy) {
            HibernateProxy hibernateProxy = (HibernateProxy)proxy;
            LazyInitializer initializer = hibernateProxy.getHibernateLazyInitializer();
            return initializer.getImplementation();
        }
        return proxy;
    }

    public static <T> T unproxy(T proxy, Class<T> entityClass) {
        return entityClass.cast(Hibernate.unproxy(proxy));
    }
}

