/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.user.User
 */
package com.atlassian.confluence.plugins.dailysummary.components;

import com.atlassian.user.User;

public interface SingleUseUnsubscribeTokenManager {
    public String getUserToken(User var1);

    public boolean isValidToken(User var1, String var2);
}

