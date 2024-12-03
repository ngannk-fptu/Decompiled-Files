/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.troubleshooting.jfr.domain;

import com.atlassian.troubleshooting.jfr.config.JfrProperties;
import org.codehaus.jackson.annotate.JsonProperty;

public class JfrConfigurationPropertiesDto {
    @JsonProperty
    private final long maxAge;
    @JsonProperty
    private final long maxSize;
    @JsonProperty
    private final int numberOfFilesToRemain;
    @JsonProperty
    private final String recordingPath;
    @JsonProperty
    private final String threadDumpPath;
    @JsonProperty
    private final String dumpCronExpression;
    @JsonProperty
    private final String jfrTemplatePath;
    @JsonProperty
    private final Long threadDumpInterval;

    public JfrConfigurationPropertiesDto(JfrProperties jfrProperties) {
        this.maxAge = jfrProperties.getMaxAge();
        this.maxSize = jfrProperties.getMaxSize();
        this.numberOfFilesToRemain = jfrProperties.getNumberOfFilesToRemain();
        this.recordingPath = jfrProperties.getRecordingPath();
        this.threadDumpPath = jfrProperties.getThreadDumpPath();
        this.dumpCronExpression = jfrProperties.getDumpCronExpression();
        this.jfrTemplatePath = jfrProperties.getJfrTemplatePath();
        this.threadDumpInterval = jfrProperties.getThreadDumpInterval();
    }

    public long getMaxAge() {
        return this.maxAge;
    }

    public long getMaxSize() {
        return this.maxSize;
    }

    public int getNumberOfFilesToRemain() {
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

    public Long getThreadDumpInterval() {
        return this.threadDumpInterval;
    }
}

