/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.user.User
 */
package com.atlassian.confluence.spaces;

import com.atlassian.confluence.spaces.Space;
import com.atlassian.user.User;

public interface SpaceLogoManager {
    @Deprecated
    public String getLogoDownloadPath(Space var1, User var2);

    public String getLogoUriReference(Space var1, User var2);
}

