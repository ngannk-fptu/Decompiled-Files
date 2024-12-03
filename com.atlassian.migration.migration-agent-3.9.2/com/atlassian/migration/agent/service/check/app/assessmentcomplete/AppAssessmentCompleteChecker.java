/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.cmpt.check.base.CheckResult
 *  com.atlassian.cmpt.check.base.Checker
 */
package com.atlassian.migration.agent.service.check.app.assessmentcomplete;

import com.atlassian.cmpt.check.base.CheckResult;
import com.atlassian.cmpt.check.base.Checker;
import com.atlassian.migration.agent.dto.assessment.AppSummaryDto;
import com.atlassian.migration.agent.entity.AppAssessmentUserAttributedStatus;
import com.atlassian.migration.agent.service.check.app.assessmentcomplete.AppAssessmentCompleteContext;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class AppAssessmentCompleteChecker
implements Checker<AppAssessmentCompleteContext> {
    private static final String VIOLATIONS_KEY = "violations";

    public CheckResult check(AppAssessmentCompleteContext ctx) {
        List appsWithIncompleteAssessment = ctx.apps.stream().filter(app -> app.getMigrationStatus().equals((Object)AppAssessmentUserAttributedStatus.Unassigned)).map(AppSummaryDto::getKey).collect(Collectors.toList());
        return new CheckResult(appsWithIncompleteAssessment.isEmpty(), Collections.singletonMap(VIOLATIONS_KEY, appsWithIncompleteAssessment));
    }

    public static List<String> retrieveAppsWithIncompleteAssessment(Map<String, Object> details) {
        return details.getOrDefault(VIOLATIONS_KEY, Collections.emptyList());
    }
}

