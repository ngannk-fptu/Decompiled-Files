/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.streams.api.Html
 *  com.atlassian.streams.api.common.Either
 *  com.atlassian.streams.api.common.Option
 */
package com.atlassian.streams.thirdparty.api;

import com.atlassian.streams.api.Html;
import com.atlassian.streams.api.common.Either;
import com.atlassian.streams.api.common.Option;
import com.atlassian.streams.thirdparty.api.ValidationErrors;
import java.net.URI;

public class ActivityObject {
    private final Option<String> displayName;
    private final Option<URI> id;
    private final Option<URI> type;
    private final Option<Html> summary;
    private final Option<URI> url;

    public static Builder builder() {
        return new Builder();
    }

    private ActivityObject(Builder builder) {
        this.displayName = builder.displayName;
        this.id = builder.id;
        this.type = builder.type;
        this.summary = builder.summary;
        this.url = builder.url;
    }

    public Option<String> getDisplayName() {
        return this.displayName;
    }

    public Option<URI> getId() {
        return this.id;
    }

    public Option<Html> getSummary() {
        return this.summary;
    }

    public Option<URI> getType() {
        return this.type;
    }

    public Option<URI> getUrl() {
        return this.url;
    }

    public boolean equals(Object other) {
        if (other instanceof ActivityObject) {
            ActivityObject o = (ActivityObject)other;
            return this.displayName.equals(o.displayName) && this.id.equals(o.id) && this.summary.equals(o.summary) && this.type.equals(o.type) && this.url.equals(o.url);
        }
        return false;
    }

    public int hashCode() {
        return this.displayName.hashCode() + 37 * (this.id.hashCode() + 37 * (this.summary.hashCode() + 37 * (this.type.hashCode() + 37 * this.url.hashCode())));
    }

    public static final class Builder {
        private final ValidationErrors.Builder errors = new ValidationErrors.Builder();
        private Option<String> displayName = Option.none();
        private Option<URI> id = Option.none();
        private Option<URI> type = Option.none();
        private Option<Html> summary = Option.none();
        private Option<URI> url = Option.none();

        public Either<ValidationErrors, ActivityObject> build() {
            if (this.errors.isEmpty()) {
                return Either.right((Object)new ActivityObject(this));
            }
            return Either.left((Object)this.errors.build());
        }

        public Builder displayName(Option<String> displayName) {
            this.displayName = this.errors.checkString(displayName, "displayName");
            return this;
        }

        public Builder id(Option<URI> id) {
            this.id = this.errors.checkAbsoluteUri(id, "id");
            return this;
        }

        public Builder idString(Option<String> id) {
            this.id = this.errors.checkAbsoluteUriString(id, "id");
            return this;
        }

        public Builder summary(Option<Html> summary) {
            this.summary = this.errors.checkHtml(summary, "summary", 255);
            return this;
        }

        public Builder type(Option<URI> type) {
            this.type = this.errors.checkSimpleNameOrAbsoluteUri(type, "type");
            return this;
        }

        public Builder typeString(Option<String> type) {
            this.type = this.errors.checkSimpleNameOrAbsoluteUriString(type, "type");
            return this;
        }

        public Builder url(Option<URI> url) {
            this.url = this.errors.checkSimpleNameOrAbsoluteUri(url, "url");
            return this;
        }

        public Builder urlString(Option<String> url) {
            this.url = this.errors.checkSimpleNameOrAbsoluteUriString(url, "url");
            return this;
        }
    }
}

