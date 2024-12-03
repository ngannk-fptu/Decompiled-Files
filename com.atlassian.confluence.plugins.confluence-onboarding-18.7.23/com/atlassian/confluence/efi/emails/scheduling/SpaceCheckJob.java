/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.user.UserAccessor
 *  com.atlassian.crowd.embedded.api.CrowdService
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.sal.api.user.UserKey
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.efi.emails.scheduling;

import com.atlassian.confluence.efi.OnboardingManager;
import com.atlassian.confluence.efi.emails.events.OnboardingEvent;
import com.atlassian.confluence.efi.emails.events.OnboardingNoSpaceCreatedEvent;
import com.atlassian.confluence.efi.emails.scheduling.AbstractOnboardingJob;
import com.atlassian.confluence.efi.store.GlobalStorageService;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.crowd.embedded.api.CrowdService;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.sal.api.user.UserKey;
import org.springframework.stereotype.Component;

@Component
public class SpaceCheckJob
extends AbstractOnboardingJob {
    private final OnboardingManager onboardingManager;

    public SpaceCheckJob(@ComponentImport UserAccessor userAccessor, @ComponentImport EventPublisher eventPublisher, @ComponentImport CrowdService crowdService, GlobalStorageService globalStorageService, OnboardingManager onboardingManager) {
        super(userAccessor, eventPublisher, crowdService, globalStorageService);
        this.onboardingManager = onboardingManager;
    }

    @Override
    public void doExecute() {
        if (!this.onboardingManager.isFirstSpaceCreated()) {
            this.triggerEvent();
        }
    }

    @Override
    protected OnboardingEvent createEventForUser(UserKey userKey) {
        return new OnboardingNoSpaceCreatedEvent(this, userKey);
    }
}

