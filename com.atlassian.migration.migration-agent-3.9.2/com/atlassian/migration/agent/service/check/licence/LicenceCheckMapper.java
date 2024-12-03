/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.cmpt.check.base.CheckResult
 *  com.atlassian.cmpt.check.dto.CheckDetailsDto
 *  com.atlassian.cmpt.check.dto.CheckResultDto
 *  com.atlassian.cmpt.check.mapper.AbstractMapper
 */
package com.atlassian.migration.agent.service.check.licence;

import com.atlassian.cmpt.check.base.CheckResult;
import com.atlassian.cmpt.check.dto.CheckDetailsDto;
import com.atlassian.cmpt.check.dto.CheckResultDto;
import com.atlassian.cmpt.check.mapper.AbstractMapper;
import com.atlassian.migration.agent.service.check.licence.LicenceChecker;
import java.util.HashMap;

public class LicenceCheckMapper
extends AbstractMapper {
    public void inject(CheckResultDto dto, CheckResult checkResult) {
        switch (dto.getStatus()) {
            case RUNNING: {
                dto.setDescription("Checking the user limit of your cloud plan");
                return;
            }
            case EXECUTION_ERROR: {
                dto.setDescription("We couldn\u2019t check the user limit of your cloud plan");
                return;
            }
            case SUCCESS: {
                dto.setDescription("Users in your migration plan are within the user limit of your cloud plan");
                return;
            }
            case ERROR: {
                dto.setDescription("User limit exceeded in cloud");
                CheckDetailsDto details = new CheckDetailsDto();
                HashMap<String, Object> info = new HashMap<String, Object>();
                info.put("cloudFreeUsersLimit", LicenceChecker.retrieveUsersLimit(checkResult.details));
                info.put("availableLicenceSeats", LicenceChecker.retrieveAvailableLicenceSeats(checkResult.details));
                info.put("requestedLicenceSeats", LicenceChecker.retrieveRequestedLicenceSeats(checkResult.details));
                info.put("licenceType", LicenceChecker.retrieveLicenceType(checkResult.details));
                details.setInfo(info);
                dto.setDetails(details);
            }
        }
    }
}

