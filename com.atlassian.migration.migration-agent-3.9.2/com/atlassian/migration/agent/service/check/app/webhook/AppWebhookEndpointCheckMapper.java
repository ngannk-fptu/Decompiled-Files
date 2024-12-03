/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.cmpt.check.base.CheckResult
 *  com.atlassian.cmpt.check.dto.CheckDetailsDto
 *  com.atlassian.cmpt.check.dto.CheckResultDto
 *  com.atlassian.cmpt.check.dto.Status
 *  com.atlassian.cmpt.check.mapper.AbstractMapper
 *  lombok.Generated
 */
package com.atlassian.migration.agent.service.check.app.webhook;

import com.atlassian.cmpt.check.base.CheckResult;
import com.atlassian.cmpt.check.dto.CheckDetailsDto;
import com.atlassian.cmpt.check.dto.CheckResultDto;
import com.atlassian.cmpt.check.dto.Status;
import com.atlassian.cmpt.check.mapper.AbstractMapper;
import com.atlassian.migration.agent.service.check.app.webhook.AppWebhookEndpointCheckResultDto;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import lombok.Generated;

public class AppWebhookEndpointCheckMapper
extends AbstractMapper {
    private static final String APP_WEBHOOK_CHECK_WARNING_MESSAGE = "Some apps are not registered to receive migration notifications";
    private static final String APP_WEBHOOK_CHECK_SUCCESS_MESSAGE = "All apps are registered to receive migration notifications";
    private static final String APP_WEBHOOK_CHECK_RUNNING_MESSAGE = "Checking apps registration for migration notifications";
    private static final String APP_WEBHOOK_CHECK_EXECUTION_ERROR_MESSAGE = "We couldn't check if apps are registered for migration notifications";

    public void inject(CheckResultDto dto, CheckResult checkResult) {
        switch (dto.getStatus()) {
            case SUCCESS: {
                dto.setDescription(APP_WEBHOOK_CHECK_SUCCESS_MESSAGE);
                break;
            }
            case ERROR: 
            case WARNING: {
                dto.setStatus(Status.WARNING);
                dto.setDescription(APP_WEBHOOK_CHECK_WARNING_MESSAGE);
                break;
            }
            case RUNNING: {
                dto.setDescription(APP_WEBHOOK_CHECK_RUNNING_MESSAGE);
                break;
            }
            case EXECUTION_ERROR: {
                dto.setDescription(APP_WEBHOOK_CHECK_EXECUTION_ERROR_MESSAGE);
            }
        }
        if (checkResult != null) {
            CheckDetailsDto details = new CheckDetailsDto();
            details.setLongDescription(this.retrieveStepsToResolve(checkResult.details));
            dto.setDetails(details);
            Set<AppWebhookEndpointCheckResultDto> appWebhookEndpointCheckResult = this.retrieveAppKeyMissingWebhooks(checkResult.details);
            if (!appWebhookEndpointCheckResult.isEmpty()) {
                dto.setNumberOfOccurrences(Integer.valueOf(appWebhookEndpointCheckResult.size()));
                details.setListOfOccurrences(new ArrayList<AppWebhookEndpointCheckResultDto>(appWebhookEndpointCheckResult));
            }
        }
    }

    Set<AppWebhookEndpointCheckResultDto> retrieveAppKeyMissingWebhooks(Map<String, Object> checkResultDetails) {
        return checkResultDetails.getOrDefault("appKeysMissingWebhooks", Collections.emptySet());
    }

    String retrieveStepsToResolve(Map<String, Object> checkResultDetails) {
        return (String)checkResultDetails.getOrDefault("stepsToResolve", "");
    }

    @Generated
    public AppWebhookEndpointCheckMapper() {
    }
}

