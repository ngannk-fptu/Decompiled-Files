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
import java.util.Optional;
import javax.persistence.Tuple;
import org.codehaus.jackson.annotate.JsonProperty;

public class MaxPageWidthQueryResult
implements GrResult,
L1AssessmentResult {
    @JsonProperty
    private final Tuple idCount;

    public MaxPageWidthQueryResult(Optional<Tuple> idCount) {
        this.idCount = idCount.orElse(GuardrailsUtil.getTuple(new GuardrailsTupleElement<Integer>("page_id", 0, Integer.class), new GuardrailsTupleElement<Integer>("children_count", 0, Integer.class)));
    }

    @Override
    public String generateGrResult() {
        return GuardrailsUtil.formatTupleToJson(this.idCount);
    }

    @Override
    public String generateL1AssessmentData() {
        return GuardrailsUtil.formatTupleToJson(this.idCount);
    }
}

