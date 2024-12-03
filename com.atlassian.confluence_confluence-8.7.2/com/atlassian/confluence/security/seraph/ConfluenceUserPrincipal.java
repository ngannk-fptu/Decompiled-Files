/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.user.UserKey
 *  com.google.common.base.MoreObjects
 *  com.google.common.base.Preconditions
 */
package com.atlassian.confluence.security.seraph;

import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.persistence.dao.compatibility.FindUserHelper;
import com.atlassian.sal.api.user.UserKey;
import com.google.common.base.MoreObjects;
import com.google.common.base.Preconditions;
import java.io.Serializable;
import java.security.Principal;

public class ConfluenceUserPrincipal
implements Principal,
Serializable {
    private static final long serialVersionUID = -2455368024208281893L;
    private final UserKey userKey;
    private final String username;

    public ConfluenceUserPrincipal(ConfluenceUser user) {
        this.userKey = (UserKey)Preconditions.checkNotNull((Object)user.getKey());
        this.username = user.getName();
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper((Object)this).add("userKey", (Object)this.userKey).add("username", (Object)this.username).toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        ConfluenceUserPrincipal that = (ConfluenceUserPrincipal)o;
        return this.userKey.equals((Object)that.userKey);
    }

    @Override
    public int hashCode() {
        return this.userKey.hashCode();
    }

    @Override
    public String getName() {
        return this.username;
    }

    public UserKey getUserKey() {
        return this.userKey;
    }

    public static ConfluenceUserPrincipal of(Principal principal) {
        if (principal == null) {
            return null;
        }
        if (principal instanceof ConfluenceUserPrincipal) {
            return (ConfluenceUserPrincipal)principal;
        }
        if (principal instanceof ConfluenceUser) {
            return new ConfluenceUserPrincipal((ConfluenceUser)((Object)principal));
        }
        ConfluenceUser user = FindUserHelper.getUserByUsername(principal.getName());
        return user != null ? new ConfluenceUserPrincipal(user) : null;
    }
}

