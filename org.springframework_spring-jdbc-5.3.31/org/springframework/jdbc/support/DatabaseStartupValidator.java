/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 *  org.springframework.beans.factory.InitializingBean
 *  org.springframework.lang.Nullable
 */
package org.springframework.jdbc.support;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.TimeUnit;
import javax.sql.DataSource;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.jdbc.CannotGetJdbcConnectionException;
import org.springframework.jdbc.support.JdbcUtils;
import org.springframework.lang.Nullable;

public class DatabaseStartupValidator
implements InitializingBean {
    public static final int DEFAULT_INTERVAL = 1;
    public static final int DEFAULT_TIMEOUT = 60;
    protected final Log logger = LogFactory.getLog(this.getClass());
    @Nullable
    private DataSource dataSource;
    @Nullable
    private String validationQuery;
    private int interval = 1;
    private int timeout = 60;

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Deprecated
    public void setValidationQuery(String validationQuery) {
        this.validationQuery = validationQuery;
    }

    public void setInterval(int interval) {
        this.interval = interval;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void afterPropertiesSet() {
        if (this.dataSource == null) {
            throw new IllegalArgumentException("Property 'dataSource' is required");
        }
        try {
            boolean validated = false;
            long beginTime = System.currentTimeMillis();
            long deadLine = beginTime + TimeUnit.SECONDS.toMillis(this.timeout);
            SQLException latestEx = null;
            while (!validated && System.currentTimeMillis() < deadLine) {
                Statement stmt;
                Connection con;
                block15: {
                    con = null;
                    stmt = null;
                    try {
                        con = this.dataSource.getConnection();
                        if (con == null) {
                            throw new CannotGetJdbcConnectionException("Failed to execute validation: DataSource returned null from getConnection(): " + this.dataSource);
                        }
                        if (this.validationQuery == null) {
                            validated = con.isValid(this.interval);
                            break block15;
                        }
                        stmt = con.createStatement();
                        stmt.execute(this.validationQuery);
                        validated = true;
                    }
                    catch (SQLException ex) {
                        block16: {
                            try {
                                float rest;
                                latestEx = ex;
                                if (this.logger.isDebugEnabled()) {
                                    if (this.validationQuery != null) {
                                        this.logger.debug((Object)("Validation query [" + this.validationQuery + "] threw exception"), (Throwable)ex);
                                    } else {
                                        this.logger.debug((Object)"Validation check threw exception", (Throwable)ex);
                                    }
                                }
                                if (!this.logger.isInfoEnabled() || !((rest = (float)(deadLine - System.currentTimeMillis()) / 1000.0f) > (float)this.interval)) break block16;
                                this.logger.info((Object)("Database has not started up yet - retrying in " + this.interval + " seconds (timeout in " + rest + " seconds)"));
                            }
                            catch (Throwable throwable) {
                                JdbcUtils.closeStatement(stmt);
                                JdbcUtils.closeConnection(con);
                                throw throwable;
                            }
                        }
                        JdbcUtils.closeStatement(stmt);
                        JdbcUtils.closeConnection(con);
                    }
                }
                JdbcUtils.closeStatement(stmt);
                JdbcUtils.closeConnection(con);
                if (validated) continue;
                TimeUnit.SECONDS.sleep(this.interval);
            }
            if (!validated) {
                throw new CannotGetJdbcConnectionException("Database has not started up within " + this.timeout + " seconds", latestEx);
            }
            if (this.logger.isInfoEnabled()) {
                float duration = (float)(System.currentTimeMillis() - beginTime) / 1000.0f;
                this.logger.info((Object)("Database startup detected after " + duration + " seconds"));
            }
        }
        catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
    }
}

