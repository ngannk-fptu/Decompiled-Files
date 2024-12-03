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
import java.util.stream.Collectors;
import javax.persistence.Tuple;
import org.codehaus.jackson.annotate.JsonProperty;

public final class MembershipPerGroupOverLimitQueryResult
implements GrResult,
L1AssessmentResult {
    private static final int GROUP_SIZE_LIMIT = 35000;
    private static final int LIMIT = 100;
    @JsonProperty
    private List<Tuple> groups;

    public MembershipPerGroupOverLimitQueryResult(List<Tuple> groups) {
        this.groups = new ArrayList<Tuple>(this.populateGroupsOverGroupSizeLimit(groups, 100));
    }

    protected List<Tuple> populateGroupsOverGroupSizeLimit(List<Tuple> dbResults, int numberOfResultsToReturn) {
        if (!dbResults.isEmpty()) {
            if ((Long)dbResults.get(0).get("membership_count") > 35000L) {
                return dbResults.stream().filter(i -> (Long)i.get("membership_count") >= 35000L).collect(Collectors.toList());
            }
            return dbResults.subList(0, Math.min(dbResults.size(), numberOfResultsToReturn));
        }
        return dbResults;
    }

    @Override
    public String generateGrResult() {
        return GuardrailsUtil.formatTupleListToJson(this.groups);
    }

    @Override
    public String generateL1AssessmentData() {
        return GuardrailsUtil.formatTupleListToJson(this.groups);
    }
}

