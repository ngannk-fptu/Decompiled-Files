/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.ExperimentalApi
 *  org.checkerframework.checker.nullness.qual.Nullable
 *  org.codehaus.jackson.annotate.JsonCreator
 *  org.codehaus.jackson.annotate.JsonIgnoreProperties
 *  org.codehaus.jackson.annotate.JsonProperty
 *  org.codehaus.jackson.map.annotate.JsonDeserialize
 */
package com.atlassian.confluence.api.model.content;

import com.atlassian.annotations.ExperimentalApi;
import com.atlassian.confluence.api.model.JsonString;
import com.atlassian.confluence.api.model.content.AbstractJsonProperty;
import com.atlassian.confluence.api.model.content.Content;
import com.atlassian.confluence.api.model.content.Version;
import com.atlassian.confluence.api.model.content.id.JsonContentPropertyId;
import com.atlassian.confluence.api.model.reference.ExpandedReference;
import com.atlassian.confluence.api.model.reference.Reference;
import com.atlassian.confluence.api.nav.Navigation;
import com.atlassian.confluence.api.nav.NavigationAware;
import com.atlassian.confluence.api.nav.NavigationService;
import com.atlassian.confluence.api.serialization.RestEnrichable;
import java.util.Objects;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JsonDeserialize;

@ExperimentalApi
@JsonIgnoreProperties(ignoreUnknown=true)
@RestEnrichable
public class JsonContentProperty
extends AbstractJsonProperty
implements NavigationAware {
    @JsonProperty
    private final JsonContentPropertyId id;
    @JsonDeserialize(as=ExpandedReference.class, contentAs=Content.class)
    @JsonProperty
    private final Reference<Content> content;

    @JsonCreator
    private JsonContentProperty() {
        this(JsonContentProperty.builder());
    }

    private JsonContentProperty(ContentPropertyBuilder builder) {
        super(builder);
        this.id = builder.id;
        this.content = Reference.orEmpty(builder.content, Content.class);
    }

    public static ContentPropertyBuilder builder() {
        return new ContentPropertyBuilder();
    }

    public static ContentPropertyBuilder builder(JsonContentProperty propertyTemplate) {
        return ((ContentPropertyBuilder)JsonContentProperty.builder().copyParentProperties(propertyTemplate)).id(propertyTemplate.id).content(propertyTemplate.content);
    }

    public static Reference<JsonContentProperty> buildReference(@Nullable JsonContentPropertyId id) {
        if (id == null) {
            return Reference.empty(JsonContentProperty.class);
        }
        return Reference.collapsed(JsonContentProperty.builder().id(id).build());
    }

    public JsonContentPropertyId getId() {
        return this.id;
    }

    public Reference<Content> getContentRef() {
        return this.content;
    }

    public Content getContent() {
        return this.getContentRef().get();
    }

    @Override
    public String getKey() {
        return super.getKey();
    }

    @Override
    public JsonString getValue() {
        return super.getValue();
    }

    @Override
    public @Nullable Version getVersion() {
        return super.getVersion();
    }

    @Override
    public Navigation.Builder resolveNavigation(NavigationService navigationService) {
        return this.content.exists() ? navigationService.createNavigation().content(this.content).property(this) : null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        JsonContentProperty that = (JsonContentProperty)o;
        if (!super.equals(that)) {
            return false;
        }
        if (!Objects.equals(this.id, that.id)) {
            return false;
        }
        return Objects.equals(this.content, that.content);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (this.id != null ? this.id.hashCode() : 0);
        result = 31 * result + (this.content != null ? this.content.hashCode() : 0);
        return result;
    }

    public String toString() {
        return "JsonContentProperty{id='" + this.id + '\'' + ", key='" + this.getKey() + '\'' + ", content=" + this.content + ", version=" + this.getVersion() + '}';
    }

    public static class Expansions {
        public static final String CONTENT = "content";
        public static final String VERSION = "version";
    }

    private static enum IdProperties {
        content,
        key;

    }

    public static class ContentPropertyBuilder
    extends AbstractJsonProperty.AbstractJsonPropertyBuilder<ContentPropertyBuilder, JsonContentProperty> {
        private JsonContentPropertyId id;
        private Reference<Content> content;

        @Override
        public JsonContentProperty build() {
            return new JsonContentProperty(this);
        }

        public ContentPropertyBuilder content(Content content) {
            this.content = Reference.to(content);
            return this;
        }

        public ContentPropertyBuilder id(JsonContentPropertyId id) {
            this.id = id;
            return this;
        }

        public ContentPropertyBuilder content(Reference<Content> contentRef) {
            this.content = contentRef;
            return this;
        }

        @Override
        public ContentPropertyBuilder value(JsonString value) {
            return (ContentPropertyBuilder)super.value(value);
        }

        @Override
        public ContentPropertyBuilder version(Reference<Version> version) {
            return (ContentPropertyBuilder)super.version(version);
        }

        @Override
        public ContentPropertyBuilder version(Version version) {
            return (ContentPropertyBuilder)super.version(version);
        }

        @Override
        public ContentPropertyBuilder key(String key) {
            return (ContentPropertyBuilder)super.key(key);
        }
    }
}

