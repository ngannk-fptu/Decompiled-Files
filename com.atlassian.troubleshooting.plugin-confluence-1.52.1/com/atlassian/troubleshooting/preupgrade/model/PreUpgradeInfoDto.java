/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  javax.annotation.ParametersAreNonnullByDefault
 *  org.apache.commons.lang3.builder.EqualsBuilder
 *  org.apache.commons.lang3.builder.HashCodeBuilder
 */
package com.atlassian.troubleshooting.preupgrade.model;

import com.atlassian.troubleshooting.preupgrade.AnalyticsKey;
import com.atlassian.troubleshooting.preupgrade.accessors.PupEnvironmentAccessor;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

@ParametersAreNonnullByDefault
public class PreUpgradeInfoDto {
    public final List<Version> versions;
    public final Version selectedVersion;
    public final InstanceData instanceData;
    public final boolean stale;

    public PreUpgradeInfoDto(List<Version> versions, Version selectedVersion, InstanceData instanceData, boolean stale) {
        this.versions = Objects.requireNonNull(versions);
        this.selectedVersion = selectedVersion;
        this.instanceData = Objects.requireNonNull(instanceData);
        this.stale = stale;
    }

    public static class InstanceData {
        public final String platformId;
        public final String fullName;
        public final String productDisplayName;
        public final String upgradeUrl;
        public final Date releaseDate;
        public final String analyticsVersion;
        public final PupEnvironmentAccessor.OperatingSystem operatingSystem;

        public InstanceData(String platformId, String fullName, String productDisplayName, String upgradeUrl, @Nullable Date releaseDate, String analyticsVersion, PupEnvironmentAccessor.OperatingSystem operatingSystem) {
            this.platformId = Objects.requireNonNull(platformId);
            this.fullName = Objects.requireNonNull(fullName);
            this.productDisplayName = Objects.requireNonNull(productDisplayName);
            this.upgradeUrl = Objects.requireNonNull(upgradeUrl);
            this.releaseDate = releaseDate;
            this.analyticsVersion = Objects.requireNonNull(analyticsVersion);
            this.operatingSystem = Objects.requireNonNull(operatingSystem);
        }
    }

    public static class Version {
        public final String fullName;
        public final String shortName;
        public final String analyticsVersion;
        public final String upgradeInstructionsUrl;
        public final String installerUrl;
        public final String archiveUrl;
        public final Date releaseDate;
        public final String releaseNotesUrl;
        public final List<SupportedPlatformComponentStatus> supportedPlatforms;
        public final List<UpgradePathSection> upgradePath;
        public final List<String> modifiedFiles;
        public final boolean isZduAvailable;

        public Version(String fullName, String shortName, String analyticsVersion, String upgradeInstructionsUrl, String installerUrl, String archiveUrl, Date releaseDate, String releaseNotesUrl, List<SupportedPlatformComponentStatus> supportedPlatforms, List<UpgradePathSection> upgradePath, List<String> modifiedFiles, boolean isZduAvailable) {
            this.fullName = Objects.requireNonNull(fullName);
            this.shortName = Objects.requireNonNull(shortName);
            this.analyticsVersion = Objects.requireNonNull(analyticsVersion);
            this.upgradeInstructionsUrl = Objects.requireNonNull(upgradeInstructionsUrl);
            this.installerUrl = Objects.requireNonNull(installerUrl);
            this.archiveUrl = Objects.requireNonNull(archiveUrl);
            this.releaseDate = Objects.requireNonNull(releaseDate);
            this.releaseNotesUrl = Objects.requireNonNull(releaseNotesUrl);
            this.supportedPlatforms = Objects.requireNonNull(supportedPlatforms);
            this.upgradePath = Objects.requireNonNull(upgradePath);
            this.modifiedFiles = Objects.requireNonNull(modifiedFiles);
            this.isZduAvailable = isZduAvailable;
        }

