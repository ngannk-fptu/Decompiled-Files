/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.cmpt.check.base.CheckResult
 *  com.atlassian.cmpt.check.base.Checker
 *  com.atlassian.cmpt.check.dto.CheckDetailsDto
 *  com.atlassian.cmpt.check.dto.CheckResultDto
 *  com.atlassian.cmpt.check.dto.Status
 *  com.atlassian.cmpt.check.mapper.AbstractMapper
 *  com.atlassian.migration.app.dto.check.CheckStatus
 */
package com.atlassian.migration.agent.service.check.app.vendorcheck;

import com.atlassian.cmpt.check.base.CheckResult;
import com.atlassian.cmpt.check.base.Checker;
import com.atlassian.cmpt.check.dto.CheckDetailsDto;
import com.atlassian.cmpt.check.dto.CheckResultDto;
import com.atlassian.cmpt.check.dto.Status;
import com.atlassian.cmpt.check.mapper.AbstractMapper;
import com.atlassian.migration.app.dto.check.CheckStatus;
import java.util.HashMap;
import java.util.stream.Collectors;

public class AppVendorCheckMapper
extends AbstractMapper {
    private static final String APP_VENDOR_CHECK_SUCCESS_DESCRIPTION = "App vendor checks are complete";
    private static final String APP_VENDOR_CHECK_RUNNING_DESCRIPTION = "Checking that 'App vendor checks' are complete";
    private static final String APP_VENDOR_CHECK_EXECUTION_ERROR_DESCRIPTION = "App vendor checks could not be completed";
    private static final String APP_VENDOR_CHECK_WARNING_DESCRIPTION = "App vendor checks";
    private static final String APP_VENDOR_CHECK_LONG_DESCRIPTION = "App vendors have run checks on the apps you've marked as 'Needed in cloud' while assessing apps. We recommend that you select View all app vendor checks and resolve any warnings before continuing with the migration.";

    protected void inject(CheckResultDto dto, CheckResult checkResult) {
        switch (dto.getStatus()) {
            case SUCCESS: {
                dto.setDescription(APP_VENDOR_CHECK_SUCCESS_DESCRIPTION);
                break;
            }
            case ERROR: 
            case WARNING: {
                dto.setDescription(APP_VENDOR_CHECK_WARNING_DESCRIPTION);
                dto.setStatus(Status.WARNING);
                break;
            }
            case RUNNING: {
                dto.setDescription(APP_VENDOR_CHECK_RUNNING_DESCRIPTION);
                break;
            }
            case EXECUTION_ERROR: {
                dto.setDescription(APP_VENDOR_CHECK_EXECUTION_ERROR_DESCRIPTION);
            }
        }
        if (checkResult != null) {
            dto.setNumberOfOccurrences(Integer.valueOf(this.getUnsuccessfulAppVendorCheckCount(checkResult)));
            CheckDetailsDto details = new CheckDetailsDto();
            details.setLongDescription(APP_VENDOR_CHECK_LONG_DESCRIPTION);
            details.setInfo(checkResult.details);
            dto.setDetails(details);
        }
    }

    private int getUnsuccessfulAppVendorCheckCount(CheckResult checkResult) {
        if (Checker.retrieveExecutionErrorCode((CheckResult)checkResult) != null) {
            return 0;
        }
        return checkResult.details.values().stream().flatMap(checkResultsForAnApp -> ((HashMap)checkResultsForAnApp).values().stream()).filter(it -> it.status != CheckStatus.SUCCESS).collect(Collectors.toList()).size();
    }
}

