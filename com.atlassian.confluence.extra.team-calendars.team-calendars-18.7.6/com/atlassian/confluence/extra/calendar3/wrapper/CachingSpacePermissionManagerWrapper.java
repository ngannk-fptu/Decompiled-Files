/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.security.SpacePermissionManager
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.confluence.util.RequestCacheThreadLocal
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.user.User
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.extra.calendar3.wrapper;

import com.atlassian.confluence.extra.calendar3.wrapper.SpacePermissionsManagerWrapper;
import com.atlassian.confluence.security.SpacePermissionManager;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.util.RequestCacheThreadLocal;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.user.User;
import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component(value="cachingSpacePermissionManagerWrapper")
public class CachingSpacePermissionManagerWrapper
implements SpacePermissionsManagerWrapper {
    private final SpacePermissionManager spacePermissionManager;
    private static final String USE_CONFLUENCE_PERMISSION_CACHE = "use.confluence.permission.cache";

    @Autowired
    public CachingSpacePermissionManagerWrapper(@ComponentImport SpacePermissionManager spacePermissionManager) {
        this.spacePermissionManager = spacePermissionManager;
    }

    @Override
    public boolean getUseConfluencePermission(ConfluenceUser user) {
        Map requestCache = RequestCacheThreadLocal.getRequestCache();
        Map confluencePermissionThreadLocalCache = (Map)requestCache.computeIfAbsent(USE_CONFLUENCE_PERMISSION_CACHE, key -> new HashMap());
        return confluencePermissionThreadLocalCache.computeIfAbsent(user, confluenceUser -> this.spacePermissionManager.hasPermission("USECONFLUENCE", null, (User)confluenceUser));
    }
}

