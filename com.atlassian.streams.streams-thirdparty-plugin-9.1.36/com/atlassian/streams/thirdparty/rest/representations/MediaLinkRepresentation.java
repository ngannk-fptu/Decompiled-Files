/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.streams.api.common.Either
 *  com.atlassian.streams.api.common.Option
 *  com.google.common.base.Preconditions
 *  org.codehaus.jackson.annotate.JsonCreator
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.streams.thirdparty.rest.representations;

import com.atlassian.streams.api.common.Either;
import com.atlassian.streams.api.common.Option;
import com.atlassian.streams.thirdparty.api.Image;
import com.atlassian.streams.thirdparty.api.ValidationErrors;
import com.google.common.base.Preconditions;
import java.net.URI;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

public class MediaLinkRepresentation {
    @JsonProperty
    Integer duration;
    @JsonProperty
    Integer height;
    @JsonProperty
    String url;
    @JsonProperty
    Integer width;

    @JsonCreator
    public MediaLinkRepresentation(@JsonProperty(value="duration") Integer duration, @JsonProperty(value="height") Integer height, @JsonProperty(value="url") String url, @JsonProperty(value="width") Integer width) {
        this.duration = duration;
        this.height = height;
        this.url = url;
        this.width = width;
    }

    public static Builder builder(URI url) {
        return new Builder(url);
    }

    private MediaLinkRepresentation(Builder builder) {
        this.duration = (Integer)builder.duration.getOrElse((Object)null);
        this.height = (Integer)builder.height.getOrElse((Object)null);
        this.url = builder.url;
        this.width = (Integer)builder.width.getOrElse((Object)null);
    }

    public Either<ValidationErrors, Image> toImage() {
        if (this.getUrl() == null) {
            return Either.left((Object)ValidationErrors.validationError("url is required"));
        }
        return new Image.Builder(this.getUrl()).height((Option<Integer>)Option.option((Object)this.getHeight())).width((Option<Integer>)Option.option((Object)this.getWidth())).build();
    }

    public Integer getDuration() {
        return this.duration;
    }

    public Integer getHeight() {
        return this.height;
    }

    public String getUrl() {
        return this.url;
    }

    public Integer getWidth() {
        return this.width;
    }

    public static class Builder {
        private Option<Integer> duration = Option.none();
        private Option<Integer> height = Option.none();
        private String url;
        private Option<Integer> width = Option.none();

        public Builder(URI url) {
            this.url = url.toString();
        }

        public Builder(String urlString) {
            this.url = urlString;
        }

        public MediaLinkRepresentation build() {
            return new MediaLinkRepresentation(this);
        }

        public Builder duration(Option<Integer> duration) {
            this.duration = (Option)Preconditions.checkNotNull(duration, (Object)"duration");
            return this;
        }

        public Builder height(Option<Integer> height) {
            this.height = (Option)Preconditions.checkNotNull(height, (Object)"height");
            return this;
        }

        public Builder width(Option<Integer> width) {
            this.width = (Option)Preconditions.checkNotNull(width, (Object)"width");
            return this;
        }
    }
}

