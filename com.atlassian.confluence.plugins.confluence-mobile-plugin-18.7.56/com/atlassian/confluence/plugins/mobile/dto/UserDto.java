/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.web.Icon
 *  org.codehaus.jackson.annotate.JsonCreator
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.confluence.plugins.mobile.dto;

import com.atlassian.confluence.api.model.web.Icon;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

public class UserDto {
    @JsonProperty
    private String username;
    @JsonProperty
    private String displayName;
    @JsonProperty
    private String userKey;
    @JsonProperty
    private String email;
    @JsonProperty
    private Icon profilePicture;

    public static Builder builder() {
        return new Builder();
    }

    @JsonCreator
    private UserDto() {
        this(UserDto.builder());
    }

    private UserDto(Builder builder) {
        this.username = builder.username;
        this.displayName = builder.displayName;
        this.userKey = builder.userKey;
        this.email = builder.email;
        this.profilePicture = builder.profilePicture;
    }

    public String getEmail() {
        return this.email;
    }

    public String getUsername() {
        return this.username;
    }

    public String getDisplayName() {
        return this.displayName;
    }

    public String getUserKey() {
        return this.userKey;
    }

    public Icon getProfilePicture() {
        return this.profilePicture;
    }

    public static final class Builder {
        private String username;
        private String displayName;
        private String userKey;
        private String email;
        private Icon profilePicture;

        public Builder username(String username) {
            this.username = username;
            return this;
        }

        public Builder displayName(String displayName) {
            this.displayName = displayName;
            return this;
        }

        public Builder userKey(String userKey) {
            this.userKey = userKey;
            return this;
        }

        public Builder email(String email) {
            this.email = email;
            return this;
        }

        public Builder profilePicture(Icon profilePicture) {
            this.profilePicture = profilePicture;
            return this;
        }

        public UserDto build() {
            return new UserDto(this);
        }
    }
}

