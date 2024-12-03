/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.ExperimentalApi
 *  com.atlassian.graphql.annotations.GraphQLIDType
 *  com.atlassian.graphql.annotations.GraphQLTypeName
 *  com.atlassian.soy.renderer.CustomSoyDataMapper
 *  org.codehaus.jackson.annotate.JsonCreator
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.confluence.api.model.web;

import com.atlassian.annotations.ExperimentalApi;
import com.atlassian.confluence.api.model.web.WebItemView;
import com.atlassian.graphql.annotations.GraphQLIDType;
import com.atlassian.graphql.annotations.GraphQLTypeName;
import com.atlassian.soy.renderer.CustomSoyDataMapper;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

@ExperimentalApi
@CustomSoyDataMapper(value="jackson2soy")
@GraphQLTypeName(value="WebSection")
public class WebSectionView {
    @JsonProperty
    @GraphQLIDType
    private final String id;
    @JsonProperty
    private final String label;
    @JsonProperty
    private List<WebItemView> items;
    @JsonProperty
    private String styleClass;

    @JsonCreator
    private WebSectionView(@JsonProperty(value="id") String id, @JsonProperty(value="label") String label) {
        this.id = id;
        this.label = label;
    }

    public String getLabel() {
        return this.label;
    }

    public String getId() {
        return this.id;
    }

    public Iterable<WebItemView> getLinks() {
        return this.items;
    }

    public String getStyleClass() {
        return this.styleClass;
    }

    public static class Builder {
        private final List<WebItemView> items = new ArrayList<WebItemView>();
        private String styleClass;

        public Builder setStyleClass(String styleClass) {
            this.styleClass = styleClass;
            return this;
        }

        public Builder addItems(WebItemView ... items) {
            this.items.addAll(Arrays.asList(items));
            return this;
        }

        public Builder addItems(Iterable<WebItemView> items) {
            items.forEach(this.items::add);
            return this;
        }

        public WebSectionView create(String id, String label) {
            WebSectionView section = new WebSectionView(id, label);
            section.items = Collections.unmodifiableList(this.items);
            section.styleClass = this.styleClass;
            return section;
        }
    }
}

