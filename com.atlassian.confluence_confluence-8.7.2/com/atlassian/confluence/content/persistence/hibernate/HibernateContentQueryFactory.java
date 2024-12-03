/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.EntityManager
 *  javax.persistence.PersistenceException
 *  javax.persistence.Query
 */
package com.atlassian.confluence.content.persistence.hibernate;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;
import javax.persistence.Query;

public interface HibernateContentQueryFactory {
    public Query getQuery(EntityManager var1, Object ... var2) throws PersistenceException;
}

