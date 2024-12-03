/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  lombok.Generated
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.migration.agent.rest;

import com.atlassian.migration.agent.entity.CloudSite;
import com.atlassian.migration.agent.rest.ContainerTokenState;
import com.atlassian.migration.agent.service.cloud.CloudSiteService;
import com.atlassian.migration.agent.service.impl.MigrationPlatformService;
import java.time.Instant;
import javax.annotation.Nullable;
import lombok.Generated;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ContainerTokenValidator {
    @Generated
    private static final Logger log = LoggerFactory.getLogger(ContainerTokenValidator.class);
    private final CloudSiteService cloudSiteService;
    private final MigrationPlatformService migrationPlatformService;

    public ContainerTokenState validateContainerToken(String cloudId) {
        if (cloudId == null || cloudId.isEmpty()) {
            return ContainerTokenState.INVALID;
        }
        return this.validateContainerToken((CloudSite)this.cloudSiteService.getByCloudId(cloudId).orElse(null));
    }

    public ContainerTokenState validateContainerToken(@Nullable CloudSite cloudSite) {
        if (cloudSite == null) {
            return ContainerTokenState.INVALID;
        }
        String containerToken = cloudSite.getContainerToken();
        if (StringUtils.isBlank((CharSequence)containerToken)) {
            return ContainerTokenState.INVALID;
        }
        if (!this.isTokenExpired(containerToken)) {
            return ContainerTokenState.EXPIRED;
        }
        return ContainerTokenState.VALID;
    }

    private boolean isTokenExpired(String containerToken) {
        try {
            Instant timeToContainerTokenExpiry = this.migrationPlatformService.getContainerTokenExpiry(containerToken).toInstant();
            return timeToContainerTokenExpiry.compareTo(Instant.now()) > 0;
        }
        catch (Exception e) {
            log.error("Error while checking if token is valid, exception", (Throwable)e);
            return false;
        }
    }

    @Generated
    public ContainerTokenValidator(CloudSiteService cloudSiteService, MigrationPlatformService migrationPlatformService) {
        this.cloudSiteService = cloudSiteService;
        this.migrationPlatformService = migrationPlatformService;
    }
}

