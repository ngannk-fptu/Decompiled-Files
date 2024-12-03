/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.hql.internal.classic;

import java.util.Map;
import org.hibernate.QueryException;
import org.hibernate.engine.query.spi.EntityGraphQueryHint;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.hql.internal.classic.QueryTranslatorImpl;
import org.hibernate.hql.spi.FilterTranslator;
import org.hibernate.hql.spi.QueryTranslator;
import org.hibernate.hql.spi.QueryTranslatorFactory;

public class ClassicQueryTranslatorFactory
implements QueryTranslatorFactory {
    @Override
    public QueryTranslator createQueryTranslator(String queryIdentifier, String queryString, Map filters, SessionFactoryImplementor factory, EntityGraphQueryHint entityGraphQueryHint) {
        if (entityGraphQueryHint != null) {
            throw new QueryException("EntityGraphs cannot be applied queries using the classic QueryTranslator!");
        }
        return new QueryTranslatorImpl(queryIdentifier, queryString, filters, factory);
    }

    @Override
    public FilterTranslator createFilterTranslator(String queryIdentifier, String queryString, Map filters, SessionFactoryImplementor factory) {
        return new QueryTranslatorImpl(queryIdentifier, queryString, filters, factory);
    }
}

