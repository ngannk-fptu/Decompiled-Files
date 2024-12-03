/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.plugins.synchrony.config.SynchronyConfigurationManager
 *  com.atlassian.confluence.security.PermissionManager
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.json.marshal.Jsonable
 *  com.atlassian.json.marshal.wrapped.JsonableBoolean
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.user.User
 *  com.atlassian.webresource.api.data.WebResourceDataProvider
 */
package com.atlassian.confluence.plugins.synchrony.status;

import com.atlassian.confluence.plugins.synchrony.config.SynchronyConfigurationManager;
import com.atlassian.confluence.plugins.synchrony.status.SynchronyStatusCache;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.json.marshal.Jsonable;
import com.atlassian.json.marshal.wrapped.JsonableBoolean;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.user.User;
import com.atlassian.webresource.api.data.WebResourceDataProvider;
import java.util.Objects;

public class SynchronyBannerDataProvider
implements WebResourceDataProvider {
    private final SynchronyConfigurationManager synchronyConfigurationManager;
    private final SynchronyStatusCache synchronyStatusCache;
    private final PermissionManager permissionManager;

    public SynchronyBannerDataProvider(@ComponentImport SynchronyConfigurationManager synchronyConfigurationManager, @ComponentImport PermissionManager permissionManager, SynchronyStatusCache synchronyStatusCache) {
        this.synchronyConfigurationManager = Objects.requireNonNull(synchronyConfigurationManager);
        this.synchronyStatusCache = Objects.requireNonNull(synchronyStatusCache);
        this.permissionManager = Objects.requireNonNull(permissionManager);
    }

    public Jsonable get() {
        return new JsonableBoolean(Boolean.valueOf(this.isSysAdmin() && this.isCollabEditingOn() && !this.synchronyStatusCache.isSynchronyRunning()));
    }

    private boolean isCollabEditingOn() {
        return this.synchronyConfigurationManager.isSharedDraftsEnabled();
    }

    private boolean isSysAdmin() {
        return this.permissionManager.isSystemAdministrator((User)AuthenticatedUserThreadLocal.get());
    }
}

