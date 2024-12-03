/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.internal;

import java.util.Iterator;
import org.hibernate.EntityNameResolver;
import org.hibernate.Interceptor;
import org.hibernate.engine.spi.SessionFactoryImplementor;

public class CoordinatingEntityNameResolver
implements EntityNameResolver {
    private final SessionFactoryImplementor sessionFactory;
    private final Interceptor interceptor;

    public CoordinatingEntityNameResolver(SessionFactoryImplementor sessionFactory, Interceptor interceptor) {
        this.sessionFactory = sessionFactory;
        this.interceptor = interceptor;
    }

    @Override
    public String resolveEntityName(Object entity) {
        EntityNameResolver resolver;
        String entityName = this.interceptor.getEntityName(entity);
        if (entityName != null) {
            return entityName;
        }
        Iterator<EntityNameResolver> iterator = this.sessionFactory.getMetamodel().getEntityNameResolvers().iterator();
        while (iterator.hasNext() && (entityName = (resolver = iterator.next()).resolveEntityName(entity)) == null) {
        }
        if (entityName != null) {
            return entityName;
        }
        return entity.getClass().getName();
    }
}

