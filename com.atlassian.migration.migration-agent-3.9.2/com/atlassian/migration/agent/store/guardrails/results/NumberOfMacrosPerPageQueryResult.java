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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.persistence.Tuple;
import lombok.Generated;
import org.codehaus.jackson.annotate.JsonProperty;

public class NumberOfMacrosPerPageQueryResult
implements GrResult,
L1AssessmentResult {
    private static final int TOPK = 100;
    @JsonProperty
    private List<Tuple> results = new ArrayList<Tuple>();

    public NumberOfMacrosPerPageQueryResult(Map<Long, Integer> counts) {
        counts.forEach((k, v) -> this.results.add(GuardrailsUtil.getTuple(new GuardrailsTupleElement<Long>("page_id", (Long)k, Long.class), new GuardrailsTupleElement<Integer>("macro_count", (Integer)v, Integer.class))));
        this.results.sort((t1, t2) -> (Integer)t2.get(1, Integer.class) - (Integer)t1.get(1, Integer.class));
    }

    @Override
    public String generateGrResult() {
        return GuardrailsUtil.formatPerPageDistributionResult(this.results, 100);
    }

    @Override
    public String generateL1AssessmentData() {
        return GuardrailsUtil.formatPerPageDistributionResult(this.results, 100);
    }

    @Generated
    public List<Tuple> getResults() {
        return this.results;
    }

    @Generated
    public void setResults(List<Tuple> results) {
        this.results = results;
    }
}

