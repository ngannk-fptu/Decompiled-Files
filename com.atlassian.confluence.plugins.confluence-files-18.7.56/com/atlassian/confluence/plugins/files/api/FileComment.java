/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.content.Content
 *  com.atlassian.confluence.api.model.content.ContentBody
 *  com.atlassian.confluence.api.model.content.ContentRepresentation
 *  com.atlassian.confluence.api.model.content.History
 *  com.atlassian.confluence.api.model.content.Version
 *  com.atlassian.confluence.api.model.content.id.ContentId
 *  com.atlassian.confluence.api.model.people.Person
 *  com.atlassian.confluence.api.model.reference.EnrichableMap
 *  com.atlassian.confluence.api.model.reference.ExpandedReference
 *  com.atlassian.confluence.api.model.reference.Reference
 *  com.atlassian.confluence.api.serialization.RestEnrichable
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 *  org.codehaus.jackson.annotate.JsonCreator
 *  org.codehaus.jackson.annotate.JsonIgnoreProperties
 *  org.codehaus.jackson.annotate.JsonProperty
 *  org.codehaus.jackson.map.annotate.JsonDeserialize
 */
package com.atlassian.confluence.plugins.files.api;

import com.atlassian.confluence.api.model.content.Content;
import com.atlassian.confluence.api.model.content.ContentBody;
import com.atlassian.confluence.api.model.content.ContentRepresentation;
import com.atlassian.confluence.api.model.content.History;
import com.atlassian.confluence.api.model.content.Version;
import com.atlassian.confluence.api.model.content.id.ContentId;
import com.atlassian.confluence.api.model.people.Person;
import com.atlassian.confluence.api.model.reference.EnrichableMap;
import com.atlassian.confluence.api.model.reference.ExpandedReference;
import com.atlassian.confluence.api.model.reference.Reference;
import com.atlassian.confluence.api.serialization.RestEnrichable;
import com.atlassian.confluence.plugins.files.api.CommentAnchor;
import java.util.List;
import java.util.Map;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JsonDeserialize;

@JsonIgnoreProperties(ignoreUnknown=true)
@RestEnrichable
public class FileComment {
    @JsonProperty
    private final ContentId id;
    @JsonDeserialize(as=ExpandedReference.class, contentAs=History.class)
    @JsonProperty
    private final Reference<History> history;
    @JsonDeserialize(as=EnrichableMap.class)
    @JsonProperty
    private final Map<ContentRepresentation, ContentBody> body;
    @JsonProperty
    private final CommentAnchor anchor;
    @JsonProperty
    private final Reference<Version> version;
    @JsonProperty
    private final List<FileComment> children;
    @JsonProperty
    private final Resolution resolved;
    @JsonProperty
    private final boolean hasEditPermission;
    @JsonProperty
    private final boolean hasDeletePermission;
    @JsonProperty
    private final boolean hasReplyPermission;
    @JsonProperty
    private final boolean hasResolvePermission;

    @JsonCreator
    public FileComment(@JsonProperty(value="id") ContentId contentId, @JsonProperty(value="history") ExpandedReference<History> history, @JsonProperty(value="body") EnrichableMap<ContentRepresentation, ContentBody> body, @JsonProperty(value="version") ExpandedReference<Version> version, @JsonProperty(value="children") List<FileComment> children, @JsonProperty(value="anchor") @Nullable CommentAnchor anchor, @JsonProperty(value="resolved") Resolution resolved, @JsonProperty(value="hasEditPermission") boolean hasEditPermission, @JsonProperty(value="hasDeletePermission") boolean hasDeletePermission, @JsonProperty(value="hasReplyPermission") boolean hasReplyPermission, @JsonProperty(value="hasResolvePermission") boolean hasResolvePermission) {
        this.id = contentId;
        this.history = history;
        this.body = body;
        this.version = version;
        this.children = children;
        this.anchor = anchor;
        this.resolved = resolved;
        this.hasEditPermission = hasEditPermission;
        this.hasDeletePermission = hasDeletePermission;
        this.hasReplyPermission = hasReplyPermission;
        this.hasResolvePermission = hasResolvePermission;
    }

    public FileComment(@Nonnull Content content, @Nullable CommentAnchor anchor, boolean resolved, @Nonnull Person resolvingPerson, @Nonnull List<FileComment> replies, boolean hasEditPermission, boolean hasDeletePermission, boolean hasReplyPermission, boolean hasResolvePermission) {
        this.id = content.getId();
        this.history = content.getHistoryRef();
        this.body = content.getBody();
        this.version = Reference.to((Object)content.getVersion());
        this.children = replies;
        this.hasEditPermission = hasEditPermission;
        this.hasDeletePermission = hasDeletePermission;
        this.hasReplyPermission = hasReplyPermission;
        this.hasResolvePermission = hasResolvePermission;
        this.anchor = anchor;
        this.resolved = new Resolution(resolved, resolvingPerson);
    }

    public ContentId getId() {
        return this.id;
    }

    public Reference<History> getHistory() {
        return this.history;
    }

    public Map<ContentRepresentation, ContentBody> getBody() {
        return this.body;
    }

    public CommentAnchor getAnchor() {
        return this.anchor;
    }

    public Reference<Version> getVersion() {
        return this.version;
    }

    public List<FileComment> getChildren() {
        return this.children;
    }

    public Resolution getResolved() {
        return this.resolved;
    }

    public boolean getHasEditPermission() {
        return this.hasEditPermission;
    }

    public boolean getHasDeletePermission() {
        return this.hasDeletePermission;
    }

    public boolean getHasReplyPermission() {
        return this.hasReplyPermission;
    }

    public boolean getResolvePermission() {
        return this.hasResolvePermission;
    }

    public static class Resolution {
        @JsonProperty
        private final boolean value;
        @JsonProperty
        private final Person by;

        @JsonCreator
        private Resolution(@JsonProperty(value="value") boolean value, @JsonProperty(value="by") Person by) {
            this.value = value;
            this.by = by;
        }

        public boolean getValue() {
            return this.value;
        }

        public Person getBy() {
            return this.by;
        }
    }
}

