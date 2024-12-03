/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.tenancy.TenancyScope
 *  com.atlassian.annotations.tenancy.TenantAware
 *  com.google.common.base.MoreObjects
 *  com.google.common.base.Splitter
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.Iterables
 */
package com.atlassian.plugins.rest.doclet.generators.schema;

import com.atlassian.annotations.tenancy.TenancyScope;
import com.atlassian.annotations.tenancy.TenantAware;
import com.google.common.base.MoreObjects;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;
import java.util.stream.Collectors;
import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonProperty;

@JsonAutoDetect
public class Schema {
    private final String $ref;
    private final String id;
    private final String title;
    private final String description;
    private final String type;
    private final String format;
    @TenantAware(value=TenancyScope.TENANTLESS)
    private final Map<String, Schema> properties;
    private final Schema items;
    @JsonProperty(value="enum")
    private final List<String> _enum;
    @TenantAware(value=TenancyScope.TENANTLESS)
    private final Map<String, Schema> patternProperties;
    private final List<Schema> anyOf;
    @TenantAware(value=TenancyScope.TENANTLESS)
    private final Map<String, Schema> definitions;
    private Boolean additionalProperties;
    private final List<String> required;

    private Schema(String $ref, String id, String title, String description, Type type, Map<String, Schema> properties, Schema items, Iterable<String> _enum, Iterable<String> required, Map<String, Schema> patternProperties, Map<String, Schema> definitions, List<Schema> anyOf) {
        this.$ref = $ref;
        this.id = id;
        this.title = title;
        this.description = description;
        this.type = type != null && type != Type.Any ? type.toString() : null;
        this.format = this.type != null ? type.format() : null;
        this.anyOf = anyOf != null && !anyOf.isEmpty() ? ImmutableList.copyOf(anyOf) : null;
        this.properties = properties != null && properties.size() > 0 ? ImmutableMap.copyOf(properties) : null;
        this.items = items;
        this._enum = _enum != null && Iterables.size(_enum) > 0 ? ImmutableList.copyOf(_enum) : null;
        this.required = required != null && Iterables.size(required) > 0 ? ImmutableList.copyOf(required) : null;
        this.patternProperties = patternProperties != null && patternProperties.size() > 0 ? ImmutableMap.copyOf(patternProperties) : null;
        this.definitions = definitions != null && definitions.size() > 0 ? ImmutableMap.copyOf(definitions) : null;
        this.additionalProperties = type == Type.Object && (this.properties != null || this.patternProperties != null) ? Boolean.valueOf(false) : null;
    }

    public static Schema ref(String title) {
        return new Schema("#/definitions/" + Schema.titleToId(title), null, null, null, null, null, null, null, null, null, null, null);
    }

    public static String titleToId(String title) {
        return title != null ? String.join((CharSequence)"-", Splitter.on((String)" ").split((CharSequence)title)).toLowerCase() : null;
    }

    public String get$ref() {
        return this.$ref;
    }

    public String getId() {
        return this.id;
    }

    public String getTitle() {
        return this.title;
    }

    public String getDescription() {
        return this.description;
    }

    public String getType() {
        return this.type;
    }

    public String getFormat() {
        return this.format;
    }

    public Map<String, Schema> getProperties() {
        return this.properties;
    }

    public Schema getItems() {
        return this.items;
    }

    public List<String> get_enum() {
        return this._enum;
    }

    public Map<String, Schema> getPatternProperties() {
        return this.patternProperties;
    }

    public List<Schema> getAnyOf() {
        return this.anyOf;
    }

    public Map<String, Schema> getDefinitions() {
        return this.definitions;
    }

    public Boolean getAdditionalProperties() {
        return this.additionalProperties;
    }

    public List<String> getRequired() {
        return this.required;
    }

    public static Builder builder() {
        return new Builder();
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        Schema that = (Schema)o;
        return Objects.equals(this.title, that.title) && Objects.equals(this.description, that.description) && Objects.equals(this.type, that.type) && Objects.equals(this.properties, that.properties) && Objects.equals(this.items, that.items) && Objects.equals(this._enum, that._enum) && Objects.equals(this.required, that.required);
    }

    public int hashCode() {
        return Objects.hash(this.title, this.description, this.type, this.properties, this.items, this._enum, this.required);
    }

    public String toString() {
        return MoreObjects.toStringHelper((Object)this).add("title", (Object)this.title).add("description", (Object)this.description).add("type", (Object)this.type).add("properties", this.properties).add("items", (Object)this.items).add("enum", this._enum).add("required", this.required).toString();
    }

    public static final class Builder {
        private String id;
        private String title;
        private String description;
        private Type type;
        @TenantAware(value=TenancyScope.SUPPRESS)
        private Map<String, Schema> properties = new LinkedHashMap<String, Schema>();
        @TenantAware(value=TenancyScope.SUPPRESS)
        private Map<String, Schema> patternProperties = new LinkedHashMap<String, Schema>();
        private Schema items;
        private List<String> _enum = new ArrayList<String>();
        private List<String> required = new ArrayList<String>();
        @TenantAware(value=TenancyScope.SUPPRESS)
        private Map<String, Schema> definitions = new TreeMap<String, Schema>();
        private List<Schema> anyOf = new ArrayList<Schema>();

        private Builder() {
        }

        public Builder setId(String id) {
            this.id = id;
            return this;
        }

        public Builder setTitle(String title) {
            this.title = title;
            return this;
        }

        public Builder setDescription(String description) {
            this.description = description;
            return this;
        }

        public Builder setType(Type type) {
            this.type = type;
            return this;
        }

        public Builder setProperties(Map<String, Schema> properties) {
            this.properties = properties;
            return this;
        }

        public Builder addProperty(String propertyName, Schema propertySchema) {
            this.properties.put(propertyName, propertySchema);
            return this;
        }

        public Builder addPatternProperty(String pattern, Schema schema) {
            this.patternProperties.put(pattern, schema);
            return this;
        }

        public Builder setItems(Schema items) {
            this.items = items;
            return this;
        }

        public <T extends Enum<T>> Builder setEnum(Class<T> enumType) {
            this._enum = ImmutableList.copyOf((Collection)Arrays.stream(enumType.getEnumConstants()).map(Enum::toString).collect(Collectors.toList()));
            return this;
        }

        public Builder setRequired(List<String> required) {
            this.required = required;
            return this;
        }

        public Builder addRequired(String required) {
            this.required.add(required);
            return this;
        }

        public Builder addDefinition(Schema schema) {
            this.definitions.put(Schema.titleToId(schema.getTitle()), schema);
            return this;
        }

        public Builder addAnyOf(Schema schema) {
            this.anyOf.add(schema);
            return this;
        }

        public Schema build() {
            return new Schema(null, this.id, this.title, this.description, this.type, this.properties, this.items, this._enum, this.required, this.patternProperties, this.definitions, this.anyOf);
        }
    }

    public static enum Type {
        Any,
        Object,
        Array,
        Number,
        Integer,
        Boolean,
        String,
        Uri("string", "uri");

        private final String name;
        private final String format;

        private Type(String name, String format) {
            this.name = name;
            this.format = format;
        }

        private Type() {
            this(null, null);
        }

        public String toString() {
            return (String)MoreObjects.firstNonNull((Object)this.name, (Object)this.name().toLowerCase());
        }

        public String format() {
            return this.format;
        }
    }
}

