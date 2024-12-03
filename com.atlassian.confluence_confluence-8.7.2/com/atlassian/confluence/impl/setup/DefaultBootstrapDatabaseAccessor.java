/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.config.db.HibernateConfig
 *  com.atlassian.confluence.upgrade.BuildNumber
 *  javax.annotation.Nullable
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.impl.setup;

import com.atlassian.config.db.HibernateConfig;
import com.atlassian.confluence.cluster.ZduStatus;
import com.atlassian.confluence.impl.setup.BootstrapDatabaseAccessor;
import com.atlassian.confluence.impl.util.db.SingleConnectionProvider;
import com.atlassian.confluence.upgrade.BuildNumber;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultBootstrapDatabaseAccessor
implements BootstrapDatabaseAccessor {
    private static final Logger log = LoggerFactory.getLogger(DefaultBootstrapDatabaseAccessor.class);
    private final SingleConnectionProvider databaseHelper;
    private final HibernateConfig hibernateConfig;

    public DefaultBootstrapDatabaseAccessor(SingleConnectionProvider databaseHelper, HibernateConfig hibernateConfig) {
        this.databaseHelper = Objects.requireNonNull(databaseHelper);
        this.hibernateConfig = Objects.requireNonNull(hibernateConfig);
    }

    @Override
    public BootstrapDatabaseAccessor.BootstrapDatabaseData getBootstrapData() {
        ZduStatus defaultZduStatus = ZduStatus.disabled();
        if (this.hibernateConfig.isHibernateSetup()) {
            BootstrapDatabaseAccessor.BootstrapDatabaseData bootstrapDatabaseData;
            block9: {
                Connection conn = this.databaseHelper.getConnection(this.hibernateConfig.getHibernateProperties());
                try {
                    bootstrapDatabaseData = new BootstrapDatabaseAccessor.BootstrapDatabaseData(this.readValue(conn, "select STATE, ORIG_VER from CONFZDU", ex -> log.warn("Unable to determine ZDU status from database [message: {}]. If you are upgrading from a Confluence version prior to 7.9, this is expected.", (Object)ex.getMessage()), rs -> Optional.of(new ZduStatus(ZduStatus.State.valueOf(rs.getString(1)), rs.getString(2))), () -> defaultZduStatus), this.readValue(conn, "select max(BUILDNUMBER) from CONFVERSION where FINALIZED='Y'", ex -> log.warn("Unable to determine finalized build number from database [message: {}]. If you are upgrading from a Confluence version prior to 7.14, this is expected.", (Object)ex.getMessage()), this.buildNumberExtractor(), () -> this.readValue(conn, "select max(BUILDNUMBER) from CONFVERSION", ex -> log.warn("Unable to determine build number from database [message: {}]. If you are upgrading from a Confluence version prior to 2.3, this is expected.", (Object)ex.getMessage()), this.buildNumberExtractor(), () -> null)));
                    if (conn == null) break block9;
                }
                catch (Throwable throwable) {
                    try {
                        if (conn != null) {
                            try {
                                conn.close();
                            }
                            catch (Throwable throwable2) {
                                throwable.addSuppressed(throwable2);
                            }
                        }
                        throw throwable;
                    }
                    catch (SQLException e) {
                        log.error("Unable to open database connection during bootstrap.", (Throwable)e);
                    }
                }
                conn.close();
            }
            return bootstrapDatabaseData;
        }
        return new BootstrapDatabaseAccessor.BootstrapDatabaseData(defaultZduStatus, null);
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    @Nullable
    private <T> T readValue(Connection conn, String query, Consumer<Exception> exceptionLogger, Extractor<T> extractor, Supplier<T> defaultValueSupplier) {
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query);){
            if (rs.next()) {
                T t = extractor.extract(rs).orElse(defaultValueSupplier.get());
                return t;
            }
            log.debug("No results from query [{}]", (Object)query);
            return defaultValueSupplier.get();
        }
        catch (RuntimeException | SQLException e) {
            exceptionLogger.accept(e);
            log.debug("Failed to get value from SQL query [{}]", (Object)query, (Object)e);
        }
        return defaultValueSupplier.get();
    }

    private Extractor<BuildNumber> buildNumberExtractor() {
        return rs -> {
            int latestBuildNumber = rs.getInt(1);
            if (latestBuildNumber > 0) {
                return Optional.of(new BuildNumber(Integer.toString(latestBuildNumber)));
            }
            return Optional.empty();
        };
    }

    @FunctionalInterface
    private static interface Extractor<T> {
        public Optional<T> extract(ResultSet var1) throws SQLException;
    }
}

