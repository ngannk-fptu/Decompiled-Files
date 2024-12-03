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
package com.atlassian.migration.agent.service.check.attachment;

import com.atlassian.cmpt.check.base.CheckResult;
import com.atlassian.cmpt.check.dto.CheckDetailsDto;
import com.atlassian.cmpt.check.dto.CheckResultDto;
import com.atlassian.cmpt.check.dto.Status;
import com.atlassian.cmpt.check.mapper.AbstractMapper;
import com.atlassian.migration.agent.service.check.attachment.MissingAttachmentChecker;

public class MissingAttachmentMapper
extends AbstractMapper {
    private static final String SUCCESS_DESCRIPTION = "No missing attachments";
    private static final String RUNNING_DESCRIPTION = "Checking all attachments";
    private static final String WARNING_DESCRIPTION = "Spaces won\u2019t have these attachments";
    private static final String ERROR_DESCRIPTION = "Attachments missing from the selected spaces";
    private static final String EXECUTION_ERROR_DESCRIPTION = "We couldn\u2019t check for missing attachments";

    public void inject(CheckResultDto dto, CheckResult checkResult) {
        int missingAttachmentCount;
        String description;
        switch (dto.getStatus()) {
            case SUCCESS: {
                description = SUCCESS_DESCRIPTION;
                break;
            }
            case ERROR: 
            case WARNING: {
                description = ERROR_DESCRIPTION;
                dto.setStatus(Status.ERROR);
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
        if (checkResult != null && (missingAttachmentCount = MissingAttachmentChecker.retrieveMissingAttachmentsCount(checkResult.details)) > 0) {
            CheckDetailsDto details = new CheckDetailsDto();
            details.setLongDescription("Long description for missing attachments");
            dto.setDetails(details);
            dto.setNumberOfOccurrences(Integer.valueOf(missingAttachmentCount));
        }
    }

    public static void changeStatusToWarning(CheckResultDto checkResultDto) {
        checkResultDto.setStatus(Status.WARNING);
        checkResultDto.setDescription(WARNING_DESCRIPTION);
    }
}

