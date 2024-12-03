/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.ParametersAreNonnullByDefault
 *  lombok.Generated
 *  org.codehaus.jackson.annotate.JsonIgnoreProperties
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.migration.agent.service.catalogue.model;

import javax.annotation.ParametersAreNonnullByDefault;
import lombok.Generated;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

@ParametersAreNonnullByDefault
@JsonIgnoreProperties(ignoreUnknown=true)
public class CloudPageTemplate {
    @JsonProperty
    private String templateId;
    @JsonProperty
    private String name;
    @JsonProperty
    private OriginalTemplate originalTemplate;

    @Generated
    public String getTemplateId() {
        return this.templateId;
    }

    @Generated
    public String getName() {
        return this.name;
    }

    @Generated
    public OriginalTemplate getOriginalTemplate() {
        return this.originalTemplate;
    }

    @Generated
    public void setTemplateId(String templateId) {
        this.templateId = templateId;
    }

    @Generated
    public void setName(String name) {
        this.name = name;
    }

    @Generated
    public void setOriginalTemplate(OriginalTemplate originalTemplate) {
        this.originalTemplate = originalTemplate;
    }

    @JsonIgnoreProperties(ignoreUnknown=true)
    public static class OriginalTemplate {
        @JsonProperty
        private String pluginKey;
        @JsonProperty
        private String moduleKey;

        @Generated
        public String getPluginKey() {
            return this.pluginKey;
        }

        @Generated
        public String getModuleKey() {
            return this.moduleKey;
        }

        @Generated
        public void setPluginKey(String pluginKey) {
            this.pluginKey = pluginKey;
        }

        @Generated
        public void setModuleKey(String moduleKey) {
            this.moduleKey = moduleKey;
        }
    }
}

