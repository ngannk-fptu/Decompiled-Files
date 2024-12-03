/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.ParametersAreNonnullByDefault
 */
package com.atlassian.migration.agent.service.user;

import com.atlassian.migration.agent.service.stepexecutor.space.CreateTombstoneAccountRequest;
import com.atlassian.migration.agent.service.stepexecutor.space.PublishTombstoneMappingRequest;
import com.atlassian.migration.agent.service.stepexecutor.space.TombstoneAccountsResponse;
import com.atlassian.migration.agent.service.user.CloudEditionCheckResponse;
import com.atlassian.migration.agent.service.user.GroupConflictsCheckRequest;
import com.atlassian.migration.agent.service.user.GroupsConflictCheckResponse;
import com.atlassian.migration.agent.service.user.UsersMigrationStatusResponse;
import com.atlassian.migration.agent.service.user.request.v2.UsersMigrationV2Request;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public interface UsersMigrationService {
    public String initiateUsersAndGroupsMigrationV2(String var1, UsersMigrationV2Request var2);

    public UsersMigrationStatusResponse getUsersAndGroupsMigrationProgress(String var1, String var2);

    public String startGroupConflictsCheck(String var1, GroupConflictsCheckRequest var2);

    public GroupsConflictCheckResponse getGroupConflictsCheckStatus(String var1, String var2);

    public CloudEditionCheckResponse getCloudEditionCheck(String var1);

    public UsersMigrationStatusResponse cancelUsersAndGroupsMigration(String var1, String var2);

    public TombstoneAccountsResponse createTombstoneAccounts(String var1, int var2, CreateTombstoneAccountRequest var3);

    public void publishTombstoneMappings(String var1, PublishTombstoneMappingRequest var2);
}

