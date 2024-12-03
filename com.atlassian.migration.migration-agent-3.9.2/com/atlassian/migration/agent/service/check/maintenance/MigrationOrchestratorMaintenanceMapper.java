/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.cmpt.check.base.CheckResult
 *  com.atlassian.cmpt.check.dto.CheckResultDto
 *  com.atlassian.cmpt.check.mapper.AbstractMapper
 */
package com.atlassian.migration.agent.service.check.maintenance;

import com.atlassian.cmpt.check.base.CheckResult;
import com.atlassian.cmpt.check.dto.CheckResultDto;
import com.atlassian.cmpt.check.mapper.AbstractMapper;

public class MigrationOrchestratorMaintenanceMapper
extends AbstractMapper {
    public static final String SUCCESS_DESCRIPTION = "The migration service is available";
    public static final String ERROR_DESCRIPTION = "We are currently updating the migration service";
    public static final String RUNNING_DESCRIPTION = "Checking if the migration service is available";
    public static final String EXEC_ERROR_DESCRIPTION = "We couldn't check for the availability of the migration service";

    protected void inject(CheckResultDto dto, CheckResult checkResult) {
        String description;
        switch (dto.getStatus()) {
            case RUNNING: {
                description = RUNNING_DESCRIPTION;
                break;
            }
            case SUCCESS: {
                description = SUCCESS_DESCRIPTION;
                break;
            }
            case WARNING: 
            case ERROR: {
                description = ERROR_DESCRIPTION;
                break;
            }
            case EXECUTION_ERROR: {
                description = EXEC_ERROR_DESCRIPTION;
                break;
            }
            default: {
                throw new IllegalArgumentException("Unsupported status.");
            }
        }
        dto.setDescription(description);
    }
}

