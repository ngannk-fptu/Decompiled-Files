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
import java.math.BigDecimal;
import org.codehaus.jackson.annotate.JsonProperty;

public final class TotalAttachmentsSizeQueryResult
implements GrResult,
L1AssessmentResult {
    @JsonProperty
    private BigDecimal count;

    public TotalAttachmentsSizeQueryResult(BigDecimal count) {
        this.count = count == null ? BigDecimal.ZERO : count;
    }

    @Override
    public String generateGrResult() {
        return GuardrailsUtil.formatDecimal(this.count);
    }

    @Override
    public String generateL1AssessmentData() {
        return GuardrailsUtil.formatDecimal(this.count);
    }
}

