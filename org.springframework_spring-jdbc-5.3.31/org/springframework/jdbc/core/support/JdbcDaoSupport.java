/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.dao.support.DaoSupport
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 */
package org.springframework.jdbc.core.support;

import java.sql.Connection;
import javax.sql.DataSource;
import org.springframework.dao.support.DaoSupport;
import org.springframework.jdbc.CannotGetJdbcConnectionException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.jdbc.support.SQLExceptionTranslator;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

public abstract class JdbcDaoSupport
extends DaoSupport {
    @Nullable
    private JdbcTemplate jdbcTemplate;

    public final void setDataSource(DataSource dataSource) {
        if (this.jdbcTemplate == null || dataSource != this.jdbcTemplate.getDataSource()) {
            this.jdbcTemplate = this.createJdbcTemplate(dataSource);
            this.initTemplateConfig();
        }
    }

    protected JdbcTemplate createJdbcTemplate(DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

    @Nullable
    public final DataSource getDataSource() {
        return this.jdbcTemplate != null ? this.jdbcTemplate.getDataSource() : null;
    }

    public final void setJdbcTemplate(@Nullable JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.initTemplateConfig();
    }

    @Nullable
    public final JdbcTemplate getJdbcTemplate() {
        return this.jdbcTemplate;
    }

    protected void initTemplateConfig() {
    }

    protected void checkDaoConfig() {
        if (this.jdbcTemplate == null) {
            throw new IllegalArgumentException("'dataSource' or 'jdbcTemplate' is required");
        }
    }

    protected final SQLExceptionTranslator getExceptionTranslator() {
        JdbcTemplate jdbcTemplate = this.getJdbcTemplate();
        Assert.state((jdbcTemplate != null ? 1 : 0) != 0, (String)"No JdbcTemplate set");
        return jdbcTemplate.getExceptionTranslator();
    }

    protected final Connection getConnection() throws CannotGetJdbcConnectionException {
        DataSource dataSource = this.getDataSource();
        Assert.state((dataSource != null ? 1 : 0) != 0, (String)"No DataSource set");
        return DataSourceUtils.getConnection(dataSource);
    }

    protected final void releaseConnection(Connection con) {
        DataSourceUtils.releaseConnection(con, this.getDataSource());
    }
}

