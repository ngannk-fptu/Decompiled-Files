/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.jdbc.datasource.lookup;

import javax.sql.DataSource;
import org.springframework.jdbc.datasource.lookup.DataSourceLookupFailureException;

@FunctionalInterface
public interface DataSourceLookup {
    public DataSource getDataSource(String var1) throws DataSourceLookupFailureException;
}

