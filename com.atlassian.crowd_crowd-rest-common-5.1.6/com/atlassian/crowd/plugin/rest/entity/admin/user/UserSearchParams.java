/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.MoreObjects
 *  com.google.common.collect.ImmutableList
 *  javax.annotation.Nullable
 *  org.codehaus.jackson.annotate.JsonCreator
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.crowd.plugin.rest.entity.admin.user;

import com.google.common.base.MoreObjects;
import com.google.common.collect.ImmutableList;
import java.util.List;
import java.util.Objects;
import javax.annotation.Nullable;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

public class UserSearchParams {
    @JsonProperty(value="search")
    private final String search;
    @JsonProperty(value="directoryIds")
    @Nullable
    private final List<Long> directoryIds;
    @JsonProperty(value="active")
    private final Boolean active;
    @JsonProperty(value="avatarSizeHint")
    private final int avatarSizeHint;

    @JsonCreator
    public UserSearchParams(@JsonProperty(value="search") String search, @JsonProperty(value="directoryIds") List<Long> directoryIds, @JsonProperty(value="active") Boolean active, @JsonProperty(value="avatarSizeHint") int avatarSizeHint) {
        this.search = search;
        this.directoryIds = directoryIds != null ? ImmutableList.copyOf(directoryIds) : null;
        this.active = active;
        this.avatarSizeHint = avatarSizeHint;
    }

    public String getSearch() {
        return this.search;
    }

    public List<Long> getDirectoryIds() {
        return this.directoryIds;
    }

    public Boolean getActive() {
        return this.active;
    }

    public int getAvatarSizeHint() {
        return this.avatarSizeHint;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        UserSearchParams that = (UserSearchParams)o;
        return Objects.equals(this.getSearch(), that.getSearch()) && Objects.equals(this.getDirectoryIds(), that.getDirectoryIds()) && Objects.equals(this.getActive(), that.getActive()) && Objects.equals(this.getAvatarSizeHint(), that.getAvatarSizeHint());
    }

    public int hashCode() {
        return Objects.hash(this.getSearch(), this.getDirectoryIds(), this.getActive(), this.getAvatarSizeHint());
    }

    public String toString() {
        return MoreObjects.toStringHelper((Object)this).add("search", (Object)this.getSearch()).add("directoryIds", this.getDirectoryIds()).add("active", (Object)this.getActive()).add("avatarSizeHint", this.getAvatarSizeHint()).toString();
    }
}

