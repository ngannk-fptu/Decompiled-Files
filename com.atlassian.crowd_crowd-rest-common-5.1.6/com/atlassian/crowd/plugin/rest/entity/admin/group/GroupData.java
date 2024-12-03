/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.embedded.api.Directory
 *  com.atlassian.crowd.model.group.Group
 *  com.google.common.base.MoreObjects
 *  org.codehaus.jackson.annotate.JsonCreator
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.crowd.plugin.rest.entity.admin.group;

import com.atlassian.crowd.embedded.api.Directory;
import com.atlassian.crowd.model.group.Group;
import com.atlassian.crowd.plugin.rest.entity.admin.directory.DirectoryData;
import com.atlassian.crowd.plugin.rest.entity.admin.directory.DirectoryEntityId;
import com.google.common.base.MoreObjects;
import java.util.Objects;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

public class GroupData {
    @JsonProperty(value="id")
    private final DirectoryEntityId id;
    @JsonProperty(value="displayName")
    private final String displayName;
    @JsonProperty(value="description")
    private final String description;
    @JsonProperty(value="directory")
    private final DirectoryData directory;

    public static GroupData fromGroup(Group group, Directory directoryById) {
        return GroupData.builder().setId(DirectoryEntityId.fromGroup(group)).setDisplayName(group.getName()).setDescription(group.getDescription()).setDirectory(DirectoryData.fromDirectory(directoryById)).build();
    }

    @JsonCreator
    public GroupData(@JsonProperty(value="id") DirectoryEntityId id, @JsonProperty(value="displayName") String displayName, @JsonProperty(value="description") String description, @JsonProperty(value="directory") DirectoryData directory) {
        this.id = id;
        this.displayName = displayName;
        this.description = description;
        this.directory = directory;
    }

    public DirectoryEntityId getId() {
        return this.id;
    }

    public String getDisplayName() {
        return this.displayName;
    }

    public String getDescription() {
        return this.description;
    }

    public DirectoryData getDirectory() {
        return this.directory;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static Builder builder(GroupData data) {
        return new Builder(data);
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        GroupData that = (GroupData)o;
        return Objects.equals(this.getId(), that.getId()) && Objects.equals(this.getDisplayName(), that.getDisplayName()) && Objects.equals(this.getDescription(), that.getDescription()) && Objects.equals(this.getDirectory(), that.getDirectory());
    }

    public int hashCode() {
        return Objects.hash(this.getId(), this.getDisplayName(), this.getDescription(), this.getDirectory());
    }

    public String toString() {
        return MoreObjects.toStringHelper((Object)this).add("id", (Object)this.getId()).add("displayName", (Object)this.getDisplayName()).add("description", (Object)this.getDescription()).add("directory", (Object)this.getDirectory()).toString();
    }

    public static final class Builder {
        private DirectoryEntityId id;
        private String displayName;
        private String description;
        private DirectoryData directory;

        private Builder() {
        }

        private Builder(GroupData initialData) {
            this.id = initialData.getId();
            this.displayName = initialData.getDisplayName();
            this.description = initialData.getDescription();
            this.directory = initialData.getDirectory();
        }

        public Builder setId(DirectoryEntityId id) {
            this.id = id;
            return this;
        }

        public Builder setDisplayName(String displayName) {
            this.displayName = displayName;
            return this;
        }

        public Builder setDescription(String description) {
            this.description = description;
            return this;
        }

        public Builder setDirectory(DirectoryData directory) {
            this.directory = directory;
            return this;
        }

        public GroupData build() {
            return new GroupData(this.id, this.displayName, this.description, this.directory);
        }
    }
}

