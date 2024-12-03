/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.plugins.recentlyviewed.RecentlyViewedManager
 *  com.fasterxml.jackson.annotation.JsonProperty
 *  javax.persistence.Tuple
 *  lombok.Generated
 */
package com.atlassian.migration.agent.store.guardrails.results;

import com.atlassian.confluence.plugins.recentlyviewed.RecentlyViewedManager;
import com.atlassian.migration.agent.store.guardrails.GrResult;
import com.atlassian.migration.agent.store.guardrails.GuardrailsTupleElement;
import com.atlassian.migration.agent.store.guardrails.GuardrailsUtil;
import com.atlassian.migration.agent.store.guardrails.L1AssessmentResult;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import javax.persistence.Tuple;
import lombok.Generated;

public class TablesPerPageQueryResult
implements GrResult,
L1AssessmentResult {
    private static final int TOPK = 50;
    @JsonProperty
    private List<Tuple> results;

    public TablesPerPageQueryResult(List<Tuple> results, RecentlyViewedManager recentlyViewedManager) {
        this.results = results == null ? new ArrayList() : results;
        this.results.sort((t1, t2) -> (Integer)t2.get(1, Integer.class) - (Integer)t1.get(1, Integer.class));
        int size = this.results.size();
        this.results = IntStream.range(0, size).mapToObj(i -> {
            Tuple tuple = this.results.get(i);
            long pageId = (Long)tuple.get(0, Long.class);
            if (i < 50) {
                int numberOfViews = ((Collection)recentlyViewedManager.getRecentViewers(Collections.singleton(pageId)).getOrDefault(pageId, Collections.emptyList())).size();
                return GuardrailsUtil.getTuple(new GuardrailsTupleElement<Long>("page_id", pageId, Long.class), new GuardrailsTupleElement<Integer>("count", (Integer)tuple.get(1, Integer.class), Integer.class), new GuardrailsTupleElement<Integer>("number_of_views", numberOfViews, Integer.class), new GuardrailsTupleElement<Integer>("page_edit_frequency", (Integer)tuple.get(2, Integer.class), Integer.class));
            }
            return GuardrailsUtil.getTuple(new GuardrailsTupleElement<Long>("page_id", pageId, Long.class), new GuardrailsTupleElement<Integer>("count", (Integer)tuple.get(1, Integer.class), Integer.class), new GuardrailsTupleElement<Integer>("number_of_views", 0, Integer.class), new GuardrailsTupleElement<Integer>("page_edit_frequency", (Integer)tuple.get(2, Integer.class), Integer.class));
        }).collect(Collectors.toList());
    }

    @Override
    public String generateGrResult() {
        return GuardrailsUtil.formatPerPageDistributionResult(this.results, 50);
    }

    @Override
    public String generateL1AssessmentData() {
        return GuardrailsUtil.formatPerPageDistributionResult(this.results, 50);
    }

    @Generated
    public List<Tuple> getResults() {
        return this.results;
    }

    @JsonProperty
    @Generated
    public void setResults(List<Tuple> results) {
        this.results = results;
    }
}

