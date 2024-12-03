/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.jpa.spi;

import org.hibernate.jpa.HibernateEntityManagerFactory;

@Deprecated
public interface HibernateEntityManagerFactoryAware {
    public HibernateEntityManagerFactory getFactory();
}

