/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.cmpt.domain.Edition
 *  com.atlassian.confluence.core.ConfluenceActionSupport
 *  com.atlassian.sal.api.websudo.WebSudoRequired
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.migration.agent.web;

import com.atlassian.cmpt.domain.Edition;
import com.atlassian.confluence.core.ConfluenceActionSupport;
import com.atlassian.migration.agent.dto.CloudType;
import com.atlassian.migration.agent.mma.service.MigrationMetadataAggregatorService;
import com.atlassian.migration.agent.rest.GenerateCloudSiteSetupUrlDto;
import com.atlassian.migration.agent.service.cloud.CloudSiteService;
import com.atlassian.migration.agent.service.cloud.LegalService;
import com.atlassian.migration.agent.service.cloud.NonceService;
import com.atlassian.migration.agent.service.impl.DetectedUserEmailAnalyticsService;
import com.atlassian.sal.api.websudo.WebSudoRequired;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@WebSudoRequired
public class SaveCloudSiteAction
extends ConfluenceActionSupport {
    private static final String NONCE_ERROR_CODE = "nonceError-";
    private static final String AUTH_ERROR_CODE = "authError-";
    private static final String SUCCESS = "success-";
    private static final String CLOUD_CONNECT_ERROR_CODE = "cloudConnectError-";
    private String cloudUrl;
    private String cloudId;
    private String containerToken;
    private String nonce;
    private String cloudEdition;
    private String redirectTo;
    private String planId;
    private String cloudType;
    private final transient NonceService nonceService;
    private final transient CloudSiteService cloudSiteService;
    private final transient LegalService legalService;
    private final transient DetectedUserEmailAnalyticsService detectedUserEmailAnalyticsService;
    private final transient MigrationMetadataAggregatorService migrationMetadataAggregatorService;
    private static final Logger log = LoggerFactory.getLogger(SaveCloudSiteAction.class);

    SaveCloudSiteAction(NonceService nonceService, CloudSiteService cloudSiteService, LegalService legalService, DetectedUserEmailAnalyticsService detectedUserEmailAnalyticsService, MigrationMetadataAggregatorService migrationMetadataAggregatorService) {
        this.nonceService = nonceService;
        this.cloudSiteService = cloudSiteService;
        this.legalService = legalService;
        this.detectedUserEmailAnalyticsService = detectedUserEmailAnalyticsService;
        this.migrationMetadataAggregatorService = migrationMetadataAggregatorService;
    }

    public String execute() {
        if (Stream.of(this.cloudUrl, this.cloudId, this.containerToken, this.nonce, this.redirectTo, this.cloudType).anyMatch(Objects::isNull)) {
            return AUTH_ERROR_CODE + this.redirectOrDefault(this.redirectTo);
        }
        if (!this.nonceService.validateAndDeleteNonce(this.nonce)) {
            return NONCE_ERROR_CODE + this.redirectTo;
        }
        Optional<Edition> edition = Optional.ofNullable(this.cloudEdition).map(s -> Edition.getByKey((String)this.cloudEdition));
        this.cloudSiteService.createOrUpdate(this.cloudId, this.cloudUrl, this.containerToken, edition, CloudType.valueOf(this.cloudType));
        this.detectedUserEmailAnalyticsService.triggerForCloudId(this.cloudId);
        try {
            this.migrationMetadataAggregatorService.createServerInstanceCloudSitePairInMMA(this.containerToken, this.cloudId);
        }
        catch (Exception e) {
            log.error("Error while connecting to MMA: [{}]\n{}", (Object)e.getMessage(), (Object)e.getStackTrace());
            return CLOUD_CONNECT_ERROR_CODE + this.redirectTo;
        }
        this.legalService.rememberLegalOptIn();
        return SUCCESS + this.redirectTo;
    }

    private String redirectOrDefault(String redirectTo) {
        return StringUtils.isNotEmpty((CharSequence)redirectTo) ? redirectTo : GenerateCloudSiteSetupUrlDto.DEFAULT_REDIRECT;
    }

    public String getPlanId() {
        return this.planId;
    }

    public String getCloudId() {
        return this.cloudId;
    }

    public void setPlanId(String planId) {
        this.planId = planId;
    }

    public void setCloudUrl(String cloudUrl) {
        this.cloudUrl = cloudUrl;
    }

    public void setCloudId(String cloudId) {
        this.cloudId = cloudId;
    }

    public void setContainerToken(String containerToken) {
        this.containerToken = containerToken;
    }

    public void setNonce(String nonce) {
        this.nonce = nonce;
    }

    public void setCloudEdition(String cloudEdition) {
        this.cloudEdition = cloudEdition;
    }

    public void setRedirectTo(String redirectTo) {
        this.redirectTo = redirectTo;
    }

    public void setCloudType(String cloudType) {
        this.cloudType = cloudType;
    }

    public String getCloudType() {
        return this.cloudType;
    }
}

