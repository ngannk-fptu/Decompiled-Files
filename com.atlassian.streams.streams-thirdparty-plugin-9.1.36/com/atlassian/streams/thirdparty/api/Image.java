/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.streams.api.common.Either
 *  com.atlassian.streams.api.common.Option
 *  com.google.common.base.Preconditions
 */
package com.atlassian.streams.thirdparty.api;

import com.atlassian.streams.api.common.Either;
import com.atlassian.streams.api.common.Option;
import com.atlassian.streams.thirdparty.api.ValidationErrors;
import com.google.common.base.Preconditions;
import java.net.URI;

public class Image {
    private final Option<Integer> height;
    private final URI url;
    private final Option<Integer> width;

    private Image(Builder builder) {
        this.height = builder.height;
        this.url = builder.url;
        this.width = builder.width;
    }

    public static Image withUrl(URI url) {
        Either<ValidationErrors, Image> ret = Image.builder(url).build();
        if (ret.isLeft()) {
            throw new IllegalArgumentException(((ValidationErrors)ret.left().get()).toString());
        }
        return (Image)ret.right().get();
    }

    public static Builder builder(URI url) {
        return new Builder(url);
    }

    public Option<Integer> getHeight() {
        return this.height;
    }

    public URI getUrl() {
        return this.url;
    }

    public Option<Integer> getWidth() {
        return this.width;
    }

    public boolean equals(Object other) {
        if (other instanceof Image) {
            Image i = (Image)other;
            return this.url.equals(i.url) && this.height.equals(i.height) && this.width.equals(i.width);
        }
        return false;
    }

    public int hashCode() {
        return (this.height.hashCode() * 37 + this.url.hashCode()) * 37 + this.width.hashCode();
    }

    public static final class Builder {
        private final ValidationErrors.Builder errors = new ValidationErrors.Builder();
        private URI url;
        private Option<Integer> height = Option.none();
        private Option<Integer> width = Option.none();

        public Builder(URI url) {
            this.errors.checkAbsoluteUri((Option<URI>)Option.some((Object)url), "url");
            this.url = url;
        }

        public Builder(String urlString) {
            this.url = (URI)this.errors.checkAbsoluteUriString((Option<String>)Option.some((Object)urlString), "url").getOrElse((Object)null);
        }

        public Either<ValidationErrors, Image> build() {
            if (this.errors.isEmpty()) {
                return Either.right((Object)new Image(this));
            }
            return Either.left((Object)this.errors.build());
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

