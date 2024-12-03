/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.Query
 */
package org.hibernate.jpa;

import org.hibernate.Query;

public interface HibernateQuery
extends javax.persistence.Query {
    public Query getHibernateQuery();
}