        public String toString() {
            return "Version{fullName='" + this.fullName + '\'' + ", shortName='" + this.shortName + '\'' + ", analyticsVersion='" + this.analyticsVersion + '\'' + ", upgradeInstructionsUrl='" + this.upgradeInstructionsUrl + '\'' + ", installerUrl='" + this.installerUrl + '\'' + ", archiveUrl='" + this.archiveUrl + '\'' + ", releaseDate=" + this.releaseDate + ", releaseNotesUrl='" + this.releaseNotesUrl + '\'' + ", supportedPlatforms=" + this.supportedPlatforms + ", upgradePath=" + this.upgradePath + ", modifiedFiles=" + this.modifiedFiles + ", zduAvailable=" + this.isZduAvailable + '}';
        }

        public static class UpgradePathSection {
            public final AnalyticsKey analyticsKey;
            public final String title;
            public final List<UpgradePathSubSection> subSections;
            public final String suffix;

            public UpgradePathSection(AnalyticsKey analyticsKey, String title, List<UpgradePathSubSection> subSections) {
                this(analyticsKey, title, subSections, null);
            }

            public UpgradePathSection(AnalyticsKey analyticsKey, String title, List<UpgradePathSubSection> subSections, @Nullable String suffix) {
                this.analyticsKey = Objects.requireNonNull(analyticsKey);
                this.title = Objects.requireNonNull(title);
                this.subSections = Objects.requireNonNull(subSections);
                this.suffix = suffix;
            }

            public AnalyticsKey getAnalyticsKey() {
                return this.analyticsKey;
            }

            public String getTitle() {
                return this.title;
            }

            public String getSuffix() {
                return this.suffix;
            }

            public List<UpgradePathSubSection> getSubSections() {
                return this.subSections;
            }

            public static class UpgradePathSubSection {
                public final String description;
                public final List<String> steps;

                public UpgradePathSubSection(String description, List<String> steps) {
                    this.description = Objects.requireNonNull(description);
                    this.steps = Objects.requireNonNull(steps);
                }
            }
        }

        public static class SupportedPlatformComponentStatus {
            public final Status status;
            public final String message;
            public final AnalyticsKey analyticsKey;

            public SupportedPlatformComponentStatus(Status status, String message, AnalyticsKey analyticsKey) {
                this.status = Objects.requireNonNull(status);
                this.message = Objects.requireNonNull(message);
                this.analyticsKey = Objects.requireNonNull(analyticsKey);
            }

            public Status getStatus() {
                return this.status;
            }

            public String getMessage() {
                return this.message;
            }

            public AnalyticsKey getAnalyticsKey() {
                return this.analyticsKey;
            }

            public boolean equals(Object o) {
                if (this == o) {
                    return true;
                }
                if (o == null || this.getClass() != o.getClass()) {
                    return false;
                }
                SupportedPlatformComponentStatus that = (SupportedPlatformComponentStatus)o;
                return new EqualsBuilder().append((Object)this.status, (Object)that.status).append((Object)this.message, (Object)that.message).isEquals();
            }

            public int hashCode() {
                return new HashCodeBuilder(17, 37).append((Object)this.status).append((Object)this.message).toHashCode();
            }

            public String toString() {
                return "SupportedPlatformComponentStatus{status=" + (Object)((Object)this.status) + ", message='" + this.message + '\'' + '}';
            }

            public static enum Status {
                ERROR,
                WARNING,
                SUCCESS;

            }
        }
    }

    public static class Installer {
        public final String platform;
        public final String link;

        public Installer(String platform, String link) {
            this.platform = platform;
            this.link = link;
        }
    }

    public static enum LicenseType {
        JIRA_CORE("jira-core"),
        JSW("jira-software"),
        JSD("jira-servicedesk"),
        CONFLUENCE("conf"),
        BITBUCKET("bitbucket");

        private final String licenseProductKey;

        private LicenseType(String licenseProductKey) {
            this.licenseProductKey = Objects.requireNonNull(licenseProductKey);
        }

        public static Optional<LicenseType> fromKey(String key) {
            return Arrays.stream(LicenseType.values()).filter(v -> v.licenseProductKey.equals(key)).findFirst();
        }
    }

    public static enum ReleaseType {
        STANDARD,
        ENTERPRISE;

    }
}

