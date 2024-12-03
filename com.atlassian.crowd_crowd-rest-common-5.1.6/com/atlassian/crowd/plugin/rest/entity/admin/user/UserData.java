/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.embedded.api.Directory
 *  com.atlassian.crowd.embedded.api.User
 *  com.fasterxml.jackson.annotation.JsonInclude
 *  com.fasterxml.jackson.annotation.JsonInclude$Include
 *  com.google.common.base.MoreObjects
 *  org.codehaus.jackson.annotate.JsonCreator
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.crowd.plugin.rest.entity.admin.user;

import com.atlassian.crowd.embedded.api.Directory;
import com.atlassian.crowd.embedded.api.User;
import com.atlassian.crowd.plugin.rest.entity.admin.directory.DirectoryData;
import com.atlassian.crowd.plugin.rest.entity.admin.directory.DirectoryEntityId;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.google.common.base.MoreObjects;
import java.util.Objects;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

@JsonInclude(value=JsonInclude.Include.NON_NULL)
public class UserData {
    @JsonProperty(value="id")
    private final DirectoryEntityId id;
    @JsonProperty(value="username")
    private final String username;
    @JsonProperty(value="displayName")
    private final String displayName;
    @JsonProperty(value="email")
    private final String email;
    @JsonProperty(value="active")
    private final Boolean active;
    @JsonProperty(value="directory")
    private final DirectoryData directory;
    @JsonProperty(value="avatarUrl")
    private final String avatarUrl;

    public static UserData fromUser(User user, Directory directory) {
        return UserData.fromUser(user, DirectoryData.fromDirectory(directory));
    }

    public static UserData fromUser(User user, DirectoryData directoryData) {
        return UserData.builderFromUser(user).setDirectory(directoryData).build();
    }

    public static UserData fromUserWithAvatarUrl(User user, DirectoryData directoryData, String avatarUrl) {
        return UserData.builderFromUser(user).setDirectory(directoryData).setAvatarUrl(avatarUrl).build();
    }

    public static UserData fromUser(User user) {
        return UserData.builderFromUser(user).build();
    }

    private static Builder builderFromUser(User user) {
        return UserData.builder().setId(DirectoryEntityId.fromUser(user)).setUsername(user.getName()).setDisplayName(user.getDisplayName()).setEmail(user.getEmailAddress()).setActive(user.isActive());
    }

    public static Builder builder(DirectoryData directory, String name) {
        return UserData.builder().setId(new DirectoryEntityId(directory.getId(), name)).setDirectory(directory).setUsername(name);
    }

    public Builder toBuilder() {
        return UserData.builder(this);
    }

    @JsonCreator
    public UserData(@JsonProperty(value="id") DirectoryEntityId id, @JsonProperty(value="username") String username, @JsonProperty(value="displayName") String displayName, @JsonProperty(value="email") String email, @JsonProperty(value="active") Boolean active, @JsonProperty(value="directory") DirectoryData directory, @JsonProperty(value="avatarUrl") String avatarUrl) {
        this.id = id;
        this.username = username;
        this.displayName = displayName;
        this.email = email;
        this.active = active;
        this.directory = directory;
        this.avatarUrl = avatarUrl;
    }

    public DirectoryEntityId getId() {
        return this.id;
    }

    public String getUsername() {
        return this.username;
    }

    public String getDisplayName() {
        return this.displayName;
    }

    public String getEmail() {
        return this.email;
    }

    public Boolean getActive() {
        return this.active;
    }

    public DirectoryData getDirectory() {
        return this.directory;
    }

    public String getAvatarUrl() {
        return this.avatarUrl;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static Builder builder(UserData data) {
        return new Builder(data);
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        UserData that = (UserData)o;
        return Objects.equals(this.getId(), that.getId()) && Objects.equals(this.getUsername(), that.getUsername()) && Objects.equals(this.getDisplayName(), that.getDisplayName()) && Objects.equals(this.getEmail(), that.getEmail()) && Objects.equals(this.getActive(), that.getActive()) && Objects.equals(this.getDirectory(), that.getDirectory()) && Objects.equals(this.getAvatarUrl(), that.getAvatarUrl());
    }

    public int hashCode() {
        return Objects.hash(this.getId(), this.getUsername(), this.getDisplayName(), this.getEmail(), this.getActive(), this.getDirectory(), this.getAvatarUrl());
    }

    public String toString() {
        return MoreObjects.toStringHelper((Object)this).add("id", (Object)this.getId()).add("username", (Object)this.getUsername()).add("displayName", (Object)this.getDisplayName()).add("email", (Object)this.getEmail()).add("active", (Object)this.getActive()).add("directory", (Object)this.getDirectory()).add("avatarUrl", (Object)this.getAvatarUrl()).toString();
    }

    public static final class Builder {
        private DirectoryEntityId id;
        private String username;
        private String displayName;
        private String email;
        private Boolean active;
        private DirectoryData directory;
        private String avatarUrl;

        private Builder() {
        }

        private Builder(UserData initialData) {
            this.id = initialData.getId();
            this.username = initialData.getUsername();
            this.displayName = initialData.getDisplayName();
            this.email = initialData.getEmail();
            this.active = initialData.getActive();
            this.directory = initialData.getDirectory();
            this.avatarUrl = initialData.getAvatarUrl();
        }

        public Builder setId(DirectoryEntityId id) {
            this.id = id;
            return this;
        }

        public Builder setUsername(String username) {
            this.username = username;
            return this;
        }

        public Builder setDisplayName(String displayName) {
            this.displayName = displayName;
            return this;
        }

        public Builder setEmail(String email) {
            this.email = email;
            return this;
        }

        public Builder setActive(Boolean active) {
            this.active = active;
            return this;
        }

        public Builder setDirectory(DirectoryData directory) {
            this.directory = directory;
            return this;
        }

        public Builder setAvatarUrl(String avatarUrl) {
            this.avatarUrl = avatarUrl;
            return this;
        }

        public UserData build() {
            return new UserData(this.id, this.username, this.displayName, this.email, this.active, this.directory, this.avatarUrl);
        }
    }
}

