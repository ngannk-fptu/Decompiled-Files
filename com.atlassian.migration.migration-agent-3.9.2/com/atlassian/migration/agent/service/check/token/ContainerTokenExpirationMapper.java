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
package com.atlassian.migration.agent.service.check.token;

import com.atlassian.cmpt.check.base.CheckResult;
import com.atlassian.cmpt.check.dto.CheckDetailsDto;
import com.atlassian.cmpt.check.dto.CheckResultDto;
import com.atlassian.cmpt.check.dto.Status;
import com.atlassian.cmpt.check.mapper.AbstractMapper;

public class ContainerTokenExpirationMapper
extends AbstractMapper {
    public static final String STATUS_SUCCESS_DESCRIPTION = "Your cloud token is valid";
    public static final String STATUS_RUNNING_DESCRIPTION = "Checking if your cloud token is valid";
    public static final String STATUS_WARNING_DESCRIPTION = "Your cloud token is about to expire";
    public static final String STATUS_ERROR_DESCRIPTION = "Your cloud token has expired";
    public static final String STATUS_EXEC_ERROR_DESCRIPTION = "We couldn't check if your cloud token is valid";

    protected void inject(CheckResultDto dto, CheckResult checkResult) {
        String description;
        if (checkResult != null && checkResult.details != null) {
            CheckDetailsDto details = new CheckDetailsDto();
            details.setInfo(checkResult.details);
            dto.setDetails(details);
            try {
                if (checkResult.details.containsKey("status")) {
                    dto.setStatus((Status)checkResult.details.get("status"));
                }
            }
            catch (Exception e) {
                throw new IllegalArgumentException("Unsupported status ", e);
            }
        }
        Status status = dto.getStatus();
        switch (status) {
            case SUCCESS: {
                description = STATUS_SUCCESS_DESCRIPTION;
                break;
            }
            case WARNING: {
                description = STATUS_WARNING_DESCRIPTION;
                break;
            }
            case ERROR: {
                description = STATUS_ERROR_DESCRIPTION;
                break;
            }
            case RUNNING: {
                description = STATUS_RUNNING_DESCRIPTION;
                break;
            }
            case EXECUTION_ERROR: {
                description = STATUS_EXEC_ERROR_DESCRIPTION;
                break;
            }
            default: {
                throw new IllegalArgumentException("Unsupported status.");
            }
        }
        dto.setDescription(description);
    }
}

