/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.troubleshooting.jfr.domain;

import com.atlassian.troubleshooting.jfr.config.JfcTemplateDetails;
import com.atlassian.troubleshooting.jfr.domain.JfrConfigurationPropertiesDto;
import javax.annotation.Nullable;
import jdk.jfr.Configuration;
import org.codehaus.jackson.annotate.JsonProperty;

public class ConfigurationDetails {
    @JsonProperty
    private final JfrConfigurationPropertiesDto jfrProperties;
    @JsonProperty
    private final JfcTemplateDetails jfcTemplate;
    @JsonProperty
    private final String nodeId;

    public ConfigurationDetails(Builder builder) {
        this.jfrProperties = builder.jfrProperties;
        this.jfcTemplate = builder.jfcTemplate;
        this.nodeId = builder.nodeId;
    }

    @Nullable
    public JfrConfigurationPropertiesDto getJfrProperties() {
        return this.jfrProperties;
    }

    @Nullable
    public JfcTemplateDetails getJfcTemplate() {
        return this.jfcTemplate;
    }

    @Nullable
    public String getNodeId() {
        return this.nodeId;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static ConfigurationDetails from(String nodeId, Configuration configuration, JfrConfigurationPropertiesDto jfrProperties) {
        return new Builder().withJfrProperties(jfrProperties).withJfcTemplate(configuration).withNodeId(nodeId).build();
    }

    public static class Builder {
        private JfrConfigurationPropertiesDto jfrProperties;
        private JfcTemplateDetails jfcTemplate;
        private String nodeId;

        private Builder() {
        }

        public Builder withJfrProperties(JfrConfigurationPropertiesDto jfrProperties) {
            this.jfrProperties = jfrProperties;
            return this;
        }

        public Builder withJfcTemplate(Configuration nativeConfiguration) {
            this.jfcTemplate = new JfcTemplateDetails(nativeConfiguration);
            return this;
        }

        public Builder withNodeId(String nodeId) {
            this.nodeId = nodeId;
            return this;
        }

        public ConfigurationDetails build() {
            return new ConfigurationDetails(this);
        }
    }
}

