/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.common.util.Base64Util
 *  com.atlassian.crowd.embedded.api.User
 *  com.atlassian.crowd.model.DirectoryEntity
 *  com.atlassian.crowd.model.group.Group
 *  com.google.common.base.Splitter
 *  org.codehaus.jackson.annotate.JsonCreator
 *  org.codehaus.jackson.annotate.JsonValue
 */
package com.atlassian.crowd.plugin.rest.entity.admin.directory;

import com.atlassian.crowd.common.util.Base64Util;
import com.atlassian.crowd.embedded.api.User;
import com.atlassian.crowd.model.DirectoryEntity;
import com.atlassian.crowd.model.group.Group;
import com.google.common.base.Splitter;
import java.util.Base64;
import java.util.Iterator;
import java.util.Objects;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonValue;

public class DirectoryEntityId {
    private static Splitter SPLITTER = Splitter.on((String)"-").limit(2);
    private final long directoryId;
    private final String entityName;

    public DirectoryEntityId(long directoryId, String entityName) {
        this.directoryId = directoryId;
        this.entityName = entityName;
    }

    public long getDirectoryId() {
        return this.directoryId;
    }

    public String getEntityName() {
        return this.entityName;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        DirectoryEntityId that = (DirectoryEntityId)o;
        return this.directoryId == that.directoryId && Objects.equals(this.entityName, that.entityName);
    }

    public int hashCode() {
        return Objects.hash(this.directoryId, this.entityName);
    }

    @JsonValue
    public String marshal() {
        return DirectoryEntityId.marshal(this.directoryId, this.entityName);
    }

    public String toString() {
        return this.directoryId + "-" + this.entityName;
    }

    public static String marshal(long directoryId, String entityName) {
        String entityNameEncoded = Base64Util.urlSafeEncoderWithoutPadding().encodeToString(entityName.getBytes());
        return directoryId + "-" + entityNameEncoded;
    }

    @JsonCreator
    public static DirectoryEntityId fromString(String identifier) {
        Iterator split = SPLITTER.split((CharSequence)identifier).iterator();
        long directoryId = Long.parseLong((String)split.next());
        String entityNameEncoded = (String)split.next();
        String entityName = new String(Base64.getUrlDecoder().decode(entityNameEncoded.getBytes()));
        return new DirectoryEntityId(directoryId, entityName);
    }

    public static DirectoryEntityId fromGroup(Group group) {
        return DirectoryEntityId.fromDirectoryEntity((DirectoryEntity)group);
    }

    public static DirectoryEntityId fromUser(User user) {
        return new DirectoryEntityId(user.getDirectoryId(), user.getName());
    }

    public static DirectoryEntityId fromDirectoryEntity(DirectoryEntity entity) {
        return new DirectoryEntityId(entity.getDirectoryId(), entity.getName());
    }

    public static String marshal(DirectoryEntity entity) {
        return DirectoryEntityId.marshal(entity.getDirectoryId(), entity.getName());
    }
}

