/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.user.UserKey
 *  javax.annotation.Nullable
 */
package com.atlassian.confluence.search.v2.query;

import com.atlassian.confluence.search.v2.SearchPrimitive;
import com.atlassian.confluence.search.v2.SearchQuery;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.sal.api.user.UserKey;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.annotation.Nullable;

@SearchPrimitive
public class ContentPermissionsQuery
implements SearchQuery {
    public static final String KEY = "contentPermissions";
    private final UserKey userKey;
    private final List<String> groupNames;

    @Override
    public String getKey() {
        return KEY;
    }

    @Override
    public List getParameters() {
        return Collections.EMPTY_LIST;
    }

    private ContentPermissionsQuery(@Nullable UserKey userKey, @Nullable List<String> groupNames) {
        this.userKey = userKey;
        this.groupNames = groupNames;
    }

    public UserKey getUserKey() {
        return this.userKey;
    }

    public List<String> getGroupNames() {
        return this.groupNames;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private ConfluenceUser user;
        private List<String> groupNames;

        public ContentPermissionsQuery build() {
            return new ContentPermissionsQuery(this.user != null ? this.user.getKey() : null, this.groupNames);
        }

        public Builder user(@Nullable ConfluenceUser user) {
            this.user = user;
            return this;
        }

        public Builder groupNames(List<String> groupNames) {
            this.groupNames = groupNames != null ? new ArrayList<String>(groupNames) : new ArrayList();
            return this;
        }
    }
}

