/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.analytics.api.annotations.EventName
 *  javax.annotation.Nullable
 *  org.apache.commons.lang3.SerializationUtils
 *  org.apache.commons.lang3.builder.ToStringBuilder
 *  org.apache.commons.lang3.builder.ToStringStyle
 */
package com.atlassian.troubleshooting.jfr.event;

import com.atlassian.analytics.api.annotations.EventName;
import com.atlassian.troubleshooting.jfr.config.JfcTemplateDetails;
import com.atlassian.troubleshooting.jfr.domain.ConfigurationDetails;
import com.atlassian.troubleshooting.jfr.domain.JfrConfigurationPropertiesDto;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.Nullable;
import org.apache.commons.lang3.SerializationUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

@EventName(value="atst.jfr.settings.state")
public class JfrSettingsStateAnalyticsEvent {
    private final Long maxAge;
    private final Long maxSize;
    private final Integer numberOfFilesToRemain;
    private final String recordingPath;
    private final String threadDumpPath;
    private final String dumpCronExpression;
    private final String jfrTemplatePath;
    private final String jfcTemplateName;
    private final String jfcTemplateDescription;
    private final String jfcTemplateLabel;
    private final String jfcTemplateProvider;
    private final Map<String, String> jfcTemplateSettings;

    private JfrSettingsStateAnalyticsEvent(Builder builder) {
        this.maxAge = builder.maxAge;
        this.maxSize = builder.maxSize;
        this.numberOfFilesToRemain = builder.numberOfFilesToRemain;
        this.recordingPath = builder.recordingPath;
        this.threadDumpPath = builder.threadDumpPath;
        this.dumpCronExpression = builder.dumpCronExpression;
        this.jfrTemplatePath = builder.jfrTemplatePath;
        this.jfcTemplateName = builder.jfcTemplateName;
        this.jfcTemplateDescription = builder.jfcTemplateDescription;
        this.jfcTemplateLabel = builder.jfcTemplateLabel;
        this.jfcTemplateProvider = builder.jfcTemplateProvider;
        this.jfcTemplateSettings = builder.jfcTemplateSettings;
    }

    public Long getMaxAge() {
        return this.maxAge;
    }

    public Long getMaxSize() {
        return this.maxSize;
    }

    public Integer getNumberOfFilesToRemain() {
        return this.numberOfFilesToRemain;
    }

    public String getRecordingPath() {
        return this.recordingPath;
    }

    public String getThreadDumpPath() {
        return this.threadDumpPath;
    }

    public String getDumpCronExpression() {
        return this.dumpCronExpression;
    }

    public String getJfrTemplatePath() {
        return this.jfrTemplatePath;
    }

    public String getJfcTemplateName() {
        return this.jfcTemplateName;
    }

    public String getJfcTemplateDescription() {
        return this.jfcTemplateDescription;
    }

    public String getJfcTemplateLabel() {
        return this.jfcTemplateLabel;
    }

    public String getJfcTemplateProvider() {
        return this.jfcTemplateProvider;
    }

    public Map<String, String> getJfcTemplateSettings() {
        return this.jfcTemplateSettings;
    }

    public String toString() {
        return ToStringBuilder.reflectionToString((Object)this, (ToStringStyle)ToStringStyle.MULTI_LINE_STYLE);
    }

    public static JfrSettingsStateAnalyticsEvent from(ConfigurationDetails configurationDetails) {
        if (configurationDetails == null) {
            return new Builder().build();
        }
        return new Builder().withJfrProperties(configurationDetails.getJfrProperties()).withJfcTemplateDetails(configurationDetails.getJfcTemplate()).build();
    }

    private static class Builder {
        private Long maxAge;
        private Long maxSize;
        private Integer numberOfFilesToRemain;
        private String recordingPath;
        private String threadDumpPath;
        private String dumpCronExpression;
        private String jfrTemplatePath;
        private String jfcTemplateName;
        private String jfcTemplateDescription;
        private String jfcTemplateLabel;
        private String jfcTemplateProvider;
        private Map<String, String> jfcTemplateSettings;

        private Builder() {
        }

        private JfrSettingsStateAnalyticsEvent build() {
            return new JfrSettingsStateAnalyticsEvent(this);
        }

        private Builder withJfrProperties(@Nullable JfrConfigurationPropertiesDto jfrConfigurationProperties) {
            if (jfrConfigurationProperties == null) {
                return this;
            }
            this.maxAge = jfrConfigurationProperties.getMaxAge();
            this.maxSize = jfrConfigurationProperties.getMaxSize();
            this.numberOfFilesToRemain = jfrConfigurationProperties.getNumberOfFilesToRemain();
            this.recordingPath = jfrConfigurationProperties.getRecordingPath();
            this.threadDumpPath = jfrConfigurationProperties.getThreadDumpPath();
            this.dumpCronExpression = jfrConfigurationProperties.getDumpCronExpression();
            this.jfrTemplatePath = jfrConfigurationProperties.getJfrTemplatePath();
            return this;
        }

        private Builder withJfcTemplateDetails(@Nullable JfcTemplateDetails jfcTemplateDetails) {
            if (jfcTemplateDetails == null) {
                return this;
            }
            this.jfcTemplateName = jfcTemplateDetails.getName();
            this.jfcTemplateDescription = jfcTemplateDetails.getDescription();
            this.jfcTemplateLabel = jfcTemplateDetails.getLabel();
            this.jfcTemplateProvider = jfcTemplateDetails.getProvider();
            this.jfcTemplateSettings = (Map)((Object)SerializationUtils.clone(new HashMap<String, String>(jfcTemplateDetails.getSettings())));
            return this;
        }
    }
}

