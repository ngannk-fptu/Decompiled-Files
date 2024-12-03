/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.jdbc.datasource.embedded;

import javax.sql.DataSource;
import org.springframework.jdbc.datasource.embedded.ConnectionProperties;

public interface DataSourceFactory {
    public ConnectionProperties getConnectionProperties();

    public DataSource getDataSource();
}

