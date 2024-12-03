/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.cmpt.check.base.CheckContext
 */
package com.atlassian.migration.agent.service.check.app.assessmentcomplete;

import com.atlassian.cmpt.check.base.CheckContext;
import com.atlassian.migration.agent.dto.assessment.AppSummaryDto;
import java.util.Collection;

public class AppAssessmentCompleteContext
implements CheckContext {
    public final Collection<AppSummaryDto> apps;

    public AppAssessmentCompleteContext(Collection<AppSummaryDto> apps) {
        this.apps = apps;
    }
}

