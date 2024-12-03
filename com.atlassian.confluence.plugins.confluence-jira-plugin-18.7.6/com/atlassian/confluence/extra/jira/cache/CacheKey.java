/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.google.common.base.MoreObjects
 */
package com.atlassian.confluence.extra.jira.cache;

import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.google.common.base.MoreObjects;
import java.io.Serializable;
import java.util.Set;

public final class CacheKey
implements Serializable {
    private final String partialUrl;
    private final Set<String> columns;
    private final boolean showCount;
    private final String userName;
    private final boolean forFlexigrid;
    private final String appId;
    private final boolean mapped;
    private final String version;

    public CacheKey(String partialUrl, String appId, Set<String> columns, boolean showCount, boolean forceAnonymous, boolean forFlexigrid, boolean mapped, String version) {
        this.appId = appId;
        this.partialUrl = partialUrl;
        this.columns = columns;
        this.showCount = showCount;
        this.userName = !forceAnonymous ? AuthenticatedUserThreadLocal.getUsername() : null;
        this.forFlexigrid = forFlexigrid;
        this.mapped = mapped;
        this.version = version;
    }

    public Set<String> getColumns() {
        return this.columns;
    }

    private boolean isMapped() {
        return this.mapped;
    }

    public String getVersion() {
        return this.version;
    }

    public String toString() {
        return "partialUrl:" + this.partialUrl + " columns:" + this.columns.toString() + " showCount:" + this.showCount + " userName=" + this.userName + " isMapped=" + this.isMapped();
    }

    public int hashCode() {
        int prime = 31;
        int result = 1;
        result = 31 * result + (this.appId == null ? 0 : this.appId.hashCode());
        result = 31 * result + (this.columns == null ? 0 : this.columns.hashCode());
        result = 31 * result + (this.forFlexigrid ? 1231 : 1237);
        result = 31 * result + (this.mapped ? 1231 : 1237);
        result = 31 * result + (this.partialUrl == null ? 0 : this.partialUrl.hashCode());
        result = 31 * result + (this.showCount ? 1231 : 1237);
        result = 31 * result + (this.userName == null ? 0 : this.userName.hashCode());
        result = 31 * result + (this.version == null ? 0 : this.version.hashCode());
        return result;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        CacheKey other = (CacheKey)obj;
        if (this.appId == null ? other.appId != null : !this.appId.equals(other.appId)) {
            return false;
        }
        if (this.columns == null ? other.columns != null : !this.columns.equals(other.columns)) {
            return false;
        }
        if (this.forFlexigrid != other.forFlexigrid) {
            return false;
        }
        if (this.mapped != other.mapped) {
            return false;
        }
        if (this.partialUrl == null ? other.partialUrl != null : !this.partialUrl.equals(other.partialUrl)) {
            return false;
        }
        if (this.showCount != other.showCount) {
            return false;
        }
        if (this.userName == null ? other.userName != null : !this.userName.equals(other.userName)) {
            return false;
        }
        if (this.version == null) {
            return other.version == null;
        }
        return this.version.equals(other.version);
    }

    public String toKey() {
        return MoreObjects.toStringHelper((Object)this).addValue((Object)this.partialUrl).addValue(this.columns).addValue(this.showCount).addValue((Object)this.userName).addValue(this.forFlexigrid).addValue((Object)this.appId).addValue(this.mapped).addValue((Object)this.version).toString();
    }
}

