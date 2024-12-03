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
package com.atlassian.migration.agent.service.check.app.teamcalendars;

import com.atlassian.cmpt.check.base.CheckResult;
import com.atlassian.cmpt.check.dto.CheckDetailsDto;
import com.atlassian.cmpt.check.dto.CheckResultDto;
import com.atlassian.cmpt.check.dto.LinkDto;
import com.atlassian.cmpt.check.dto.Status;
import com.atlassian.cmpt.check.mapper.AbstractMapper;
import com.atlassian.migration.agent.service.check.app.teamcalendars.TcVersionChecker;
import java.util.HashMap;

public class TcVersionMapper
extends AbstractMapper {
    private final String upmLink;
    public static final String STATUS_SUCCESS_DESCRIPTION = "Your Team Calendars version is up to date";
    public static final String STATUS_RUNNING_DESCRIPTION = "Checking version of Team Calendars";
    public static final String STATUS_FAILURE_DESCRIPTION = "Your Team calendars is not up to date";
    public static final String STATUS_EXEC_ERROR_DESCRIPTION = "We couldn\u2019t check Team Calendars version";

    public TcVersionMapper(String upmLink) {
        this.upmLink = upmLink;
    }

    protected void inject(CheckResultDto dto, CheckResult checkResult) {
        String description;
        if (checkResult != null && checkResult.details != null) {
            try {
                String appVersion = TcVersionChecker.retrieveTcAppVersion(checkResult.details);
                if (appVersion != null && !checkResult.success) {
                    CheckDetailsDto details = new CheckDetailsDto();
                    HashMap<String, String> infoMap = new HashMap<String, String>();
                    infoMap.put("appVersion", appVersion);
                    details.setInfo(infoMap);
                    details.setLink(new LinkDto("Update now", this.upmLink));
                    dto.setDetails(details);
                }
            }
            catch (Exception e) {
                throw new IllegalArgumentException("Exception while forming dto", e);
            }
        }
        Status status = dto.getStatus();
        switch (status) {
            case SUCCESS: {
                description = STATUS_SUCCESS_DESCRIPTION;
                break;
            }
            case ERROR: {
                description = STATUS_FAILURE_DESCRIPTION;
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

