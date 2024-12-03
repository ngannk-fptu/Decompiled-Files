/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.user.UserKey
 *  javax.annotation.Nullable
 */
package com.atlassian.confluence.search.v2;

import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.security.ContentPermissionSet;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.sal.api.user.UserKey;
import java.util.Collection;
import javax.annotation.Nullable;

public interface ContentPermissionCalculator {
    public Collection<ContentPermissionSet> calculate(@Nullable ContentEntityObject var1);

    @Deprecated
    public String getEncodedContentPermissionSets(Collection<ContentPermissionSet> var1);

    public Collection<String> getEncodedPermissionsCollection(ContentPermissionSet var1);

    public String getEncodedUserKey(ConfluenceUser var1);

    public String getEncodedUserKey(UserKey var1);

    public String getEncodedGroupName(String var1);
}

