/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.impl.health.checks;

import com.atlassian.confluence.impl.health.checks.DataSourceConfiguration;
import com.atlassian.confluence.impl.health.checks.DbHealthCheckHelper;
import java.util.Objects;

public class DefaultDbHealthCheckHelper
implements DbHealthCheckHelper {
    private final DataSourceConfiguration dataSourceConfiguration;

    public DefaultDbHealthCheckHelper(DataSourceConfiguration dataSourceConfiguration) {
        this.dataSourceConfiguration = Objects.requireNonNull(dataSourceConfiguration);
    }

    @Override
    public String getJDBCUrlConfigLocationDescription() {
        return this.isUsingDatasource() ? "conf/server.xml" : "confluence.cfg.xml";
    }

    private boolean isUsingDatasource() {
        return this.dataSourceConfiguration.getJdbcUrl().isPresent();
    }
}

