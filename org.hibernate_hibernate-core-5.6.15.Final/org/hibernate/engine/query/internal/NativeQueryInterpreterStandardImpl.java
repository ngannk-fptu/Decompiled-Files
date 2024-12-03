/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.engine.query.internal;

import org.hibernate.engine.query.spi.NativeQueryInterpreter;
import org.hibernate.engine.query.spi.NativeSQLQueryPlan;
import org.hibernate.engine.query.spi.ParamLocationRecognizer;
import org.hibernate.engine.query.spi.sql.NativeSQLQuerySpecification;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.loader.custom.sql.SQLCustomQuery;
import org.hibernate.query.internal.ParameterMetadataImpl;

public class NativeQueryInterpreterStandardImpl
implements NativeQueryInterpreter {
    private final SessionFactoryImplementor sessionFactory;

    public NativeQueryInterpreterStandardImpl(SessionFactoryImplementor sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public ParameterMetadataImpl getParameterMetadata(String nativeQuery) {
        ParamLocationRecognizer recognizer = ParamLocationRecognizer.parseLocations(nativeQuery, this.sessionFactory);
        return new ParameterMetadataImpl(recognizer.getOrdinalParameterDescriptionMap(), recognizer.getNamedParameterDescriptionMap());
    }

    @Override
    public NativeSQLQueryPlan createQueryPlan(NativeSQLQuerySpecification specification, SessionFactoryImplementor sessionFactory) {
        SQLCustomQuery customQuery = new SQLCustomQuery(specification.getQueryString(), specification.getQueryReturns(), specification.getQuerySpaces(), sessionFactory);
        return new NativeSQLQueryPlan(specification.getQueryString(), customQuery);
    }
}

