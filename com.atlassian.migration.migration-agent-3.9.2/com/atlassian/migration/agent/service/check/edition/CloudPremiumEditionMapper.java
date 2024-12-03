/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.VisibleForTesting
 *  com.atlassian.cmpt.check.base.CheckResult
 *  com.atlassian.cmpt.check.dto.CheckDetailsDto
 *  com.atlassian.cmpt.check.dto.CheckResultDto
 *  com.atlassian.cmpt.check.dto.Status
 *  com.atlassian.cmpt.check.mapper.AbstractMapper
 */
package com.atlassian.migration.agent.service.check.edition;

import com.atlassian.annotations.VisibleForTesting;
import com.atlassian.cmpt.check.base.CheckResult;
import com.atlassian.cmpt.check.dto.CheckDetailsDto;
import com.atlassian.cmpt.check.dto.CheckResultDto;
import com.atlassian.cmpt.check.dto.Status;
import com.atlassian.cmpt.check.mapper.AbstractMapper;

public class CloudPremiumEditionMapper
extends AbstractMapper {
    @VisibleForTesting
    static final String SUCCESS_DESCRIPTION = "Your cloud subscription supports Team Calendars";
    @VisibleForTesting
    static final String WARNING_DESCRIPTION = "Your cloud subscription doesn't support Team Calendars";
    @VisibleForTesting
    static final String RUNNING_DESCRIPTION = "Checking subscription on cloud";
    @VisibleForTesting
    static final String EXECUTION_ERROR_DESCRIPTION = "We couldn't check the cloud subscription plan";

    public void inject(CheckResultDto dto, CheckResult checkResult) {
        String description;
        switch (dto.getStatus()) {
            case SUCCESS: {
                description = SUCCESS_DESCRIPTION;
                break;
            }
            case ERROR: 
            case WARNING: {
                description = WARNING_DESCRIPTION;
                dto.setStatus(Status.WARNING);
                break;
            }
            case RUNNING: {
                description = RUNNING_DESCRIPTION;
                break;
            }
            case EXECUTION_ERROR: {
                description = EXECUTION_ERROR_DESCRIPTION;
                break;
            }
            default: {
                throw new IllegalArgumentException("Unsupported status.");
            }
        }
        dto.setDescription(description);
        if (checkResult != null && checkResult.details != null && !checkResult.details.isEmpty()) {
            CheckDetailsDto details = new CheckDetailsDto();
            details.setInfo(checkResult.details);
            dto.setDetails(details);
        }
    }
}

