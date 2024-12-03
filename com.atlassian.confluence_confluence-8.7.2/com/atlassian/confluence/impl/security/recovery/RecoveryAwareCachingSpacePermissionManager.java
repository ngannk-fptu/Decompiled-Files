/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.embedded.api.CrowdService
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.user.User
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.atlassian.confluence.impl.security.recovery;

import com.atlassian.confluence.impl.cache.tx.TransactionAwareCacheFactory;
import com.atlassian.confluence.impl.security.CachingSpacePermissionManager;
import com.atlassian.confluence.impl.security.access.SpacePermissionAccessMapper;
import com.atlassian.confluence.impl.security.delegate.ScopesRequestCacheDelegate;
import com.atlassian.confluence.impl.security.recovery.RecoveryUtil;
import com.atlassian.confluence.internal.accessmode.AccessModeManager;
import com.atlassian.confluence.security.PermissionCheckExemptions;
import com.atlassian.confluence.security.SpacePermissionDefaultsStoreFactory;
import com.atlassian.confluence.security.access.ConfluenceAccessManager;
import com.atlassian.confluence.security.persistence.dao.SpacePermissionDao;
import com.atlassian.confluence.setup.settings.GlobalSettingsManager;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.user.ConfluenceUserResolver;
import com.atlassian.confluence.user.GroupResolver;
import com.atlassian.crowd.embedded.api.CrowdService;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.user.User;
import org.checkerframework.checker.nullness.qual.Nullable;

public class RecoveryAwareCachingSpacePermissionManager
extends CachingSpacePermissionManager {
    public RecoveryAwareCachingSpacePermissionManager(SpacePermissionDao spacePermissionDao, PermissionCheckExemptions permissionCheckExemptions, SpacePermissionDefaultsStoreFactory spacePermissionDefaultsStoreFactory, TransactionAwareCacheFactory cacheFactory, EventPublisher eventPublisher, ConfluenceAccessManager confluenceAccessManager, SpacePermissionAccessMapper spacePermissionAccessMapper, CrowdService crowdService, ConfluenceUserResolver userResolver, AccessModeManager accessModeManager, ScopesRequestCacheDelegate scopesRequestCacheDelegate, GlobalSettingsManager settingsManager, GroupResolver groupResolver) {
        super(spacePermissionDao, permissionCheckExemptions, spacePermissionDefaultsStoreFactory, cacheFactory, eventPublisher, confluenceAccessManager, spacePermissionAccessMapper, crowdService, userResolver, accessModeManager, scopesRequestCacheDelegate, settingsManager, groupResolver);
    }

    @Override
    public boolean hasPermissionNoExemptions(String permissionType, @Nullable Space space, @Nullable User remoteUser) {
        if (RecoveryUtil.isRecoveryAdmin(remoteUser)) {
            return true;
        }
        return super.hasPermissionNoExemptions(permissionType, space, remoteUser);
    }
}

