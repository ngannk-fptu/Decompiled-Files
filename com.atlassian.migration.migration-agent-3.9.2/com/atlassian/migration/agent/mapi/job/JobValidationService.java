/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.license.LicenseHandler
 *  javax.annotation.ParametersAreNonnullByDefault
 *  lombok.Generated
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.migration.agent.mapi.job;

import com.atlassian.migration.agent.mapi.external.model.JobValidationException;
import com.atlassian.migration.agent.mapi.job.JobDefinition;
import com.atlassian.migration.agent.mapi.job.scope.AppScope;
import com.atlassian.migration.agent.mapi.job.scope.ScopeMode;
import com.atlassian.migration.agent.mapi.job.scope.SpaceMode;
import com.atlassian.migration.agent.mapi.job.scope.SpaceScope;
import com.atlassian.migration.agent.mapi.job.scope.UsersGroupsScope;
import com.atlassian.sal.api.license.LicenseHandler;
import java.util.List;
import java.util.Objects;
import javax.annotation.ParametersAreNonnullByDefault;
import lombok.Generated;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ParametersAreNonnullByDefault
public class JobValidationService {
    @Generated
    private static final Logger log = LoggerFactory.getLogger(JobValidationService.class);
    private LicenseHandler licenseHandler;
    private static final String INVALID_SOURCE_MESSAGE = "Your SERVER_ID:%s does not match with expected server id provided in job definition.";

    public JobValidationService(LicenseHandler licenseHandler) {
        this.licenseHandler = licenseHandler;
    }

    public void validateJobDefinition(JobDefinition jobDefinition) {
        SpaceScope spaceScope = jobDefinition.getScope().getSpaces();
        SpaceMode spacesIncludedData = spaceScope.getIncludedData();
        List<String> spacesIncludeKeys = spaceScope.getIncludedKeys();
        UsersGroupsScope usersAndGroupsScope = jobDefinition.getScope().getUsersAndGroups();
        ScopeMode usersAndGroupsMode = usersAndGroupsScope.getMode();
        AppScope appScope = jobDefinition.getScope().getApps();
        if (!Objects.equals(jobDefinition.getSource().getServerId(), this.licenseHandler.getServerId())) {
            throw new JobValidationException(String.format(INVALID_SOURCE_MESSAGE, this.licenseHandler.getServerId()));
        }
        if (usersAndGroupsMode == ScopeMode.REFERENCED && spacesIncludeKeys.isEmpty() && spacesIncludedData == SpaceMode.ALL) {
            throw new JobValidationException("You have passed an empty scope or usersAndGroups mode passed as referenced with no includedKeys in spaces during job creation using public API");
        }
        if (spacesIncludedData == SpaceMode.ATTACHMENTS && (usersAndGroupsScope != null || appScope != null)) {
            log.warn("includedData=ATTACHMENTS in spaces scope with usersAndGroups/apps scope present during job creation using public API");
        }
    }
}

