/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Sets
 */
package com.atlassian.migration.agent.service.check.licence;

import com.atlassian.migration.agent.entity.GlobalEntityType;
import com.atlassian.migration.agent.service.check.CheckContextProvider;
import com.atlassian.migration.agent.service.check.ContextProviderUtil;
import com.atlassian.migration.agent.service.check.licence.LicenceCheckContext;
import com.atlassian.migration.agent.service.user.TombstoneFileParameters;
import com.atlassian.migration.agent.service.user.UsersMigrationRequestBuilder;
import com.atlassian.migration.agent.service.user.request.v2.UsersMigrationV2FilePayload;
import com.google.common.collect.Sets;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;

public class LicenceCheckContextProvider
implements CheckContextProvider<LicenceCheckContext> {
    private final UsersMigrationRequestBuilder usersMigrationRequestBuilder;

    public LicenceCheckContextProvider(UsersMigrationRequestBuilder usersMigrationRequestBuilder) {
        this.usersMigrationRequestBuilder = usersMigrationRequestBuilder;
    }

    @Override
    public LicenceCheckContext apply(Map<String, Object> parameters) {
        String cloudId = ContextProviderUtil.getCloudId(parameters);
        String executionId = ContextProviderUtil.getExecutionId(parameters);
        HashSet spaceKeys = !parameters.isEmpty() && ContextProviderUtil.containsSpaceKeys(parameters) ? Sets.newHashSet((Object[])ContextProviderUtil.getSpaceKeys(parameters)) : Collections.emptySet();
        Optional<GlobalEntityType> globalEntityType = ContextProviderUtil.checkAndGetGlobalEntityType(parameters);
        UsersMigrationV2FilePayload usersMigrationRequestV2FilePayload = this.usersMigrationRequestBuilder.createUsersMigrationRequestV2FilePayload(spaceKeys, cloudId, TombstoneFileParameters.withoutFile(), globalEntityType);
        return new LicenceCheckContext(cloudId, executionId, spaceKeys, usersMigrationRequestV2FilePayload);
    }
}

