/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.cmpt.check.base.CheckResult
 *  com.atlassian.cmpt.check.dto.CheckDetailsDto
 *  com.atlassian.cmpt.check.dto.CheckResultDto
 *  com.atlassian.cmpt.check.dto.LinkDto
 *  com.atlassian.cmpt.check.dto.Status
 *  com.atlassian.cmpt.check.mapper.AbstractMapper
 *  com.google.common.annotations.VisibleForTesting
 */
package com.atlassian.migration.agent.service.check.group;

import com.atlassian.cmpt.check.base.CheckResult;
import com.atlassian.cmpt.check.dto.CheckDetailsDto;
import com.atlassian.cmpt.check.dto.CheckResultDto;
import com.atlassian.cmpt.check.dto.LinkDto;
import com.atlassian.cmpt.check.dto.Status;
import com.atlassian.cmpt.check.mapper.AbstractMapper;
import com.atlassian.migration.agent.service.check.email.DuplicateGroupName;
import com.atlassian.migration.agent.service.check.group.GroupNamesConflictChecker;
import com.google.common.annotations.VisibleForTesting;
import java.util.List;
import java.util.stream.Collectors;

public class GroupNamesConflictMapper
extends AbstractMapper {
    @VisibleForTesting
    public static final String RUNNING_DESCRIPTION = "Checking for conflicting groups in your cloud site";

    public void inject(CheckResultDto dto, CheckResult checkResult) {
        List<String> violations;
        String description;
        switch (dto.getStatus()) {
            case SUCCESS: {
                description = "There are no conflicting group names";
                break;
            }
            case ERROR: 
            case WARNING: {
                description = "Some groups already exist in your cloud site";
                dto.setStatus(Status.WARNING);
                break;
            }
            case RUNNING: {
                description = RUNNING_DESCRIPTION;
                break;
            }
            case EXECUTION_ERROR: {
                description = "We couldn\u2019t check for group conflicts";
                break;
            }
            default: {
                throw new IllegalArgumentException("Unsupported status.");
            }
        }
        dto.setDescription(description);
        if (checkResult != null && !(violations = GroupNamesConflictChecker.retrieveDuplicateGroupNames(checkResult.details)).isEmpty()) {
            List duplicateGroupNames = violations.stream().map(DuplicateGroupName::new).collect(Collectors.toList());
            CheckDetailsDto details = new CheckDetailsDto();
            details.setListOfOccurrences(duplicateGroupNames);
            details.setLongDescription("Users from groups on server will be merged into the groups on cloud with the same name, and will inherit their permissions. You can continue, but if you want to prevent this, make sure all groups across server and cloud have unique names.");
            details.setLink(new LinkDto("Learn more", "https://confluence.atlassian.com/confcloud/cloud-migration-assistant-for-confluence-959303217.html"));
            dto.setDetails(details);
            dto.setNumberOfOccurrences(Integer.valueOf(violations.size()));
        }
    }

    public static void convertToRunning(CheckResultDto resultDto) {
        resultDto.setStatus(Status.RUNNING);
        resultDto.setDescription(RUNNING_DESCRIPTION);
        resultDto.setDetails(null);
    }
}

