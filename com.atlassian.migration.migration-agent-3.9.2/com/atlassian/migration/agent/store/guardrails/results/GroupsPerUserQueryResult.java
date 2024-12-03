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

public class GroupsPerUserQueryResult
implements GrResult,
L1AssessmentResult {
    @JsonProperty
    private List<Tuple> groupsPerUserList = new ArrayList<Tuple>();

    public GroupsPerUserQueryResult(List<Tuple> dbResult) {
        if (dbResult != null) {
            this.groupsPerUserList = dbResult;
        }
    }

    public List<Tuple> getGroupsPerUserList() {
        return this.groupsPerUserList;
    }

    @Override
    public String generateGrResult() {
        return GuardrailsUtil.formatTupleListToJson(this.groupsPerUserList);
    }

    @Override
    public String generateL1AssessmentData() {
        return GuardrailsUtil.formatTupleListToJson(this.groupsPerUserList);
    }
}

