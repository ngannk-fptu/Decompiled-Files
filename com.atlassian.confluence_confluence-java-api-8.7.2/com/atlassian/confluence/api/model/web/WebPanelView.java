/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.ExperimentalApi
 *  com.atlassian.graphql.annotations.GraphQLTypeName
 *  com.atlassian.soy.renderer.CustomSoyDataMapper
 *  org.codehaus.jackson.annotate.JsonCreator
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.confluence.api.model.web;

import com.atlassian.annotations.ExperimentalApi;
import com.atlassian.graphql.annotations.GraphQLTypeName;
import com.atlassian.soy.renderer.CustomSoyDataMapper;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

@ExperimentalApi
@CustomSoyDataMapper(value="jackson2soy")
@GraphQLTypeName(value="WebPanel")
public class WebPanelView {
    @JsonProperty
    private final String moduleKey;
    @JsonProperty
    private final String completeKey;
    @JsonProperty
    private final String html;
    @JsonProperty
    private final String location;
    @JsonProperty
    private final String label;
    @JsonProperty
    private final int weight;
    @JsonProperty
    private final String name;

    @JsonCreator
    private WebPanelView(@JsonProperty(value="moduleKey") String moduleKey, @JsonProperty(value="completeKey") String completeKey, @JsonProperty(value="name") String name, @JsonProperty(value="html") String html, @JsonProperty(value="location") String location, @JsonProperty(value="label") String label, @JsonProperty(value="weight") int weight) {
        this.moduleKey = moduleKey;
        this.completeKey = completeKey;
        this.name = name;
        this.html = html;
        this.location = location;
        this.label = label;
        this.weight = weight;
    }

    public String getHtml() {
        return this.html;
    }

    public String getLocation() {
        return this.location;
    }

    public static class Builder {
        private String moduleKey;
        private String completeKey;
        private String name;
        private String location;
        private String label;
        private int weight;

        public WebPanelView create(String html) {
            return new WebPanelView(this.moduleKey, this.completeKey, this.name, html, this.location, this.label, this.weight);
        }

        @Deprecated
        public Builder setKey(String key) {
            this.moduleKey = key;
            return this;
        }

        public Builder setModuleKey(String moduleKey) {
            this.moduleKey = moduleKey;
            return this;
        }

        public Builder setCompleteKey(String completeKey) {
            this.completeKey = completeKey;
            return this;
        }

        public Builder setName(String name) {
            this.name = name;
            return this;
        }

        public Builder setLocation(String location) {
            this.location = location;
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

        @Deprecated
        public Builder setHtml(String html) {
            return this;
        }
    }
}

