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
import java.util.List;
import javax.persistence.Tuple;
import lombok.Generated;
import org.codehaus.jackson.annotate.JsonProperty;

public class CurrentAttPerPageQueryResult
implements GrResult,
L1AssessmentResult {
    @JsonProperty
    List<Tuple> list;

    public CurrentAttPerPageQueryResult(List<Tuple> results) {
        this.list = results;
    }

    @Override
    public String generateGrResult() {
        return GuardrailsUtil.formatTupleListToJson(this.list);
    }

    @Override
    public String generateL1AssessmentData() {
        return GuardrailsUtil.formatTupleListToJson(this.list);
    }

    @Generated
    public List<Tuple> getList() {
        return this.list;
    }

    @Generated
    public void setList(List<Tuple> list) {
        this.list = list;
    }
}

