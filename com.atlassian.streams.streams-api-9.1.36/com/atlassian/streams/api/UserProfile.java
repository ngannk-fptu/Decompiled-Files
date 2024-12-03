/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 */
package com.atlassian.streams.api;

import com.atlassian.streams.api.common.Option;
import com.google.common.base.Preconditions;
import java.net.URI;

public class UserProfile {
    private final String username;
    private final String fullName;
    private final Option<String> email;
    private final Option<URI> profilePageUri;
    private final Option<URI> profilePictureUri;

    UserProfile(Builder builder) {
        this.username = builder.username;
        this.fullName = builder.fullName != null ? builder.fullName : builder.username;
        this.email = builder.email;
        this.profilePageUri = builder.profilePageUri;
        this.profilePictureUri = builder.profilePictureUri;
    }

    public String getUsername() {
        return this.username;
    }

    public String getFullName() {
        return this.fullName;
    }

    public Option<String> getEmail() {
        return this.email;
    }

    public Option<URI> getProfilePageUri() {
        return this.profilePageUri;
    }

    public Option<URI> getProfilePictureUri() {
        return this.profilePictureUri;
    }

    public boolean equals(Object other) {
        if (other instanceof UserProfile) {
            UserProfile u = (UserProfile)other;
            return this.username.equals(u.username) && this.fullName.equals(u.fullName) && this.email.equals(u.email) && this.profilePageUri.equals(u.profilePageUri) && this.profilePictureUri.equals(u.profilePictureUri);
        }
        return false;
    }

    public int hashCode() {
        return this.username.hashCode() + 37 * (this.fullName.hashCode() + 37 * (this.email.hashCode() + 37 * (this.profilePageUri.hashCode() + 37 * this.profilePictureUri.hashCode())));
    }

    public static class Builder {
        private final String username;
        private String fullName;
        private Option<String> email = Option.none();
        private Option<URI> profilePageUri = Option.none();
        private Option<URI> profilePictureUri = Option.none();

        public Builder(String username) {
            this.username = (String)Preconditions.checkNotNull((Object)username, (Object)"username");
        }

        public UserProfile build() {
            return new UserProfile(this);
        }

        public Builder fullName(String fullName) {
            this.fullName = fullName;
            return this;
        }

        public Builder email(Option<String> email) {
            this.email = email;
            return this;
        }

        public Builder profilePageUri(Option<URI> profilePageUri) {
            this.profilePageUri = (Option)Preconditions.checkNotNull(profilePageUri, (Object)"profilePageUri");
            return this;
        }

        public Builder profilePictureUri(Option<URI> profilePictureUri) {
            this.profilePictureUri = (Option)Preconditions.checkNotNull(profilePictureUri, (Object)"profilePictureUri");
            return this;
        }
    }
}

