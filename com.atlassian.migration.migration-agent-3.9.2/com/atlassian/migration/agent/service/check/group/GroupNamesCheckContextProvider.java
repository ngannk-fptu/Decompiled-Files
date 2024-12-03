/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.ParametersAreNonnullByDefault
 */
package com.atlassian.migration.agent.service.check.group;

import com.atlassian.migration.agent.entity.GlobalEntityType;
import com.atlassian.migration.agent.service.check.CheckContextProvider;
import com.atlassian.migration.agent.service.check.ContextProviderUtil;
import com.atlassian.migration.agent.service.check.group.GroupNamesConflictContext;
import com.atlassian.migration.agent.service.extract.UserGroupExtractFacade;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class GroupNamesCheckContextProvider
implements CheckContextProvider<GroupNamesConflictContext> {
    private final UserGroupExtractFacade userGroupExtractFacade;

    public GroupNamesCheckContextProvider(UserGroupExtractFacade userGroupExtractFacade) {
        this.userGroupExtractFacade = userGroupExtractFacade;
    }

    @Override
    public GroupNamesConflictContext apply(Map<String, Object> parameters) {
        String cloudId = ContextProviderUtil.getCloudId(parameters);
        Set<String> groupNames = this.getGroupNames(parameters);
        return new GroupNamesConflictContext(cloudId, new ArrayList<String>(groupNames));
    }

    private Set<String> getGroupNames(Map<String, Object> parameters) {
        if (parameters.containsKey("spaceKeys")) {
            HashSet<String> groupNames = new HashSet<String>();
            String[] spaceKeys = ContextProviderUtil.getSpaceKeys(parameters);
            Optional<GlobalEntityType> globalEntityType = ContextProviderUtil.checkAndGetGlobalEntityType(parameters);
            groupNames.addAll(this.userGroupExtractFacade.getGroupsFromSpacesAndGlobalEntities(Arrays.asList(spaceKeys), globalEntityType));
            return groupNames;
        }
        try {
            return this.userGroupExtractFacade.getAllGroupNames();
        }
        catch (Exception e) {
            throw new RuntimeException(String.format("Unable to build context for group names conflict, %s", e.getMessage()), e);
        }
    }
}

