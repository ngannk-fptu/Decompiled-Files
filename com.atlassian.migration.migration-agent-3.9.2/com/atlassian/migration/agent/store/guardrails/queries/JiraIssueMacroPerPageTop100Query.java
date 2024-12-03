/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.Tuple
 */
package com.atlassian.migration.agent.store.guardrails.queries;

import com.atlassian.migration.agent.store.guardrails.GrQuery;
import com.atlassian.migration.agent.store.guardrails.L1AssessmentQuery;
import com.atlassian.migration.agent.store.guardrails.QueryIds;
import com.atlassian.migration.agent.store.guardrails.results.JiraIssueMacroPerPageTopKQueryResult;
import com.atlassian.migration.agent.store.jpa.EntityManagerTemplate;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import javax.persistence.Tuple;

public class JiraIssueMacroPerPageTop100Query
implements GrQuery<JiraIssueMacroPerPageTopKQueryResult>,
L1AssessmentQuery<JiraIssueMacroPerPageTopKQueryResult> {
    private static final String CONTENT_TYPE = "contentTypes";
    private static final String MACRO_PREFIX = "'macro-count.jira%'";
    private final EntityManagerTemplate tmpl;

    public JiraIssueMacroPerPageTop100Query(EntityManagerTemplate tmpl) {
        this.tmpl = tmpl;
    }

    @Override
    public String getQueryId() {
        return QueryIds.JIRA_ISSUE_MACRO_TOP_100.name();
    }

    @Override
    public JiraIssueMacroPerPageTopKQueryResult execute() {
        String query = "select c.id as page_id, c.status as page_status, cp.name as content_property_name, cp.stringval as content_property_value from Content as c inner join ContentProperty as cp on c.id=cp.content.id where c.type in :contentTypes and c.status in ('current', 'draft') and c.previousVersion is null and cp.name like 'macro-count.jira%'";
        List<Tuple> result = this.tmpl.query(Tuple.class, query).param(CONTENT_TYPE, new HashSet<String>(Arrays.asList("PAGE", "BLOGPOST"))).list();
        return new JiraIssueMacroPerPageTopKQueryResult(result, 100);
    }
}

