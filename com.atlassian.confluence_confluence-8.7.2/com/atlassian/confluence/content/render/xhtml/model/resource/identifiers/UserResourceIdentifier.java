/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.user.UserKey
 *  com.google.common.base.Preconditions
 */
package com.atlassian.confluence.content.render.xhtml.model.resource.identifiers;

import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.ResourceIdentifier;
import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.UnresolvedUsernameResourceIdentifier;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.persistence.dao.compatibility.FindUserHelper;
import com.atlassian.sal.api.user.UserKey;
import com.google.common.base.Preconditions;

public class UserResourceIdentifier
implements ResourceIdentifier {
    private final UserKey userKey;
    private final String userName;

    @Deprecated
    public UserResourceIdentifier(String username) {
        this(UserResourceIdentifier.lookupUserKey(username), (String)Preconditions.checkNotNull((Object)username));
    }

    private static UserKey lookupUserKey(String userName) {
        ConfluenceUser user = FindUserHelper.getUserByUsername(userName);
        if (user != null) {
            return user.getKey();
        }
        return null;
    }

    private UserResourceIdentifier(UserKey userKey, String userName) {
        Preconditions.checkArgument((userKey != null || userName != null ? 1 : 0) != 0, (Object)"One of userKey or userName must be non-null");
        this.userKey = userKey;
        this.userName = userName;
    }

    @Deprecated
    public String getUsername() {
        if (this.userName != null) {
            return this.userName;
        }
        ConfluenceUser user = FindUserHelper.getUserByUserKey(this.userKey);
        return user != null ? user.getName() : "";
    }

    public boolean isCreatedFromUsernameSource() {
        return this.userName != null;
    }

    public boolean hasUserKey() {
        return this.userKey != null;
    }

    public UserKey getUserKey() {
        return this.userKey;
    }

    public UnresolvedUsernameResourceIdentifier getUnresolvedUsernameResourceIdentifier() {
        Preconditions.checkState((this.userKey == null ? 1 : 0) != 0, (Object)"Not an unresolved user");
        return new UnresolvedUsernameResourceIdentifier(this.userName);
    }

    public String toString() {
        return "UserResourceIdentifier{userKey=" + this.userKey + ", userName='" + this.userName + "'}";
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof UserResourceIdentifier)) {
            return false;
        }
        UserResourceIdentifier that = (UserResourceIdentifier)o;
        if (this.userKey != null && that.userKey != null) {
            return this.userKey.equals((Object)that.userKey);
        }
        if (this.userKey == null && that.userKey != null || this.userKey != null && that.userKey == null) {
            return false;
        }
        return this.userName != null ? this.userName.equals(that.userName) : that.userName == null;
    }

    public int hashCode() {
        int result = this.userKey != null ? this.userKey.hashCode() : 0;
        result = 31 * result + (this.userName != null ? this.userName.hashCode() : 0);
        return result;
    }

    public static UserResourceIdentifier create(UserKey userKey) {
        return new UserResourceIdentifier((UserKey)Preconditions.checkNotNull((Object)userKey), null);
    }

    public static UserResourceIdentifier createFromUsernameSource(UserKey userKey, String username) {
        return new UserResourceIdentifier((UserKey)Preconditions.checkNotNull((Object)userKey), (String)Preconditions.checkNotNull((Object)username));
    }

    public static UserResourceIdentifier createForNonExistentUser(String userName) {
        return new UserResourceIdentifier(null, userName);
    }
}

