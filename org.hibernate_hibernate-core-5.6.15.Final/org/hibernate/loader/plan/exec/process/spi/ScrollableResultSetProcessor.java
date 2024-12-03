/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.loader.plan.exec.process.spi;

import java.sql.ResultSet;
import org.hibernate.engine.spi.QueryParameters;
import org.hibernate.engine.spi.SessionImplementor;

public interface ScrollableResultSetProcessor {
    public Object extractSingleRow(ResultSet var1, SessionImplementor var2, QueryParameters var3);

    public Object extractLogicalRowForward(ResultSet var1, SessionImplementor var2, QueryParameters var3);

    public Object extractLogicalRowReverse(ResultSet var1, SessionImplementor var2, QueryParameters var3, boolean var4);
}

