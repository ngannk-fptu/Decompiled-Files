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

public class FindRecentlyUsedSpacesQueryResult
implements GrResult,
L1AssessmentResult {
    @JsonProperty
    private List<Tuple> recentlyUsedSpaces = new ArrayList<Tuple>();

    public FindRecentlyUsedSpacesQueryResult(List<Tuple> dbResult) {
        if (dbResult != null) {
            this.recentlyUsedSpaces = dbResult;
        }
    }

    public List<Tuple> getRecentlyUsedSpaces() {
        return this.recentlyUsedSpaces;
    }

    @Override
    public String generateGrResult() {
        return GuardrailsUtil.formatTupleListToJson(this.recentlyUsedSpaces);
    }

    @Override
    public String generateL1AssessmentData() {
        return GuardrailsUtil.formatTupleListToJson(this.recentlyUsedSpaces);
    }
}

