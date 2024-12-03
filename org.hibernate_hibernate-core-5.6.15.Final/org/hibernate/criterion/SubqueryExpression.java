/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.criterion;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.criterion.CriteriaQuery;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.engine.spi.QueryParameters;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.engine.spi.TypedValue;
import org.hibernate.internal.CriteriaImpl;
import org.hibernate.loader.criteria.CriteriaJoinWalker;
import org.hibernate.loader.criteria.CriteriaQueryTranslator;
import org.hibernate.persister.entity.OuterJoinLoadable;
import org.hibernate.type.Type;

public abstract class SubqueryExpression
implements Criterion {
    private CriteriaImpl criteriaImpl;
    private String quantifier;
    private String op;
    private QueryParameters params;
    private Type[] types;
    private CriteriaQueryTranslator innerQuery;

    protected SubqueryExpression(String op, String quantifier, DetachedCriteria dc) {
        this.criteriaImpl = dc.getCriteriaImpl();
        this.quantifier = quantifier;
        this.op = op;
    }

    protected Type[] getTypes() {
        return this.types;
    }

    protected abstract String toLeftSqlString(Criteria var1, CriteriaQuery var2);

    @Override
    public String toSqlString(Criteria criteria, CriteriaQuery criteriaQuery) throws HibernateException {
        StringBuilder buf = new StringBuilder(this.toLeftSqlString(criteria, criteriaQuery));
        if (this.op != null) {
            buf.append(' ').append(this.op).append(' ');
        }
        if (this.quantifier != null) {
            buf.append(this.quantifier).append(' ');
        }
        SessionFactoryImplementor factory = criteriaQuery.getFactory();
        OuterJoinLoadable persister = (OuterJoinLoadable)factory.getMetamodel().entityPersister(this.criteriaImpl.getEntityOrClassName());
        this.createAndSetInnerQuery(criteriaQuery, factory);
        this.criteriaImpl.setSession(this.deriveRootSession(criteria));
        CriteriaJoinWalker walker = new CriteriaJoinWalker(persister, this.innerQuery, factory, this.criteriaImpl, this.criteriaImpl.getEntityOrClassName(), this.criteriaImpl.getSession().getLoadQueryInfluencers(), this.innerQuery.getRootSQLALias());
        return buf.append('(').append(walker.getSQLString()).append(')').toString();
    }

    private SharedSessionContractImplementor deriveRootSession(Criteria criteria) {
        if (criteria instanceof CriteriaImpl) {
            return ((CriteriaImpl)criteria).getSession();
        }
        if (criteria instanceof CriteriaImpl.Subcriteria) {
            return this.deriveRootSession(((CriteriaImpl.Subcriteria)criteria).getParent());
        }
        return null;
    }

    @Override
    public TypedValue[] getTypedValues(Criteria criteria, CriteriaQuery criteriaQuery) throws HibernateException {
        SessionFactoryImplementor factory = criteriaQuery.getFactory();
        this.createAndSetInnerQuery(criteriaQuery, factory);
        Type[] ppTypes = this.params.getPositionalParameterTypes();
        Object[] ppValues = this.params.getPositionalParameterValues();
        TypedValue[] tv = new TypedValue[ppTypes.length];
        for (int i = 0; i < ppTypes.length; ++i) {
            tv[i] = new TypedValue(ppTypes[i], ppValues[i]);
        }
        return tv;
    }

    private void createAndSetInnerQuery(CriteriaQuery criteriaQuery, SessionFactoryImplementor factory) {
        if (this.innerQuery == null) {
            String alias = this.criteriaImpl.getAlias() == null ? criteriaQuery.generateSQLAlias() : this.criteriaImpl.getAlias() + "_";
            this.innerQuery = new CriteriaQueryTranslator(factory, this.criteriaImpl, this.criteriaImpl.getEntityOrClassName(), alias, criteriaQuery);
            this.params = this.innerQuery.getQueryParameters();
            this.types = this.innerQuery.getProjectedTypes();
        }
    }
}

