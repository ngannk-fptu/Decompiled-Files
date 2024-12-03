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
package com.atlassian.migration.agent.service.check.app.notinstalled;

import com.atlassian.cmpt.check.base.CheckResult;
import com.atlassian.cmpt.check.dto.CheckDetailsDto;
import com.atlassian.cmpt.check.dto.CheckResultDto;
import com.atlassian.cmpt.check.dto.Status;
import com.atlassian.cmpt.check.mapper.AbstractMapper;
import com.atlassian.migration.agent.service.check.app.notinstalled.AppsNotInstalledOnCloudChecker;
import com.atlassian.migration.agent.service.check.app.notinstalled.AppsNotInstalledOnCloudDto;
import java.util.List;

public class AppsNotInstalledOnCloudMapper
extends AbstractMapper {
    private static final String SUCCESS_DESCRIPTION = "All 'Needed in cloud' apps are installed on your cloud site";
    private static final String RUNNING_DESCRIPTION = "Checking that all 'Needed in cloud' apps are installed on your cloud site";
    private static final String EXECUTION_ERROR_DESCRIPTION = "We couldn't check that all 'Needed in cloud' apps are installed on your cloud site";
    private static final String ERROR_OR_WARNING_DESCRIPTION = "Some 'Needed in cloud' apps are not installed on your cloud site";
    private static final String LONG_DESCRIPTION = "In order to migrate your app data, your cloud site needs the relevant apps installed first. To continue you can:";

    protected void inject(CheckResultDto dto, CheckResult checkResult) {
        List<AppsNotInstalledOnCloudDto> appsNotInstalledOnCloud;
        switch (dto.getStatus()) {
            case SUCCESS: {
                dto.setDescription(SUCCESS_DESCRIPTION);
                break;
            }
            case WARNING: 
            case ERROR: {
                dto.setStatus(Status.ERROR);
                dto.setDescription(ERROR_OR_WARNING_DESCRIPTION);
                break;
            }
            case RUNNING: {
                dto.setDescription(RUNNING_DESCRIPTION);
                break;
            }
            case EXECUTION_ERROR: {
                dto.setDescription(EXECUTION_ERROR_DESCRIPTION);
                break;
            }
            default: {
                throw new IllegalArgumentException("Unsupported status.");
            }
        }
        if (checkResult != null && !(appsNotInstalledOnCloud = AppsNotInstalledOnCloudChecker.retrieveAppsNotInstalledOnCloud(checkResult.details)).isEmpty()) {
            dto.setNumberOfOccurrences(Integer.valueOf(appsNotInstalledOnCloud.size()));
            CheckDetailsDto details = new CheckDetailsDto();
            details.setLongDescription(LONG_DESCRIPTION);
            details.setListOfOccurrences(appsNotInstalledOnCloud);
            dto.setDetails(details);
        }
    }
}

