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

public class MaxSpaceUserPermissionQueryResult
implements GrResult,
L1AssessmentResult {
    @JsonProperty
    private final Tuple idCount;

    public MaxSpaceUserPermissionQueryResult(Optional<Tuple> idCount) {
        this.idCount = idCount.orElseGet(() -> GuardrailsUtil.getTuple(new GuardrailsTupleElement<Long>("space_id", 0L, Long.class), new GuardrailsTupleElement<Long>("space_permission_count", 0L, Long.class)));
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

