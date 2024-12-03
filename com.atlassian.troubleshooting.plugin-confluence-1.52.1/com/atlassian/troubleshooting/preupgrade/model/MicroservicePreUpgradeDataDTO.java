/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  javax.annotation.ParametersAreNonnullByDefault
 *  org.apache.commons.lang3.ObjectUtils
 *  org.apache.commons.lang3.builder.EqualsBuilder
 *  org.apache.commons.lang3.builder.HashCodeBuilder
 *  org.codehaus.jackson.annotate.JsonCreator
 *  org.codehaus.jackson.annotate.JsonIgnoreProperties
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.troubleshooting.preupgrade.model;

import com.atlassian.troubleshooting.healthcheck.model.DbType;
import com.atlassian.troubleshooting.healthcheck.model.SearchDistribution;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

@ParametersAreNonnullByDefault
@JsonIgnoreProperties(ignoreUnknown=true)
public class MicroservicePreUpgradeDataDTO {
    @Nonnull
    public final String product;
    @Nonnull
    public final List<Version> versions;
    @Nonnull
    public final List<SupportedPlatform> supportedPlatforms;

    @JsonCreator
    public MicroservicePreUpgradeDataDTO(@JsonProperty(value="product") String product, @JsonProperty(value="versions") List<Version> versions, @JsonProperty(value="supportedPlatforms") List<SupportedPlatform> supportedPlatforms) {
        this.product = Objects.requireNonNull(product);
        this.versions = Objects.requireNonNull(versions);
        this.supportedPlatforms = Objects.requireNonNull(supportedPlatforms);
    }

    @Nonnull
    public String getProduct() {
        return this.product;
    }

    @Nonnull
    public List<Version> getVersions() {
        return this.versions;
    }

    @Nonnull
    public List<SupportedPlatform> getSupportedPlatforms() {
        return this.supportedPlatforms;
    }

    @JsonIgnoreProperties(ignoreUnknown=true)
    public static class SupportedPlatform {
        @Nonnull
        public final Version version;
        @Nonnull
        public final Map<DbType, List<String>> databases;
        @Nonnull
        public final List<String> java;
        @Nonnull
        public final Optional<List<String>> git;
        @Nonnull
        public final Optional<Map<SearchDistribution, List<String>>> searches;
        @Nonnull
        public final String docUrl;
        @Nonnull
        public final List<String> zduBaseVersions;

        @JsonCreator
        public SupportedPlatform(@JsonProperty(value="version") Version version, @JsonProperty(value="databases") Map<DbType, List<String>> databases, @JsonProperty(value="java") List<String> java, @JsonProperty(value="git") List<String> git, @JsonProperty(value="searches") Map<SearchDistribution, List<String>> searches, @JsonProperty(value="docUrl") String docUrl, @JsonProperty(value="zduBaseVersions") List<String> zduBaseVersions) {
            this.version = Objects.requireNonNull(version);
            this.databases = Objects.requireNonNull(databases);
            this.java = Objects.requireNonNull(java);
            this.git = Optional.ofNullable(git);
            this.searches = Optional.ofNullable(searches);
            this.docUrl = Objects.requireNonNull(docUrl);
            this.zduBaseVersions = (List)ObjectUtils.firstNonNull((Object[])new List[]{zduBaseVersions, new ArrayList()});
        }

        @Nonnull
        public Version getVersion() {
            return this.version;
        }

        @Nonnull
        public Map<DbType, List<String>> getDatabases() {
            return this.databases;
        }

        @Nonnull
        public List<String> getJava() {
            return this.java;
        }

        @Nonnull
        public String getDocUrl() {
            return this.docUrl;
        }

        @Nonnull
        public List<String> getZduBaseVersions() {
            return this.zduBaseVersions;
        }

        @Nonnull
        public List<String> getGit() {
            return this.git.orElse(Collections.emptyList());
        }

        @Nonnull
        public Map<SearchDistribution, List<String>> getSearches() {
            return this.searches.orElse(Collections.emptyMap());
        }

        @JsonIgnoreProperties(ignoreUnknown=true)
        public static class Version {
            public final int major;
            public final int minor;

            @JsonCreator
            public Version(@JsonProperty(value="major") int major, @JsonProperty(value="minor") int minor) {
                this.major = major;
                this.minor = minor;
            }

            public boolean isSameMajorAndMinorVersion(Version.VersionNumber versionNumber) {
                return versionNumber.getMajor() == this.major && versionNumber.getMinor() == this.minor;
            }

