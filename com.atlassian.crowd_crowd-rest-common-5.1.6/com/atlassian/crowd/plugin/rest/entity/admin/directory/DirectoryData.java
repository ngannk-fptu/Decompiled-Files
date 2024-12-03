/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.embedded.api.Directory
 *  com.atlassian.crowd.embedded.api.DirectoryType
 *  com.google.common.base.MoreObjects
 *  org.codehaus.jackson.annotate.JsonCreator
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.crowd.plugin.rest.entity.admin.directory;

import com.atlassian.crowd.embedded.api.Directory;
import com.atlassian.crowd.embedded.api.DirectoryType;
import com.google.common.base.MoreObjects;
import java.util.Objects;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

public class DirectoryData {
    @JsonProperty(value="id")
    private final long id;
    @JsonProperty(value="displayName")
    private final String displayName;
    @JsonProperty(value="directoryType")
    private final DirectoryType directoryType;

    public static DirectoryData fromDirectory(Directory directory) {
        return new DirectoryData(directory.getId(), directory.getName(), directory.getType());
    }

    @JsonCreator
    public DirectoryData(@JsonProperty(value="id") long id, @JsonProperty(value="displayName") String displayName, @JsonProperty(value="directoryType") DirectoryType directoryType) {
        this.id = id;
        this.displayName = displayName;
        this.directoryType = directoryType;
    }

    public long getId() {
        return this.id;
    }

    public String getDisplayName() {
        return this.displayName;
    }

    public DirectoryType getDirectoryType() {
        return this.directoryType;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        DirectoryData that = (DirectoryData)o;
        return Objects.equals(this.getId(), that.getId()) && Objects.equals(this.getDisplayName(), that.getDisplayName()) && Objects.equals(this.getDirectoryType(), that.getDirectoryType());
    }

    public int hashCode() {
        return Objects.hash(this.getId(), this.getDisplayName(), this.getDirectoryType());
    }

    public String toString() {
        return MoreObjects.toStringHelper((Object)this).add("id", this.getId()).add("displayName", (Object)this.getDisplayName()).add("directoryType", (Object)this.getDirectoryType()).toString();
    }
}

