/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.criterion;

import java.io.Serializable;
import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.LockMode;
import org.hibernate.Session;
import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projection;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.internal.CriteriaImpl;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.ResultTransformer;

public class DetachedCriteria
implements CriteriaSpecification,
Serializable {
    private final CriteriaImpl impl;
    private final Criteria criteria;

    protected DetachedCriteria(String entityName) {
        this.impl = new CriteriaImpl(entityName, null);
        this.criteria = this.impl;
    }

    protected DetachedCriteria(String entityName, String alias) {
        this.impl = new CriteriaImpl(entityName, alias, null);
        this.criteria = this.impl;
    }

    protected DetachedCriteria(CriteriaImpl impl, Criteria criteria) {
        this.impl = impl;
        this.criteria = criteria;
    }

    public Criteria getExecutableCriteria(Session session) {
        this.impl.setSession((SessionImplementor)session);
        return this.impl;
    }

    public String getAlias() {
        return this.criteria.getAlias();
    }

    CriteriaImpl getCriteriaImpl() {
        return this.impl;
    }

    public static DetachedCriteria forEntityName(String entityName) {
        return new DetachedCriteria(entityName);
    }

    public static DetachedCriteria forEntityName(String entityName, String alias) {
        return new DetachedCriteria(entityName, alias);
    }

    public static DetachedCriteria forClass(Class clazz) {
        return new DetachedCriteria(clazz.getName());
    }

    public static DetachedCriteria forClass(Class clazz, String alias) {
        return new DetachedCriteria(clazz.getName(), alias);
    }

    public DetachedCriteria add(Criterion criterion) {
        this.criteria.add(criterion);
        return this;
    }

    public DetachedCriteria addOrder(Order order) {
        this.criteria.addOrder(order);
        return this;
    }

    public DetachedCriteria setFetchMode(String associationPath, FetchMode mode) {
        this.criteria.setFetchMode(associationPath, mode);
        return this;
    }

    public DetachedCriteria setProjection(Projection projection) {
        this.criteria.setProjection(projection);
        return this;
    }

    public DetachedCriteria setResultTransformer(ResultTransformer resultTransformer) {
        this.criteria.setResultTransformer(resultTransformer);
        return this;
    }

    public DetachedCriteria createAlias(String associationPath, String alias) {
        this.criteria.createAlias(associationPath, alias);
        return this;
    }

    public DetachedCriteria createAlias(String associationPath, String alias, JoinType joinType) {
        this.criteria.createAlias(associationPath, alias, joinType);
        return this;
    }

    public DetachedCriteria createAlias(String associationPath, String alias, JoinType joinType, Criterion withClause) {
        this.criteria.createAlias(associationPath, alias, joinType, withClause);
        return this;
    }

    @Deprecated
    public DetachedCriteria createAlias(String associationPath, String alias, int joinType) {
        return this.createAlias(associationPath, alias, JoinType.parse(joinType));
    }

    @Deprecated
    public DetachedCriteria createAlias(String associationPath, String alias, int joinType, Criterion withClause) {
        return this.createAlias(associationPath, alias, JoinType.parse(joinType), withClause);
    }

    public DetachedCriteria createCriteria(String associationPath, String alias) {
        return new DetachedCriteria(this.impl, this.criteria.createCriteria(associationPath, alias));
    }

    public DetachedCriteria createCriteria(String associationPath) {
        return new DetachedCriteria(this.impl, this.criteria.createCriteria(associationPath));
    }

    public DetachedCriteria createCriteria(String associationPath, JoinType joinType) {
        return new DetachedCriteria(this.impl, this.criteria.createCriteria(associationPath, joinType));
    }

    public DetachedCriteria createCriteria(String associationPath, String alias, JoinType joinType) {
        return new DetachedCriteria(this.impl, this.criteria.createCriteria(associationPath, alias, joinType));
    }

    public DetachedCriteria createCriteria(String associationPath, String alias, JoinType joinType, Criterion withClause) {
        return new DetachedCriteria(this.impl, this.criteria.createCriteria(associationPath, alias, joinType, withClause));
    }

    @Deprecated
    public DetachedCriteria createCriteria(String associationPath, int joinType) {
        return this.createCriteria(associationPath, JoinType.parse(joinType));
    }

    @Deprecated
    public DetachedCriteria createCriteria(String associationPath, String alias, int joinType) {
        return this.createCriteria(associationPath, alias, JoinType.parse(joinType));
    }

    @Deprecated
    public DetachedCriteria createCriteria(String associationPath, String alias, int joinType, Criterion withClause) {
        return this.createCriteria(associationPath, alias, JoinType.parse(joinType), withClause);
    }

    public DetachedCriteria setComment(String comment) {
        this.criteria.setComment(comment);
        return this;
    }

    public DetachedCriteria setLockMode(LockMode lockMode) {
        this.criteria.setLockMode(lockMode);
        return this;
    }

    public DetachedCriteria setLockMode(String alias, LockMode lockMode) {
        this.criteria.setLockMode(alias, lockMode);
        return this;
    }

    public DetachedCriteria setTimeout(int timeout) {
        this.criteria.setTimeout(timeout);
        return this;
    }

    public String toString() {
        return "DetachableCriteria(" + this.criteria.toString() + ')';
    }
}

