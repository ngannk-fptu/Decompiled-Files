/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.proxy;

import org.hibernate.proxy.HibernateProxy;
import org.hibernate.proxy.LazyInitializer;

public final class HibernateProxyHelper {
    public static Class getClassWithoutInitializingProxy(Object object) {
        if (object instanceof HibernateProxy) {
            HibernateProxy proxy = (HibernateProxy)object;
            LazyInitializer li = proxy.getHibernateLazyInitializer();
            return li.getPersistentClass();
        }
        return object.getClass();
    }

    private HibernateProxyHelper() {
    }
}

