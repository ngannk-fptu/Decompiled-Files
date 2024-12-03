/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.model.DirectoryEntity
 */
package com.atlassian.crowd.model.user;

import com.atlassian.crowd.model.DirectoryEntity;
import java.util.Objects;

public class MinimalUser
implements DirectoryEntity {
    private final String name;
    private final long id;
    private final long directoryId;

    public MinimalUser(long id, long directoryId, String name) {
        this.name = name;
        this.id = id;
        this.directoryId = directoryId;
    }

    public long getDirectoryId() {
        return this.directoryId;
    }

    public String getName() {
        return this.name;
    }

    public long getId() {
        return this.id;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || this.getClass() != obj.getClass()) {
            return false;
        }
        MinimalUser that = (MinimalUser)obj;
        return this.id == that.id && this.directoryId == that.directoryId && Objects.equals(this.name, that.name);
    }

    public int hashCode() {
        return Objects.hash(this.id, this.directoryId, this.name);
    }
}

