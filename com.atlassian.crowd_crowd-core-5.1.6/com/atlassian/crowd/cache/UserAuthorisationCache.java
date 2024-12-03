/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.model.user.User
 *  javax.annotation.Nullable
 */
package com.atlassian.crowd.cache;

import com.atlassian.crowd.model.user.User;
import javax.annotation.Nullable;

public interface UserAuthorisationCache {
    public void setPermitted(User var1, String var2, boolean var3);

    @Nullable
    public Boolean isPermitted(User var1, String var2);

    public void clear();
}

