/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.user.UserKey
 */
package com.atlassian.upm.core.token;

import com.atlassian.sal.api.user.UserKey;

public interface TokenManager {
    public String getTokenForUser(UserKey var1);

    public boolean attemptToMatchAndInvalidateToken(UserKey var1, String var2);
}

