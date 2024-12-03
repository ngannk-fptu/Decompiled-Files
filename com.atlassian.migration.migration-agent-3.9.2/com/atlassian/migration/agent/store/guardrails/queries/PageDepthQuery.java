/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.Tuple
 *  org.slf4j.Logger
 */
package com.atlassian.migration.agent.store.guardrails.queries;

import com.atlassian.migration.agent.logging.ContextLoggerFactory;
import com.atlassian.migration.agent.newexport.DbType;
import com.atlassian.migration.agent.store.guardrails.GrQuery;
import com.atlassian.migration.agent.store.guardrails.GuardrailsTupleElement;
import com.atlassian.migration.agent.store.guardrails.GuardrailsUtil;
import com.atlassian.migration.agent.store.guardrails.L1AssessmentQuery;
import com.atlassian.migration.agent.store.guardrails.QueryIds;
import com.atlassian.migration.agent.store.guardrails.queries.GuardrailsQueries;
import com.atlassian.migration.agent.store.guardrails.results.PageDepthQueryResult;
import com.atlassian.migration.agent.store.jpa.EntityManagerTemplate;
import com.atlassian.migration.agent.store.jpa.impl.DialectResolver;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.persistence.Tuple;
import org.slf4j.Logger;

public class PageDepthQuery
implements GrQuery<PageDepthQueryResult>,
L1AssessmentQuery<PageDepthQueryResult> {
    private static final Logger log = ContextLoggerFactory.getLogger(PageDepthQuery.class);
    private static final int LIMIT = 100;
    private static final int PAGE_DEPTH_CUTOFF = 200;
    private final EntityManagerTemplate tmpl;
    private final DialectResolver dialectResolver;

    public PageDepthQuery(EntityManagerTemplate tmpl, DialectResolver dialectResolver) {
        this.tmpl = tmpl;
        this.dialectResolver = dialectResolver;
    }

    @Override
    public String getQueryId() {
        return QueryIds.PAGE_TREE_DEPTH.name();
    }

    @Override
    public PageDepthQueryResult execute() {
        String query = this.getPageTreeDepthQueryForCurrentDb();
        List<Tuple> dbResult = null;
        if (DbType.MYSQL == this.dialectResolver.getDbType()) {
            dbResult = this.findPageDepthPerSpace();
            dbResult = dbResult.subList(0, Math.min(100, dbResult.size()));
        } else {
            dbResult = this.tmpl.nativeQuery(Tuple.class, query).max(100).list();
        }
        return new PageDepthQueryResult(dbResult);
    }

    private String getPageTreeDepthQueryForCurrentDb() {
        try {
            DbType dbType = this.dialectResolver.getDbType();
            log.info("Resolved hibernate dialect: {}", (Object)dbType);
            return GuardrailsQueries.PAGE_DEPTH_QUERY.query(dbType);
        }
        catch (Exception e) {
            log.debug("{} query failed to retrieve dialect, exception {}", (Object)QueryIds.PAGE_TREE_DEPTH.name(), (Object)e);
            return GuardrailsQueries.PAGE_DEPTH_QUERY.query();
        }
    }

    private List<Tuple> findPageDepthPerSpace() {
        int depth = 0;
        List<Tuple> result = this.tmpl.nativeQuery(Tuple.class, "select CONTENTID FROM CONTENT WHERE PARENTID is NULL and CONTENT_STATUS = 'current' and PREVVER is NULL and CONTENTTYPE = 'PAGE';").list();
        HashMap<Object, Integer> maxSpaceDepth = new HashMap<Object, Integer>();
        while (!result.isEmpty() || depth > 200) {
            result = this.tmpl.nativeQuery(Tuple.class, GuardrailsQueries.pageDepthChildPageQuery(result)).list();
            ++depth;
            for (Tuple t : result) {
                maxSpaceDepth.put(t.get(1, Number.class), depth);
            }
        }
        ArrayList<Tuple> pageDepthResult = new ArrayList<Tuple>();
        for (Map.Entry e : maxSpaceDepth.entrySet()) {
            pageDepthResult.add(GuardrailsUtil.getTuple(new GuardrailsTupleElement("space_id", e.getKey()), new GuardrailsTupleElement("max_depth", e.getValue())));
        }
        pageDepthResult.sort((t1, t2) -> Long.compare(((Number)t2.get(1)).longValue(), ((Number)t1.get(1)).longValue()));
        return new ArrayList<Tuple>(pageDepthResult);
    }
}

