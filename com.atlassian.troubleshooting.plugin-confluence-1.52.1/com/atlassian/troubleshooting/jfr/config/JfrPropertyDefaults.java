/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap$Builder
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 */
package com.atlassian.troubleshooting.jfr.config;

import com.atlassian.troubleshooting.jfr.config.JfrProperty;
import com.google.common.collect.ImmutableMap;
import java.util.Map;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class JfrPropertyDefaults {
    private final long maxAge;
    private final long maxSize;
    private final int numberOfFilesToRemain;
    private final String recordingPath;
    private final String threadDumpPath;
    private final String dumpCronExpression;
    private final String jfrTemplatePath;
    private final Map<JfrProperty, String> propertiesMap;

    private JfrPropertyDefaults(Builder builder) {
        this.maxAge = Objects.requireNonNull(builder.maxAge);
        this.maxSize = Objects.requireNonNull(builder.maxSize);
        this.numberOfFilesToRemain = Objects.requireNonNull(builder.numberOfFilesToRemain);
        this.recordingPath = Objects.requireNonNull(builder.recordingPath);
        this.threadDumpPath = Objects.requireNonNull(builder.threadDumpPath);
        this.dumpCronExpression = Objects.requireNonNull(builder.dumpCronExpression);
        this.jfrTemplatePath = builder.jfrTemplatePath;
        ImmutableMap.Builder mapBuilder = new ImmutableMap.Builder().put((Object)JfrProperty.DUMP_CRON_EXPRESSION, (Object)this.dumpCronExpression).put((Object)JfrProperty.FILE_TO_REMAIN, (Object)String.valueOf(this.numberOfFilesToRemain)).put((Object)JfrProperty.MAX_AGE, (Object)String.valueOf(this.maxAge)).put((Object)JfrProperty.MAX_SIZE, (Object)String.valueOf(this.maxSize)).put((Object)JfrProperty.RECORDING_PATH, (Object)this.recordingPath).put((Object)JfrProperty.THREAD_DUMP_PATH, (Object)this.threadDumpPath);
        if (this.jfrTemplatePath != null) {
            mapBuilder.put((Object)JfrProperty.JFR_TEMPLATE_PATH, (Object)this.jfrTemplatePath);
        }
        this.propertiesMap = mapBuilder.build();
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

    public String getProperty(@Nonnull JfrProperty jfrProperty) {
        return this.propertiesMap.get((Object)jfrProperty);
    }

    public static class Builder {
        private Long maxAge;
        private Long maxSize;
        private Integer numberOfFilesToRemain;
        private String recordingPath;
        private String threadDumpPath;
        private String dumpCronExpression;
        private String jfrTemplatePath;

        public Builder maxAge(@Nonnull Long maxAge) {
            this.maxAge = Objects.requireNonNull(maxAge);
            return this;
        }

        public Builder maxSize(@Nonnull Long maxSize) {
            this.maxSize = Objects.requireNonNull(maxSize);
            return this;
        }

        public Builder numberOfFilesToRemain(@Nonnull Integer numberOfFilesToRemain) {
            this.numberOfFilesToRemain = Objects.requireNonNull(numberOfFilesToRemain);
            return this;
        }

        public Builder recordingPath(@Nonnull String recordingPath) {
            this.recordingPath = Objects.requireNonNull(recordingPath);
            return this;
        }

        public Builder threadDumpPath(@Nonnull String threadDumpPath) {
            this.threadDumpPath = Objects.requireNonNull(threadDumpPath);
            return this;
        }

        public Builder dumpCronExpression(@Nonnull String dumpCronExpression) {
            this.dumpCronExpression = Objects.requireNonNull(dumpCronExpression);
            return this;
        }

        public Builder jfrTemplatePath(@Nullable String jfrTemplatePath) {
            this.jfrTemplatePath = jfrTemplatePath;
            return this;
        }

        public JfrPropertyDefaults build() {
            return new JfrPropertyDefaults(this);
        }
    }
}

