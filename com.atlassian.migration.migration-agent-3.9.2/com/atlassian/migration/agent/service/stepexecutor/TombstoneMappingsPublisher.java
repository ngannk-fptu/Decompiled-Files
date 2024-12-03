/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.VisibleForTesting
 *  com.atlassian.cmpt.validation.IdentityAcceptedEmailValidator
 *  com.google.common.collect.Lists
 *  javax.annotation.ParametersAreNonnullByDefault
 *  lombok.Generated
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.migration.agent.service.stepexecutor;

import com.atlassian.annotations.VisibleForTesting;
import com.atlassian.cmpt.validation.IdentityAcceptedEmailValidator;
import com.atlassian.migration.MigrationDarkFeaturesManager;
import com.atlassian.migration.agent.entity.CloudSite;
import com.atlassian.migration.agent.entity.Plan;
import com.atlassian.migration.agent.entity.Step;
import com.atlassian.migration.agent.entity.TombstoneAccount;
import com.atlassian.migration.agent.service.execution.UncheckedInterruptedException;
import com.atlassian.migration.agent.service.stepexecutor.TombstoneMappingsPublisherException;
import com.atlassian.migration.agent.service.stepexecutor.space.PublishTombstoneMappingRequest;
import com.atlassian.migration.agent.service.stepexecutor.space.TombstoneUser;
import com.atlassian.migration.agent.service.user.RetryingUsersMigrationService;
import com.atlassian.migration.agent.service.user.UsersMigrationRequestBuilder;
import com.atlassian.migration.agent.service.user.UsersMigrationService;
import com.atlassian.migration.agent.service.util.StopConditionCheckingUtil;
import com.atlassian.migration.agent.store.TombstoneAccountStore;
import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import javax.annotation.ParametersAreNonnullByDefault;
import lombok.Generated;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ParametersAreNonnullByDefault
public class TombstoneMappingsPublisher {
    @Generated
    private static final Logger log = LoggerFactory.getLogger(TombstoneMappingsPublisher.class);
    private static final int MAX_TOMBSTONE_ACCOUNTS_PER_REQUEST = 5000;
    private static final int MAX_TOMBSTONE_ACCOUNTS_PER_PUBLISH_REQUEST = 200;
    private final UsersMigrationService usersMigrationService;
    private final UsersMigrationRequestBuilder usersMigrationRequestBuilder;
    private final TombstoneAccountStore tombstoneAccountStore;
    private final MigrationDarkFeaturesManager migrationDarkFeaturesManager;

    public TombstoneMappingsPublisher(RetryingUsersMigrationService usersMigrationService, UsersMigrationRequestBuilder usersMigrationRequestBuilder, TombstoneAccountStore tombstoneAccountStore, MigrationDarkFeaturesManager migrationDarkFeaturesManager) {
        this.usersMigrationService = usersMigrationService;
        this.usersMigrationRequestBuilder = usersMigrationRequestBuilder;
        this.tombstoneAccountStore = tombstoneAccountStore;
        this.migrationDarkFeaturesManager = migrationDarkFeaturesManager;
    }

    public List<TombstoneUser> createAndPublishTombstoneMappings(Step step, List<TombstoneUser> tombstoneUsers) throws TombstoneMappingsPublisherException {
        if (tombstoneUsers.isEmpty()) {
            return Collections.emptyList();
        }
        Plan plan = step.getPlan();
        String migrationScopeId = plan.getMigrationScopeId();
        CloudSite cloudSite = plan.getCloudSite();
        String containerToken = cloudSite.getContainerToken();
        boolean sendEmails = !this.migrationDarkFeaturesManager.shouldHandleGlobalEmailFixes();
        List<TombstoneUser> cleansedTombstoneUsers = this.cleanseTombstoneUsers(tombstoneUsers, sendEmails);
        List tombstoneUsersBatches = Lists.partition(cleansedTombstoneUsers, (int)5000);
        try {
            ArrayList<TombstoneUser> tombstoneUsersWithAaids = new ArrayList<TombstoneUser>();
            tombstoneUsersBatches.stream().map(batch -> this.toTombstoneUsersWithAAIDMapping((List<TombstoneUser>)batch, cloudSite)).forEach(batch -> {
                tombstoneUsersWithAaids.addAll((Collection<TombstoneUser>)batch);
                this.publishTombstoneUsersMappingToUMS(migrationScopeId, containerToken, (List<TombstoneUser>)batch);
            });
            return tombstoneUsersWithAaids;
        }
        catch (Exception e) {
            if (StopConditionCheckingUtil.isStoppingExceptionInCausalChain(e)) {
                log.warn("Creating and publishing tombstone mappings failed due to Migration Step being stopped", (Throwable)e);
                throw new UncheckedInterruptedException(e);
            }
            log.error("An error occurred while creating and publishing tombstone mappings.", (Throwable)e);
            throw new TombstoneMappingsPublisherException(e);
        }
    }

