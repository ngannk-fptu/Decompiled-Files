/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.checkerframework.checker.nullness.qual.Nullable
 *  org.codehaus.jackson.annotate.JsonProperty
 *  org.codehaus.jackson.map.annotate.JsonDeserialize
 */
package com.atlassian.confluence.api.model.content;

import com.atlassian.confluence.api.model.JsonString;
import com.atlassian.confluence.api.model.content.Version;
import com.atlassian.confluence.api.model.reference.ExpandedReference;
import com.atlassian.confluence.api.model.reference.Reference;
import java.util.Objects;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JsonDeserialize;

abstract class AbstractJsonProperty {
    @JsonProperty
    private final String key;
    @JsonProperty
    private final JsonString value;
    @JsonDeserialize(as=ExpandedReference.class, contentAs=Version.class)
    @JsonProperty
    private final Reference<Version> version;

    protected AbstractJsonProperty(AbstractJsonPropertyBuilder builder) {
        this.key = builder.key;
        this.value = builder.value;
        this.version = Reference.orEmpty(builder.version, Version.class);
    }

    public String getKey() {
        return this.key;
    }

    public JsonString getValue() {
        return this.value;
    }

    public @Nullable Version getVersion() {
        return this.version != null ? this.version.get() : null;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof AbstractJsonProperty)) {
            return false;
        }
        AbstractJsonProperty that = (AbstractJsonProperty)o;
        return Objects.equals(this.key, that.key) && Objects.equals(this.value, that.value) && Objects.equals(this.version, that.version);
    }

    public int hashCode() {
        int result = Objects.hashCode(this.key);
        result = 31 * result + Objects.hashCode(this.value);
        result = 31 * result + Objects.hashCode(this.version);
        return result;
    }

    public static abstract class AbstractJsonPropertyBuilder<T extends AbstractJsonPropertyBuilder, S extends AbstractJsonProperty> {
        private String key;
        private Reference<Version> version = null;
        private JsonString value;
        private T self = this;

        public abstract S build();

        public T key(String key) {
            this.key = key;
            return this.self;
        }

        public T version(Version version) {
            this.version = Reference.to(version);
            return this.self;
        }

        public T version(Reference<Version> version) {
            this.version = version;
            return this.self;
        }

        public T value(JsonString value) {
            this.value = value;
            return this.self;
        }

        protected T copyParentProperties(AbstractJsonProperty templateProperty) {
            this.key(templateProperty.key);
            this.version(templateProperty.version);
            this.value(templateProperty.value);
            return this.self;
        }
    }
}

