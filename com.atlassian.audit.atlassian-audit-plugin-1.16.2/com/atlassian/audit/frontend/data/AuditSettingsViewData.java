/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.codehaus.jackson.annotate.JsonIgnoreProperties
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.audit.frontend.data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown=true)
public class AuditSettingsViewData {
    private String fileLocation;
    private int maxExportRecordsWarning = 10000;
    private int maxRecordsInDb = 10000000;
    private int fileSizeLimitInMb = 100;
    private int maxFileCount = 100;
    private List<ConfigurationArea> areas = new ArrayList<ConfigurationArea>();
    private ConfigurationLevel levels;
    private boolean logFileEnabled;
    private boolean excludedActionsEnabled;

    public AuditSettingsViewData withMaxFileCount(int maxFileCount) {
        this.maxFileCount = maxFileCount;
        return this;
    }

    public AuditSettingsViewData withFileLocation(String fileLocation) {
        this.fileLocation = fileLocation;
        return this;
    }

    public AuditSettingsViewData withLogFileEnabled(boolean logFileEnabled) {
        this.logFileEnabled = logFileEnabled;
        return this;
    }

    public AuditSettingsViewData withExcludedActionsEnabled(boolean excludedActionsEnabled) {
        this.excludedActionsEnabled = excludedActionsEnabled;
        return this;
    }

    public AuditSettingsViewData area(ConfigurationArea area) {
        this.areas.add(area);
        return this;
    }

    public AuditSettingsViewData withMaxExportRecordsWarning(int maxExportRecordsWarning) {
        this.maxExportRecordsWarning = maxExportRecordsWarning;
        return this;
    }

    public AuditSettingsViewData withMaxRecordsInDb(int maxRecordsInDb) {
        this.maxRecordsInDb = maxRecordsInDb;
        return this;
    }

    public AuditSettingsViewData levels(ConfigurationLevel levels) {
        this.levels = levels;
        return this;
    }

    public AuditSettingsViewData withFileSizeLimitInMb(int fileSizeLimitInMb) {
        this.fileSizeLimitInMb = fileSizeLimitInMb;
        return this;
    }

    public int getMaxFileCount() {
        return this.maxFileCount;
    }

    public List<ConfigurationArea> getAreas() {
        return this.areas;
    }

    public ConfigurationLevel getLevels() {
        return this.levels;
    }

    public boolean isLogFileEnabled() {
        return this.logFileEnabled;
    }

    public int getMaxExportRecordsWarning() {
        return this.maxExportRecordsWarning;
    }

    public int getMaxRecordsInDb() {
        return this.maxRecordsInDb;
    }

    public int getFileSizeLimitInMb() {
        return this.fileSizeLimitInMb;
    }

    public String getFileLocation() {
        return this.fileLocation;
    }

    public boolean isExcludedActionsEnabled() {
        return this.excludedActionsEnabled;
    }

    public String toString() {
        return "AuditSettingsViewData{fileLocation='" + this.fileLocation + '\'' + ", maxExportRecordsWarning=" + this.maxExportRecordsWarning + ", maxRecordsInDb=" + this.maxRecordsInDb + ", fileSizeLimitInMb=" + this.fileSizeLimitInMb + ", maxFileCount=" + this.maxFileCount + ", areas=" + this.areas + ", levels=" + this.levels + ", logFileEnabled=" + this.logFileEnabled + ", excludedActionsEnabled=" + this.excludedActionsEnabled + '}';
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof AuditSettingsViewData)) {
            return false;
        }
        AuditSettingsViewData that = (AuditSettingsViewData)o;
        return this.maxExportRecordsWarning == that.maxExportRecordsWarning && this.maxRecordsInDb == that.maxRecordsInDb && this.fileSizeLimitInMb == that.fileSizeLimitInMb && this.maxFileCount == that.maxFileCount && this.logFileEnabled == that.logFileEnabled && this.excludedActionsEnabled == that.excludedActionsEnabled && Objects.equals(this.fileLocation, that.fileLocation) && Objects.equals(this.areas, that.areas) && Objects.equals(this.levels, that.levels);
    }

    public int hashCode() {
        return Objects.hash(this.fileLocation, this.maxExportRecordsWarning, this.maxRecordsInDb, this.fileSizeLimitInMb, this.maxFileCount, this.areas, this.levels, this.logFileEnabled, this.excludedActionsEnabled);
    }

    @JsonIgnoreProperties(ignoreUnknown=true)
    public static class ConfigurationArea {
        private String key;
        private String label;
        private String description;
        private List<String> levels;

        public ConfigurationArea(String key, String label, String description, String ... levels) {
            this.key = key;
            this.label = label;
            this.description = description;
            this.levels = Arrays.asList(levels);
        }

        public String getKey() {
            return this.key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public String getLabel() {
            return this.label;
        }

        public void setLabel(String label) {
            this.label = label;
        }

        public String getDescription() {
            return this.description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public List<String> getLevels() {
            return this.levels;
        }

        public void setLevels(List<String> levels) {
            this.levels = levels;
        }

        public String toString() {
            return "ConfigurationArea{key='" + this.key + '\'' + ", label='" + this.label + '\'' + ", description='" + this.description + '\'' + ", levels=" + this.levels + '}';
        }

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || this.getClass() != o.getClass()) {
                return false;
            }
            ConfigurationArea that = (ConfigurationArea)o;
            return Objects.equals(this.key, that.key) && Objects.equals(this.label, that.label) && Objects.equals(this.description, that.description) && Objects.equals(this.levels, that.levels);
        }

        public int hashCode() {
            return Objects.hash(this.key, this.label, this.description, this.levels);
        }
    }

    @JsonIgnoreProperties(ignoreUnknown=true)
    public static class ConfigurationLevel {
        @JsonProperty(value="off")
        private String off;
        @JsonProperty(value="base")
        private String base;
        @JsonProperty(value="advanced")
        private String advanced;
        @JsonProperty(value="full")
        private String full;

        public ConfigurationLevel(String off, String base, String advanced, String full) {
            this.off = off;
            this.base = base;
            this.advanced = advanced;
            this.full = full;
        }

        public String toString() {
            return "ConfigurationLevel{off='" + this.off + '\'' + ", base='" + this.base + '\'' + ", advanced='" + this.advanced + '\'' + ", full='" + this.full + '\'' + '}';
        }

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || this.getClass() != o.getClass()) {
                return false;
            }
            ConfigurationLevel that = (ConfigurationLevel)o;
            return Objects.equals(this.off, that.off) && Objects.equals(this.base, that.base) && Objects.equals(this.advanced, that.advanced) && Objects.equals(this.full, that.full);
        }

        public int hashCode() {
            return Objects.hash(this.off, this.base, this.advanced, this.full);
        }
    }
}