    public Map<String, TombstoneAccount> prepareTombstoneAccounts(List<String> userKeys, CloudSite cloudSite) {
        Map<String, TombstoneAccount> existingTombstoneAccounts = this.tombstoneAccountStore.loadByUserkeys(userKeys).stream().collect(Collectors.toMap(TombstoneAccount::getUserKey, Function.identity()));
        ArrayList<String> userKeysWithoutTombstoneAccounts = new ArrayList<String>(userKeys);
        userKeysWithoutTombstoneAccounts.removeAll(existingTombstoneAccounts.keySet());
        if (!userKeysWithoutTombstoneAccounts.isEmpty()) {
            Map<String, TombstoneAccount> savedTombstoneAccounts = this.createAndSaveTombstoneAccounts(cloudSite, userKeysWithoutTombstoneAccounts);
            HashMap<String, TombstoneAccount> mergedTombstoneAccounts = new HashMap<String, TombstoneAccount>();
            mergedTombstoneAccounts.putAll(savedTombstoneAccounts);
            mergedTombstoneAccounts.putAll(existingTombstoneAccounts);
            return mergedTombstoneAccounts;
        }
        return existingTombstoneAccounts;
    }

    private void publishTombstoneUsersMappingToUMS(String migrationScopeId, String containerToken, List<TombstoneUser> tombstoneUsers) {
        List tombstoneUsersBatches = Lists.partition(tombstoneUsers, (int)200);
        for (List tombstoneUsersBatch : tombstoneUsersBatches) {
            PublishTombstoneMappingRequest requestForPublishingTombstoneMappings = new PublishTombstoneMappingRequest(migrationScopeId, tombstoneUsersBatch);
            this.usersMigrationService.publishTombstoneMappings(containerToken, requestForPublishingTombstoneMappings);
        }
    }

    private List<TombstoneUser> toTombstoneUsersWithAAIDMapping(List<TombstoneUser> tombstoneUsers, CloudSite cloudSite) {
        List<String> userKeys = this.extractUserKeys(tombstoneUsers);
        Map<String, TombstoneAccount> userKeyToTombstoneAccountMap = this.prepareTombstoneAccounts(userKeys, cloudSite);
        return tombstoneUsers.stream().map(user -> TombstoneUser.fromTombstoneUserWithAaid(user, ((TombstoneAccount)userKeyToTombstoneAccountMap.get(user.getUserKey())).getAaid())).collect(Collectors.toList());
    }

    private List<TombstoneUser> cleanseTombstoneUsers(List<TombstoneUser> tombstoneUsers, boolean sendEmails) {
        return tombstoneUsers.stream().map(u -> {
            String email = sendEmails ? IdentityAcceptedEmailValidator.cleanse((String)u.getEmail()) : null;
            return new TombstoneUser(u.getUserName(), u.getUserKey(), email, u.getDisplayName());
        }).collect(Collectors.toList());
    }

    private List<String> extractUserKeys(List<TombstoneUser> tombstoneUsers) {
        return tombstoneUsers.stream().map(TombstoneUser::getUserKey).collect(Collectors.toList());
    }

    @VisibleForTesting
    Map<String, TombstoneAccount> createAndSaveTombstoneAccounts(CloudSite cloudSite, List<String> userKeys) {
        List<String> aaidsForTombstoneUsers = this.usersMigrationService.createTombstoneAccounts(cloudSite.getContainerToken(), userKeys.size(), this.usersMigrationRequestBuilder.createTombstoneAccountCreationRequest(cloudSite)).getTombstoneAccountIds();
        if (userKeys.size() != aaidsForTombstoneUsers.size()) {
            log.warn("Number of users to tombstone and aaids generated are not the same. Number of users to tombstone: {} and number of aaids generated: {}", (Object)userKeys.size(), (Object)aaidsForTombstoneUsers.size());
        }
        List tombstoneAccounts = IntStream.range(0, userKeys.size()).mapToObj(i -> {
            TombstoneAccount tombstoneAccount = new TombstoneAccount((String)userKeys.get(i), (String)aaidsForTombstoneUsers.get(i));
            this.tombstoneAccountStore.save(tombstoneAccount);
            return tombstoneAccount;
        }).collect(Collectors.toList());
        return tombstoneAccounts.stream().collect(Collectors.toMap(TombstoneAccount::getUserKey, Function.identity()));
    }
}

