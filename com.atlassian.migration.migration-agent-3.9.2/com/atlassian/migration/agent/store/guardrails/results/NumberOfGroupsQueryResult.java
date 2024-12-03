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
import lombok.Generated;
import org.codehaus.jackson.annotate.JsonProperty;

public final class NumberOfGroupsQueryResult
implements GrResult,
L1AssessmentResult {
    @JsonProperty
    private long count;

    public NumberOfGroupsQueryResult(Long count) {
        this.count = count == null ? 0L : count;
    }

    @Override
    public String generateGrResult() {
        return GuardrailsUtil.formatLong(this.count);
    }

    @Override
    public String generateL1AssessmentData() {
        return GuardrailsUtil.formatLong(this.count);
    }

    @Generated
    public long getCount() {
        return this.count;
    }

    @Generated
    public void setCount(long count) {
        this.count = count;
    }
}