            public boolean equals(Object o) {
                if (this == o) {
                    return true;
                }
                if (o == null || this.getClass() != o.getClass()) {
                    return false;
                }
                Version version = (Version)o;
                return new EqualsBuilder().append(this.major, version.major).append(this.minor, version.minor).isEquals();
            }

            public int hashCode() {
                return new HashCodeBuilder(17, 37).append(this.major).append(this.minor).toHashCode();
            }

            public String toString() {
                return this.major + "." + this.minor;
            }
        }
    }

    @JsonIgnoreProperties(ignoreUnknown=true)
    public static class Version {
        private final boolean enterprise;
        @Nonnull
        private final VersionNumber version;
        @Nonnull
        private final Date releaseDate;
        @Nonnull
        private final List<Note> releaseNotes;
        @Nonnull
        private final String upgradeInstructionsUrl;
        @Nonnull
        private final String stagingEnvironmentInstructionsUrl;
        @Nonnull
        private final List<Note> upgradeNotes;
        @Nonnull
        private final List<Installer> windowsInstallerDistribution;
        @Nonnull
        private final List<Installer> linuxInstallerDistribution;
        @Nonnull
        private final List<Archive> windowsArchiveDistribution;
        @Nonnull
        private final List<Archive> linuxArchiveDistribution;
        private final boolean hidden;

        @JsonCreator
        public Version(@JsonProperty(value="enterprise") boolean enterprise, @JsonProperty(value="version") VersionNumber version, @JsonProperty(value="releaseDate") Date releaseDate, @JsonProperty(value="releaseNotes") List<Note> releaseNotes, @JsonProperty(value="upgradeInstructionsUrl") String upgradeInstructionsUrl, @JsonProperty(value="stagingEnvironmentInstructionsUrl") String stagingEnvironmentInstructionsUrl, @JsonProperty(value="upgradeNotes") List<Note> upgradeNotes, @JsonProperty(value="windowsInstallerDistribution") List<Installer> windowsInstallerDistribution, @JsonProperty(value="linuxInstallerDistribution") List<Installer> linuxInstallerDistribution, @JsonProperty(value="windowsArchiveDistribution") List<Archive> windowsArchiveDistribution, @JsonProperty(value="linuxArchiveDistribution") List<Archive> linuxArchiveDistribution, @JsonProperty(value="hidden") boolean hidden) {
            this.hidden = hidden;
            this.enterprise = enterprise;
            this.version = Objects.requireNonNull(version);
            this.releaseDate = Objects.requireNonNull(releaseDate);
            this.releaseNotes = Objects.requireNonNull(releaseNotes);
            this.upgradeInstructionsUrl = Objects.requireNonNull(upgradeInstructionsUrl);
            this.stagingEnvironmentInstructionsUrl = Objects.requireNonNull(stagingEnvironmentInstructionsUrl);
            this.upgradeNotes = Objects.requireNonNull(upgradeNotes);
            this.windowsInstallerDistribution = Objects.requireNonNull(windowsInstallerDistribution);
            this.linuxInstallerDistribution = Objects.requireNonNull(linuxInstallerDistribution);
            this.windowsArchiveDistribution = Objects.requireNonNull(windowsArchiveDistribution);
            this.linuxArchiveDistribution = Objects.requireNonNull(linuxArchiveDistribution);
        }

        public boolean isEnterprise() {
            return this.enterprise;
        }

        @Nonnull
        public VersionNumber getVersion() {
            return this.version;
        }

        @Nonnull
        public Date getReleaseDate() {
            return this.releaseDate;
        }

        @Nonnull
        public List<Note> getReleaseNotes() {
            return this.releaseNotes;
        }

        @Nonnull
        public List<Note> getUpgradeNotes() {
            return this.upgradeNotes;
        }

        @Nonnull
        public List<Installer> getWindowsInstallerDistribution() {
            return this.windowsInstallerDistribution;
        }

        @Nonnull
        public List<Installer> getLinuxInstallerDistribution() {
            return this.linuxInstallerDistribution;
        }

        @Nonnull
        public List<Archive> getWindowsArchiveDistribution() {
            return this.windowsArchiveDistribution;
        }

        @Nonnull
        public List<Archive> getLinuxArchiveDistribution() {
            return this.linuxArchiveDistribution;
        }

