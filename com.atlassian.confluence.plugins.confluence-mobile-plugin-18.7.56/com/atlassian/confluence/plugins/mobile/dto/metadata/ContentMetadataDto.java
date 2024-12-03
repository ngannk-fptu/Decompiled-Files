/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.codehaus.jackson.annotate.JsonCreator
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.confluence.plugins.mobile.dto.metadata;

import com.atlassian.confluence.plugins.mobile.dto.LocationDto;
import com.atlassian.confluence.plugins.mobile.dto.metadata.ChildrenMetadataDto;
import com.atlassian.confluence.plugins.mobile.dto.metadata.CurrentUserMetadataDto;
import com.atlassian.confluence.plugins.mobile.dto.metadata.LikeMetadataDto;
import java.util.Objects;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

public class ContentMetadataDto {
    @JsonProperty
    private CurrentUserMetadataDto currentUser;
    @JsonProperty
    private LikeMetadataDto likes;
    @JsonProperty
    private ChildrenMetadataDto children;
    @JsonProperty
    private LocationDto location;
    public static final String CURRENT_USER_META = "currentuser";

    public static Builder builder() {
        return new Builder();
    }

    public static Builder builder(ContentMetadataDto metadataDto) {
        return new Builder().currentUser(metadataDto.currentUser).likes(metadataDto.likes).children(metadataDto.children);
    }

    @JsonCreator
    private ContentMetadataDto() {
        this(ContentMetadataDto.builder());
    }

    private ContentMetadataDto(Builder builder) {
        this.currentUser = builder.currentUser;
        this.likes = builder.likes;
        this.children = builder.children;
        this.location = builder.location;
    }

    public CurrentUserMetadataDto getCurrentUser() {
        return this.currentUser;
    }

    public LikeMetadataDto getLikes() {
        return this.likes;
    }

    public ChildrenMetadataDto getChildren() {
        return this.children;
    }

    public LocationDto getLocation() {
        return this.location;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        ContentMetadataDto that = (ContentMetadataDto)o;
        return Objects.equals(this.getCurrentUser(), that.getCurrentUser()) && Objects.equals(this.getLikes(), that.getLikes());
    }

    public int hashCode() {
        int result = this.currentUser != null ? this.currentUser.hashCode() : 0;
        return 31 * result + (this.likes != null ? this.likes.hashCode() : 0);
    }

    public static class Builder {
        private CurrentUserMetadataDto currentUser;
        private LikeMetadataDto likes;
        private ChildrenMetadataDto children;
        private LocationDto location;

        public Builder currentUser(CurrentUserMetadataDto currentUser) {
            this.currentUser = currentUser;
            return this;
        }

        public Builder likes(LikeMetadataDto likes) {
            this.likes = likes;
            return this;
        }

        public Builder children(ChildrenMetadataDto children) {
            this.children = children;
            return this;
        }

        public Builder location(LocationDto location) {
            this.location = location;
            return this;
        }

        public ContentMetadataDto build() {
            return new ContentMetadataDto(this);
        }
    }
}

