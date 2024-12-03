/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.cmpt.check.base.CheckResult
 *  com.atlassian.cmpt.check.base.Checker
 *  com.atlassian.cmpt.check.dto.Status
 *  com.google.common.collect.ImmutableMap
 *  org.apache.commons.lang.time.DateUtils
 *  org.slf4j.Logger
 */
package com.atlassian.migration.agent.service.check.token;

import com.atlassian.cmpt.check.base.CheckResult;
import com.atlassian.cmpt.check.base.Checker;
import com.atlassian.cmpt.check.dto.Status;
import com.atlassian.migration.agent.entity.CloudSite;
import com.atlassian.migration.agent.logging.ContextLoggerFactory;
import com.atlassian.migration.agent.okhttp.HttpServiceException;
import com.atlassian.migration.agent.service.PreflightErrorCode;
import com.atlassian.migration.agent.service.check.token.ContainerTokenExpirationContext;
import com.atlassian.migration.agent.service.cloud.CloudSiteService;
import com.atlassian.migration.agent.service.impl.MigrationPlatformService;
import com.google.common.collect.ImmutableMap;
import java.time.Duration;
import java.time.Instant;
import java.util.Collections;
import java.util.Date;
import java.util.Map;
import java.util.Optional;
import org.apache.commons.lang.time.DateUtils;
import org.slf4j.Logger;

public class ContainerTokenExpirationChecker
implements Checker<ContainerTokenExpirationContext> {
    private static final Logger log = ContextLoggerFactory.getLogger(ContainerTokenExpirationChecker.class);
    public static final Duration BUFFER_BEFORE_CONTAINER_TOKEN_EXPIRY = Duration.ofDays(19L);
    public static final Duration BUFFER_BEFORE_CONTAINER_TOKEN_EXPIRY_WARNING = Duration.ofDays(24L);
    static final String STATUS_KEY = "status";
    static final String EXPIRY_KEY = "expiry";
    static final String CLOUD_URL = "cloudUrl";
    private final CloudSiteService cloudSiteService;
    private final MigrationPlatformService migrationPlatformService;

    public ContainerTokenExpirationChecker(CloudSiteService cloudSiteService, MigrationPlatformService migrationPlatformService) {
        this.cloudSiteService = cloudSiteService;
        this.migrationPlatformService = migrationPlatformService;
    }

    public CheckResult check(ContainerTokenExpirationContext ctx) {
        Optional<CloudSite> cloudSite = this.cloudSiteService.getByCloudId(ctx.cloudId);
        PreflightErrorCode cloudErrorCode = PreflightErrorCode.CLOUD_ERROR;
        if (!cloudSite.isPresent()) {
            log.error("Cannot find CloudSite using cloudId: {}.", (Object)ctx.cloudId);
            return Checker.buildCheckResultWithExecutionError((int)cloudErrorCode.getCode());
        }
        try {
            String containerToken = cloudSite.get().getContainerToken();
            Date containerTokenExpiry = this.migrationPlatformService.getContainerTokenExpiry(containerToken);
            Duration timeToContainerTokenExpiry = Duration.between(Instant.now(), containerTokenExpiry.toInstant());
            if (timeToContainerTokenExpiry.compareTo(BUFFER_BEFORE_CONTAINER_TOKEN_EXPIRY) < 0) {
                return new CheckResult(false, Collections.singletonMap(CLOUD_URL, cloudSite.get().getCloudUrl()));
            }
            if (timeToContainerTokenExpiry.compareTo(BUFFER_BEFORE_CONTAINER_TOKEN_EXPIRY_WARNING) < 0) {
                Date containerTokenExpiryForMigrations = DateUtils.addDays((Date)containerTokenExpiry, (int)(-1 * (int)BUFFER_BEFORE_CONTAINER_TOKEN_EXPIRY.toDays()));
                return new CheckResult(true, (Map)ImmutableMap.of((Object)STATUS_KEY, (Object)Status.WARNING, (Object)EXPIRY_KEY, (Object)String.valueOf(containerTokenExpiryForMigrations.getTime()), (Object)CLOUD_URL, (Object)cloudSite.get().getCloudUrl()));
            }
            return new CheckResult(true);
        }
        catch (Exception e) {
            if (e instanceof HttpServiceException && ((HttpServiceException)e).getStatusCode() == 401) {
                return new CheckResult(false, Collections.singletonMap(CLOUD_URL, cloudSite.get().getCloudUrl()));
            }
            log.error("An error occurred during container token expiration check.", (Throwable)e);
            return Checker.buildCheckResultWithExecutionError((int)PreflightErrorCode.CONTAINER_TOKEN_EXPIRY_CHECK_ERROR.getCode());
        }
    }
}

