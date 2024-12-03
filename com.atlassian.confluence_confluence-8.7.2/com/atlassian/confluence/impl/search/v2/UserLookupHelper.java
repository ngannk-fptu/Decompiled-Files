/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.Internal
 *  com.atlassian.sal.api.user.UserKey
 *  com.google.common.base.Function
 *  org.apache.commons.lang3.StringUtils
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.atlassian.confluence.impl.search.v2;

import com.atlassian.annotations.Internal;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.persistence.dao.compatibility.FindUserHelper;
import com.atlassian.sal.api.user.UserKey;
import com.google.common.base.Function;
import org.apache.commons.lang3.StringUtils;
import org.checkerframework.checker.nullness.qual.Nullable;

@Internal
public class UserLookupHelper
implements Function<String, ConfluenceUser> {
    public static final UserLookupHelper INSTANCE = new UserLookupHelper();

    public ConfluenceUser apply(@Nullable String userKeyOrName) {
        if (StringUtils.isBlank((CharSequence)userKeyOrName)) {
            return null;
        }
        ConfluenceUser byUsername = FindUserHelper.getUserByUsername(userKeyOrName);
        if (byUsername != null) {
            return byUsername;
        }
        return FindUserHelper.getUserByUserKey(new UserKey(userKeyOrName));
    }
}

