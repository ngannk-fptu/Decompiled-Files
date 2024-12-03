/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.Tuple
 *  org.slf4j.Logger
 */
package com.atlassian.migration.agent.store.guardrails.queries;

import com.atlassian.migration.agent.logging.ContextLoggerFactory;
import com.atlassian.migration.agent.store.guardrails.GrQuery;
import com.atlassian.migration.agent.store.guardrails.GuardrailsUtil;
import com.atlassian.migration.agent.store.guardrails.L1AssessmentQuery;
import com.atlassian.migration.agent.store.guardrails.QueryIds;
import com.atlassian.migration.agent.store.guardrails.results.NumberOfMacrosPerPageQueryResult;
import com.atlassian.migration.agent.store.jpa.EntityManagerTemplate;
import com.atlassian.migration.agent.store.jpa.Page;
import java.util.HashMap;
import java.util.Map;
import javax.persistence.Tuple;
import org.slf4j.Logger;

public class NumberOfMacrosPerPageQuery
implements GrQuery<NumberOfMacrosPerPageQueryResult>,
L1AssessmentQuery<NumberOfMacrosPerPageQueryResult> {
    private static final Logger log = ContextLoggerFactory.getLogger(NumberOfMacrosPerPageQuery.class);
    private static final int PAGE_SIZE = 5000;
    private final EntityManagerTemplate tmpl;

    public NumberOfMacrosPerPageQuery(EntityManagerTemplate tmpl) {
        this.tmpl = tmpl;
    }

    @Override
    public String getQueryId() {
        return QueryIds.NUMBER_OF_MACROS_PER_PAGE.name();
    }

    @Override
    public NumberOfMacrosPerPageQueryResult execute() {
        log.info("Starting to retrieve the number of macros per page data...");
        long startTime = System.currentTimeMillis();
        Map<Long, Integer> counts = this.countMacros();
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        log.info("Got all number of macros per page {}", (Object)duration);
        return new NumberOfMacrosPerPageQueryResult(counts);
    }

    private Map<Long, Integer> countMacros() {
        HashMap<Long, Integer> counts = new HashMap<Long, Integer>();
        Page<Tuple> page = this.listMacros();
        while (page.hasContent()) {
            page.getContent().forEach(tuple -> {
                long key = (Long)tuple.get(0, Long.class);
                counts.put(key, counts.getOrDefault(key, 0) + GuardrailsUtil.getMacrosCount((String)tuple.get(2, String.class)));
            });
            page = page.next();
        }
        return counts;
    }

    private Page<Tuple> listMacros() {
        String query = "select cp.content.id as page_id, cp.name as content_property_name, cp.stringval as content_property_value from ContentProperty cp where (cp.name like 'macro-count.style%' or cp.name like 'macro-count.gadget%' or cp.name like 'macro-count.multimedia%' or cp.name like 'macro-count.spaces%' or cp.name like 'macro-count.unmigrated-wiki-markup%' or cp.name like 'macro-count.portfolioforjiraplan%') and (cp.stringval is not null or cp.stringval <> '') and cp.content.id in (select content.id from Content content where content.type in ('PAGE', 'BLOGPOST') and content.status in ('current', 'draft') and content.previousVersion is null) order by cp.content.id";
        return this.tmpl.query(Tuple.class, query).page(5000);
    }
}

