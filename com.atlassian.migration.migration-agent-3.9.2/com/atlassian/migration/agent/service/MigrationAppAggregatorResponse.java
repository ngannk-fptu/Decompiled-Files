/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.migration.app.dto.MigrationPath
 *  javax.annotation.ParametersAreNonnullByDefault
 *  org.codehaus.jackson.annotate.JsonCreator
 *  org.codehaus.jackson.annotate.JsonIgnoreProperties
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.migration.agent.service;

import com.atlassian.migration.app.MigratabliltyInfo;
import com.atlassian.migration.app.dto.MigrationPath;
import java.io.Serializable;
import java.util.List;
import javax.annotation.ParametersAreNonnullByDefault;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown=true)
@ParametersAreNonnullByDefault
public class MigrationAppAggregatorResponse
implements Serializable {
    private static final long serialVersionUID = 2L;
    @JsonProperty
    private final String name;
    @JsonProperty
    private final String latestVersion;
    @JsonProperty
    private final boolean hasCloud;
    @JsonProperty
    private final String cloudUrl;
    @JsonProperty
    private final String icon;
    @JsonProperty
    private final String cloudKey;
    @JsonProperty
    private final String featureDifference;
    @JsonProperty
    private final String migratable;
    @JsonProperty
    private final String featureDifferenceUrl;
    @JsonProperty
    private final String migrationPathInstructions;
    @JsonProperty
    private final String contactSupportUrl;
    @JsonProperty
    private final String privacyPolicyUrl;
    @JsonProperty
    private final String relativeInstallUrl;
    @JsonProperty
    private final String cloudMigrationAssistantCompatibility;
    @JsonProperty
    private final MigrationPath migrationPath;
    private final Integer aggregatorHttpErrorCode;
    @JsonProperty
    private final boolean topVendor;
    @JsonProperty
    private final String migrationRoadmapRequest;
    @JsonProperty
    private final List<MigratabliltyInfo.VersionRange> cloudMigrationAssistantCompatibilityRangeList;

    @JsonCreator
    public MigrationAppAggregatorResponse(@JsonProperty(value="name") String name, @JsonProperty(value="latestVersion") String latestVersion, @JsonProperty(value="hasCloud") boolean hasCloud, @JsonProperty(value="cloudUrl") String cloudUrl, @JsonProperty(value="icon") String icon, @JsonProperty(value="cloudKey") String cloudKey, @JsonProperty(value="featureDifference") String featureDifference, @JsonProperty(value="migratable") String migratable, @JsonProperty(value="featureDifferenceUrl") String featureDifferenceUrl, @JsonProperty(value="migrationPathInstructions") String migrationPathInstructions, @JsonProperty(value="contactSupport") String contactSupportUrl, @JsonProperty(value="privacyPolicyUrl") String privacyPolicyUrl, @JsonProperty(value="relativeCloudAppInstallUrl") String relativeInstallUrl, @JsonProperty(value="cloudMigrationAssistantCompatibility") String cloudMigrationAssistantCompatibility, @JsonProperty(value="migrationPath") MigrationPath migrationPath, @JsonProperty(value="topVendor") boolean topVendor, @JsonProperty(value="migrationRoadmapRequest") String migrationRoadmapRequest, @JsonProperty(value="cloudMigrationAssistantCompatibilityRangeList") List<MigratabliltyInfo.VersionRange> cloudMigrationAssistantCompatibilityRangeList) {
        this(name, latestVersion, hasCloud, cloudUrl, icon, cloudKey, featureDifference, migratable, featureDifferenceUrl, migrationPathInstructions, contactSupportUrl, privacyPolicyUrl, relativeInstallUrl, cloudMigrationAssistantCompatibility, migrationPath, null, topVendor, migrationRoadmapRequest, cloudMigrationAssistantCompatibilityRangeList);
    }

    private MigrationAppAggregatorResponse(int httpCode) {
        this(null, null, false, null, null, null, null, "NO", null, null, null, null, null, null, MigrationPath.UNKNOWN, httpCode, false, null, null);
    }

    private MigrationAppAggregatorResponse(String name, String latestVersion, boolean hasCloud, String cloudUrl, String icon, String cloudKey, String featureDifference, String migratable, String featureDifferenceUrl, String migrationPathInstructions, String contactSupportUrl, String privacyPolicyUrl, String relativeInstallUrl, String cloudMigrationAssistantCompatibility, MigrationPath migrationPath, Integer aggregatorHttpErrorCode, boolean topVendor, String migrationRoadmapRequest, List<MigratabliltyInfo.VersionRange> cloudMigrationAssistantCompatibilityRangeList) {
        this.name = name;
        this.latestVersion = latestVersion;
        this.hasCloud = hasCloud;
        this.cloudUrl = cloudUrl;
        this.icon = icon;
        this.cloudKey = cloudKey;
        this.featureDifference = featureDifference;
        this.migratable = migratable;
        this.featureDifferenceUrl = featureDifferenceUrl;
        this.migrationPathInstructions = migrationPathInstructions;
        this.contactSupportUrl = contactSupportUrl;
        this.privacyPolicyUrl = privacyPolicyUrl;
        this.relativeInstallUrl = relativeInstallUrl;
        this.cloudMigrationAssistantCompatibility = cloudMigrationAssistantCompatibility;
        this.migrationPath = migrationPath;
        this.aggregatorHttpErrorCode = aggregatorHttpErrorCode;
        this.topVendor = topVendor;
        this.migrationRoadmapRequest = migrationRoadmapRequest;
        this.cloudMigrationAssistantCompatibilityRangeList = cloudMigrationAssistantCompatibilityRangeList;
    }

    public static MigrationAppAggregatorResponse empty(int httpCode) {
        return new MigrationAppAggregatorResponse(httpCode);
    }

    public String getName() {
        return this.name;
    }

    public String getIcon() {
        return this.icon;
    }

    public boolean hasCloud() {
        return this.hasCloud;
    }

    public String getCloudUrl() {
        return this.cloudUrl;
    }

    public String getLatestVersion() {
        return this.latestVersion;
    }

    public String getCloudKey() {
        return this.cloudKey;
    }

    public String getFeatureDifference() {
        return this.featureDifference;
    }

    public String getMigratable() {
        return this.migratable;
    }

    public String getFeatureDifferenceUrl() {
        return this.featureDifferenceUrl;
    }

    public String getMigrationPathInstructions() {
        return this.migrationPathInstructions;
    }

    public String getContactSupportUrl() {
        return this.contactSupportUrl;
    }

    public Integer getAggregatorHttpErrorCode() {
        return this.aggregatorHttpErrorCode;
    }

    public String getPrivacyPolicyUrl() {
        return this.privacyPolicyUrl;
    }

    public String getRelativeInstallUrl() {
        return this.relativeInstallUrl;
    }

    public String getCloudMigrationAssistantCompatibility() {
        return this.cloudMigrationAssistantCompatibility;
    }

    public MigrationPath getMigrationPath() {
        return this.migrationPath;
    }

    public boolean isTopVendor() {
        return this.topVendor;
    }

    public String getMigrationRoadmapRequest() {
        return this.migrationRoadmapRequest;
    }

    public List<MigratabliltyInfo.VersionRange> getCloudMigrationAssistantCompatibilityRangeList() {
        return this.cloudMigrationAssistantCompatibilityRangeList;
    }
}

