/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.ExperimentalApi
 *  com.atlassian.graphql.annotations.GraphQLTypeName
 *  com.atlassian.soy.renderer.CustomSoyDataMapper
 *  org.codehaus.jackson.annotate.JsonCreator
 *  org.codehaus.jackson.annotate.JsonProperty
 *  org.codehaus.jackson.map.annotate.JsonDeserialize
 */
package com.atlassian.confluence.api.model.web;

import com.atlassian.annotations.ExperimentalApi;
import com.atlassian.confluence.api.model.reference.EnrichableMap;
import com.atlassian.confluence.api.model.web.Icon;
import com.atlassian.graphql.annotations.GraphQLTypeName;
import com.atlassian.soy.renderer.CustomSoyDataMapper;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JsonDeserialize;

@ExperimentalApi
@CustomSoyDataMapper(value="jackson2soy")
@GraphQLTypeName(value="WebItem")
public class WebItemView {
    @JsonProperty
    private final String url;
    @JsonProperty
    private final String label;
    @JsonProperty
    private final int weight;
    @JsonProperty
    private String moduleKey;
    @JsonProperty
    private String id;
    @JsonProperty
    private String accessKey;
    @JsonProperty
    private String completeKey;
    @JsonProperty
    private String section;
    @JsonProperty
    private String tooltip;
    @JsonProperty
    private String styleClass;
    @JsonProperty
    private Icon icon;
    @JsonProperty
    private String urlWithoutContextPath;
    @JsonProperty
    @JsonDeserialize(as=EnrichableMap.class)
    private Map<String, String> params;

    @JsonCreator
    private WebItemView(@JsonProperty(value="moduleKey") String moduleKey, @JsonProperty(value="url") String url, @JsonProperty(value="label") String label, @JsonProperty(value="weight") int weight, @JsonProperty(value="urlWithoutContextPath") String urlWithoutContextPath) {
        this.moduleKey = moduleKey;
        this.url = url;
        this.label = label;
        this.weight = weight;
        this.urlWithoutContextPath = urlWithoutContextPath;
    }

    public WebItemView(Builder builder) {
        this(builder.moduleKey, builder.url, builder.label, builder.weight, builder.urlWithoutContextPath);
        this.id = builder.id;
        this.completeKey = builder.completeKey;
        this.accessKey = builder.accessKey;
        this.tooltip = builder.tooltip;
        this.styleClass = builder.styleClass;
        this.icon = builder.icon;
        this.params = builder.params;
    }

    public String getLinkUrl() {
        return this.url;
    }

    public String getId() {
        return this.id;
    }

    public String getAccessKey() {
        return this.accessKey;
    }

    public String getCompleteKey() {
        return this.completeKey;
    }

    public String getSection() {
        return this.section;
    }

    public String getLabel() {
        return this.label;
    }

    public String getTooltip() {
        return this.tooltip;
    }

    public String getStyleClass() {
        return this.styleClass;
    }

    public Icon getIcon() {
        return this.icon;
    }

    public int getWeight() {
        return this.weight;
    }

    @Deprecated
    public String getKey() {
        return this.moduleKey;
    }

    public String getModuleKey() {
        return this.moduleKey;
    }

    public String getUrlWithoutContextPath() {
        return this.urlWithoutContextPath;
    }

    public Map<String, String> getParams() {
        return this.params;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String id;
        private String accessKey;
        private String completeKey;
        private String section;
        private String tooltip;
        private String styleClass;
        private Icon icon;
        private String urlWithoutContextPath;
        private Map<String, String> params;
        private String moduleKey;
        private String url;
        private String label;
        private int weight;

        public Builder setId(String id) {
            this.id = id;
            return this;
        }

        public Builder setAccessKey(String accessKey) {
            this.accessKey = accessKey;
            return this;
        }

        public Builder setCompleteKey(String completeKey) {
            this.completeKey = completeKey;
            return this;
        }

        public Builder setSection(String section) {
            this.section = section;
            return this;
        }

        public Builder setTooltip(String tooltip) {
            this.tooltip = tooltip;
            return this;
        }

        public Builder setStyleClass(String styleClass) {
            this.styleClass = styleClass;
            return this;
        }

        @Deprecated
        public Builder setIcon(Icon icon) {
            this.icon = icon;
            return this;
        }

        public Builder setIcon(Optional<Icon> icon) {
            if (icon.isPresent()) {
                this.icon = icon.get();
            }
            return this;
        }

        public Builder setUrlWithoutContextPath(String urlWithoutContextPath) {
            this.urlWithoutContextPath = urlWithoutContextPath;
            return this;
        }

        public Builder setParams(Map<String, String> params) {
            this.params = params;
            return this;
        }

        public Builder setModuleKey(String moduleKey) {
            this.moduleKey = moduleKey;
            return this;
        }

        @Deprecated
        public Builder setKey(String key) {
            this.moduleKey = key;
            return this;
        }

        public Builder setUrl(String url) {
            this.url = url;
            return this;
        }

        public Builder setLabel(String label) {
            this.label = label;
            return this;
        }

        public Builder setWeight(int weight) {
            this.weight = weight;
            return this;
        }

        public WebItemView create(String key, String url, String label, int weight) {
            WebItemView link = new WebItemView(key, url, label, weight, this.urlWithoutContextPath);
            link.id = this.id;
            link.accessKey = this.accessKey;
            link.moduleKey = this.moduleKey;
            link.completeKey = this.completeKey;
            link.section = this.section;
            link.tooltip = this.tooltip;
            link.styleClass = this.styleClass;
            link.icon = this.icon;
            link.params = this.params;
            return link;
        }

        public WebItemView build() {
            Objects.requireNonNull(this.moduleKey);
            Objects.requireNonNull(this.url);
            Objects.requireNonNull(this.label);
            return new WebItemView(this);
        }
    }
}

