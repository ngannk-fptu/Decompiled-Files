/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.Tuple
 *  lombok.Generated
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.migration.agent.store.guardrails.results;

import com.atlassian.migration.agent.store.guardrails.GrResult;
import com.atlassian.migration.agent.store.guardrails.GuardrailsTupleElement;
import com.atlassian.migration.agent.store.guardrails.GuardrailsUtil;
import com.atlassian.migration.agent.store.guardrails.L1AssessmentResult;
import java.util.Optional;
import javax.persistence.Tuple;
import lombok.Generated;
import org.codehaus.jackson.annotate.JsonProperty;

public final class MaxRestrictionsInPageQueryResult
implements GrResult,
L1AssessmentResult {
    @JsonProperty
    private Tuple counts;

    public MaxRestrictionsInPageQueryResult(Optional<Tuple> result) {
        this.counts = result.orElseGet(() -> GuardrailsUtil.getTuple(new GuardrailsTupleElement<Long>("page_id", 0L, Long.class), new GuardrailsTupleElement<Long>("restriction_count", 0L, Long.class)));
    }

    @Override
    public String generateGrResult() {
        return GuardrailsUtil.formatTupleToJson(this.counts);
    }

    @Override
    public String generateL1AssessmentData() {
        return GuardrailsUtil.formatTupleToJson(this.counts);
    }

    @Generated
    public Tuple getCounts() {
        return this.counts;
    }

    @Generated
    public void setCounts(Tuple counts) {
        this.counts = counts;
    }
}

