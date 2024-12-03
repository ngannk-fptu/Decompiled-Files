/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.migration.agent.service.check.app.assessmentcomplete;

import com.atlassian.migration.agent.service.check.CheckContextProvider;
import com.atlassian.migration.agent.service.check.app.assessmentcomplete.AppAssessmentCompleteContext;
import com.atlassian.migration.agent.service.impl.AppAssessmentFacade;
import java.util.Map;

public class AppAssessmentCompleteContextProvider
implements CheckContextProvider<AppAssessmentCompleteContext> {
    private final AppAssessmentFacade appAssessmentService;

    public AppAssessmentCompleteContextProvider(AppAssessmentFacade appAssessmentService) {
        this.appAssessmentService = appAssessmentService;
    }

    @Override
    public AppAssessmentCompleteContext apply(Map<String, Object> stringObjectMap) {
        return new AppAssessmentCompleteContext(this.appAssessmentService.getPlugins().getApps());
    }
}

