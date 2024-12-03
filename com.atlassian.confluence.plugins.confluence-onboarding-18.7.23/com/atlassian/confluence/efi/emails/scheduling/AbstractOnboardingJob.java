/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.confluence.user.UserAccessor
 *  com.atlassian.crowd.embedded.api.CrowdService
 *  com.atlassian.crowd.embedded.api.User
 *  com.atlassian.crowd.model.user.TimestampedUser
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.fugue.Maybe
 *  com.atlassian.fugue.Option
 *  com.atlassian.sal.api.user.UserKey
 *  com.atlassian.scheduler.JobRunner
 *  com.atlassian.scheduler.JobRunnerRequest
 *  com.atlassian.scheduler.JobRunnerResponse
 *  com.atlassian.user.Group
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.Iterables
 */
package com.atlassian.confluence.efi.emails.scheduling;

import com.atlassian.confluence.efi.emails.events.OnboardingEvent;
import com.atlassian.confluence.efi.store.GlobalStorageService;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.crowd.embedded.api.CrowdService;
import com.atlassian.crowd.embedded.api.User;
import com.atlassian.crowd.model.user.TimestampedUser;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.fugue.Maybe;
import com.atlassian.fugue.Option;
import com.atlassian.sal.api.user.UserKey;
import com.atlassian.scheduler.JobRunner;
import com.atlassian.scheduler.JobRunnerRequest;
import com.atlassian.scheduler.JobRunnerResponse;
import com.atlassian.user.Group;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;

public abstract class AbstractOnboardingJob
implements JobRunner {
    private static final String JOB_STATUS_FIRST_EXECUTE = "JOB_FIRST_EXECUTE";
    private static final String JOB_STATUS_EXECUTED = "JOB_EXECUTED";
    protected final UserAccessor userAccessor;
    protected final EventPublisher eventPublisher;
    protected final CrowdService crowdService;
    protected final GlobalStorageService globalStorageService;

    public AbstractOnboardingJob(UserAccessor userAccessor, EventPublisher eventPublisher, CrowdService crowdService, GlobalStorageService globalStorageService) {
        this.userAccessor = userAccessor;
        this.eventPublisher = eventPublisher;
        this.crowdService = crowdService;
        this.globalStorageService = globalStorageService;
    }

    public JobRunnerResponse runJob(JobRunnerRequest request) {
        String jobName = request.getJobId().toString();
        String jobStatus = this.globalStorageService.get(jobName);
        if (JOB_STATUS_EXECUTED.equals(jobStatus)) {
            return JobRunnerResponse.aborted((String)"Job need not be executed again");
        }
        if (jobStatus == null) {
            this.globalStorageService.set(jobName, JOB_STATUS_FIRST_EXECUTE);
            return JobRunnerResponse.aborted((String)"Job need not be executed immediately on startup");
        }
        this.globalStorageService.set(jobName, JOB_STATUS_EXECUTED);
        this.doExecute();
        return JobRunnerResponse.success();
    }

    protected void triggerEvent() {
        ConfluenceUser firstAdmin = (ConfluenceUser)this.getFirstAdmin().getOrNull();
        this.eventPublisher.publish((Object)this.createEventForUser(firstAdmin.getKey()));
    }

    protected abstract void doExecute();

    protected abstract OnboardingEvent createEventForUser(UserKey var1);

    private Maybe<ConfluenceUser> getFirstAdmin() {
        ConfluenceUser firstAdminConfUser = null;
        Group adminGroup = this.userAccessor.getGroup("confluence-administrators");
        ImmutableList confluenceUsers = ImmutableList.copyOf((Iterable)this.userAccessor.getMembers(adminGroup));
        if (!Iterables.isEmpty((Iterable)confluenceUsers)) {
            Iterable users = Iterables.transform((Iterable)confluenceUsers, input -> this.crowdService.getUser(input.getName()));
            Iterable sortedUsers = Iterables.mergeSorted((Iterable)ImmutableList.of((Object)users), (u1, u2) -> {
                if (u1 instanceof TimestampedUser && u2 instanceof TimestampedUser) {
                    TimestampedUser user1 = (TimestampedUser)u1;
                    TimestampedUser user2 = (TimestampedUser)u2;
                    return user1.getCreatedDate().compareTo(user2.getCreatedDate());
                }
                return 0;
            });
            User firstAdmin = (User)sortedUsers.iterator().next();
            firstAdminConfUser = this.userAccessor.getUserByName(firstAdmin.getName());
        }
        return Option.option(firstAdminConfUser);
    }
}

