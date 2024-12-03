/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.ExperimentalApi
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.codehaus.jackson.annotate.JsonCreator
 *  org.codehaus.jackson.annotate.JsonIgnoreProperties
 *  org.codehaus.jackson.annotate.JsonProperty
 *  org.codehaus.jackson.map.annotate.JsonDeserialize
 */
package com.atlassian.confluence.api.model.content;

import com.atlassian.annotations.ExperimentalApi;
import com.atlassian.confluence.api.model.content.Content;
import com.atlassian.confluence.api.model.content.ContentRepresentation;
import com.atlassian.confluence.api.model.content.ContentSelector;
import com.atlassian.confluence.api.model.content.FormattedBody;
import com.atlassian.confluence.api.model.content.id.ContentId;
import com.atlassian.confluence.api.model.content.webresource.WebResourceDependencies;
import com.atlassian.confluence.api.model.reference.ExpandedReference;
import com.atlassian.confluence.api.model.reference.Reference;
import com.atlassian.confluence.api.serialization.RestEnrichable;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JsonDeserialize;

@ExperimentalApi
@RestEnrichable
@JsonIgnoreProperties(ignoreUnknown=true)
public class ContentBody
extends FormattedBody {
    @JsonDeserialize(as=ExpandedReference.class, contentAs=Content.class)
    @JsonProperty
    private final Reference<Content> content;

    @Deprecated
    public ContentBody(ContentRepresentation representation, String value, Reference<Content> contentReference) {
        this((ContentBodyBuilder)((ContentBodyBuilder)((ContentBodyBuilder)new ContentBodyBuilder().representation(representation)).value(value)).content(contentReference).webresource((Reference)Reference.empty(WebResourceDependencies.class)));
    }

    @Deprecated
    public ContentBody(ContentRepresentation representation, String value, Reference<Content> contentReference, Reference<WebResourceDependencies> webresource) {
        this((ContentBodyBuilder)((ContentBodyBuilder)((ContentBodyBuilder)new ContentBodyBuilder().representation(representation)).value(value)).content(contentReference).webresource((Reference)webresource));
    }

    @Deprecated
    public ContentBody(ContentRepresentation representation, String value, ContentId contentId) {
        this(((ContentBodyBuilder)((ContentBodyBuilder)new ContentBodyBuilder().representation(representation)).value(value)).content(Content.buildReference(ContentSelector.fromId(contentId))));
    }

    @Deprecated
    public ContentBody(ContentRepresentation representation, String value, Content content) {
        this(representation, value, Reference.to(content));
    }

    @JsonCreator
    public ContentBody(@JsonProperty(value="representation") ContentRepresentation representation, @JsonProperty(value="value") String value) {
        this(representation, value, Reference.empty(Content.class));
    }

    public ContentBody(ContentBodyBuilder contentBodyBuilder) {
        super(contentBodyBuilder);
        this.content = contentBodyBuilder.ref;
    }

    public static ContentBody emptyBody(ContentRepresentation representation, Reference<Content> contentReference) {
        return ((ContentBodyBuilder)((ContentBodyBuilder)ContentBody.contentBodyBuilder().representation(representation)).value(null)).content(contentReference).build();
    }

    public static ContentBodyBuilder contentBodyBuilder() {
        return new ContentBodyBuilder();
    }

    public Reference<Content> getContentRef() {
        if (this.content == null) {
            return Reference.empty(Content.class);
        }
        return this.content;
    }

    public boolean hasExpandedContentRef() {
        Reference<Content> contentRef = this.getContentRef();
        return contentRef.exists() && contentRef.isExpanded();
    }

    public static final class ContentBodyBuilder
    extends FormattedBody.BaseFormattedBodyBuilder<ContentBodyBuilder> {
        private Reference<Content> ref = Reference.empty(Content.class);

        ContentBodyBuilder() {
        }

        @Override
        public ContentBody build() {
            return new ContentBody(this);
        }

        public ContentBodyBuilder content(@NonNull Reference<Content> contentRef) {
            this.ref = contentRef;
            return this;
        }

        @Deprecated
        public ContentBodyBuilder contentId(ContentId contentId) {
            return this.content(ContentSelector.fromId(contentId));
        }

        public ContentBodyBuilder content(ContentSelector selector) {
            this.ref = Content.buildReference(selector);
            return this;
        }

        public ContentBodyBuilder content(Content content) {
            this.ref = Reference.to(content);
            return this;
        }
    }
}

