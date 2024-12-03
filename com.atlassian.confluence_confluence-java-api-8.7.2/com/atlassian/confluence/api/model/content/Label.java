/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.ExperimentalApi
 *  com.atlassian.graphql.annotations.GraphQLIDType
 *  org.codehaus.jackson.annotate.JsonIgnoreProperties
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.confluence.api.model.content;

import com.atlassian.annotations.ExperimentalApi;
import com.atlassian.graphql.annotations.GraphQLIDType;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown=true)
@ExperimentalApi
public class Label {
    @JsonProperty
    private String prefix;
    @JsonProperty
    private String name;
    @JsonProperty
    @GraphQLIDType
    private String id;

    public Label(@JsonProperty(value="prefix") String prefix, @JsonProperty(value="name") String name, @JsonProperty(value="id") String id) {
        this.prefix = prefix;
        this.name = name;
        this.id = id;
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Label name cannot be blank");
        }
    }

    public Label(LabelBuilder labelBuilder) {
        this(labelBuilder.prefix, labelBuilder.name, labelBuilder.id);
    }

    private Label() {
    }

    public String getLabel() {
        return this.name;
    }

    public String getId() {
        return this.id;
    }

    public String getPrefix() {
        return this.prefix;
    }

    public String toString() {
        return "Label{prefix='" + this.prefix + '\'' + ", name='" + this.name + '\'' + ", id='" + this.id + '\'' + '}';
    }

    public String serialise() {
        StringBuilder builder = new StringBuilder();
        if (this.prefix != null && !this.prefix.isEmpty() && !this.prefix.equals(Prefix.global.name())) {
            builder.append(this.prefix).append(":");
        }
        builder.append(this.name);
        return builder.toString();
    }

    public static LabelBuilder builder(String name) {
        int index = name.indexOf(":");
        if (index > -1) {
            return new LabelBuilder(name.substring(index + 1)).prefix(name.substring(0, index));
        }
        return new LabelBuilder(name);
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        Label label = (Label)o;
        if (!this.name.equals(label.name)) {
            return false;
        }
        return !(this.prefix != null ? !this.prefix.equals(label.prefix) : label.prefix != null);
    }

    public int hashCode() {
        int result = this.prefix != null ? this.prefix.hashCode() : 0;
        result = 31 * result + this.name.hashCode();
        return result;
    }

    public static class LabelBuilder {
        private String name;
        private String id;
        private String prefix = Prefix.global.toString();

        public LabelBuilder(String name) {
            this.name = name;
        }

        public LabelBuilder name(String name) {
            this.name = name;
            return this;
        }

        public LabelBuilder prefix(String prefix) {
            this.prefix = prefix;
            return this;
        }

        public LabelBuilder prefix(Prefix prefix) {
            return this.prefix(prefix.toString());
        }

        public LabelBuilder id(String id) {
            this.id = id;
            return this;
        }

        public Label build() {
            return new Label(this);
        }
    }

    public static enum Prefix {
        my,
        global,
        team,
        system;

    }
}

