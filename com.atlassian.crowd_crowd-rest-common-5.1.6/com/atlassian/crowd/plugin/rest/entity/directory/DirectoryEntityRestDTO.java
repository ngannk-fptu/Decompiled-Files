/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.embedded.api.User
 *  com.atlassian.crowd.model.group.Group
 *  com.google.common.base.MoreObjects
 *  org.codehaus.jackson.annotate.JsonCreator
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.crowd.plugin.rest.entity.directory;

import com.atlassian.crowd.embedded.api.User;
import com.atlassian.crowd.model.group.Group;
import com.atlassian.crowd.plugin.rest.entity.admin.directory.DirectoryEntityId;
import com.atlassian.crowd.plugin.rest.entity.admin.user.UserData;
import com.atlassian.crowd.plugin.rest.entity.directory.DirectoryEntityType;
import com.google.common.base.MoreObjects;
import java.util.Objects;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

public class DirectoryEntityRestDTO {
    @JsonProperty(value="id")
    private final DirectoryEntityId id;
    @JsonProperty(value="type")
    private final DirectoryEntityType type;

    public static DirectoryEntityRestDTO fromGroup(Group group) {
        return DirectoryEntityRestDTO.fromGroupName(group.getDirectoryId(), group.getName());
    }

    public static DirectoryEntityRestDTO fromUser(User user) {
        return DirectoryEntityRestDTO.fromUserName(user.getDirectoryId(), user.getName());
    }

    public static DirectoryEntityRestDTO fromUserData(UserData userData) {
        return new DirectoryEntityRestDTO(userData.getId(), DirectoryEntityType.USER);
    }

    public static DirectoryEntityRestDTO fromUserName(long directoryId, String userName) {
        return new DirectoryEntityRestDTO(new DirectoryEntityId(directoryId, userName), DirectoryEntityType.USER);
    }

    public static DirectoryEntityRestDTO fromGroupName(long directoryId, String groupName) {
        return new DirectoryEntityRestDTO(new DirectoryEntityId(directoryId, groupName), DirectoryEntityType.GROUP);
    }

    @JsonCreator
    public DirectoryEntityRestDTO(@JsonProperty(value="id") DirectoryEntityId id, @JsonProperty(value="type") DirectoryEntityType type) {
        this.id = id;
        this.type = type;
    }

    public DirectoryEntityId getId() {
        return this.id;
    }

    public DirectoryEntityType getType() {
        return this.type;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        DirectoryEntityRestDTO that = (DirectoryEntityRestDTO)o;
        return Objects.equals(this.getId(), that.getId()) && Objects.equals((Object)this.getType(), (Object)that.getType());
    }

    public int hashCode() {
        return Objects.hash(new Object[]{this.getId(), this.getType()});
    }

    public String toString() {
        return MoreObjects.toStringHelper((Object)this).add("id", (Object)this.getId()).add("type", (Object)this.getType()).toString();
    }
}

