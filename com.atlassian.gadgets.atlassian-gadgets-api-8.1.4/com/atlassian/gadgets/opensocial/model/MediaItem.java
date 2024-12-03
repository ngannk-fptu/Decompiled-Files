/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.jcip.annotations.Immutable
 */
package com.atlassian.gadgets.opensocial.model;

import java.net.URI;
import net.jcip.annotations.Immutable;

@Immutable
public final class MediaItem {
    private final String mimeType;
    private final Type type;
    private final URI url;

    public MediaItem(Builder builder) {
        if (builder.mimeType == null) {
            throw new NullPointerException("builder.mimeType must not be null");
        }
        if (builder.type == null) {
            throw new NullPointerException("builder.type must not be null");
        }
        if (builder.url == null) {
            throw new NullPointerException("builder.url must not be null");
        }
        this.mimeType = builder.mimeType;
        this.type = builder.type;
        this.url = builder.url;
    }

    public String getMimeType() {
        return this.mimeType;
    }

    public Type getType() {
        return this.type;
    }

    public URI getUrl() {
        return this.url;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        MediaItem mediaItem = (MediaItem)o;
        if (this.mimeType != null ? !this.mimeType.equals(mediaItem.mimeType) : mediaItem.mimeType != null) {
            return false;
        }
        return !(this.url != null ? !this.url.equals(mediaItem.url) : mediaItem.url != null);
    }

    public int hashCode() {
        int result = this.mimeType != null ? this.mimeType.hashCode() : 0;
        result = 31 * result + (this.url != null ? this.url.hashCode() : 0);
        return result;
    }

    public static class Builder {
        private String mimeType;
        private Type type;
        private URI url;

        public Builder(URI url) {
            this.url = url;
        }

        public Builder mimeType(String mimeType) {
            this.mimeType = mimeType;
            return this;
        }

        public Builder type(Type type) {
            this.type = type;
            return this;
        }

        public Builder url(URI url) {
            this.url = url;
            return this;
        }

        public MediaItem build() {
            return new MediaItem(this);
        }
    }

    public static enum Type {
        AUDIO,
        IMAGE,
        VIDEO;

    }
}

