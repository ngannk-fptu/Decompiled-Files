/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.config.db.HibernateConfig
 *  com.atlassian.confluence.impl.util.db.SingleConnectionProvider
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.internal.diagnostics.ipd.db;

import com.atlassian.config.db.HibernateConfig;
import com.atlassian.confluence.impl.util.db.SingleConnectionProvider;
import com.atlassian.confluence.internal.diagnostics.ipd.db.DatabaseLatencyMeter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultDatabaseLatencyMeter
implements DatabaseLatencyMeter {
    private static final Logger log = LoggerFactory.getLogger(DefaultDatabaseLatencyMeter.class);
    private static final int TIMEOUT_SECONDS = 10;
    private final HibernateConfig hibernateConfig;
    private final SingleConnectionProvider singleConnectionProvider;
    private final Clock clock;

    DefaultDatabaseLatencyMeter(HibernateConfig hibernateConfig, SingleConnectionProvider singleConnectionProvider, Clock clock) {
        this.hibernateConfig = Objects.requireNonNull(hibernateConfig);
        this.singleConnectionProvider = Objects.requireNonNull(singleConnectionProvider);
        this.clock = Objects.requireNonNull(clock);
    }

    /*
     * Enabled aggressive exception aggregation
     */
    @Override
    public Optional<Duration> measure() {
        try (Connection connection = this.singleConnectionProvider.getConnection(this.hibernateConfig.getHibernateProperties());){
            Optional<Duration> optional;
            block14: {
                PreparedStatement statement = connection.prepareStatement("select * from CLUSTERSAFETY");
                try {
                    statement.setQueryTimeout(10);
                    Instant startTime = this.clock.instant();
                    ResultSet resultSet = statement.executeQuery();
                    resultSet.close();
                    Instant endTime = this.clock.instant();
                    optional = Optional.of(Duration.between(startTime, endTime));
                    if (statement == null) break block14;
                }
                catch (Throwable throwable) {
                    if (statement != null) {
                        try {
                            statement.close();
                        }
                        catch (Throwable throwable2) {
                            throwable.addSuppressed(throwable2);
                        }
                    }
                    throw throwable;
                }
                statement.close();
            }
            return optional;
        }
        catch (Exception ex) {
            log.debug("Couldn't measure latency", (Throwable)ex);
            return Optional.empty();
        }
    }
}

