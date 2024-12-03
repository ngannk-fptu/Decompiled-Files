/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.query.criteria.internal.compile;

import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.query.criteria.internal.compile.InterpretedParameterMetadata;
import org.hibernate.query.spi.QueryImplementor;

public interface CriteriaInterpretation {
    public QueryImplementor buildCompiledQuery(SharedSessionContractImplementor var1, InterpretedParameterMetadata var2);
}

