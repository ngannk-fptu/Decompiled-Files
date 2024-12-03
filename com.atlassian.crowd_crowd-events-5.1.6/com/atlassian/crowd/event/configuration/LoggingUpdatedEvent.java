/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.crowd.event.configuration;

import java.util.List;
import java.util.Objects;

public class LoggingUpdatedEvent {
    private final List<LoggingConfig> oldConfiguration;
    private final List<LoggingConfig> newConfiguration;

    public LoggingUpdatedEvent(List<LoggingConfig> oldConfiguration, List<LoggingConfig> newConfiguration) {
        this.oldConfiguration = oldConfiguration;
        this.newConfiguration = newConfiguration;
    }

    public List<LoggingConfig> getOldConfiguration() {
        return this.oldConfiguration;
    }

    public List<LoggingConfig> getNewConfiguration() {
        return this.newConfiguration;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        LoggingUpdatedEvent that = (LoggingUpdatedEvent)o;
        return Objects.equals(this.oldConfiguration, that.oldConfiguration) && Objects.equals(this.newConfiguration, that.newConfiguration);
    }

    public int hashCode() {
        return Objects.hash(this.oldConfiguration, this.newConfiguration);
    }

    public static class LoggingConfig {
        private final String clazz;
        private final String level;

        public LoggingConfig(String clazz, String level) {
            this.clazz = clazz;
            this.level = level;
        }

        public String getClazz() {
            return this.clazz;
        }

        public String getLevel() {
            return this.level;
        }

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || this.getClass() != o.getClass()) {
                return false;
            }
            LoggingConfig that = (LoggingConfig)o;
            return Objects.equals(this.clazz, that.clazz) && Objects.equals(this.level, that.level);
        }

        public int hashCode() {
            return Objects.hash(this.clazz, this.level);
        }
    }
}

