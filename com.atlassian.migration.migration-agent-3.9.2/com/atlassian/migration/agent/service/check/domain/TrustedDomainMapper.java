/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.cmpt.check.base.CheckResult
 *  com.atlassian.cmpt.check.dto.CheckDetailsDto
 *  com.atlassian.cmpt.check.dto.CheckResultDto
 *  com.atlassian.cmpt.check.mapper.AbstractMapper
 */
package com.atlassian.migration.agent.service.check.domain;

import com.atlassian.cmpt.check.base.CheckResult;
import com.atlassian.cmpt.check.dto.CheckDetailsDto;
import com.atlassian.cmpt.check.dto.CheckResultDto;
import com.atlassian.cmpt.check.mapper.AbstractMapper;

public class TrustedDomainMapper
extends AbstractMapper {
    private static final String SUCCESS_DESCRIPTION = "You trust all your email domains";
    private static final String RUNNING_DESCRIPTION = "Checking if you've trusted all email domains";
    private static final String ERROR_DESCRIPTION = "You haven't reviewed all your domains";
    private static final String EXECUTION_ERROR_DESCRIPTION = "We couldn't check if you've trusted all email domains";

    protected void inject(CheckResultDto dto, CheckResult checkResult) {
        String description;
        switch (dto.getStatus()) {
            case SUCCESS: {
                description = SUCCESS_DESCRIPTION;
                break;
            }
            case WARNING: 
            case ERROR: {
                description = ERROR_DESCRIPTION;
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
        if (checkResult != null) {
            CheckDetailsDto details = new CheckDetailsDto();
            details.setLongDescription("Long description for trusted domain");
            dto.setDetails(details);
        }
    }
}

