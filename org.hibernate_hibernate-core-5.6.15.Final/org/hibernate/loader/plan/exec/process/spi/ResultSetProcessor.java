/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.loader.plan.exec.process.spi;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import org.hibernate.engine.spi.QueryParameters;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.loader.plan.exec.process.spi.ScrollableResultSetProcessor;
import org.hibernate.loader.plan.exec.query.spi.NamedParameterContext;
import org.hibernate.loader.spi.AfterLoadAction;
import org.hibernate.transform.ResultTransformer;

public interface ResultSetProcessor {
    @Deprecated
    public ScrollableResultSetProcessor toOnDemandForm();

    public List extractResults(ResultSet var1, SharedSessionContractImplementor var2, QueryParameters var3, NamedParameterContext var4, boolean var5, boolean var6, ResultTransformer var7, List<AfterLoadAction> var8) throws SQLException;
}

