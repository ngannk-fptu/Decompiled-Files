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
package com.atlassian.migration.agent.service.check.network;

import com.atlassian.cmpt.check.base.CheckResult;
import com.atlassian.cmpt.check.dto.CheckDetailsDto;
import com.atlassian.cmpt.check.dto.CheckResultDto;
import com.atlassian.cmpt.check.dto.Status;
import com.atlassian.cmpt.check.mapper.AbstractMapper;
import com.atlassian.migration.agent.service.check.network.NetworkCheckResult;
import com.atlassian.migration.agent.service.check.network.NetworkHealthChecker;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class NetworkHealthMapper
extends AbstractMapper {
    public static final String RUNNING_DESCRIPTION = "Checking for authorization of URLs required for migration";
    public static final String EXECUTION_ERROR_DESCRIPTION = "We couldn\u2019t check for authorization of URLs required for migration";
    public static final String WARNING_ERROR_DESCRIPTION = "Some required URLs aren't authorized for migration";
    public static final String SUCCESS_DESCRIPTION = "All URLs required for migration are authorized";

    protected void inject(CheckResultDto dto, CheckResult checkResult) {
        switch (dto.getStatus()) {
            case RUNNING: {
                dto.setDescription(RUNNING_DESCRIPTION);
                break;
            }
            case EXECUTION_ERROR: {
                dto.setDescription(EXECUTION_ERROR_DESCRIPTION);
                break;
            }
            case ERROR: 
            case WARNING: {
                dto.setDescription(WARNING_ERROR_DESCRIPTION);
                List violations = NetworkHealthChecker.retrieveFailedNetworkHealthUrls(checkResult.details).stream().map(NetworkCheckResult::getFailedDomains).flatMap(Collection::stream).collect(Collectors.toList());
                CheckDetailsDto details = new CheckDetailsDto();
                details.setListOfOccurrences(violations);
                dto.setDetails(details);
                dto.setNumberOfOccurrences(Integer.valueOf(violations.size()));
                dto.setStatus(Status.WARNING);
                break;
            }
            case SUCCESS: {
                dto.setDescription(SUCCESS_DESCRIPTION);
                break;
            }
            default: {
                throw new IllegalArgumentException("Unsupported status.");
            }
        }
    }
}

