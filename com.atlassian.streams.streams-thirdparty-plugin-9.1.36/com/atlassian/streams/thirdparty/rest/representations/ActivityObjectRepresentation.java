/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.streams.api.Html
 *  com.atlassian.streams.api.UserProfile
 *  com.atlassian.streams.api.UserProfile$Builder
 *  com.atlassian.streams.api.common.Either
 *  com.atlassian.streams.api.common.Option
 *  com.google.common.base.Functions
 *  com.google.common.base.Preconditions
 *  org.codehaus.jackson.annotate.JsonCreator
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.streams.thirdparty.rest.representations;

import com.atlassian.streams.api.Html;
import com.atlassian.streams.api.UserProfile;
import com.atlassian.streams.api.common.Either;
import com.atlassian.streams.api.common.Option;
import com.atlassian.streams.thirdparty.api.ActivityObject;
import com.atlassian.streams.thirdparty.api.ValidationErrors;
import com.atlassian.streams.thirdparty.rest.representations.MediaLinkRepresentation;
import com.google.common.base.Functions;
import com.google.common.base.Preconditions;
import java.net.URI;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

public class ActivityObjectRepresentation {
    @JsonProperty
    String content;
    @JsonProperty
    String displayName;
    @JsonProperty
    String id;
    @JsonProperty
    MediaLinkRepresentation image;
    @JsonProperty
    String objectType;
    @JsonProperty
    String summary;
    @JsonProperty
    String url;

    @JsonCreator
    public ActivityObjectRepresentation(@JsonProperty(value="content") String content, @JsonProperty(value="displayName") String displayName, @JsonProperty(value="id") String id, @JsonProperty(value="image") MediaLinkRepresentation image, @JsonProperty(value="objectType") String objectType, @JsonProperty(value="summary") String summary, @JsonProperty(value="uri") String url) {
        this.content = content;
        this.displayName = displayName;
        this.id = id;
        this.image = image;
        this.objectType = objectType;
        this.summary = summary;
        this.url = url;
    }

    public static Builder builder() {
        return new Builder();
    }

    private ActivityObjectRepresentation(Builder builder) {
        this.content = (String)builder.content.getOrElse((Object)null);
        this.displayName = (String)builder.displayName.getOrElse((Object)null);
        this.id = (String)builder.id.getOrElse((Object)null);
        this.image = (MediaLinkRepresentation)builder.image.getOrElse((Object)null);
        this.objectType = (String)builder.objectType.getOrElse((Object)null);
        this.summary = (String)builder.summary.map(Html.htmlToString()).getOrElse((Object)null);
        this.url = (String)builder.url.getOrElse((Object)null);
    }

    public Either<ValidationErrors, ActivityObject> toActivityObject() {
        return ActivityObject.builder().displayName((Option<String>)Option.option((Object)this.getDisplayName())).idString((Option<String>)Option.option((Object)this.getId())).typeString((Option<String>)Option.option((Object)this.getObjectType())).summary((Option<Html>)Option.option((Object)this.getSummary()).map(Html.html())).urlString((Option<String>)Option.option((Object)this.getUrl())).build();
    }

    public Either<ValidationErrors, UserProfile> toUserProfile() {
        ValidationErrors.Builder errors = new ValidationErrors.Builder();
        Option<URI> profileUri = errors.checkAbsoluteUriString((Option<String>)Option.option((Object)this.url), "url");
        Option<URI> pictureUri = this.image == null ? Option.none(URI.class) : errors.checkAbsoluteUriString((Option<String>)Option.option((Object)this.image.getUrl()), "image.url");
        if (errors.isEmpty()) {
            return Either.right((Object)new UserProfile.Builder(this.id == null ? "" : this.id).fullName(this.displayName == null ? "" : this.displayName).profilePageUri(profileUri).profilePictureUri(pictureUri).build());
        }
        return Either.left((Object)errors.build());
    }

    public String getContent() {
        return this.content;
    }

    public String getDisplayName() {
        return this.displayName;
    }

    public String getId() {
        return this.id;
    }

    public MediaLinkRepresentation getImage() {
        return this.image;
    }

    public String getObjectType() {
        return this.objectType;
    }

    public String getSummary() {
        return this.summary;
    }

    public String getUrl() {
        return this.url;
    }

    public static class Builder {
        private Option<String> content = Option.none();
        private Option<String> displayName = Option.none();
        private Option<String> id = Option.none();
        private Option<MediaLinkRepresentation> image = Option.none();
        private Option<String> objectType = Option.none();
        private Option<Html> summary = Option.none();
        private Option<String> url = Option.none();

        public Builder content(Option<String> content) {
            this.content = (Option)Preconditions.checkNotNull(content, (Object)"content");
            return this;
        }

        public Builder displayName(Option<String> displayName) {
            this.displayName = (Option)Preconditions.checkNotNull(displayName, (Object)"displayName");
            return this;
        }

        public Builder id(Option<URI> id) {
            this.id = ((Option)Preconditions.checkNotNull(id, (Object)"id")).map(Functions.toStringFunction());
            return this;
        }

        public Builder idString(Option<String> id) {
            this.id = (Option)Preconditions.checkNotNull(id, (Object)"id");
            return this;
        }

        public Builder image(Option<MediaLinkRepresentation> image) {
            this.image = (Option)Preconditions.checkNotNull(image, (Object)"image");
            return this;
        }

        public Builder objectType(Option<URI> objectType) {
            this.objectType = ((Option)Preconditions.checkNotNull(objectType, (Object)"objectType")).map(Functions.toStringFunction());
            return this;
        }

        public Builder objectTypeString(Option<String> objectType) {
            this.objectType = (Option)Preconditions.checkNotNull(objectType, (Object)"objectType");
            return this;
        }

        public Builder summary(Option<Html> summary) {
            this.summary = (Option)Preconditions.checkNotNull(summary, (Object)"summary");
            return this;
        }

        public Builder url(Option<URI> url) {
            this.url = ((Option)Preconditions.checkNotNull(url, (Object)"url")).map(Functions.toStringFunction());
            return this;
        }

        public Builder urlString(Option<String> url) {
            this.url = (Option)Preconditions.checkNotNull(url, (Object)"url");
            return this;
        }

        public ActivityObjectRepresentation build() {
            return new ActivityObjectRepresentation(this);
        }
    }
}

