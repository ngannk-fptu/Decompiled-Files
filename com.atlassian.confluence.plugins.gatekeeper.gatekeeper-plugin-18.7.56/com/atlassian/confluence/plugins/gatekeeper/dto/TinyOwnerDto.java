/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.fasterxml.jackson.annotation.JsonAutoDetect
 *  com.fasterxml.jackson.annotation.JsonAutoDetect$Visibility
 *  com.fasterxml.jackson.annotation.JsonInclude
 *  com.fasterxml.jackson.annotation.JsonInclude$Include
 *  com.fasterxml.jackson.annotation.JsonProperty
 */
package com.atlassian.confluence.plugins.gatekeeper.dto;

import com.atlassian.confluence.plugins.gatekeeper.model.owner.TinyOwner;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonAutoDetect(fieldVisibility=JsonAutoDetect.Visibility.NONE, setterVisibility=JsonAutoDetect.Visibility.NONE, getterVisibility=JsonAutoDetect.Visibility.NONE, isGetterVisibility=JsonAutoDetect.Visibility.NONE, creatorVisibility=JsonAutoDetect.Visibility.NONE)
public final class TinyOwnerDto {
    @JsonProperty(value="n")
    public final String name;
    @JsonProperty(value="d")
    @JsonInclude(value=JsonInclude.Include.NON_DEFAULT)
    public final String displayName;
    @JsonProperty(value="anonymous")
    public final boolean anonymous;
    @JsonProperty(value="u")
    public final String avatarUrl;

    public TinyOwnerDto(TinyOwner owner, String avatarUrl) {
        this.name = owner.getName();
        this.displayName = owner.getDisplayName();
        this.anonymous = owner.isAnonymous();
        this.avatarUrl = avatarUrl;
    }

    private TinyOwnerDto(String name, String displayName, boolean anonymous, String avatarUrl) {
        this.name = name;
        this.displayName = displayName;
        this.anonymous = anonymous;
        this.avatarUrl = avatarUrl;
    }

    public String getName() {
        return this.name;
    }

    public String getDisplayName() {
        return this.displayName;
    }

    public boolean isAnonymous() {
        return this.anonymous;
    }

    public String getAvatarUrl() {
        return this.avatarUrl;
    }

    public static class Builder {
        private String name;
        private String displayName;
        private boolean anonymous = false;
        private String avatarUrl;

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder displayName(String displayName) {
            this.displayName = displayName;
            return this;
        }

        public Builder anonymous(boolean anonymous) {
            this.anonymous = anonymous;
            return this;
        }

        public Builder avatarUrl(String avatarUrl) {
            this.avatarUrl = avatarUrl;
            return this;
        }

        public TinyOwnerDto build() {
            return new TinyOwnerDto(this.name, this.displayName, this.anonymous, this.avatarUrl);
        }
    }
}

