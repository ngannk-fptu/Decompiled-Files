/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.cmpt.check.base.CheckResult
 *  com.atlassian.cmpt.check.dto.CheckResultDto
 *  com.atlassian.cmpt.check.dto.Status
 *  com.atlassian.cmpt.check.mapper.AbstractMapper
 */
package com.atlassian.migration.agent.service.check.template;

import com.atlassian.cmpt.check.base.CheckResult;
import com.atlassian.cmpt.check.dto.CheckResultDto;
import com.atlassian.cmpt.check.dto.Status;
import com.atlassian.cmpt.check.mapper.AbstractMapper;
import java.util.Map;

public class GlobalDataTemplateConflictMapper
extends AbstractMapper {
    public static final String VIOLATIONS_KEY = "violations";
    public static final String ERROR_DESCRIPTION = "Global templates selected for migration already exist in your cloud site";
    public static final String SUCCESS_DESCRIPTION = "No duplicate global templates found";
    public static final String RUNNING_DESCRIPTION = "Checking for duplicate global templates in your cloud site";
    public static final String EXECUTION_ERROR_DESCRIPTION = "We couldn't check for global templates";
    public static final String WARNING_DESCRIPTION = "Duplicate global templates will be removed from this migration";

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
                dto.setNumberOfOccurrences(GlobalDataTemplateConflictMapper.retrieveConflictingTemplates(checkResult.details));
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
    }

    static Integer retrieveConflictingTemplates(Map<String, Object> details) {
        return Integer.parseInt(details.getOrDefault("violationsCount", 0).toString());
    }

    public static void changeStatusToWarning(CheckResultDto checkResultDto) {
        checkResultDto.setStatus(Status.WARNING);
        checkResultDto.setDescription(WARNING_DESCRIPTION);
    }
}

