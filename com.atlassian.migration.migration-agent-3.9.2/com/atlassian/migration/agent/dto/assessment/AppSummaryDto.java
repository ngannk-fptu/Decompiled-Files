/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.migration.agent.dto.assessment;

import com.atlassian.migration.agent.dto.assessment.FeatureDifferenceState;
import com.atlassian.migration.agent.dto.assessment.ReliabilityState;
import com.atlassian.migration.agent.entity.AppAssessmentUserAttributedStatus;
import com.atlassian.migration.app.AppCloudCapability;
import org.codehaus.jackson.annotate.JsonProperty;

public class AppSummaryDto {
    @JsonProperty(value="key")
    private String key;
    @JsonProperty(value="name")
    private String name;
    @JsonProperty(value="logoUrl")
    private String logoUrl;
    @JsonProperty(value="cloudUrl")
    private String cloudUrl;
    @JsonProperty(value="cloudKey")
    private String cloudKey;
    @JsonProperty(value="isEnabled")
    private boolean enabled;
    @JsonProperty(value="hasCloudVersion")
    private boolean cloudVersion;
    @JsonProperty(value="hasFeatureDifferences")
    private FeatureDifferenceState featureDifference;
    @JsonProperty(value="canBeMigrated")
    private AppCloudCapability canBeMigrated;
    @JsonProperty(value="featureDifferencesUrl")
    private String featureDifferencesUrl;
    @JsonProperty(value="migrationPathInstructionsUrl")
    private String migrationInstructionsUrl;
    @JsonProperty(value="contactVendorUrl")
    private String contactVendorUrl;
    @JsonProperty(value="migrationStatus")
    private AppAssessmentUserAttributedStatus migrationStatus = AppAssessmentUserAttributedStatus.Unassigned;
    @JsonProperty(value="migrationNotes")
    private String migrationNotes;
    @JsonProperty(value="alternativeAppKey")
    private String alternativeAppKey;
    @JsonProperty(value="upgradeAppUrl")
    private String upgradeAppUrl;
    @JsonProperty(value="reliabilityState")
    private ReliabilityState reliabilityState;
    @JsonProperty(value="migrationRoadmapRequest")
    private String migrationRoadmapRequest;
    @JsonProperty(value="vendorName")
    private String vendorName;

    public AppSummaryDto() {
    }

    public AppSummaryDto(String key, String name, String logoUrl, String cloudUrl, String cloudKey, boolean enabled, boolean cloudVersion, FeatureDifferenceState featureDifference, AppCloudCapability canBeMigrated, String featureDifferencesUrl, String migrationInstructionsUrl, String contactVendorUrl, AppAssessmentUserAttributedStatus migrationStatus, String migrationNotes, String alternativeAppKey, String upgradeAppUrl, ReliabilityState reliabilityState, String migrationRoadmapRequest, String vendorName) {
        this.key = key;
        this.name = name;
        this.logoUrl = logoUrl;
        this.cloudUrl = cloudUrl;
        this.cloudKey = cloudKey;
        this.enabled = enabled;
        this.cloudVersion = cloudVersion;
        this.featureDifference = featureDifference;
        this.canBeMigrated = canBeMigrated;
        this.featureDifferencesUrl = featureDifferencesUrl;
        this.migrationInstructionsUrl = migrationInstructionsUrl;
        this.contactVendorUrl = contactVendorUrl;
        this.migrationStatus = migrationStatus;
        this.migrationNotes = migrationNotes;
        this.alternativeAppKey = alternativeAppKey;
        this.upgradeAppUrl = upgradeAppUrl;
        this.reliabilityState = reliabilityState;
        this.migrationRoadmapRequest = migrationRoadmapRequest;
        this.vendorName = vendorName;
    }

    public String getKey() {
        return this.key;
    }

    public String getName() {
        return this.name;
    }

    public String getLogoUrl() {
        return this.logoUrl;
    }

    public String getCloudUrl() {
        return this.cloudUrl;
    }

    public String getCloudKey() {
        return this.cloudKey;
    }

    public boolean isEnabled() {
        return this.enabled;
    }

    public boolean isCloudVersion() {
        return this.cloudVersion;
    }

    public FeatureDifferenceState getFeatureDifference() {
        return this.featureDifference;
    }

    public AppCloudCapability getCanBeMigrated() {
        return this.canBeMigrated;
    }

    public String getFeatureDifferencesUrl() {
        return this.featureDifferencesUrl;
    }

    public String getMigrationInstructionsUrl() {
        return this.migrationInstructionsUrl;
    }

    public String getContactVendorUrl() {
        return this.contactVendorUrl;
    }

    public AppAssessmentUserAttributedStatus getMigrationStatus() {
        return this.migrationStatus;
    }

    public String getMigrationNotes() {
        return this.migrationNotes;
    }

    public String getAlternativeAppKey() {
        return this.alternativeAppKey;
    }

    public String getUpgradeAppUrl() {
        return this.upgradeAppUrl;
    }

    public ReliabilityState getReliabilityState() {
        return this.reliabilityState;
    }

    public String getMigrationRoadmapRequest() {
        return this.migrationRoadmapRequest;
    }

    public String getVendorName() {
        return this.vendorName;
    }
}

