/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.migration.utils.MigrationStatusCalculator$OverallAppMigrationStatus
 *  javax.annotation.Nullable
 *  lombok.Generated
 *  org.codehaus.jackson.annotate.JsonCreator
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.migration.agent.dto;

import com.atlassian.migration.utils.MigrationStatusCalculator;
import java.util.Collections;
import java.util.List;
import javax.annotation.Nullable;
import lombok.Generated;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

public class AppsProgressDto {
    @Nullable
    @JsonProperty
    private final MigrationStatusCalculator.OverallAppMigrationStatus aggregateStatus;
    @JsonProperty
    private final List<App> apps;

    @JsonCreator
    public AppsProgressDto(@Nullable @JsonProperty(value="aggregateStatus") MigrationStatusCalculator.OverallAppMigrationStatus aggregateStatus, @JsonProperty(value="apps") List<App> apps) {
        this.aggregateStatus = aggregateStatus;
        this.apps = apps;
    }

    public static AppsProgressDto empty() {
        return AppsProgressDto.builder().aggregateStatus(null).apps(Collections.emptyList()).build();
    }

    @Generated
    public static AppsProgressDtoBuilder builder() {
        return new AppsProgressDtoBuilder();
    }

    @Nullable
    @Generated
    public MigrationStatusCalculator.OverallAppMigrationStatus getAggregateStatus() {
        return this.aggregateStatus;
    }

    @Generated
    public List<App> getApps() {
        return this.apps;
    }

    @Generated
    public static class AppsProgressDtoBuilder {
        @Generated
        private MigrationStatusCalculator.OverallAppMigrationStatus aggregateStatus;
        @Generated
        private List<App> apps;

        @Generated
        AppsProgressDtoBuilder() {
        }

        @Generated
        public AppsProgressDtoBuilder aggregateStatus(@Nullable MigrationStatusCalculator.OverallAppMigrationStatus aggregateStatus) {
            this.aggregateStatus = aggregateStatus;
            return this;
        }

        @Generated
        public AppsProgressDtoBuilder apps(List<App> apps) {
            this.apps = apps;
            return this;
        }

        @Generated
        public AppsProgressDto build() {
            return new AppsProgressDto(this.aggregateStatus, this.apps);
        }

        @Generated
        public String toString() {
            return "AppsProgressDto.AppsProgressDtoBuilder(aggregateStatus=" + this.aggregateStatus + ", apps=" + this.apps + ")";
        }
    }

    public static class App {
        @JsonProperty
        private final String serverAppName;
        @JsonProperty
        private final String serverAppKey;
        @JsonProperty
        private final String containerId;
        @JsonProperty
        private final String cloudAppKey;
        @JsonProperty
        private final Integer completionPercent;
        @JsonProperty
        private final String status;
        @JsonProperty
        private final String statusMessage;
        @JsonProperty
        private final Long lastUpdatedAt;
        @JsonProperty
        private final String appVendorName;
        @JsonProperty
        private final String contactVendorUrl;

        @JsonCreator
        public App(@JsonProperty(value="serverAppName") String serverAppName, @JsonProperty(value="serverAppKey") String serverAppKey, @JsonProperty(value="containerId") String containerId, @JsonProperty(value="cloudAppKey") String cloudAppKey, @JsonProperty(value="completionPercent") Integer completionPercent, @JsonProperty(value="status") String status, @JsonProperty(value="statusMessage") String statusMessage, @JsonProperty(value="lastUpdatedAt") Long lastUpdatedAt, @JsonProperty(value="appVendorName") String appVendorName, @JsonProperty(value="contactVendorUrl") String contactVendorUrl) {
            this.serverAppName = serverAppName;
            this.serverAppKey = serverAppKey;
            this.containerId = containerId;
            this.cloudAppKey = cloudAppKey;
            this.completionPercent = completionPercent;
            this.status = status;
            this.statusMessage = statusMessage;
            this.lastUpdatedAt = lastUpdatedAt;
            this.appVendorName = appVendorName;
            this.contactVendorUrl = contactVendorUrl;
        }

