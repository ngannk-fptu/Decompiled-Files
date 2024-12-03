/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.codehaus.jackson.annotate.JsonCreator
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.confluence.plugins.mobile.dto.metadata;

import com.atlassian.confluence.plugins.mobile.dto.metadata.CurrentUserPermissionDto;
import java.util.Objects;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

public class CurrentUserMetadataDto {
    @JsonProperty
    private Boolean liked;
    @JsonProperty
    private Boolean saved;
    @JsonProperty
    private Boolean watched;
    @JsonProperty
    private CurrentUserPermissionDto permission;

    private CurrentUserMetadataDto(Builder builder) {
        this.liked = builder.liked;
        this.saved = builder.saved;
        this.watched = builder.watched;
        this.permission = builder.permission;
    }

    @JsonCreator
    private CurrentUserMetadataDto() {
        this(CurrentUserMetadataDto.builder());
    }

    public static Builder builder() {
        return new Builder();
    }

    public Boolean isLiked() {
        return this.liked;
    }

    public Boolean isSaved() {
        return this.saved;
    }

    public Boolean isWatched() {
        return this.watched;
    }

    public CurrentUserPermissionDto getPermission() {
        return this.permission;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        CurrentUserMetadataDto that = (CurrentUserMetadataDto)o;
        return Objects.equals(this.isLiked(), that.isLiked()) && Objects.equals(this.isSaved(), that.isSaved()) && Objects.equals(this.isWatched(), that.isWatched()) && Objects.equals(this.getPermission(), that.getPermission());
    }

    public int hashCode() {
        int result = this.liked != null ? Boolean.hashCode(this.liked) : 0;
        result = 31 * result + (this.watched != null ? Boolean.hashCode(this.watched) : 0);
        result = 31 * result + (this.permission != null ? this.permission.hashCode() : 0);
        return 31 * result + (this.saved != null ? Boolean.hashCode(this.saved) : 0);
    }

    public static class Builder {
        private Boolean liked;
        private Boolean saved;
        private Boolean watched;
        private CurrentUserPermissionDto permission;

        public Builder liked(boolean liked) {
            this.liked = liked;
            return this;
        }

        public Builder saved(boolean saved) {
            this.saved = saved;
            return this;
        }

        public Builder watched(boolean watched) {
            this.watched = watched;
            return this;
        }

        public Builder permission(CurrentUserPermissionDto permission) {
            this.permission = permission;
            return this;
        }

        public CurrentUserMetadataDto build() {
            return new CurrentUserMetadataDto(this);
        }
    }
}

