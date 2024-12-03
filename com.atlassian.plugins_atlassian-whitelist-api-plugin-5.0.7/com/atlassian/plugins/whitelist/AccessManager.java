/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.PublicSpi
 *  com.atlassian.sal.api.user.UserKey
 */
package com.atlassian.plugins.whitelist;

import com.atlassian.annotations.PublicSpi;
import com.atlassian.sal.api.user.UserKey;

@PublicSpi
public interface AccessManager {
    public boolean canUserAccessProduct(UserKey var1);
}

