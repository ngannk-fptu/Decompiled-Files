/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.EntityManagerFactory
 *  org.hibernate.SessionFactory
 *  org.springframework.beans.factory.FactoryBean
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 *  org.springframework.util.ReflectionUtils
 */
package org.springframework.orm.jpa.vendor;

import java.lang.reflect.Method;
import javax.persistence.EntityManagerFactory;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.lang.Nullable;
import org.springframework.orm.jpa.EntityManagerFactoryAccessor;
import org.springframework.util.Assert;
import org.springframework.util.ReflectionUtils;

@Deprecated
public class HibernateJpaSessionFactoryBean
extends EntityManagerFactoryAccessor
implements FactoryBean<SessionFactory> {
    @Nullable
    public SessionFactory getObject() {
        EntityManagerFactory emf = this.getEntityManagerFactory();
        Assert.state((emf != null ? 1 : 0) != 0, (String)"EntityManagerFactory must not be null");
        try {
            Method getSessionFactory = emf.getClass().getMethod("getSessionFactory", new Class[0]);
            return (SessionFactory)ReflectionUtils.invokeMethod((Method)getSessionFactory, (Object)emf);
        }
        catch (NoSuchMethodException ex) {
            throw new IllegalStateException("No compatible Hibernate EntityManagerFactory found: " + ex);
        }
    }

    public Class<?> getObjectType() {
        return SessionFactory.class;
    }

    public boolean isSingleton() {
        return true;
    }
}

