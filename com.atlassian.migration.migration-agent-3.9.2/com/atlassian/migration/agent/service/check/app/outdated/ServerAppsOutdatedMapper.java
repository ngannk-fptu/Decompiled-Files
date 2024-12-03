/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.cmpt.check.base.CheckResult
 *  com.atlassian.cmpt.check.dto.CheckDetailsDto
 *  com.atlassian.cmpt.check.dto.CheckResultDto
 *  com.atlassian.cmpt.check.dto.Status
 *  com.atlassian.cmpt.check.mapper.AbstractMapper
 */
package com.atlassian.migration.agent.service.check.app.outdated;

import com.atlassian.cmpt.check.base.CheckResult;
import com.atlassian.cmpt.check.dto.CheckDetailsDto;
import com.atlassian.cmpt.check.dto.CheckResultDto;
import com.atlassian.cmpt.check.dto.Status;
import com.atlassian.cmpt.check.mapper.AbstractMapper;
import com.atlassian.migration.agent.service.check.app.outdated.ServerAppsOutdatedChecker;
import com.atlassian.migration.agent.service.check.app.outdated.ServerAppsOutdatedDto;
import java.util.List;

public class ServerAppsOutdatedMapper
extends AbstractMapper {
    public void inject(CheckResultDto dto, CheckResult checkResult) {
        List<ServerAppsOutdatedDto> appsOutdated;
        String description;
        switch (dto.getStatus()) {
            case SUCCESS: {
                description = "Apps are updated";
                break;
            }
            case ERROR: 
            case WARNING: {
                description = "Some apps marked as 'Needed in cloud' on your server are out of date";
                dto.setStatus(Status.ERROR);
                break;
            }
            case RUNNING: {
                description = "Checking for apps outdated";
                break;
            }
            case EXECUTION_ERROR: {
                description = "We couldn\u2019t check for apps outdated";
                break;
            }
            default: {
                throw new IllegalArgumentException("Unsupported status.");
            }
        }
        dto.setDescription(description);
        if (checkResult != null && !(appsOutdated = ServerAppsOutdatedChecker.retrieveOutdatedServerApps(checkResult.details)).isEmpty()) {
            CheckDetailsDto details = new CheckDetailsDto();
            details.setListOfOccurrences(appsOutdated);
            details.setLongDescription("To be included in this migration, the below apps must be updated to a version that has an automated migration path. You can do the following:");
            dto.setDetails(details);
            dto.setNumberOfOccurrences(Integer.valueOf(appsOutdated.size()));
        }
    }
}

