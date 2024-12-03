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

public class TotalAttachmentsPerSpaceQueryResult
implements GrResult,
L1AssessmentResult {
    @JsonProperty
    List<Tuple> result;

    public TotalAttachmentsPerSpaceQueryResult(List<Tuple> result) {
        this.result = result == null ? new ArrayList() : result;
    }

    @Override
    public String generateGrResult() {
        return GuardrailsUtil.formatTupleListToJson(this.result);
    }

    @Override
    public String generateL1AssessmentData() {
        return GuardrailsUtil.formatTupleListToJson(this.result);
    }

    @Generated
    public List<Tuple> getResult() {
        return this.result;
    }

    @Generated
    public void setResult(List<Tuple> result) {
        this.result = result;
    }
}

