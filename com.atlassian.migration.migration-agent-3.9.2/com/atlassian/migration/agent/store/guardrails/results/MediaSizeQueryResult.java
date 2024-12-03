/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  lombok.Generated
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.migration.agent.store.guardrails.results;

import com.atlassian.migration.agent.store.guardrails.GrResult;
import com.atlassian.migration.agent.store.guardrails.GuardrailsUtil;
import com.atlassian.migration.agent.store.guardrails.L1AssessmentResult;
import java.math.BigDecimal;
import lombok.Generated;
import org.codehaus.jackson.annotate.JsonProperty;

public class MediaSizeQueryResult
implements GrResult,
L1AssessmentResult {
    @JsonProperty
    BigDecimal size;

    public MediaSizeQueryResult(BigDecimal size) {
        this.size = size == null ? BigDecimal.ZERO : size;
    }

    @Override
    public String generateGrResult() {
        return GuardrailsUtil.formatDecimal(this.size);
    }

    @Override
    public String generateL1AssessmentData() {
        return GuardrailsUtil.formatDecimal(this.size);
    }

    @Generated
    public BigDecimal getSize() {
        return this.size;
    }

    @Generated
    public void setSize(BigDecimal size) {
        this.size = size;
    }
}

