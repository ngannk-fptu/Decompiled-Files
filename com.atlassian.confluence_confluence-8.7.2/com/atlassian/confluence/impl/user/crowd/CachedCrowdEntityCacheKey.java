/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.model.group.Group
 *  com.atlassian.crowd.model.user.User
 *  com.google.errorprone.annotations.Immutable
 */
package com.atlassian.confluence.impl.user.crowd;

import com.atlassian.crowd.model.group.Group;
import com.atlassian.crowd.model.user.User;
import com.google.errorprone.annotations.Immutable;
import java.io.Serializable;
import java.util.Locale;

@Immutable
public class CachedCrowdEntityCacheKey
implements Serializable {
    private final long directoryId;
    private final String name;

    public CachedCrowdEntityCacheKey(long directoryId, String name) {
        this.directoryId = directoryId;
        this.name = name.toLowerCase(Locale.ENGLISH);
    }

    public CachedCrowdEntityCacheKey(User user) {
        this.directoryId = user.getDirectoryId();
        this.name = user.getName().toLowerCase(Locale.ENGLISH);
    }

    public CachedCrowdEntityCacheKey(Group group) {
        this.directoryId = group.getDirectoryId();
        this.name = group.getName().toLowerCase(Locale.ENGLISH);
    }

    public long getDirectoryId() {
        return this.directoryId;
    }

    public String getName() {
        return this.name;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        CachedCrowdEntityCacheKey that = (CachedCrowdEntityCacheKey)o;
        return this.directoryId == that.directoryId && this.name.equals(that.name);
    }

    public int hashCode() {
        int result = (int)(this.directoryId ^ this.directoryId >>> 32);
        result = 31 * result + this.name.hashCode();
        return result;
    }

    public String toString() {
        return this.asStringKey();
    }

    String asStringKey() {
        return "CachedCrowdEntityCacheKey{directoryId=" + this.directoryId + ", name='" + this.name + "'}";
    }
}

