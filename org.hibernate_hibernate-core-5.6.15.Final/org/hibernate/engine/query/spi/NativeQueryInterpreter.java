/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.engine.query.spi;

import org.hibernate.engine.query.spi.NativeSQLQueryPlan;
import org.hibernate.engine.query.spi.sql.NativeSQLQuerySpecification;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.loader.custom.CustomLoader;
import org.hibernate.loader.custom.CustomQuery;
import org.hibernate.query.internal.ParameterMetadataImpl;
import org.hibernate.service.Service;

public interface NativeQueryInterpreter
extends Service {
    public ParameterMetadataImpl getParameterMetadata(String var1);

    public NativeSQLQueryPlan createQueryPlan(NativeSQLQuerySpecification var1, SessionFactoryImplementor var2);

    @Deprecated
    default public CustomLoader createCustomLoader(CustomQuery customQuery, SessionFactoryImplementor sessionFactory) {
        return new CustomLoader(customQuery, sessionFactory);
    }
}

