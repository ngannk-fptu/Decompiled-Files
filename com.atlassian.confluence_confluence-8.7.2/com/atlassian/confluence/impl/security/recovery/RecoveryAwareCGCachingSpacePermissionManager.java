/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.cache.CacheFactory
 *  com.atlassian.crowd.embedded.api.CrowdService
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.user.User
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.atlassian.confluence.impl.security.recovery;

import com.atlassian.cache.CacheFactory;
import com.atlassian.confluence.impl.security.CoarseGrainedCachingSpacePermissionManager;
import com.atlassian.confluence.impl.security.access.SpacePermissionAccessMapper;
import com.atlassian.confluence.impl.security.delegate.ScopesRequestCacheDelegate;
import com.atlassian.confluence.impl.security.recovery.RecoveryUtil;
import com.atlassian.confluence.internal.accessmode.AccessModeManager;
import com.atlassian.confluence.internal.security.SpacePermissionManagerInternal;
import com.atlassian.confluence.security.PermissionCheckExemptions;
import com.atlassian.confluence.security.access.ConfluenceAccessManager;
import com.atlassian.confluence.security.persistence.dao.SpacePermissionDao;
import com.atlassian.confluence.setup.settings.GlobalSettingsManager;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.crowd.embedded.api.CrowdService;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.user.User;
import org.checkerframework.checker.nullness.qual.Nullable;

public class RecoveryAwareCGCachingSpacePermissionManager
extends CoarseGrainedCachingSpacePermissionManager {
    public RecoveryAwareCGCachingSpacePermissionManager(PermissionCheckExemptions permissionCheckExemptions, CacheFactory cacheFactory, SpacePermissionManagerInternal delegate, SpacePermissionDao spacePermissionDao, EventPublisher eventPublisher, ConfluenceAccessManager confluenceAccessManager, SpacePermissionAccessMapper spacePermissionAccessMapper, CrowdService crowdService, AccessModeManager accessModeManager, ScopesRequestCacheDelegate scopesRequestCacheDelegate, GlobalSettingsManager settingsManager) {
        super(permissionCheckExemptions, cacheFactory, delegate, spacePermissionDao, eventPublisher, confluenceAccessManager, spacePermissionAccessMapper, crowdService, accessModeManager, scopesRequestCacheDelegate, settingsManager);
    }

    @Override
    public boolean hasPermissionNoExemptions(String permissionType, @Nullable Space space, @Nullable User remoteUser) {
        if (RecoveryUtil.isRecoveryAdmin(remoteUser)) {
            return true;
        }
        return super.hasPermissionNoExemptions(permissionType, space, remoteUser);
    }
}