        public static App ready() {
            return App.builder().status("READY").completionPercent(0).build();
        }

        public String toString() {
            return "App{serverAppKey='" + this.serverAppKey + '\'' + ", status='" + this.status + '\'' + '}';
        }

        @Generated
        public static AppBuilder builder() {
            return new AppBuilder();
        }

        @Generated
        public String getServerAppName() {
            return this.serverAppName;
        }

        @Generated
        public String getServerAppKey() {
            return this.serverAppKey;
        }

        @Generated
        public String getContainerId() {
            return this.containerId;
        }

        @Generated
        public String getCloudAppKey() {
            return this.cloudAppKey;
        }

        @Generated
        public Integer getCompletionPercent() {
            return this.completionPercent;
        }

        @Generated
        public String getStatus() {
            return this.status;
        }

        @Generated
        public String getStatusMessage() {
            return this.statusMessage;
        }

        @Generated
        public Long getLastUpdatedAt() {
            return this.lastUpdatedAt;
        }

        @Generated
        public String getAppVendorName() {
            return this.appVendorName;
        }

        @Generated
        public String getContactVendorUrl() {
            return this.contactVendorUrl;
        }

        @Generated
        public static class AppBuilder {
            @Generated
            private String serverAppName;
            @Generated
            private String serverAppKey;
            @Generated
            private String containerId;
            @Generated
            private String cloudAppKey;
            @Generated
            private Integer completionPercent;
            @Generated
            private String status;
            @Generated
            private String statusMessage;
            @Generated
            private Long lastUpdatedAt;
            @Generated
            private String appVendorName;
            @Generated
            private String contactVendorUrl;

            @Generated
            AppBuilder() {
            }

            @Generated
            public AppBuilder serverAppName(String serverAppName) {
                this.serverAppName = serverAppName;
                return this;
            }

            @Generated
            public AppBuilder serverAppKey(String serverAppKey) {
                this.serverAppKey = serverAppKey;
                return this;
            }

            @Generated
            public AppBuilder containerId(String containerId) {
                this.containerId = containerId;
                return this;
            }

            @Generated
            public AppBuilder cloudAppKey(String cloudAppKey) {
                this.cloudAppKey = cloudAppKey;
                return this;
            }

            @Generated
            public AppBuilder completionPercent(Integer completionPercent) {
                this.completionPercent = completionPercent;
                return this;
            }

            @Generated
            public AppBuilder status(String status) {
                this.status = status;
                return this;
            }

            @Generated
            public AppBuilder statusMessage(String statusMessage) {
                this.statusMessage = statusMessage;
                return this;
            }

            @Generated
            public AppBuilder lastUpdatedAt(Long lastUpdatedAt) {
                this.lastUpdatedAt = lastUpdatedAt;
                return this;
            }

            @Generated
            public AppBuilder appVendorName(String appVendorName) {
                this.appVendorName = appVendorName;
                return this;
            }

            @Generated
            public AppBuilder contactVendorUrl(String contactVendorUrl) {
                this.contactVendorUrl = contactVendorUrl;
                return this;
            }

            @Generated
            public App build() {
                return new App(this.serverAppName, this.serverAppKey, this.containerId, this.cloudAppKey, this.completionPercent, this.status, this.statusMessage, this.lastUpdatedAt, this.appVendorName, this.contactVendorUrl);
            }

            @Generated
            public String toString() {
                return "AppsProgressDto.App.AppBuilder(serverAppName=" + this.serverAppName + ", serverAppKey=" + this.serverAppKey + ", containerId=" + this.containerId + ", cloudAppKey=" + this.cloudAppKey + ", completionPercent=" + this.completionPercent + ", status=" + this.status + ", statusMessage=" + this.statusMessage + ", lastUpdatedAt=" + this.lastUpdatedAt + ", appVendorName=" + this.appVendorName + ", contactVendorUrl=" + this.contactVendorUrl + ")";
            }
        }
    }
}

