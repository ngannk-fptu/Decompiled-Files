/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 */
package org.springframework.jdbc.datasource.lookup;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import javax.sql.DataSource;
import org.springframework.jdbc.datasource.lookup.DataSourceLookup;
import org.springframework.jdbc.datasource.lookup.DataSourceLookupFailureException;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

public class MapDataSourceLookup
implements DataSourceLookup {
    private final Map<String, DataSource> dataSources = new HashMap<String, DataSource>(4);

    public MapDataSourceLookup() {
    }

    public MapDataSourceLookup(Map<String, DataSource> dataSources) {
        this.setDataSources(dataSources);
    }

    public MapDataSourceLookup(String dataSourceName, DataSource dataSource) {
        this.addDataSource(dataSourceName, dataSource);
    }

    public void setDataSources(@Nullable Map<String, DataSource> dataSources) {
        if (dataSources != null) {
            this.dataSources.putAll(dataSources);
        }
    }

    public Map<String, DataSource> getDataSources() {
        return Collections.unmodifiableMap(this.dataSources);
    }

    public void addDataSource(String dataSourceName, DataSource dataSource) {
        Assert.notNull((Object)dataSourceName, (String)"DataSource name must not be null");
        Assert.notNull((Object)dataSource, (String)"DataSource must not be null");
        this.dataSources.put(dataSourceName, dataSource);
    }

    @Override
    public DataSource getDataSource(String dataSourceName) throws DataSourceLookupFailureException {
        Assert.notNull((Object)dataSourceName, (String)"DataSource name must not be null");
        DataSource dataSource = this.dataSources.get(dataSourceName);
        if (dataSource == null) {
            throw new DataSourceLookupFailureException("No DataSource with name '" + dataSourceName + "' registered");
        }
        return dataSource;
    }
}

