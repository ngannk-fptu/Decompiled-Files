/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.Tuple
 *  org.codehaus.jackson.annotate.JsonIgnore
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.migration.agent.store.guardrails.results;

import com.atlassian.migration.agent.store.guardrails.GrResult;
import com.atlassian.migration.agent.store.guardrails.GuardrailsTupleElement;
import com.atlassian.migration.agent.store.guardrails.GuardrailsUtil;
import com.atlassian.migration.agent.store.guardrails.L1AssessmentResult;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javax.persistence.Tuple;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;

public class JiraIssueMacroPerPageTopKQueryResult
implements GrResult,
L1AssessmentResult {
    @JsonProperty
    private final List<Tuple> result;
    @JsonIgnore
    private int k;

    public JiraIssueMacroPerPageTopKQueryResult(List<Tuple> result, int k) {
        this.k = k;
        this.result = result == null ? new ArrayList() : this.formatResult(result);
    }

    private List<Tuple> formatResult(List<Tuple> result) {
        return result.stream().map(tuple -> GuardrailsUtil.getTuple(new GuardrailsTupleElement<Long>("page_id", (Long)tuple.get(0, Long.class), Long.class), new GuardrailsTupleElement<String>("page_status", (String)tuple.get(1, String.class), String.class), new GuardrailsTupleElement<String>("content_property_name", (String)tuple.get(2, String.class), String.class), new GuardrailsTupleElement<Integer>("content_property_value", GuardrailsUtil.getMacrosCount((String)tuple.get(3, String.class)), Integer.class))).sorted((o1, o2) -> ((Integer)o2.get(3, Integer.class)).compareTo((Integer)o1.get(3, Integer.class))).limit(this.k).collect(Collectors.toList());
    }

    @Override
    public String generateGrResult() {
        return GuardrailsUtil.formatTupleListToJson(this.result);
    }

    @Override
    public String generateL1AssessmentData() {
        return GuardrailsUtil.formatTupleListToJson(this.result);
    }
}

