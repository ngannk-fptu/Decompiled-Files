/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.EntityManager
 */
package org.hibernate.jpa;

import javax.persistence.EntityManager;
import org.hibernate.Session;
import org.hibernate.query.QueryProducer;

@Deprecated
public interface HibernateEntityManager
extends EntityManager,
QueryProducer {
    public Session getSession();
}

