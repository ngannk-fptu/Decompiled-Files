/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.hql.internal.ast;

import java.util.Map;
import org.hibernate.engine.query.spi.EntityGraphQueryHint;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.hql.internal.ast.QueryTranslatorImpl;
import org.hibernate.hql.spi.FilterTranslator;
import org.hibernate.hql.spi.QueryTranslator;
import org.hibernate.hql.spi.QueryTranslatorFactory;
import org.hibernate.internal.CoreLogging;
import org.hibernate.internal.CoreMessageLogger;

public class ASTQueryTranslatorFactory
implements QueryTranslatorFactory {
    private static final CoreMessageLogger LOG = CoreLogging.messageLogger(ASTQueryTranslatorFactory.class);
    public static final ASTQueryTranslatorFactory INSTANCE = new ASTQueryTranslatorFactory();

    @Override
    public QueryTranslator createQueryTranslator(String queryIdentifier, String queryString, Map filters, SessionFactoryImplementor factory, EntityGraphQueryHint entityGraphQueryHint) {
        return new QueryTranslatorImpl(queryIdentifier, queryString, filters, factory, entityGraphQueryHint);
    }

    @Override
    public FilterTranslator createFilterTranslator(String queryIdentifier, String queryString, Map filters, SessionFactoryImplementor factory) {
        return new QueryTranslatorImpl(queryIdentifier, queryString, filters, factory);
    }
}

