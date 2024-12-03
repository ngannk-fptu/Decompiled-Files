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

public class PersonalSpacesForInactiveUsersQueryResult
implements GrResult,
L1AssessmentResult {
    @JsonProperty
    private List<Tuple> inactiveSpaces = new ArrayList<Tuple>();

    public PersonalSpacesForInactiveUsersQueryResult(List<Tuple> dbResult) {
        if (dbResult != null) {
            this.inactiveSpaces = dbResult;
        }
    }

    public List<Tuple> getPersonalSpaceForInactiveUsers() {
        return this.inactiveSpaces;
    }

    @Override
    public String generateGrResult() {
        return GuardrailsUtil.formatTupleListToJson(this.inactiveSpaces);
    }

    @Override
    public String generateL1AssessmentData() {
        return GuardrailsUtil.formatTupleListToJson(this.inactiveSpaces);
    }
}

