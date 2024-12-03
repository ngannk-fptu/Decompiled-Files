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

public class NumberOfSpacesPerSpaceTypeQueryResult
implements GrResult,
L1AssessmentResult {
    @JsonProperty
    List<Tuple> results;

    public NumberOfSpacesPerSpaceTypeQueryResult(List<Tuple> dbResult) {
        this.results = dbResult != null ? dbResult : new ArrayList();
    }

    @Override
    public String generateGrResult() {
        return GuardrailsUtil.formatTupleListToJson(this.results);
    }

    @Override
    public String generateL1AssessmentData() {
        return GuardrailsUtil.formatTupleListToJson(this.results);
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

