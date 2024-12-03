/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.VisibleForTesting
 *  com.atlassian.confluence.util.tomcat.TomcatConfigHelper
 */
package com.atlassian.confluence.impl.health.checks.rules;

import com.atlassian.annotations.VisibleForTesting;
import com.atlassian.confluence.impl.health.ErrorMessageProvider;
import com.atlassian.confluence.impl.health.checks.rules.AbstractHealthCheckRule;
import com.atlassian.confluence.internal.health.JohnsonEventType;
import com.atlassian.confluence.util.db.DatabaseConfigHelper;
import com.atlassian.confluence.util.tomcat.TomcatConfigHelper;
import com.atlassian.confluence.web.UrlBuilder;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URL;
import java.util.Objects;
import java.util.Optional;

public class HttpThreadsVsDbConnectionPoolRule
extends AbstractHealthCheckRule {
    private static final String FAILURE_CAUSE = "db-connection-pool-too-small-for-http-threads";
    @VisibleForTesting
    static final URL KB_URL = UrlBuilder.createURL("https://confluence.atlassian.com/confkb/startup-check-http-maxthreads-configuration-939930122.html?utm_source=Install&utm_medium=in-product&utm_campaign=csseng_fy18_q3_server_confluence_errorstate");
    @VisibleForTesting
    static final String FAILURE_MESSAGE_KEY = "johnson.message.insufficient.db.pool.size";
    private static final int HARD_CODED_DIFFERENCE = 10;
    private static final BigDecimal RATIO_OF_DB_POOL_SIZE_TO_HTTP_THREADS = BigDecimal.valueOf(1.25);
    private final TomcatConfigHelper tomcatConfigHelper;
    private final DatabaseConfigHelper databaseConfigHelper;

    public HttpThreadsVsDbConnectionPoolRule(TomcatConfigHelper tomcatConfigHelper, DatabaseConfigHelper databaseConfigHelper, ErrorMessageProvider errorMessageProvider) {
        super(errorMessageProvider, KB_URL, FAILURE_CAUSE, JohnsonEventType.DATABASE);
        this.tomcatConfigHelper = Objects.requireNonNull(tomcatConfigHelper);
        this.databaseConfigHelper = Objects.requireNonNull(databaseConfigHelper);
    }

    @Override
    protected Optional<String> doValidation() {
        int maxThreads = this.tomcatConfigHelper.getAllMaxHttpThreads().stream().map(Optional::get).max(Integer::compareTo).orElse(-1);
        if (maxThreads == -1) {
            return Optional.empty();
        }
        return this.databaseConfigHelper.getConnectionPoolSize().flatMap(poolSize -> this.validateThreadsAgainstMinPoolSize(maxThreads, (int)poolSize));
    }

    private Optional<String> validateThreadsAgainstMinPoolSize(int httpThreads, int poolSize) {
        int minDbPoolSize = HttpThreadsVsDbConnectionPoolRule.getMinDbPoolSize(httpThreads);
        return poolSize < minDbPoolSize ? Optional.of(this.getErrorMessage(FAILURE_MESSAGE_KEY, poolSize, minDbPoolSize, HttpThreadsVsDbConnectionPoolRule.getMaxHttpThreads(poolSize))) : Optional.empty();
    }

    private static int getMinDbPoolSize(int httpThreads) {
        int percentageBasedMinDbPoolSize = BigDecimal.valueOf(httpThreads).multiply(RATIO_OF_DB_POOL_SIZE_TO_HTTP_THREADS).intValue();
        return Math.max(httpThreads + 10, percentageBasedMinDbPoolSize);
    }

    private static int getMaxHttpThreads(int dbPoolSize) {
        int percentageBasedMaxHttpThreads = BigDecimal.valueOf(dbPoolSize).divide(RATIO_OF_DB_POOL_SIZE_TO_HTTP_THREADS, RoundingMode.DOWN).intValue();
        return Math.min(dbPoolSize - 10, percentageBasedMaxHttpThreads);
    }
}

