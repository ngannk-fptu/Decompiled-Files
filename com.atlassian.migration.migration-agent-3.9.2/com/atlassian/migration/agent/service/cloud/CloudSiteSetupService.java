/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.analytics.api.services.AnalyticsConfigService
 *  com.atlassian.sal.api.ApplicationProperties
 *  com.atlassian.sal.api.UrlMode
 *  javax.annotation.ParametersAreNonnullByDefault
 *  javax.ws.rs.core.UriBuilder
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.migration.agent.service.cloud;

import com.atlassian.analytics.api.services.AnalyticsConfigService;
import com.atlassian.migration.agent.config.MigrationAgentConfiguration;
import com.atlassian.migration.agent.rest.GenerateCloudSiteSetupUrlDto;
import com.atlassian.migration.agent.service.cloud.LegalService;
import com.atlassian.migration.agent.service.cloud.NonceService;
import com.atlassian.migration.agent.service.impl.SENSupplier;
import com.atlassian.sal.api.ApplicationProperties;
import com.atlassian.sal.api.UrlMode;
import java.net.URI;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.ws.rs.core.UriBuilder;
import org.apache.commons.lang3.StringUtils;

@ParametersAreNonnullByDefault
public class CloudSiteSetupService {
    private static final String QUERY_PARAM_ECTL_REDIRECT = "ectl_redirect";
    private static final String ECTL_BUNDLE_CONFLUENCE = "confluence";
    private static final String CONFLUENCE_PRODUCT_FAMILY = "confluence";
    private static final String CONFLUENCE_MIGRATION_TYPE = "S2C_MIGRATION";
    private final NonceService nonceService;
    private final SENSupplier senSupplier;
    private final MigrationAgentConfiguration migrationAgentConfiguration;
    private final ApplicationProperties applicationProperties;
    private final LegalService legalService;
    private final AnalyticsConfigService analyticsConfigService;

    public CloudSiteSetupService(NonceService nonceService, SENSupplier senSupplier, MigrationAgentConfiguration migrationAgentConfiguration, ApplicationProperties applicationProperties, LegalService legalService, AnalyticsConfigService analyticsConfigService) {
        this.nonceService = nonceService;
        this.senSupplier = senSupplier;
        this.migrationAgentConfiguration = migrationAgentConfiguration;
        this.applicationProperties = applicationProperties;
        this.legalService = legalService;
        this.analyticsConfigService = analyticsConfigService;
    }

    public String generateCloudSiteSetupUrl(GenerateCloudSiteSetupUrlDto setupDto) {
        return this.setupUrlBuilder(setupDto).build(new Object[0]).toASCIIString();
    }

    public String generateEctlRedirectUrl(GenerateCloudSiteSetupUrlDto setupDto) {
        return this.setupUrlBuilder(setupDto).queryParam(QUERY_PARAM_ECTL_REDIRECT, new Object[]{"confluence"}).build(new Object[0]).toASCIIString();
    }

    private UriBuilder setupUrlBuilder(GenerateCloudSiteSetupUrlDto setupDto) {
        String sen;
        String mgBaseUrl = this.migrationAgentConfiguration.getMigrationGatewayUrl();
        UriBuilder setupUrlBuilder = UriBuilder.fromUri((String)mgBaseUrl).path("setup").queryParam("back", new Object[]{setupDto.returnUrl}).queryParam("product", new Object[]{"confluence"}).queryParam("migrationType", new Object[]{CONFLUENCE_MIGRATION_TYPE});
        String nonce = this.nonceService.generateAndSaveNonce();
        UriBuilder continueURIBuilder = UriBuilder.fromUri((String)this.applicationProperties.getBaseUrl(UrlMode.CANONICAL)).path("admin/save-cloud-site.action").queryParam("nonce", new Object[]{nonce}).queryParam("cloudType", new Object[]{setupDto.cloudType}).queryParam("redirectTo", new Object[]{setupDto.redirectTo});
        if (StringUtils.isNotEmpty((CharSequence)setupDto.planId)) {
            continueURIBuilder.queryParam("planId", new Object[]{setupDto.planId});
        }
        URI continueUrl = continueURIBuilder.build(new Object[0]);
        setupUrlBuilder.queryParam("continue", new Object[]{continueUrl});
        if ((this.legalService.getRememberLegalOptIn() || this.analyticsConfigService.canCollectAnalytics()) && StringUtils.isNotEmpty((CharSequence)(sen = this.senSupplier.get()))) {
            setupUrlBuilder.queryParam("sen", new Object[]{sen});
        }
        return setupUrlBuilder;
    }
}

