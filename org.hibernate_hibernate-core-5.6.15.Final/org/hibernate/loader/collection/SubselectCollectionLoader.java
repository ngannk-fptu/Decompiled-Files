/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.loader.collection;

import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import org.hibernate.HibernateException;
import org.hibernate.MappingException;
import org.hibernate.engine.spi.EntityKey;
import org.hibernate.engine.spi.LoadQueryInfluencers;
import org.hibernate.engine.spi.QueryParameters;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.engine.spi.TypedValue;
import org.hibernate.loader.collection.BasicCollectionLoader;
import org.hibernate.persister.collection.QueryableCollection;
import org.hibernate.type.Type;

public class SubselectCollectionLoader
extends BasicCollectionLoader {
    private final Serializable[] keys;
    private final Type[] types;
    private final Object[] values;
    private final Map<String, TypedValue> namedParameters;
    private final Map<String, int[]> namedParameterLocMap;

    public SubselectCollectionLoader(QueryableCollection persister, String subquery, Collection entityKeys, QueryParameters queryParameters, Map<String, int[]> namedParameterLocMap, SessionFactoryImplementor factory, LoadQueryInfluencers loadQueryInfluencers) throws MappingException {
        super(persister, 1, subquery, factory, loadQueryInfluencers);
        this.keys = new Serializable[entityKeys.size()];
        Iterator iter = entityKeys.iterator();
        int i = 0;
        while (iter.hasNext()) {
            this.keys[i++] = ((EntityKey)iter.next()).getIdentifier();
        }
        this.namedParameters = queryParameters.getNamedParameters();
        this.types = queryParameters.getFilteredPositionalParameterTypes();
        this.values = queryParameters.getFilteredPositionalParameterValues();
        this.namedParameterLocMap = namedParameterLocMap;
    }

    @Override
    public void initialize(Serializable id, SharedSessionContractImplementor session) throws HibernateException {
        this.loadCollectionSubselect(session, this.keys, this.values, this.types, this.namedParameters, this.getKeyType());
    }

    @Override
    public int[] getNamedParameterLocs(String name) {
        return this.namedParameterLocMap.get(name);
    }
}

