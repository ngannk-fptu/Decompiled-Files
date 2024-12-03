/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.darkfeature.CrowdDarkFeatureManager
 *  com.atlassian.crowd.embedded.api.ApplicationFactory
 *  com.atlassian.crowd.embedded.api.Directory
 *  com.atlassian.crowd.embedded.impl.IdentifierUtils
 *  com.atlassian.crowd.event.EventStore
 *  com.atlassian.crowd.manager.avatar.AvatarProvider
 *  com.atlassian.crowd.manager.directory.DirectoryManager
 *  com.atlassian.crowd.manager.permission.PermissionManager
 *  com.atlassian.crowd.manager.webhook.WebhookRegistry
 *  com.atlassian.crowd.model.application.Application
 *  com.atlassian.crowd.model.user.User
 *  com.atlassian.event.api.EventPublisher
 *  com.google.common.base.Preconditions
 *  com.google.common.collect.ImmutableList
 */
package com.atlassian.crowd.manager.recovery;

import com.atlassian.crowd.darkfeature.CrowdDarkFeatureManager;
import com.atlassian.crowd.embedded.api.ApplicationFactory;
import com.atlassian.crowd.embedded.api.Directory;
import com.atlassian.crowd.embedded.impl.IdentifierUtils;
import com.atlassian.crowd.event.EventStore;
import com.atlassian.crowd.manager.application.ApplicationServiceGeneric;
import com.atlassian.crowd.manager.application.AuthenticationOrderOptimizer;
import com.atlassian.crowd.manager.application.filtering.AccessFilterFactory;
import com.atlassian.crowd.manager.application.search.SearchStrategyFactory;
import com.atlassian.crowd.manager.avatar.AvatarProvider;
import com.atlassian.crowd.manager.directory.DirectoryManager;
import com.atlassian.crowd.manager.permission.PermissionManager;
import com.atlassian.crowd.manager.recovery.RecoveryModeService;
import com.atlassian.crowd.manager.webhook.WebhookRegistry;
import com.atlassian.crowd.model.application.Application;
import com.atlassian.crowd.model.user.User;
import com.atlassian.event.api.EventPublisher;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import java.util.List;

public class RecoveryModeAwareApplicationService
extends ApplicationServiceGeneric {
    private final RecoveryModeService recoveryModeService;
    private final ApplicationFactory applicationFactory;

    public RecoveryModeAwareApplicationService(DirectoryManager directoryManager, SearchStrategyFactory searchStrategyFactory, PermissionManager permissionManager, EventPublisher eventPublisher, EventStore eventStore, WebhookRegistry webhookRegistry, AvatarProvider avatarProvider, ApplicationFactory applicationFactory, RecoveryModeService recoveryModeService, AuthenticationOrderOptimizer authenticationOrderOptimizer, AccessFilterFactory accessFilterFactory, CrowdDarkFeatureManager crowdDarkFeatureManager) {
        super(directoryManager, searchStrategyFactory, permissionManager, eventPublisher, eventStore, webhookRegistry, avatarProvider, authenticationOrderOptimizer, applicationFactory, accessFilterFactory, crowdDarkFeatureManager);
        this.applicationFactory = (ApplicationFactory)Preconditions.checkNotNull((Object)applicationFactory);
        this.recoveryModeService = (RecoveryModeService)Preconditions.checkNotNull((Object)recoveryModeService, (Object)"recoveryModeService");
    }

    @Override
    protected List<Directory> getActiveDirectories(Application application) {
        List<Directory> directories = super.getActiveDirectories(application);
        if (this.supportsRecoveryLogin(application)) {
            return ImmutableList.builder().add((Object)this.recoveryModeService.getRecoveryDirectory()).addAll(directories).build();
        }
        return directories;
    }

    @Override
    public boolean isUserAuthorised(Application application, String username) {
        return this.isRecoveryUserAuthorisation(application, username) || super.isUserAuthorised(application, username);
    }

    @Override
    public boolean isUserAuthorised(Application application, User user) {
        return this.isRecoveryUserAuthorisation(application, user.getName()) || super.isUserAuthorised(application, user);
    }

    private boolean isRecoveryUserAuthorisation(Application application, String username) {
        return this.supportsRecoveryLogin(application) && IdentifierUtils.equalsInLowerCase((String)username, (String)this.recoveryModeService.getRecoveryUsername());
    }

    private boolean supportsRecoveryLogin(Application application) {
        return this.recoveryModeService.isRecoveryModeOn() && application.getId().equals(this.applicationFactory.getApplication().getId());
    }
}

