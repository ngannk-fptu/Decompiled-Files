/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate;

import java.util.List;
import org.hibernate.CacheMode;
import org.hibernate.FetchMode;
import org.hibernate.FlushMode;
import org.hibernate.HibernateException;
import org.hibernate.LockMode;
import org.hibernate.ScrollMode;
import org.hibernate.ScrollableResults;
import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projection;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.ResultTransformer;

public interface Criteria
extends CriteriaSpecification {
    public String getAlias();

    public Criteria setProjection(Projection var1);

    public Criteria add(Criterion var1);

    public Criteria addOrder(Order var1);

    public Criteria setFetchMode(String var1, FetchMode var2) throws HibernateException;

    public Criteria setLockMode(LockMode var1);

    public Criteria setLockMode(String var1, LockMode var2);

    public Criteria createAlias(String var1, String var2) throws HibernateException;

    public Criteria createAlias(String var1, String var2, JoinType var3) throws HibernateException;

    @Deprecated
    public Criteria createAlias(String var1, String var2, int var3) throws HibernateException;

    public Criteria createAlias(String var1, String var2, JoinType var3, Criterion var4) throws HibernateException;

    @Deprecated
    public Criteria createAlias(String var1, String var2, int var3, Criterion var4) throws HibernateException;

    public Criteria createCriteria(String var1) throws HibernateException;

    public Criteria createCriteria(String var1, JoinType var2) throws HibernateException;

    @Deprecated
    public Criteria createCriteria(String var1, int var2) throws HibernateException;

    public Criteria createCriteria(String var1, String var2) throws HibernateException;

    public Criteria createCriteria(String var1, String var2, JoinType var3) throws HibernateException;

    @Deprecated
    public Criteria createCriteria(String var1, String var2, int var3) throws HibernateException;

    public Criteria createCriteria(String var1, String var2, JoinType var3, Criterion var4) throws HibernateException;

    @Deprecated
    public Criteria createCriteria(String var1, String var2, int var3, Criterion var4) throws HibernateException;

    public Criteria setResultTransformer(ResultTransformer var1);

    public Criteria setMaxResults(int var1);

    public Criteria setFirstResult(int var1);

    public boolean isReadOnlyInitialized();

    public boolean isReadOnly();

    public Criteria setReadOnly(boolean var1);

    public Criteria setFetchSize(int var1);

    public Criteria setTimeout(int var1);

    public Criteria setCacheable(boolean var1);

    public Criteria setCacheRegion(String var1);

    public Criteria setComment(String var1);

    public Criteria addQueryHint(String var1);

    public Criteria setFlushMode(FlushMode var1);

    public Criteria setCacheMode(CacheMode var1);

    public List list() throws HibernateException;

    public ScrollableResults scroll() throws HibernateException;

    public ScrollableResults scroll(ScrollMode var1) throws HibernateException;

    public Object uniqueResult() throws HibernateException;
}

