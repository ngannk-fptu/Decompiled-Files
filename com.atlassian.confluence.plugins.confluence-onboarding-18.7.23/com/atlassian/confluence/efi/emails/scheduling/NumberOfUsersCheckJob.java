/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.user.UserAccessor
 *  com.atlassian.confluence.util.UserChecker
 *  com.atlassian.crowd.embedded.api.CrowdService
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.sal.api.user.UserKey
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.efi.emails.scheduling;

import com.atlassian.confluence.efi.emails.events.OnboardingEvent;
import com.atlassian.confluence.efi.emails.events.OnboardingLessUsersEvent;
import com.atlassian.confluence.efi.emails.scheduling.AbstractOnboardingJob;
import com.atlassian.confluence.efi.store.GlobalStorageService;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.confluence.util.UserChecker;
import com.atlassian.crowd.embedded.api.CrowdService;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.sal.api.user.UserKey;
import org.springframework.stereotype.Component;

@Component
public class NumberOfUsersCheckJob
extends AbstractOnboardingJob {
    private static final int EXPECTED_NUMBER_OF_USERS = 5;
    private final UserChecker userChecker;

    public NumberOfUsersCheckJob(@ComponentImport UserAccessor userAccessor, @ComponentImport EventPublisher eventPublisher, @ComponentImport CrowdService crowdService, GlobalStorageService globalStorageService, @ComponentImport UserChecker userChecker) {
        super(userAccessor, eventPublisher, crowdService, globalStorageService);
        this.userChecker = userChecker;
    }

    @Override
    public void doExecute() {
        int numberOfRegisteredUsers = this.userChecker.getNumberOfRegisteredUsers();
        if (numberOfRegisteredUsers < 5) {
            this.triggerEvent();
        }
    }

    @Override
    protected OnboardingEvent createEventForUser(UserKey userKey) {
        return new OnboardingLessUsersEvent(this, userKey);
    }
}

