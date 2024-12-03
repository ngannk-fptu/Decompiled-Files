/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.query.internal;

import java.util.Iterator;
import java.util.List;
import org.hibernate.HibernateException;
import org.hibernate.ScrollMode;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.query.Query;
import org.hibernate.query.internal.AbstractProducedQuery;
import org.hibernate.query.internal.ParameterMetadataImpl;
import org.hibernate.query.internal.QueryParameterBindingsImpl;
import org.hibernate.query.spi.QueryParameterBindings;
import org.hibernate.query.spi.ScrollableResultsImplementor;
import org.hibernate.type.Type;

public class CollectionFilterImpl
extends AbstractProducedQuery {
    private final String queryString;
    private Object collection;
    private final QueryParameterBindingsImpl queryParameterBindings;

    public CollectionFilterImpl(String queryString, Object collection, SharedSessionContractImplementor session, ParameterMetadataImpl parameterMetadata) {
        super(session, parameterMetadata);
        this.queryString = queryString;
        this.collection = collection;
        this.queryParameterBindings = QueryParameterBindingsImpl.from(parameterMetadata, session.getFactory(), session.isQueryParametersValidationEnabled());
    }

    @Override
    protected QueryParameterBindings getQueryParameterBindings() {
        return this.queryParameterBindings;
    }

    @Override
    protected boolean isNativeQuery() {
        return false;
    }

    @Override
    public String getQueryString() {
        return this.queryString;
    }

    @Override
    public Iterator iterate() throws HibernateException {
        this.getQueryParameterBindings().verifyParametersBound(false);
        String expandedQuery = this.getQueryParameterBindings().expandListValuedParameters(this.getQueryString(), this.getProducer());
        return this.getProducer().iterateFilter(this.collection, expandedQuery, this.makeQueryParametersForExecution(expandedQuery));
    }

    @Override
    public List list() throws HibernateException {
        this.getQueryParameterBindings().verifyParametersBound(false);
        String expandedQuery = this.getQueryParameterBindings().expandListValuedParameters(this.getQueryString(), this.getProducer());
        return this.getProducer().listFilter(this.collection, expandedQuery, this.makeQueryParametersForExecution(expandedQuery));
    }

    @Override
    public ScrollableResultsImplementor scroll() throws HibernateException {
        throw new UnsupportedOperationException("Can't scroll filters");
    }

    @Override
    public ScrollableResultsImplementor scroll(ScrollMode scrollMode) {
        throw new UnsupportedOperationException("Can't scroll filters");
    }

    @Override
    protected Type[] getPositionalParameterTypes() {
        Type[] explicitParameterTypes = super.getPositionalParameterTypes();
        Type[] expandedParameterTypes = new Type[explicitParameterTypes.length + 1];
        System.arraycopy(explicitParameterTypes, 0, expandedParameterTypes, 1, explicitParameterTypes.length);
        return expandedParameterTypes;
    }

    @Override
    protected Object[] getPositionalParameterValues() {
        Object[] explicitParameterValues = super.getPositionalParameterValues();
        Object[] expandedParameterValues = new Object[explicitParameterValues.length + 1];
        System.arraycopy(explicitParameterValues, 0, expandedParameterValues, 1, explicitParameterValues.length);
        return expandedParameterValues;
    }

    @Override
    public Type[] getReturnTypes() {
        return this.getProducer().getFactory().getReturnTypes(this.getQueryString());
    }

    @Override
    public String[] getReturnAliases() {
        return this.getProducer().getFactory().getReturnAliases(this.getQueryString());
    }

    @Override
    public Query setEntity(int position, Object val) {
        return this.setParameter(position, val, this.getProducer().getFactory().getTypeHelper().entity(this.resolveEntityName(val)));
    }

    @Override
    public Query setEntity(String name, Object val) {
        return this.setParameter(name, val, this.getProducer().getFactory().getTypeHelper().entity(this.resolveEntityName(val)));
    }
}

