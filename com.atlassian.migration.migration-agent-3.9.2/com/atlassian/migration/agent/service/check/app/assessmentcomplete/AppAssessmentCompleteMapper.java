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
package com.atlassian.migration.agent.service.check.app.assessmentcomplete;

import com.atlassian.cmpt.check.base.CheckResult;
import com.atlassian.cmpt.check.dto.CheckDetailsDto;
import com.atlassian.cmpt.check.dto.CheckResultDto;
import com.atlassian.cmpt.check.dto.Status;
import com.atlassian.cmpt.check.mapper.AbstractMapper;
import com.atlassian.migration.agent.service.check.app.assessmentcomplete.AppAssessmentCompleteChecker;
import java.util.List;

public class AppAssessmentCompleteMapper
extends AbstractMapper {
    private static final String SUCCESS_DESCRIPTION = "App assessment is complete";
    private static final String RUNNING_DESCRIPTION = "Checking that app assessment is complete";
    private static final String EXECUTION_ERROR_DESCRIPTION = "We couldn't check that app assessment is complete";
    private static final String WARNING_DESCRIPTION = "App assessment is incomplete";
    private static final String LONG_DESCRIPTION = "You need to complete the app assessment table before your required app data can be migrated to cloud. We will migrate the data from the apps you mark as 'Needed in cloud' if a pathway exists.";

    protected void inject(CheckResultDto dto, CheckResult checkResult) {
        List<String> appsWithIncompleteAssessment;
        switch (dto.getStatus()) {
            case SUCCESS: {
                dto.setDescription(SUCCESS_DESCRIPTION);
                break;
            }
            case WARNING: 
            case ERROR: {
                dto.setDescription(WARNING_DESCRIPTION);
                dto.setStatus(Status.ERROR);
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
        if (checkResult != null && !(appsWithIncompleteAssessment = AppAssessmentCompleteChecker.retrieveAppsWithIncompleteAssessment(checkResult.details)).isEmpty()) {
            dto.setNumberOfOccurrences(Integer.valueOf(appsWithIncompleteAssessment.size()));
            CheckDetailsDto details = new CheckDetailsDto();
            details.setLongDescription(LONG_DESCRIPTION);
            dto.setDetails(details);
        }
    }
}

