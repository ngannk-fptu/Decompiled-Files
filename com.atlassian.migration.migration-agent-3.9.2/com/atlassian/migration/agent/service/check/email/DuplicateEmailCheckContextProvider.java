/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.ParametersAreNonnullByDefault
 *  lombok.Generated
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.migration.agent.service.check.email;

import com.atlassian.migration.agent.entity.GlobalEntityType;
import com.atlassian.migration.agent.service.check.CheckContextProvider;
import com.atlassian.migration.agent.service.check.ContextProviderUtil;
import com.atlassian.migration.agent.service.check.email.DuplicateEmailCheckContext;
import com.atlassian.migration.agent.service.extract.UserGroupExtractFacade;
import com.atlassian.migration.agent.service.impl.MigrationUser;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import javax.annotation.ParametersAreNonnullByDefault;
import lombok.Generated;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ParametersAreNonnullByDefault
public class DuplicateEmailCheckContextProvider
implements CheckContextProvider<DuplicateEmailCheckContext> {
    @Generated
    private static final Logger log = LoggerFactory.getLogger(DuplicateEmailCheckContextProvider.class);
    private final UserGroupExtractFacade userGroupExtractFacade;

    public DuplicateEmailCheckContextProvider(UserGroupExtractFacade userGroupExtractFacade) {
        this.userGroupExtractFacade = userGroupExtractFacade;
    }

    @Override
    public DuplicateEmailCheckContext apply(Map<String, Object> parameters) {
        String cloudId = ContextProviderUtil.getCloudId(parameters);
        if (!parameters.containsKey("spaceKeys")) {
            List<MigrationUser> users = this.userGroupExtractFacade.getAllUsers();
            return new DuplicateEmailCheckContext(users, cloudId);
        }
        String[] spaceKeys = ContextProviderUtil.getSpaceKeys(parameters);
        Optional<GlobalEntityType> globalEntityType = ContextProviderUtil.checkAndGetGlobalEntityType(parameters);
        Set<String> userSet = this.userGroupExtractFacade.getUsersFromSpacesAndGlobalEntities(Arrays.asList(spaceKeys), globalEntityType);
        List<MigrationUser> users = this.userGroupExtractFacade.getAllUsers().stream().filter(u -> userSet.contains(u.getUserKey())).collect(Collectors.toList());
        return new DuplicateEmailCheckContext(users, cloudId);
    }
}

