/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.Tuple
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.migration.agent.store.guardrails.results;

import com.atlassian.migration.agent.store.guardrails.GrResult;
import com.atlassian.migration.agent.store.guardrails.GuardrailsTupleElement;
import com.atlassian.migration.agent.store.guardrails.GuardrailsUtil;
import com.atlassian.migration.agent.store.guardrails.L1AssessmentResult;
import java.math.BigDecimal;
import java.util.Optional;
import javax.persistence.Tuple;
import org.codehaus.jackson.annotate.JsonProperty;

public class MaxSizePersonalSpaceQueryResult
implements GrResult,
L1AssessmentResult {
    @JsonProperty
    private final Tuple maxSize;

    public MaxSizePersonalSpaceQueryResult(Optional<Tuple> maxSize) {
        this.maxSize = maxSize.orElse(GuardrailsUtil.getTuple(new GuardrailsTupleElement<BigDecimal>("space_size", BigDecimal.ZERO, BigDecimal.class), new GuardrailsTupleElement<String>("space_id", "", String.class), new GuardrailsTupleElement<String>("space_status", "", String.class)));
    }

    public Tuple getMaxSizePersonalSpaceQuery() {
        return this.maxSize;
    }

    @Override
    public String generateGrResult() {
        return GuardrailsUtil.formatTupleToJson(this.maxSize);
    }

    @Override
    public String generateL1AssessmentData() {
        return GuardrailsUtil.formatTupleToJson(this.maxSize);
    }
}

