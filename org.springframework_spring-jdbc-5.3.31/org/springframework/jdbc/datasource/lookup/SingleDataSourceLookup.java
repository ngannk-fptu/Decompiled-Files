/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.util.Assert
 */
package org.springframework.jdbc.datasource.lookup;

import javax.sql.DataSource;
import org.springframework.jdbc.datasource.lookup.DataSourceLookup;
import org.springframework.util.Assert;

public class SingleDataSourceLookup
implements DataSourceLookup {
    private final DataSource dataSource;

    public SingleDataSourceLookup(DataSource dataSource) {
        Assert.notNull((Object)dataSource, (String)"DataSource must not be null");
        this.dataSource = dataSource;
    }

    @Override
    public DataSource getDataSource(String dataSourceName) {
        return this.dataSource;
    }
}

