/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.analytics.api.annotations.EventName
 *  com.atlassian.applinks.api.ApplicationId
 *  com.atlassian.applinks.api.ApplicationType
 *  com.atlassian.applinks.api.ReadOnlyApplicationLink
 *  com.atlassian.applinks.api.application.bamboo.BambooApplicationType
 *  com.atlassian.applinks.api.application.bitbucket.BitbucketApplicationType
 *  com.atlassian.applinks.api.application.confluence.ConfluenceApplicationType
 *  com.atlassian.applinks.api.application.crowd.CrowdApplicationType
 *  com.atlassian.applinks.api.application.fecru.FishEyeCrucibleApplicationType
 *  com.atlassian.applinks.api.application.generic.GenericApplicationType
 *  com.atlassian.applinks.api.application.jira.JiraApplicationType
 *  com.atlassian.applinks.api.application.refapp.RefAppApplicationType
 *  com.atlassian.applinks.api.auth.types.BasicAuthenticationProvider
 *  com.atlassian.applinks.api.auth.types.CorsAuthenticationProvider
 *  com.atlassian.applinks.api.auth.types.OAuthAuthenticationProvider
 *  com.atlassian.applinks.api.auth.types.TrustedAppsAuthenticationProvider
 *  com.atlassian.applinks.api.auth.types.TwoLeggedOAuthAuthenticationProvider
 *  com.atlassian.applinks.api.auth.types.TwoLeggedOAuthWithImpersonationAuthenticationProvider
 *  com.atlassian.applinks.host.spi.InternalHostApplication
 *  com.atlassian.applinks.spi.auth.AuthenticationConfigurationManager
 *  com.atlassian.applinks.spi.auth.AuthenticationProviderPluginModule
 *  com.atlassian.sal.api.ApplicationProperties
 *  javax.annotation.Nonnull
 *  javax.annotation.ParametersAreNonnullByDefault
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.annotation.Autowired
 */
package com.atlassian.applinks.analytics;

