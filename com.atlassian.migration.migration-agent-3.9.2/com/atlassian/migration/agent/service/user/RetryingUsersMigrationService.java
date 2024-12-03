/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.ParametersAreNonnullByDefault
 *  net.jodah.failsafe.Failsafe
 *  net.jodah.failsafe.Policy
 *  net.jodah.failsafe.RetryPolicy
 */
package com.atlassian.migration.agent.service.user;

import com.atlassian.migration.agent.okhttp.RetryPolicyBuilder;
import com.atlassian.migration.agent.service.stepexecutor.space.CreateTombstoneAccountRequest;
import com.atlassian.migration.agent.service.stepexecutor.space.PublishTombstoneMappingRequest;
import com.atlassian.migration.agent.service.stepexecutor.space.TombstoneAccountsResponse;
import com.atlassian.migration.agent.service.user.CloudEditionCheckResponse;
import com.atlassian.migration.agent.service.user.DefaultUsersMigrationService;
import com.atlassian.migration.agent.service.user.GroupConflictsCheckRequest;
import com.atlassian.migration.agent.service.user.GroupsConflictCheckResponse;
import com.atlassian.migration.agent.service.user.UsersMigrationService;
import com.atlassian.migration.agent.service.user.UsersMigrationStatusResponse;
import com.atlassian.migration.agent.service.user.request.v2.UsersMigrationV2Request;
import javax.annotation.ParametersAreNonnullByDefault;
import net.jodah.failsafe.Failsafe;
import net.jodah.failsafe.Policy;
import net.jodah.failsafe.RetryPolicy;

@ParametersAreNonnullByDefault
public class RetryingUsersMigrationService
implements UsersMigrationService {
    private final UsersMigrationService wrapped;
    private final RetryPolicy<UsersMigrationStatusResponse> usersMigrationStatusResponseRetryPolicy;
    private final RetryPolicy<String> retryPolicy;
    private final RetryPolicy<GroupsConflictCheckResponse> groupConflictCheckResponseRetryPolicy;
    private final RetryPolicy<CloudEditionCheckResponse> cloudEditionCheckResponseRetryPolicy;
    private final RetryPolicy<TombstoneAccountsResponse> tombstoneAccountsResponseRetryPolicy;
    private final RetryPolicy<String> initiateUsersRetryPolicy;
    private final RetryPolicy<Void> voidRetryPolicy;

    public RetryingUsersMigrationService(DefaultUsersMigrationService wrapped) {
        this.wrapped = wrapped;
        this.initiateUsersRetryPolicy = RetryPolicyBuilder.policyForInitiateUsersAndGroups();
        this.usersMigrationStatusResponseRetryPolicy = RetryPolicyBuilder.policyForUserMigrationService();
        this.retryPolicy = RetryPolicyBuilder.policyForUserMigrationService();
        this.groupConflictCheckResponseRetryPolicy = RetryPolicyBuilder.policyForUserMigrationService();
        this.cloudEditionCheckResponseRetryPolicy = RetryPolicyBuilder.policyForUserMigrationService();
        this.tombstoneAccountsResponseRetryPolicy = RetryPolicyBuilder.policyForUserMigrationService();
        this.voidRetryPolicy = RetryPolicyBuilder.policyForUserMigrationService();
    }

    @Override
    public String initiateUsersAndGroupsMigrationV2(String containerToken, UsersMigrationV2Request usersAndGroups) {
        return (String)Failsafe.with(this.initiateUsersRetryPolicy, (Policy[])new RetryPolicy[0]).get(() -> this.wrapped.initiateUsersAndGroupsMigrationV2(containerToken, usersAndGroups));
    }

    @Override
    public UsersMigrationStatusResponse getUsersAndGroupsMigrationProgress(String containerToken, String taskId) {
        return (UsersMigrationStatusResponse)Failsafe.with(this.usersMigrationStatusResponseRetryPolicy, (Policy[])new RetryPolicy[0]).get(() -> this.wrapped.getUsersAndGroupsMigrationProgress(containerToken, taskId));
    }

    @Override
    public String startGroupConflictsCheck(String containerToken, GroupConflictsCheckRequest groupNamesCheckRequest) {
        return (String)Failsafe.with(this.retryPolicy, (Policy[])new RetryPolicy[0]).get(() -> this.wrapped.startGroupConflictsCheck(containerToken, groupNamesCheckRequest));
    }

    @Override
    public GroupsConflictCheckResponse getGroupConflictsCheckStatus(String containerToken, String taskId) {
        return (GroupsConflictCheckResponse)Failsafe.with(this.groupConflictCheckResponseRetryPolicy, (Policy[])new RetryPolicy[0]).get(() -> this.wrapped.getGroupConflictsCheckStatus(containerToken, taskId));
    }

    @Override
    public CloudEditionCheckResponse getCloudEditionCheck(String containerToken) {
        return (CloudEditionCheckResponse)Failsafe.with(this.cloudEditionCheckResponseRetryPolicy, (Policy[])new RetryPolicy[0]).get(() -> this.wrapped.getCloudEditionCheck(containerToken));
    }

    @Override
    public UsersMigrationStatusResponse cancelUsersAndGroupsMigration(String containerToken, String taskId) {
        return (UsersMigrationStatusResponse)Failsafe.with(this.usersMigrationStatusResponseRetryPolicy, (Policy[])new RetryPolicy[0]).get(() -> this.wrapped.cancelUsersAndGroupsMigration(containerToken, taskId));
    }

    @Override
    public TombstoneAccountsResponse createTombstoneAccounts(String containerToken, int numOfUsers, CreateTombstoneAccountRequest request) {
        return (TombstoneAccountsResponse)Failsafe.with(this.tombstoneAccountsResponseRetryPolicy, (Policy[])new RetryPolicy[0]).get(() -> this.wrapped.createTombstoneAccounts(containerToken, numOfUsers, request));
    }

    @Override
    public void publishTombstoneMappings(String containerToken, PublishTombstoneMappingRequest request) {
        Failsafe.with(this.voidRetryPolicy, (Policy[])new RetryPolicy[0]).run(() -> this.wrapped.publishTombstoneMappings(containerToken, request));
    }
}

