/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugins.whitelist.AccessManager
 *  com.atlassian.sal.api.user.UserKey
 */
package com.atlassian.confluence.security.access;

import com.atlassian.confluence.security.access.ConfluenceAccessManager;
import com.atlassian.confluence.user.ConfluenceUserResolver;
import com.atlassian.plugins.whitelist.AccessManager;
import com.atlassian.sal.api.user.UserKey;

public class WhitelistConfluenceAccessManager
implements AccessManager {
    private final ConfluenceAccessManager confluenceAccessManager;
    private final ConfluenceUserResolver confluenceUserResolver;

    public WhitelistConfluenceAccessManager(ConfluenceUserResolver confluenceUserResolver, ConfluenceAccessManager confluenceAccessManager) {
        this.confluenceAccessManager = confluenceAccessManager;
        this.confluenceUserResolver = confluenceUserResolver;
    }

    public boolean canUserAccessProduct(UserKey userKey) {
        return this.confluenceAccessManager.getUserAccessStatus(this.confluenceUserResolver.getUserByKey(userKey)).canUseConfluence();
    }
}

