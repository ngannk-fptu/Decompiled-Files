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
import com.atlassian.migration.agent.store.guardrails.GuardrailsUtil;
import com.atlassian.migration.agent.store.guardrails.L1AssessmentResult;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Tuple;
import lombok.Generated;
import org.codehaus.jackson.annotate.JsonProperty;

public class NumberOfMediaPerPageQueryResult
implements GrResult,
L1AssessmentResult {
    private static final int TOPK = 50;
    @JsonProperty
    List<Tuple> results;

    public NumberOfMediaPerPageQueryResult(List<Tuple> results) {
        this.results = results == null ? new ArrayList() : results;
        this.results.sort((t1, t2) -> (Integer)t2.get(1, Integer.class) - (Integer)t1.get(1, Integer.class));
    }

    @Override
    public String generateGrResult() {
        return GuardrailsUtil.formatPerPageDistributionResult(this.results, 50);
    }

    @Override
    public String generateL1AssessmentData() {
        return GuardrailsUtil.formatPerPageDistributionResult(this.results, 50);
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

