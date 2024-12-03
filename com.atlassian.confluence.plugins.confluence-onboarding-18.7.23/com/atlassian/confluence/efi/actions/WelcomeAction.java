/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.core.ConfluenceActionSupport
 *  com.atlassian.confluence.security.PermissionManager
 *  com.atlassian.confluence.spaces.Space
 *  com.atlassian.confluence.spaces.SpaceManager
 *  com.atlassian.confluence.spaces.SpacesQuery
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.user.User
 *  com.atlassian.xwork.HttpMethod
 *  com.atlassian.xwork.PermittedMethods
 */
package com.atlassian.confluence.efi.actions;

import com.atlassian.confluence.core.ConfluenceActionSupport;
import com.atlassian.confluence.efi.OnboardingManager;
import com.atlassian.confluence.efi.store.GlobalStorageService;
import com.atlassian.confluence.efi.store.UserStorageService;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.spaces.SpaceManager;
import com.atlassian.confluence.spaces.SpacesQuery;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.user.User;
import com.atlassian.xwork.HttpMethod;
import com.atlassian.xwork.PermittedMethods;

public class WelcomeAction
extends ConfluenceActionSupport {
    private GlobalStorageService globalStorageService;
    private UserStorageService userStorageService;
    private OnboardingManager onboardingManager;
    private SpaceManager spaceManager;

    @PermittedMethods(value={HttpMethod.GET})
    public String execute() throws Exception {
        return "success";
    }

    public boolean isFirstSpaceCreated() {
        return this.onboardingManager.isFirstSpaceCreated();
    }

    public int getAvailableSpacesCount() {
        return this.spaceManager.getSpaces(SpacesQuery.newQuery().forUser((User)AuthenticatedUserThreadLocal.get()).build()).getAvailableSize();
    }

    public boolean hasCreateSpacePermission() {
        return this.permissionManager.hasCreatePermission((User)this.getAuthenticatedUser(), PermissionManager.TARGET_APPLICATION, Space.class);
    }

    public String getCurrentSequenceKey() {
        String sequenceKey = this.userStorageService.get("onboarding-state:introWorkflow", this.getAuthenticatedUser());
        return sequenceKey == null ? "" : sequenceKey;
    }

    public void setGlobalStorageService(GlobalStorageService globalStorageService) {
        this.globalStorageService = globalStorageService;
    }

    public UserStorageService getUserStorageService() {
        return this.userStorageService;
    }

    public void setUserStorageService(UserStorageService userStorageService) {
        this.userStorageService = userStorageService;
    }

    public void setOnboardingManager(OnboardingManager onboardingManager) {
        this.onboardingManager = onboardingManager;
    }

    public SpaceManager getSpaceManager() {
        return this.spaceManager;
    }

    public void setSpaceManager(SpaceManager spaceManager) {
        this.spaceManager = spaceManager;
    }
}