import com.atlassian.analytics.api.annotations.EventName;
import com.atlassian.applinks.api.ApplicationId;
import com.atlassian.applinks.api.ApplicationType;
import com.atlassian.applinks.api.ReadOnlyApplicationLink;
import com.atlassian.applinks.api.application.bamboo.BambooApplicationType;
import com.atlassian.applinks.api.application.bitbucket.BitbucketApplicationType;
import com.atlassian.applinks.api.application.confluence.ConfluenceApplicationType;
import com.atlassian.applinks.api.application.crowd.CrowdApplicationType;
import com.atlassian.applinks.api.application.fecru.FishEyeCrucibleApplicationType;
import com.atlassian.applinks.api.application.generic.GenericApplicationType;
import com.atlassian.applinks.api.application.jira.JiraApplicationType;
import com.atlassian.applinks.api.application.refapp.RefAppApplicationType;
import com.atlassian.applinks.api.auth.types.BasicAuthenticationProvider;
import com.atlassian.applinks.api.auth.types.CorsAuthenticationProvider;
import com.atlassian.applinks.api.auth.types.OAuthAuthenticationProvider;
import com.atlassian.applinks.api.auth.types.TrustedAppsAuthenticationProvider;
import com.atlassian.applinks.api.auth.types.TwoLeggedOAuthAuthenticationProvider;
import com.atlassian.applinks.api.auth.types.TwoLeggedOAuthWithImpersonationAuthenticationProvider;
import com.atlassian.applinks.core.AppLinkPluginUtil;
import com.atlassian.applinks.core.ApplinkStatus;
import com.atlassian.applinks.core.ApplinkStatusService;
import com.atlassian.applinks.core.ElevatedPermissionsService;
import com.atlassian.applinks.core.auth.AuthenticatorAccessor;
import com.atlassian.applinks.host.spi.InternalHostApplication;
import com.atlassian.applinks.internal.common.permission.PermissionLevel;
import com.atlassian.applinks.internal.status.error.ApplinkError;
import com.atlassian.applinks.internal.status.error.ApplinkErrorType;
import com.atlassian.applinks.spi.auth.AuthenticationConfigurationManager;
import com.atlassian.applinks.spi.auth.AuthenticationProviderPluginModule;
import com.atlassian.sal.api.ApplicationProperties;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class ApplinkStatusEventBuilderFactory {
    private final ApplinkStatusService applinkStatusService;
    private final AppLinkPluginUtil appLinkPluginUtil;
    private final ApplicationProperties applicationProperties;
    private final InternalHostApplication internalHostApplication;
    private final ElevatedPermissionsService elevatedPermissions;
    private final AuthenticatorAccessor authenticatorAccessor;
    private final AuthenticationConfigurationManager authenticationConfigurationManager;

    @Autowired
    public ApplinkStatusEventBuilderFactory(ApplinkStatusService applinkStatusService, AppLinkPluginUtil appLinkPluginUtil, ApplicationProperties applicationProperties, InternalHostApplication internalHostApplication, ElevatedPermissionsService elevatedPermissions, AuthenticatorAccessor authenticatorAccessor, AuthenticationConfigurationManager authenticationConfigurationManager) {
        this.applinkStatusService = applinkStatusService;
        this.appLinkPluginUtil = appLinkPluginUtil;
        this.applicationProperties = applicationProperties;
        this.internalHostApplication = internalHostApplication;
        this.elevatedPermissions = elevatedPermissions;
        this.authenticatorAccessor = authenticatorAccessor;
        this.authenticationConfigurationManager = authenticationConfigurationManager;
    }

    Builder createBuilder() {
        return new Builder(this.applinkStatusService, this.appLinkPluginUtil, this.applicationProperties, this.internalHostApplication, this.elevatedPermissions, this.authenticatorAccessor, this.authenticationConfigurationManager);
    }

    @EventName(value="applinks.status.applink")
    @ParametersAreNonnullByDefault
    static class ApplinkStatusApplinkEvent {
        private final String product;
        private final String remoteProduct;
        private final String remoteApplicationId;
        private final boolean isWorking;
        private final String applicationId;
        private final String errorCategory;
        private final String errorDetail;
        private final Map<String, Boolean> auth;

        public boolean getIsWorking() {
            return this.isWorking;
        }

        @Nonnull
        public String getErrorCategory() {
            return this.errorCategory;
        }

        @Nonnull
        public String getErrorDetail() {
            return this.errorDetail;
        }

        @Nonnull
        public String getApplicationId() {
            return this.applicationId;
        }

        @Nonnull
        public String getProduct() {
            return this.product;
        }

        @Nonnull
        public String getRemoteProduct() {
            return this.remoteProduct;
        }

        @Nonnull
        public String getRemoteApplicationId() {
            return this.remoteApplicationId;
        }

        @Nonnull
        public Map<String, Boolean> getAuth() {
            return this.auth;
        }

        ApplinkStatusApplinkEvent(String product, String applicationId, String remoteProduct, String remoteApplicationId, boolean isWorking, String errorCategory, String errorDetail, Map<String, Boolean> auth) {
            this.product = product;
            this.remoteProduct = remoteProduct;
            this.remoteApplicationId = remoteApplicationId;
            this.isWorking = isWorking;
            this.applicationId = applicationId;
            this.errorCategory = errorCategory;
            this.errorDetail = errorDetail;
            this.auth = auth;
        }
    }

    @EventName(value="applinks.status.snapshot")
    static class ApplinkStatusMainEvent {
        private final int total;
        private final int working;
        private final int failing;
        private final Map<String, Integer> occurrence;
        private final String buildNumber;
        private final String productVersion;
        private final String product;
        private final String applicationId;
        private final String pluginVersion;

        public int getTotal() {
            return this.total;
        }

        public int getWorking() {
            return this.working;
        }

        public int getFailing() {
            return this.failing;
        }

        public Map<String, Integer> getOccurrence() {
            return this.occurrence;
        }

        public String getBuildNumber() {
            return this.buildNumber;
        }

        public String getProductVersion() {
            return this.productVersion;
        }

        public String getProduct() {
            return this.product;
        }

        public String getApplicationId() {
            return this.applicationId;
        }

        public String getPluginVersion() {
            return this.pluginVersion;
        }

        ApplinkStatusMainEvent(int total, int working, int failing, Map<String, Integer> occurrence, String buildNumber, String productVersion, String product, String applicationId, String pluginVersion) {
            this.total = total;
            this.working = working;
            this.failing = failing;
            this.occurrence = occurrence;
            this.buildNumber = buildNumber;
            this.productVersion = productVersion;
            this.product = product;
            this.applicationId = applicationId;
            this.pluginVersion = pluginVersion;
        }
    }

    public static class Builder {
        private final Logger log = LoggerFactory.getLogger(Builder.class);
        private final ApplinkStatusService applinkStatusService;
        private final AppLinkPluginUtil appLinkPluginUtil;
        private final ApplicationProperties applicationProperties;
        private final InternalHostApplication internalHostApplication;
        private final ElevatedPermissionsService elevatedPermissions;
        private final AuthenticatorAccessor authenticatorAccessor;
        private final AuthenticationConfigurationManager authenticationConfigurationManager;
        private static final String BAMBOO_TYPE = "bamboo";
        private static final String BITBUCKET_TYPE = "bitbucket";
        private static final String CONFLUENCE_TYPE = "confluence";
        private static final String CROWD_TYPE = "crowd";
        private static final String FECRU_TYPE = "fecru";
        private static final String GENERIC_TYPE = "generic";
        private static final String JIRA_TYPE = "jira";
        private static final String REF_APP_TYPE = "refApp";
        private static final String UNKNOWN = "unknown";
        private int total = 0;
        private int working = 0;
        private int failing = 0;
        private final Map<String, Integer> occurenceCounter = new HashMap<String, Integer>();
        private final List<ApplinkStatusApplinkEvent> applinksDetails = new ArrayList<ApplinkStatusApplinkEvent>();

        Builder(ApplinkStatusService applinkStatusService, AppLinkPluginUtil appLinkPluginUtil, ApplicationProperties applicationProperties, InternalHostApplication internalHostApplication, ElevatedPermissionsService elevatedPermissions, AuthenticatorAccessor authenticatorAccessor, AuthenticationConfigurationManager authenticationConfigurationManager) {
            this.applinkStatusService = applinkStatusService;
            this.appLinkPluginUtil = appLinkPluginUtil;
            this.applicationProperties = applicationProperties;
            this.internalHostApplication = internalHostApplication;
            this.elevatedPermissions = elevatedPermissions;
            this.authenticatorAccessor = authenticatorAccessor;
            this.authenticationConfigurationManager = authenticationConfigurationManager;
            this.occurenceCounter.put(BAMBOO_TYPE, 0);
            this.occurenceCounter.put(BITBUCKET_TYPE, 0);
            this.occurenceCounter.put(CONFLUENCE_TYPE, 0);
            this.occurenceCounter.put(CROWD_TYPE, 0);
            this.occurenceCounter.put(FECRU_TYPE, 0);
            this.occurenceCounter.put(GENERIC_TYPE, 0);
            this.occurenceCounter.put(JIRA_TYPE, 0);
            this.occurenceCounter.put(REF_APP_TYPE, 0);
            this.occurenceCounter.put(UNKNOWN, 0);
        }

        void addApplink(ReadOnlyApplicationLink readOnlyApplicationLink) {
            ApplinkStatus applinkStatus;
            try {
                applinkStatus = this.elevatedPermissions.executeAs(PermissionLevel.ADMIN, () -> this.applinkStatusService.getApplinkStatus(readOnlyApplicationLink.getId()));
            }
            catch (Exception e) {
                this.log.error("Failed to retrieve Applink-Status for " + readOnlyApplicationLink.getName(), (Throwable)e);
                return;
            }
            this.incrementTotalCount();
            this.incrementStatusCount(applinkStatus);
            String type = this.getType(readOnlyApplicationLink.getType());
            this.incrementCountByType(type);
            this.applinksDetails.add(this.getApplinkDetailEvent(readOnlyApplicationLink, applinkStatus, this.applicationProperties.getPlatformId()));
        }

        ApplinkStatusMainEvent buildMainEvent() {
            return new ApplinkStatusMainEvent(this.total, this.working, this.failing, this.occurenceCounter, this.applicationProperties.getBuildNumber(), this.applicationProperties.getVersion(), this.applicationProperties.getPlatformId(), this.internalHostApplication.getId().get(), this.appLinkPluginUtil.getVersion().toString());
        }

        List<ApplinkStatusApplinkEvent> buildApplinkEvents() {
            return this.applinksDetails;
        }

        private void incrementCountByType(String type) {
            this.occurenceCounter.put(type, this.occurenceCounter.getOrDefault(type, 0) + 1);
        }

        private void incrementTotalCount() {
            ++this.total;
        }

        private void incrementStatusCount(ApplinkStatus applinkStatus) {
            if (applinkStatus.isWorking()) {
                ++this.working;
            } else {
                ++this.failing;
            }
        }

        private String getType(ApplicationType applicationType) {
            if (applicationType instanceof BambooApplicationType) {
                return BAMBOO_TYPE;
            }
            if (applicationType instanceof BitbucketApplicationType) {
                return BITBUCKET_TYPE;
            }
            if (applicationType instanceof ConfluenceApplicationType) {
                return CONFLUENCE_TYPE;
            }
            if (applicationType instanceof CrowdApplicationType) {
                return CROWD_TYPE;
            }
            if (applicationType instanceof FishEyeCrucibleApplicationType) {
                return FECRU_TYPE;
            }
            if (applicationType instanceof GenericApplicationType) {
                return GENERIC_TYPE;
            }
            if (applicationType instanceof JiraApplicationType) {
                return JIRA_TYPE;
            }
            if (applicationType instanceof RefAppApplicationType) {
                return REF_APP_TYPE;
            }
            return UNKNOWN;
        }

        private ApplinkStatusApplinkEvent getApplinkDetailEvent(ReadOnlyApplicationLink readOnlyApplicationLink, ApplinkStatus applinkStatus, String product) {
            Optional<ApplinkErrorType> error = this.getError(applinkStatus);
            return new ApplinkStatusApplinkEvent(product, this.internalHostApplication.getId().get(), this.getType(readOnlyApplicationLink.getType()), readOnlyApplicationLink.getId().get(), applinkStatus.isWorking(), error.map(e -> e.getCategory().name()).orElse(""), error.map(Enum::name).orElse(""), this.getConfiguredAuthTypes(readOnlyApplicationLink.getId()));
        }

        private Map<String, Boolean> getConfiguredAuthTypes(ApplicationId id) {
            HashMap<String, Boolean> authTypes = new HashMap<String, Boolean>();
            authTypes.put("basic", false);
            authTypes.put("trusted", false);
            authTypes.put("twoLo", false);
            authTypes.put("twoLoi", false);
            authTypes.put("threeLo", false);
            authTypes.put("cors", false);
            authTypes.put("other", false);
            for (AuthenticationProviderPluginModule module : this.authenticatorAccessor.getAllAuthenticationProviderPluginModules()) {
                Class providerClass = module.getAuthenticationProviderClass();
                if (!this.authenticationConfigurationManager.isConfigured(id, providerClass)) continue;
                if (BasicAuthenticationProvider.class.isAssignableFrom(providerClass)) {
                    authTypes.put("basic", true);
                    continue;
                }
                if (TrustedAppsAuthenticationProvider.class.isAssignableFrom(providerClass)) {
                    authTypes.put("trusted", true);
                    continue;
                }
                if (TwoLeggedOAuthAuthenticationProvider.class.isAssignableFrom(providerClass)) {
                    authTypes.put("twoLo", true);
                    continue;
                }
                if (TwoLeggedOAuthWithImpersonationAuthenticationProvider.class.isAssignableFrom(providerClass)) {
                    authTypes.put("twoLoi", true);
                    continue;
                }
                if (OAuthAuthenticationProvider.class.isAssignableFrom(providerClass)) {
                    authTypes.put("threeLo", true);
                    continue;
                }
                if (CorsAuthenticationProvider.class.isAssignableFrom(providerClass)) {
                    authTypes.put("cors", true);
                    continue;
                }
                authTypes.put("other", true);
            }
            return authTypes;
        }

        private Optional<ApplinkErrorType> getError(ApplinkStatus applinkStatus) {
            return Optional.ofNullable(applinkStatus.getError()).map(ApplinkError::getType);
        }
    }
}

