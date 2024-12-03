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
 */
package com.atlassian.migration.agent.service.check.space;

import com.atlassian.cmpt.check.base.CheckResult;
import com.atlassian.cmpt.check.dto.CheckDetailsDto;
import com.atlassian.cmpt.check.dto.CheckResultDto;
import com.atlassian.cmpt.check.dto.LinkDto;
import com.atlassian.cmpt.check.dto.Status;
import com.atlassian.cmpt.check.mapper.AbstractMapper;
import com.atlassian.migration.agent.service.check.space.SpaceAnonymousPermissionChecker;
import java.util.List;
import java.util.stream.Collectors;

public class SpaceAnonymousPermissionMapper
extends AbstractMapper {
    private static final String ERROR_DESCRIPTION = "Space settings will allow public access";
    private static final String SUCCESS_DESCRIPTION = "No space settings allow public access";
    private static final String RUNNING_DESCRIPTION = "Checking for spaces with anonymous permissions";
    private static final String EXECUTION_ERROR_DESCRIPTION = "We couldn\u2019t check for spaces with anonymous permissions";
    public static final String WARNING_DESCRIPTION = "Spaces will be available to the public";
    private final String permissionsUrl;

    public SpaceAnonymousPermissionMapper(String permissionsUrl) {
        this.permissionsUrl = permissionsUrl;
    }

    protected void inject(CheckResultDto dto, CheckResult checkResult) {
        switch (dto.getStatus()) {
            case SUCCESS: {
                dto.setDescription(SUCCESS_DESCRIPTION);
                break;
            }
            case WARNING: 
            case ERROR: {
                dto.setDescription(ERROR_DESCRIPTION);
                CheckDetailsDto details = new CheckDetailsDto();
                details.setLink(new LinkDto("Update the permissions", this.permissionsUrl));
                dto.setDetails(details);
                dto.setNumberOfOccurrences(SpaceAnonymousPermissionChecker.retrieveSpaceWithAnonymousAccessCount(checkResult.details));
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
    }

    public static void changeStatusToWarning(CheckResultDto checkResultDto) {
        checkResultDto.setStatus(Status.WARNING);
        checkResultDto.setDescription(WARNING_DESCRIPTION);
    }

    private List<String> transform(List<String> spaceKeys) {
        return spaceKeys.stream().collect(Collectors.toList());
    }
}

