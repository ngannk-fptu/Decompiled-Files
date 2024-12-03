/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.result.spi;

import java.util.Set;
import org.hibernate.engine.query.spi.sql.NativeSQLQueryReturn;
import org.hibernate.engine.spi.QueryParameters;
import org.hibernate.engine.spi.SharedSessionContractImplementor;

public interface ResultContext {
    public SharedSessionContractImplementor getSession();

    public Set<String> getSynchronizedQuerySpaces();

    public String getSql();

    public QueryParameters getQueryParameters();

    public NativeSQLQueryReturn[] getQueryReturns();
}