        @Nonnull
        public String getUpgradeInstructionsUrl() {
            return this.upgradeInstructionsUrl;
        }

        @Nonnull
        public String getStagingEnvironmentInstructionsUrl() {
            return this.stagingEnvironmentInstructionsUrl;
        }

        public boolean getHidden() {
            return this.hidden;
        }

        @JsonIgnoreProperties(ignoreUnknown=true)
        public static class Archive {
            @Nonnull
            public final SubProduct subProduct;
            @Nonnull
            public final String link;

            @JsonCreator
            public Archive(@JsonProperty(value="subProduct") SubProduct subProduct, @JsonProperty(value="link") String link) {
                this.subProduct = subProduct;
                this.link = link;
            }

            @Nonnull
            public SubProduct getSubProduct() {
                return this.subProduct;
            }

            @Nonnull
            public String getLink() {
                return this.link;
            }
        }

        @JsonIgnoreProperties(ignoreUnknown=true)
        public static class Installer {
            @Nonnull
            public final Platform platform;
            @Nonnull
            public final SubProduct subProduct;
            @Nonnull
            public final String link;

            @JsonCreator
            public Installer(@JsonProperty(value="platform") Platform platform, @JsonProperty(value="subProduct") SubProduct subProduct, @JsonProperty(value="link") String link) {
                this.platform = Objects.requireNonNull(platform);
                this.subProduct = Objects.requireNonNull(subProduct);
                this.link = Objects.requireNonNull(link);
            }
        }

        @JsonIgnoreProperties(ignoreUnknown=true)
        public static class VersionNumber {
            public final int major;
            public final int minor;
            public final int bugfix;

            @JsonCreator
            public VersionNumber(@JsonProperty(value="major") int major, @JsonProperty(value="minor") int minor, @JsonProperty(value="bugfix") int bugfix) {
                this.major = major;
                this.minor = minor;
                this.bugfix = bugfix;
            }

            public int getMajor() {
                return this.major;
            }

            public int getMinor() {
                return this.minor;
            }

            public int getBugfix() {
                return this.bugfix;
            }

            public String getAnalyticsString() {
                return String.format("%03d.%03d.%03d", this.major, this.minor, this.bugfix);
            }

            public boolean equals(Object o) {
                if (this == o) {
                    return true;
                }
                if (o == null || this.getClass() != o.getClass()) {
                    return false;
                }
                VersionNumber that = (VersionNumber)o;
                return new EqualsBuilder().append(this.major, that.major).append(this.minor, that.minor).append(this.bugfix, that.bugfix).isEquals();
            }

            public boolean equalsVersion(com.atlassian.troubleshooting.stp.spi.Version version) {
                return version.getMajor() == this.major && version.getMinor() == this.minor && version.getMicro() == this.bugfix;
            }

            public int hashCode() {
                return new HashCodeBuilder(17, 37).append(this.major).append(this.minor).append(this.bugfix).toHashCode();
            }

            public String toString() {
                return this.major + "." + this.minor + "." + this.bugfix;
            }
        }

        @JsonIgnoreProperties(ignoreUnknown=true)
        public static class Note {
            @Nonnull
            public final SubProduct subProduct;
            @Nonnull
            public final String link;

            @JsonCreator
            public Note(@JsonProperty(value="subProduct") SubProduct subProduct, @JsonProperty(value="link") String link) {
                this.subProduct = Objects.requireNonNull(subProduct);
                this.link = Objects.requireNonNull(link);
            }
        }

        public static enum Platform {
            x64,
            x32;

        }

        public static enum SubProduct {
            JSW{

                @Override
                public <T> T accept(SubProductVisitor<T> visitor) {
                    return visitor.visitJSW();
                }
            }
            ,
            JC{

                @Override
                public <T> T accept(SubProductVisitor<T> visitor) {
                    return visitor.visitJC();
                }
            }
            ,
            CONFLUENCE{

                @Override
                public <T> T accept(SubProductVisitor<T> visitor) {
                    return visitor.visitConfluence();
                }
            }
            ,
            BITBUCKET{

                @Override
                public <T> T accept(SubProductVisitor<T> visitor) {
                    return visitor.visitBitbucket();
                }
            };


            public abstract <T> T accept(SubProductVisitor<T> var1);

            public static interface SubProductVisitor<T> {
                public T visitJSW();

                public T visitJC();

                public T visitConfluence();

                public T visitBitbucket();
            }
        }
    }
}

