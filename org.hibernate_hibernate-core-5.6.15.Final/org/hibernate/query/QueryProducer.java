/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.criteria.CriteriaDelete
 *  javax.persistence.criteria.CriteriaQuery
 *  javax.persistence.criteria.CriteriaUpdate
 */
package org.hibernate.query;

import javax.persistence.criteria.CriteriaDelete;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.CriteriaUpdate;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.query.NativeQuery;

public interface QueryProducer {
    public Query getNamedQuery(String var1);

    public Query createQuery(String var1);

    public <R> org.hibernate.query.Query<R> createQuery(String var1, Class<R> var2);

    public org.hibernate.query.Query createNamedQuery(String var1);

    public <R> org.hibernate.query.Query<R> createNamedQuery(String var1, Class<R> var2);

    @Deprecated
    default public SQLQuery createSQLQuery(String queryString) {
        NativeQuery query = this.createNativeQuery(queryString);
        query.setComment("dynamic native SQL query");
        return query;
    }

    public NativeQuery createNativeQuery(String var1);

    public <R> NativeQuery<R> createNativeQuery(String var1, Class<R> var2);

    public NativeQuery createNativeQuery(String var1, String var2);

    @Deprecated
    default public Query getNamedSQLQuery(String name) {
        return this.getNamedNativeQuery(name);
    }

    public NativeQuery getNamedNativeQuery(String var1);

    public <T> org.hibernate.query.Query<T> createQuery(CriteriaQuery<T> var1);

    public org.hibernate.query.Query createQuery(CriteriaUpdate var1);

    public org.hibernate.query.Query createQuery(CriteriaDelete var1);
}

