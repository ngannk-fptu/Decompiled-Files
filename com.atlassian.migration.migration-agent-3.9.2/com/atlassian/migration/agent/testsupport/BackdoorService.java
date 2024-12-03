/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.migration.agent.testsupport;

import com.atlassian.migration.agent.testsupport.appassessment.AppAssessmentInfoDTO;
import java.util.List;

public interface BackdoorService {
    public void reset();

    public List<AppAssessmentInfoDTO> getAppAssessmentEntries();
}

