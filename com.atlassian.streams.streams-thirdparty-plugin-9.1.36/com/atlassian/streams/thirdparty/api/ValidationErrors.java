/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.streams.api.Html
 *  com.atlassian.streams.api.common.Option
 *  com.google.common.base.Joiner
 *  com.google.common.base.Preconditions
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableList$Builder
 *  com.google.common.collect.ImmutableSet
 *  javax.annotation.Nonnull
 */
package com.atlassian.streams.thirdparty.api;

import com.atlassian.streams.api.Html;
import com.atlassian.streams.api.common.Option;
import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Set;
import javax.annotation.Nonnull;

public class ValidationErrors {
    public static final int MAX_CONTENT_LENGTH = 5000;
    public static final int MAX_STRING_LENGTH = 255;
    private static final Set<String> ALLOWED_URI_SCHEMES = ImmutableSet.of((Object)"http", (Object)"https");
    private Iterable<String> messages;

    private ValidationErrors(Iterable<String> messages) {
        this.messages = messages;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static ValidationErrors validationError(String message) {
        return new ValidationErrors((Iterable<String>)ImmutableList.of((Object)message));
    }

    public Iterable<String> getMessages() {
        return this.messages;
    }

    public String toString() {
        return Joiner.on((String)"; ").join(this.messages);
    }

    public static class Builder {
        private ImmutableList.Builder<String> messages;

        public Builder addError(String message) {
            if (this.messages == null) {
                this.messages = new ImmutableList.Builder();
            }
            this.messages.add((Object)message);
            return this;
        }

        public Builder addAll(ValidationErrors from) {
            for (String message : from.getMessages()) {
                this.addError(message);
            }
            return this;
        }

        public Builder addAll(ValidationErrors from, String subPropertyName) {
            for (String message : from.getMessages()) {
                this.addError(subPropertyName + ": " + message);
            }
            return this;
        }

        public boolean isEmpty() {
            return this.messages == null;
        }

        public ValidationErrors build() {
            return new ValidationErrors((Iterable)this.messages.build());
        }

        @Deprecated
        public Option<Html> checkHtml(Option<Html> value, String propertyName, int maxLength) {
            Preconditions.checkNotNull(value, (Object)propertyName);
            if (value.isDefined()) {
                this.checkString(((Html)value.get()).toString(), propertyName, maxLength);
            }
            return value;
        }

        public Html checkHtml(@Nonnull Html value, @Nonnull String propertyName, int maxLength) {
            Preconditions.checkNotNull((Object)value, (Object)propertyName);
            this.checkString(value.toString(), propertyName, maxLength);
            return value;
        }

        public String checkString(String value, String propertyName) {
            return this.checkString(value, propertyName, 255);
        }

        public String checkString(String value, String propertyName, int maxLength) {
            Preconditions.checkNotNull((Object)value, (Object)propertyName);
            if (value.length() > maxLength) {
                this.addError(propertyName + " exceeds maximum length of " + maxLength + " characters");
            }
            return value;
        }

        public Option<String> checkString(Option<String> value, String propertyName) {
            return this.checkString(value, propertyName, 255);
        }

        public Option<String> checkString(Option<String> value, String propertyName, int maxLength) {
            if (value.isDefined()) {
                this.checkString((String)value.get(), propertyName, maxLength);
            }
            return value;
        }

        @Deprecated
        public Option<URI> checkSimpleNameOrAbsoluteUri(Option<URI> optionalUri, String propertyName) {
            return this.checkUriInternal(optionalUri, propertyName, false);
        }

        public URI checkSimpleNameOrAbsoluteUri(@Nonnull URI uri, @Nonnull String propertyName) {
            return this.checkUriInternal(uri, propertyName, false);
        }

        @Deprecated
        public Option<URI> checkAbsoluteUri(Option<URI> optionalUri, String propertyName) {
            return this.checkUriInternal(optionalUri, propertyName, true);
        }

        public URI checkAbsoluteUri(@Nonnull URI uri, @Nonnull String propertyName) {
            return this.checkUriInternal(uri, propertyName, true);
        }

        @Deprecated
        public Option<URI> checkSimpleNameOrAbsoluteUriString(Option<String> optionalUriString, String propertyName) {
            return this.checkUriStringInternal(optionalUriString, propertyName, false);
        }

        public URI checkSimpleNameOrAbsoluteUriString(@Nonnull String uriString, @Nonnull String propertyName) {
            return this.checkUriStringInternal(uriString, propertyName, false);
        }

        @Deprecated
        public Option<URI> checkAbsoluteUriString(Option<String> optionalUriString, String propertyName) {
            return this.checkUriStringInternal(optionalUriString, propertyName, true);
        }

        public URI checkAbsoluteUriString(@Nonnull String uriString, @Nonnull String propertyName) {
            return this.checkUriStringInternal(uriString, propertyName, true);
        }

        @Deprecated
        private Option<URI> checkUriInternal(Option<URI> optionalUri, String propertyName, boolean mustBeAbsolute) {
            Preconditions.checkNotNull(optionalUri, (Object)propertyName);
            for (URI uri : optionalUri) {
                this.checkString(uri.toASCIIString(), propertyName, 255);
                if (uri.isAbsolute()) {
                    if (ALLOWED_URI_SCHEMES.contains(uri.getScheme())) continue;
                    this.addError(propertyName + " must start with a valid scheme (http/https).");
                    continue;
                }
                if (mustBeAbsolute) {
                    this.addError(propertyName + " must be an absolute URI");
                    continue;
                }
                if (!uri.toASCIIString().contains("/")) continue;
                this.addError(propertyName + " must be either an absolute URI or a simple name, cannot contain slashes");
            }
            return optionalUri;
        }

        private URI checkUriInternal(URI uri, String propertyName, boolean mustBeAbsolute) {
            Preconditions.checkNotNull((Object)uri, (Object)propertyName);
            this.checkString(uri.toASCIIString(), propertyName, 255);
            if (uri.isAbsolute()) {
                if (!ALLOWED_URI_SCHEMES.contains(uri.getScheme())) {
                    this.addError(propertyName + " must start with a valid scheme (http/https).");
                }
            } else if (mustBeAbsolute) {
                this.addError(propertyName + " must be an absolute URI");
            } else if (uri.toASCIIString().contains("/")) {
                this.addError(propertyName + " must be either an absolute URI or a simple name, cannot contain slashes");
            }
            return uri;
        }

        @Deprecated
        private Option<URI> checkUriStringInternal(Option<String> optionalUriString, String propertyName, boolean mustBeAbsolute) {
            Preconditions.checkNotNull(optionalUriString, (Object)propertyName);
            for (String uriString : optionalUriString) {
                try {
                    return this.checkUriInternal((Option<URI>)Option.some((Object)new URI(uriString)), propertyName, mustBeAbsolute);
                }
                catch (URISyntaxException e) {
                    this.addError(propertyName + " is not a valid URI");
                }
            }
            return Option.none();
        }

        private URI checkUriStringInternal(String uriString, String propertyName, boolean mustBeAbsolute) {
            Preconditions.checkNotNull((Object)uriString, (Object)propertyName);
            try {
                return this.checkUriInternal(new URI(uriString), propertyName, mustBeAbsolute);
            }
            catch (URISyntaxException e) {
                this.addError(propertyName + " is not a valid URI");
                return null;
            }
        }
    }
}

