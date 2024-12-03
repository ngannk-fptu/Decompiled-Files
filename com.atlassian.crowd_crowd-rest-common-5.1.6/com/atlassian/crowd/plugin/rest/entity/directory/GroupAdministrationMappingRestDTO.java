/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.embedded.api.Directory
 *  com.atlassian.crowd.embedded.api.User
 *  com.atlassian.crowd.model.group.Group
 *  com.google.common.base.MoreObjects
 *  org.codehaus.jackson.annotate.JsonCreator
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.crowd.plugin.rest.entity.directory;

import com.atlassian.crowd.embedded.api.Directory;
import com.atlassian.crowd.embedded.api.User;
import com.atlassian.crowd.model.group.Group;
import com.atlassian.crowd.plugin.rest.entity.admin.directory.DirectoryData;
import com.atlassian.crowd.plugin.rest.entity.admin.user.UserData;
import com.atlassian.crowd.plugin.rest.entity.directory.DirectoryEntityRestDTO;
import com.google.common.base.MoreObjects;
import java.util.Objects;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

public class GroupAdministrationMappingRestDTO {
    @JsonProperty(value="entity")
    private final DirectoryEntityRestDTO entity;
    @JsonProperty(value="name")
    private final String name;
    @JsonProperty(value="displayName")
    private final String displayName;
    @JsonProperty(value="directory")
    private final DirectoryData directory;
    @JsonProperty(value="email")
    private final String email;
    @JsonProperty(value="active")
    private final Boolean active;

    public static GroupAdministrationMappingRestDTO fromGroup(Group group, Directory directory) {
        return GroupAdministrationMappingRestDTO.fromGroup(group, DirectoryData.fromDirectory(directory));
    }

    public static GroupAdministrationMappingRestDTO fromGroup(Group group, DirectoryData directoryData) {
        return GroupAdministrationMappingRestDTO.builder().setEntity(DirectoryEntityRestDTO.fromGroup(group)).setName(group.getName()).setActive(group.isActive()).setDirectory(directoryData).build();
    }

    public static GroupAdministrationMappingRestDTO fromUser(User user, Directory directory) {
        return GroupAdministrationMappingRestDTO.fromUser(user, DirectoryData.fromDirectory(directory));
    }

    public static GroupAdministrationMappingRestDTO fromUser(User user, DirectoryData directoryData) {
        return GroupAdministrationMappingRestDTO.builder().setEntity(DirectoryEntityRestDTO.fromUser(user)).setName(user.getName()).setDisplayName(user.getDisplayName()).setEmail(user.getEmailAddress()).setActive(user.isActive()).setDirectory(directoryData).build();
    }

    public static GroupAdministrationMappingRestDTO fromUserData(UserData userData) {
        return GroupAdministrationMappingRestDTO.builder().setEntity(DirectoryEntityRestDTO.fromUserData(userData)).setName(userData.getUsername()).setDisplayName(userData.getDisplayName()).setEmail(userData.getEmail()).setActive(userData.getActive()).setDirectory(userData.getDirectory()).build();
    }

    @JsonCreator
    public GroupAdministrationMappingRestDTO(@JsonProperty(value="entity") DirectoryEntityRestDTO entity, @JsonProperty(value="name") String name, @JsonProperty(value="displayName") String displayName, @JsonProperty(value="directory") DirectoryData directory, @JsonProperty(value="email") String email, @JsonProperty(value="active") Boolean active) {
        this.entity = entity;
        this.name = name;
        this.displayName = displayName;
        this.directory = directory;
        this.email = email;
        this.active = active;
    }

    public DirectoryEntityRestDTO getEntity() {
        return this.entity;
    }

    public String getName() {
        return this.name;
    }

    public String getDisplayName() {
        return this.displayName;
    }

    public DirectoryData getDirectory() {
        return this.directory;
    }

    public String getEmail() {
        return this.email;
    }

    public Boolean getActive() {
        return this.active;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static Builder builder(GroupAdministrationMappingRestDTO data) {
        return new Builder(data);
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        GroupAdministrationMappingRestDTO that = (GroupAdministrationMappingRestDTO)o;
        return Objects.equals(this.getEntity(), that.getEntity()) && Objects.equals(this.getName(), that.getName()) && Objects.equals(this.getDisplayName(), that.getDisplayName()) && Objects.equals(this.getDirectory(), that.getDirectory()) && Objects.equals(this.getEmail(), that.getEmail()) && Objects.equals(this.getActive(), that.getActive());
    }

    public int hashCode() {
        return Objects.hash(this.getEntity(), this.getName(), this.getDisplayName(), this.getDirectory(), this.getEmail(), this.getActive());
    }

    public String toString() {
        return MoreObjects.toStringHelper((Object)this).add("entity", (Object)this.getEntity()).add("name", (Object)this.getName()).add("displayName", (Object)this.getDisplayName()).add("directory", (Object)this.getDirectory()).add("email", (Object)this.getEmail()).add("active", (Object)this.getActive()).toString();
    }

    public static final class Builder {
        private DirectoryEntityRestDTO entity;
        private String name;
        private String displayName;
        private DirectoryData directory;
        private String email;
        private Boolean active;

        private Builder() {
        }

        private Builder(GroupAdministrationMappingRestDTO initialData) {
            this.entity = initialData.getEntity();
            this.name = initialData.getName();
            this.displayName = initialData.getDisplayName();
            this.directory = initialData.getDirectory();
            this.email = initialData.getEmail();
            this.active = initialData.getActive();
        }

        public Builder setEntity(DirectoryEntityRestDTO entity) {
            this.entity = entity;
            return this;
        }

        public Builder setName(String name) {
            this.name = name;
            return this;
        }

        public Builder setDisplayName(String displayName) {
            this.displayName = displayName;
            return this;
        }

        public Builder setDirectory(DirectoryData directory) {
            this.directory = directory;
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

        public GroupAdministrationMappingRestDTO build() {
            return new GroupAdministrationMappingRestDTO(this.entity, this.name, this.displayName, this.directory, this.email, this.active);
        }
    }
}

