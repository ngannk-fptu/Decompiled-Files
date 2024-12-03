/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.migration.agent.store.guardrails.results;

import com.atlassian.migration.agent.store.guardrails.GrResult;
import com.atlassian.migration.agent.store.guardrails.GuardrailsUtil;
import com.atlassian.migration.agent.store.guardrails.L1AssessmentResult;
import java.util.Optional;
import org.codehaus.jackson.annotate.JsonProperty;

public class MaxNumberOfPagesInASpaceQueryResult
implements GrResult,
L1AssessmentResult {
    @JsonProperty
    Long maxNumberOfPagesInSpace;

    public MaxNumberOfPagesInASpaceQueryResult(Optional<Long> maxNumberOfPagesInSpace) {
        this.maxNumberOfPagesInSpace = maxNumberOfPagesInSpace.orElse(0L);
    }

    @Override
    public String generateGrResult() {
        return GuardrailsUtil.formatLong(this.maxNumberOfPagesInSpace);
    }

    @Override
    public String generateL1AssessmentData() {
        return GuardrailsUtil.formatLong(this.maxNumberOfPagesInSpace);
    }
}

