/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.query.internal;

import org.hibernate.engine.query.spi.HQLQueryPlan;
import org.hibernate.engine.query.spi.ReturnMetadata;
import org.hibernate.engine.spi.QueryParameters;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.query.Query;
import org.hibernate.query.internal.AbstractProducedQuery;
import org.hibernate.query.internal.QueryParameterBindingsImpl;
import org.hibernate.query.spi.QueryParameterBindings;
import org.hibernate.type.Type;

public class QueryImpl<R>
extends AbstractProducedQuery<R>
implements Query<R> {
    private final String queryString;
    private final HQLQueryPlan hqlQueryPlan;
    private final QueryParameterBindingsImpl queryParameterBindings;

    public QueryImpl(SharedSessionContractImplementor producer, HQLQueryPlan hqlQueryPlan, String queryString) {
        super(producer, hqlQueryPlan.getParameterMetadata());
        this.hqlQueryPlan = hqlQueryPlan;
        this.queryString = queryString;
        this.queryParameterBindings = QueryParameterBindingsImpl.from(hqlQueryPlan.getParameterMetadata(), producer.getFactory(), producer.isQueryParametersValidationEnabled());
    }

    @Override
    protected QueryParameterBindings getQueryParameterBindings() {
        return this.queryParameterBindings;
    }

    @Override
    public String getQueryString() {
        return this.queryString;
    }

    public HQLQueryPlan getQueryPlan() {
        return this.hqlQueryPlan;
    }

    @Override
    protected boolean isNativeQuery() {
        return false;
    }

    @Override
    public Type[] getReturnTypes() {
        ReturnMetadata metadata = this.hqlQueryPlan.getReturnMetadata();
        return metadata == null ? null : metadata.getReturnTypes();
    }

    @Override
    public String[] getReturnAliases() {
        ReturnMetadata metadata = this.hqlQueryPlan.getReturnMetadata();
        return metadata == null ? null : metadata.getReturnAliases();
    }

    @Override
    public Query setEntity(int position, Object val) {
        return this.setParameter(position, val, this.getProducer().getFactory().getTypeHelper().entity(this.resolveEntityName(val)));
    }

    @Override
    public Query setEntity(String name, Object val) {
        return this.setParameter(name, val, this.getProducer().getFactory().getTypeHelper().entity(this.resolveEntityName(val)));
    }

    @Override
    protected boolean isSelect() {
        return this.hqlQueryPlan.isSelect();
    }

    @Override
    protected void appendQueryPlanToQueryParameters(String hql, QueryParameters queryParameters, HQLQueryPlan queryPlan) {
        if (queryPlan != null) {
            queryParameters.setQueryPlan(queryPlan);
        } else if (hql.equals(this.getQueryString()) && this.getQueryPlan().getEnabledFilterNames().equals(this.getProducer().getLoadQueryInfluencers().getEnabledFilters().values())) {
            queryParameters.setQueryPlan(this.getQueryPlan());
        }
    }
}

