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
package com.atlassian.migration.agent.service.check.app.consent;

import com.atlassian.cmpt.check.base.CheckResult;
import com.atlassian.cmpt.check.dto.CheckDetailsDto;
import com.atlassian.cmpt.check.dto.CheckResultDto;
import com.atlassian.cmpt.check.dto.Status;
import com.atlassian.cmpt.check.mapper.AbstractMapper;
import com.atlassian.migration.agent.service.check.app.consent.AppDataMigrationConsentChecker;
import com.atlassian.migration.agent.service.check.app.consent.NotConsentedApp;
import java.util.List;

public class AppDataMigrationConsentMapper
extends AbstractMapper {
    public void inject(CheckResultDto dto, CheckResult checkResult) {
        List<NotConsentedApp> appDataConsents;
        String description;
        switch (dto.getStatus()) {
            case SUCCESS: {
                description = "All apps marked as \u2018Needed in cloud\u2019 with automated data migration have consent to migrate";
                break;
            }
            case ERROR: 
            case WARNING: {
                description = "You have not consented to app data migration";
                dto.setStatus(Status.ERROR);
                break;
            }
            case RUNNING: {
                description = "Checking for apps marked as \u2019Needed in cloud\u2019 consented";
                break;
            }
            case EXECUTION_ERROR: {
                description = "We couldn\u2019t check for consented app data migration";
                break;
            }
            default: {
                throw new IllegalArgumentException("Unsupported status.");
            }
        }
        dto.setDescription(description);
        if (checkResult != null && !(appDataConsents = AppDataMigrationConsentChecker.retrieveNotConsentedApps(checkResult.details)).isEmpty()) {
            CheckDetailsDto details = new CheckDetailsDto();
            details.setListOfOccurrences(appDataConsents);
            details.setLongDescription("In order for automated app data migration paths to work, you must first consent to app data migration. If you've already consented, they may be app policy changes that require you to consent again.");
            dto.setDetails(details);
            dto.setNumberOfOccurrences(Integer.valueOf(appDataConsents.size()));
        }
    }
}

