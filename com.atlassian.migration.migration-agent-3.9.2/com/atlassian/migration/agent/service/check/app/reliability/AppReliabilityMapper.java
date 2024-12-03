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
package com.atlassian.migration.agent.service.check.app.reliability;

import com.atlassian.cmpt.check.base.CheckResult;
import com.atlassian.cmpt.check.dto.CheckDetailsDto;
import com.atlassian.cmpt.check.dto.CheckResultDto;
import com.atlassian.cmpt.check.dto.Status;
import com.atlassian.cmpt.check.mapper.AbstractMapper;
import com.atlassian.migration.agent.service.check.CheckType;
import com.atlassian.migration.agent.service.check.app.reliability.NotReliableApp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AppReliabilityMapper
extends AbstractMapper {
    private static final String APPS_RELIABILITY_CHECK_WARNING_MESSAGE = "Some apps selected for this migration have low or unknown migration success rates";
    private static final String APPS_RELIABILITY_CHECK_SUCCESS_MESSAGE = "All apps apps selected for this migration meet migration success rate criteria";
    private static final String APPS_RELIABILITY_CHECK_RUNNING_MESSAGE = "Checking for app migration success rate";
    private static final String APPS_RELIABILITY_CHECK_ERROR_MESSAGE = "We couldn't check for app migration success rate";

    public void inject(CheckResultDto dto, CheckResult checkResult) {
        dto.setCheckType(CheckType.APP_RELIABILITY.value());
        if (checkResult == null) {
            dto.setStatus(Status.RUNNING);
            dto.setDescription(APPS_RELIABILITY_CHECK_RUNNING_MESSAGE);
            return;
        }
        if (checkResult.success) {
            this.setSuccess(dto, Status.SUCCESS, APPS_RELIABILITY_CHECK_SUCCESS_MESSAGE);
            return;
        }
        List<NotReliableApp> occurrences = this.getOccurrences(checkResult);
        if (!occurrences.isEmpty()) {
            this.setPreflightCheckWarning(dto, occurrences);
            return;
        }
        this.setPreflightCheckError(dto);
    }

    private void setSuccess(CheckResultDto dto, Status success, String appsReliabilityCheckSuccessMessage) {
        dto.setStatus(success);
        dto.setDescription(appsReliabilityCheckSuccessMessage);
        CheckDetailsDto details = new CheckDetailsDto();
        details.setListOfOccurrences(Collections.emptyList());
        dto.setNumberOfOccurrences(Integer.valueOf(0));
        dto.setDetails(details);
    }

    private void setPreflightCheckWarning(CheckResultDto checkResultDto, List<NotReliableApp> occurrences) {
        checkResultDto.setNumberOfOccurrences(Integer.valueOf(occurrences.size()));
        checkResultDto.setStatus(Status.WARNING);
        checkResultDto.setDescription(APPS_RELIABILITY_CHECK_WARNING_MESSAGE);
        CheckDetailsDto details = new CheckDetailsDto();
        details.setListOfOccurrences(occurrences);
        details.setLongDescription(APPS_RELIABILITY_CHECK_WARNING_MESSAGE);
        checkResultDto.setDetails(details);
    }

    private void setPreflightCheckError(CheckResultDto dto) {
        dto.setStatus(Status.WARNING);
        dto.setDescription(APPS_RELIABILITY_CHECK_ERROR_MESSAGE);
        CheckDetailsDto details = new CheckDetailsDto();
        details.setListOfOccurrences(Collections.emptyList());
        dto.setNumberOfOccurrences(Integer.valueOf(0));
        dto.setDetails(details);
    }

    private List<NotReliableApp> getOccurrences(CheckResult checkResult) {
        ArrayList<NotReliableApp> result = new ArrayList<NotReliableApp>();
        Set listOfOccurrences = checkResult.details.getOrDefault("listOfOccurrences", new HashSet());
        listOfOccurrences.stream().forEach(result::add);
        return result;
    }
}

