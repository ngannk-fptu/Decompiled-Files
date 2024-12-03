/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.Internal
 *  org.codehaus.jackson.annotate.JsonCreator
 *  org.codehaus.jackson.annotate.JsonIgnoreProperties
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.confluence.api.model.content;

import com.atlassian.annotations.Internal;
import com.atlassian.confluence.api.model.content.Content;
import com.atlassian.confluence.api.model.content.ContentStatus;
import com.atlassian.confluence.api.model.content.Version;
import com.atlassian.confluence.api.model.content.id.ContentId;
import java.util.Objects;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown=true)
@Internal
public class ContentSelector {
    public static final ContentSelector UNSET = new ContentSelector();
    @JsonProperty
    private final ContentId id;
    @JsonProperty
    private final ContentStatus status;
    @JsonProperty
    private final int version;

    @JsonCreator
    private ContentSelector() {
        this(ContentSelector.builder());
    }

    private ContentSelector(ContentSelectorBuilder builder) {
        this.id = builder.id != null ? builder.id : ContentId.UNSET;
        this.version = builder.version;
        this.status = builder.status != null ? builder.status : ContentSelector.defaultStatusForVersion(this.version);
    }

    private static ContentStatus defaultStatusForVersion(int version) {
        return version != 0 ? ContentStatus.HISTORICAL : ContentStatus.CURRENT;
    }

    public ContentId getId() {
        return this.id;
    }

    public ContentStatus getStatus() {
        return this.status;
    }

    public int getVersion() {
        return this.version;
    }

    public boolean hasVersion() {
        return this.version != 0;
    }

    public boolean isEmpty() {
        return this.equals(UNSET);
    }

    public ContentSelector asCurrent() {
        return ContentSelector.builder().id(this.id).build();
    }

    public static ContentSelector from(Content content) {
        return new ContentSelectorBuilder().id(content.getId()).status(content.getStatus()).version(Version.getVersionNumber(content.getVersionRef())).build();
    }

    @Deprecated
    public static ContentSelector fromId(ContentId contentId) {
        return ContentSelector.builder().id(contentId).build();
    }

    public static ContentSelectorBuilder builder() {
        return new ContentSelectorBuilder();
    }

    public int hashCode() {
        return Objects.hash(this.id, this.status, this.version);
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        ContentSelector selector = (ContentSelector)o;
        return Objects.equals(this.id, selector.id) && Objects.equals(this.status, selector.status) && this.version == selector.version;
    }

    public String toString() {
        return "ContentSelector{id=" + this.id + ", status=" + this.status + ", version=" + this.version + '}';
    }

    public static class ContentSelectorBuilder {
        private ContentId id;
        private ContentStatus status;
        private int version;

        private ContentSelectorBuilder() {
        }

        public ContentSelectorBuilder id(ContentId id) {
            this.id = id;
            return this;
        }

        public ContentSelectorBuilder status(ContentStatus status) {
            this.status = status;
            return this;
        }

        public ContentSelectorBuilder version(int version) {
            this.version = version;
            return this;
        }

        public ContentSelector build() {
            return new ContentSelector(this);
        }
    }
}

