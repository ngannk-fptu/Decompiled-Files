/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.ExperimentalApi
 *  org.codehaus.jackson.annotate.JsonCreator
 *  org.codehaus.jackson.annotate.JsonIgnoreProperties
 *  org.codehaus.jackson.annotate.JsonProperty
 *  org.codehaus.jackson.map.annotate.JsonDeserialize
 */
package com.atlassian.confluence.api.model.content;

import com.atlassian.annotations.ExperimentalApi;
import com.atlassian.confluence.api.model.content.AbstractJsonProperty;
import com.atlassian.confluence.api.model.content.Space;
import com.atlassian.confluence.api.model.reference.ExpandedReference;
import com.atlassian.confluence.api.model.reference.Reference;
import com.atlassian.confluence.api.serialization.RestEnrichable;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JsonDeserialize;

@ExperimentalApi
@JsonIgnoreProperties(ignoreUnknown=true)
@RestEnrichable
public class JsonSpaceProperty
extends AbstractJsonProperty {
    @JsonDeserialize(as=ExpandedReference.class, contentAs=Space.class)
    @JsonProperty
    private final Reference<Space> space;

    @JsonCreator
    private JsonSpaceProperty() {
        this(JsonSpaceProperty.builder());
    }

    private JsonSpaceProperty(SpacePropertyBuilder builder) {
        super(builder);
        this.space = Reference.orEmpty(builder.space, Space.class);
    }

    public Reference<Space> getSpaceRef() {
        return this.space;
    }

    public Space getSpace() {
        return this.getSpaceRef().get();
    }

    public static SpacePropertyBuilder builder() {
        return new SpacePropertyBuilder();
    }

    public static SpacePropertyBuilder builder(JsonSpaceProperty property) {
        return ((SpacePropertyBuilder)JsonSpaceProperty.builder().copyParentProperties(property)).space(property.space);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof JsonSpaceProperty)) {
            return false;
        }
        JsonSpaceProperty that = (JsonSpaceProperty)o;
        if (!super.equals(that)) {
            return false;
        }
        return !(this.space != null ? !this.space.equals(that.space) : that.space != null);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (this.space != null ? this.space.hashCode() : 0);
        return result;
    }

    public String toString() {
        return "JsonSpaceProperty{key='" + this.getKey() + '\'' + ", space=" + this.space + ", version=" + this.getVersion() + '}';
    }

    public static class Expansions {
        public static final String SPACE = "space";
        public static final String VERSION = "version";
    }

    private static enum IdProperties {
        space,
        key;

    }

    public static class SpacePropertyBuilder
    extends AbstractJsonProperty.AbstractJsonPropertyBuilder<SpacePropertyBuilder, JsonSpaceProperty> {
        private Reference<Space> space;

        @Override
        public JsonSpaceProperty build() {
            return new JsonSpaceProperty(this);
        }

        public SpacePropertyBuilder space(Space space) {
            this.space = Reference.to(space);
            return this;
        }

        public SpacePropertyBuilder space(Reference<Space> space) {
            this.space = space;
            return this;
        }
    }
}

