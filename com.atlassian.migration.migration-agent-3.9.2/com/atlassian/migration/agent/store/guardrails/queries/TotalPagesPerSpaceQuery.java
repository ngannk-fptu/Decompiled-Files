/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.Tuple
 */
package com.atlassian.migration.agent.store.guardrails.queries;

import com.atlassian.migration.agent.store.guardrails.GrQuery;
import com.atlassian.migration.agent.store.guardrails.GuardrailsTupleElement;
import com.atlassian.migration.agent.store.guardrails.GuardrailsUtil;
import com.atlassian.migration.agent.store.guardrails.L1AssessmentQuery;
import com.atlassian.migration.agent.store.guardrails.QueryIds;
import com.atlassian.migration.agent.store.guardrails.results.TotalPagesPerSpaceQueryResult;
import com.atlassian.migration.agent.store.jpa.EntityManagerTemplate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.persistence.Tuple;

public class TotalPagesPerSpaceQuery
implements GrQuery<TotalPagesPerSpaceQueryResult>,
L1AssessmentQuery<TotalPagesPerSpaceQueryResult> {
    private static final int LIMIT = 100;
    public static final String SPACE_KEY = "space_key";
    public static final String SPACE_ID = "space_id";
    public static final String TOTAL_PAGE_COUNT = "total_page_count";
    public static final String PAGE_COUNT = "page_count";
    public static final String STATUS = "status";
    private final EntityManagerTemplate tmpl;

    public TotalPagesPerSpaceQuery(EntityManagerTemplate tmpl) {
        this.tmpl = tmpl;
    }

    @Override
    public String getQueryId() {
        return QueryIds.TOTAL_PAGES_PER_SPACE.name();
    }

    @Override
    public TotalPagesPerSpaceQueryResult execute() {
        String query = "select c.spaceId as space_id, s.key as space_key, count(*) as page_count, c.status as status from Content c inner join Space s on c.spaceId = s.id where c.type in ('PAGE', 'BLOGPOST') and c.previousVersion is null group by c.spaceId, s.key, c.status";
        List<Tuple> result = this.tmpl.query(Tuple.class, query).list();
        Map<Long, List<Tuple>> groupBySpaceId = this.groupResultsBySpaceId(result);
        List<Tuple> flattenedPageCounts = this.flattenPageCounts(groupBySpaceId);
        flattenedPageCounts.sort((t1, t2) -> ((Long)t2.get(TOTAL_PAGE_COUNT)).compareTo((Long)t1.get(TOTAL_PAGE_COUNT)));
        List<Tuple> topPageCounts = flattenedPageCounts.stream().limit(100L).collect(Collectors.toList());
        return new TotalPagesPerSpaceQueryResult(topPageCounts);
    }

    private Map<Long, List<Tuple>> groupResultsBySpaceId(List<Tuple> results) {
        HashMap<Long, List<Tuple>> groupBySpaceId = new HashMap<Long, List<Tuple>>();
        for (Tuple tuple : results) {
            groupBySpaceId.putIfAbsent((Long)tuple.get(SPACE_ID), new ArrayList());
            ((List)groupBySpaceId.get(tuple.get(SPACE_ID))).add(tuple);
        }
        return groupBySpaceId;
    }

    private List<Tuple> flattenPageCounts(Map<Long, List<Tuple>> groupBySpaceId) {
        ArrayList<Tuple> flattenedPages = new ArrayList<Tuple>();
        for (Map.Entry<Long, List<Tuple>> entry : groupBySpaceId.entrySet()) {
            flattenedPages.add(this.formatTupleRow(entry.getValue()));
        }
        return flattenedPages;
    }

    private Tuple formatTupleRow(List<Tuple> tuples) {
        ArrayList<GuardrailsTupleElement<Object>> gte = new ArrayList<GuardrailsTupleElement<Object>>();
        Long pageCount = 0L;
        String spaceKey = null;
        Long spaceId = null;
        for (Tuple t : tuples) {
            if (spaceKey == null && t.get(SPACE_KEY) != null) {
                spaceKey = (String)t.get(SPACE_KEY);
            }
            if (spaceId == null && t.get(SPACE_ID) != null) {
                spaceId = (Long)t.get(SPACE_ID);
            }
            if (t.get(PAGE_COUNT) == null) continue;
            pageCount = pageCount + (Long)t.get(PAGE_COUNT);
            if (t.get(STATUS) == null) continue;
            gte.add(new GuardrailsTupleElement<Object>((String)t.get(STATUS), t.get(PAGE_COUNT)));
        }
        gte.add(new GuardrailsTupleElement<Long>(TOTAL_PAGE_COUNT, pageCount));
        gte.add(new GuardrailsTupleElement<Object>(SPACE_ID, spaceId));
        gte.add(new GuardrailsTupleElement<String>(SPACE_KEY, spaceKey));
        return GuardrailsUtil.getTuple(gte.toArray(new GuardrailsTupleElement[0]));
    }
}

