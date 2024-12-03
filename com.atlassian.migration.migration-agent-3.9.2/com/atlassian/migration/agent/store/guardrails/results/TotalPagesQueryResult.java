/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.Tuple
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.migration.agent.store.guardrails.results;

import com.atlassian.migration.agent.store.guardrails.GrResult;
import com.atlassian.migration.agent.store.guardrails.GuardrailsUtil;
import com.atlassian.migration.agent.store.guardrails.L1AssessmentResult;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Tuple;
import org.codehaus.jackson.annotate.JsonProperty;

public class TotalPagesQueryResult
implements GrResult,
L1AssessmentResult {
    @JsonProperty
    private List<Tuple> totalPagesPerStatus = new ArrayList<Tuple>();

    public TotalPagesQueryResult(List<Tuple> dbResult) {
        if (dbResult != null) {
            this.totalPagesPerStatus = dbResult;
        }
    }

    public List<Tuple> getTotalPagesPerStatus() {
        return this.totalPagesPerStatus;
    }

    @Override
    public String generateGrResult() {
        return GuardrailsUtil.formatTupleListToJson(this.totalPagesPerStatus);
    }

    @Override
    public String generateL1AssessmentData() {
        return GuardrailsUtil.formatTupleListToJson(this.totalPagesPerStatus);
    }
}

