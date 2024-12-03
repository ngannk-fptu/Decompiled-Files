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
package com.atlassian.migration.agent.service.check.app.license;

import com.atlassian.cmpt.check.base.CheckResult;
import com.atlassian.cmpt.check.dto.CheckDetailsDto;
import com.atlassian.cmpt.check.dto.CheckResultDto;
import com.atlassian.cmpt.check.dto.Status;
import com.atlassian.cmpt.check.mapper.AbstractMapper;
import com.atlassian.migration.agent.service.check.app.license.AppLicenseChecker;
import com.atlassian.migration.agent.service.check.app.license.AppLicenseDto;
import java.util.List;

public class AppLicenseMapper
extends AbstractMapper {
    private static final String SUCCESS_DESCRIPTION = "All apps meet cloud license check requirements";
    private static final String RUNNING_DESCRIPTION = "Checking cloud license of all apps";
    private static final String EXECUTION_ERROR_DESCRIPTION = "We couldn't complete the cloud license check for apps";
    private static final String ERROR_OR_WARNING_DESCRIPTION = "Some apps need a cloud license update";
    private static final String LONG_DESCRIPTION = "Select Manage app license below to update the cloud license for the following apps.";

    protected void inject(CheckResultDto dto, CheckResult checkResult) {
        List<AppLicenseDto> appsWithInactiveLicense;
        switch (dto.getStatus()) {
            case SUCCESS: {
                dto.setDescription(SUCCESS_DESCRIPTION);
                break;
            }
            case WARNING: 
            case ERROR: {
                dto.setDescription(ERROR_OR_WARNING_DESCRIPTION);
                dto.setStatus(Status.WARNING);
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
        if (checkResult != null && !(appsWithInactiveLicense = AppLicenseChecker.retrieveAppsNoLicenseViolations(checkResult.details)).isEmpty()) {
            CheckDetailsDto details = new CheckDetailsDto();
            dto.setNumberOfOccurrences(Integer.valueOf(appsWithInactiveLicense.size()));
            details.setLongDescription(LONG_DESCRIPTION);
            details.setListOfOccurrences(appsWithInactiveLicense);
            dto.setDetails(details);
        }
    }
}

