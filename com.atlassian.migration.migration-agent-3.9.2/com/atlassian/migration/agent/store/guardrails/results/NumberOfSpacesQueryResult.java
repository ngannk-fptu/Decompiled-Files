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

public final class NumberOfSpacesQueryResult
implements GrResult,
L1AssessmentResult {
    @JsonProperty
    private List<Tuple> counts;

    public NumberOfSpacesQueryResult(List<Tuple> result) {
        this.counts = result == null ? new ArrayList() : result;
    }

    @Override
    public String generateGrResult() {
        return GuardrailsUtil.formatTupleListToJson(this.counts);
    }

    @Override
    public String generateL1AssessmentData() {
        return GuardrailsUtil.formatTupleListToJson(this.counts);
    }

    @Generated
    public List<Tuple> getCounts() {
        return this.counts;
    }

    @Generated
    public void setCounts(List<Tuple> counts) {
        this.counts = counts;
    }
}

