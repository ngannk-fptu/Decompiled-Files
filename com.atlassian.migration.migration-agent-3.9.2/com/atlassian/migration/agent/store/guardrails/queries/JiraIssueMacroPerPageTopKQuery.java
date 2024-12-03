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
import java.util.List;
import javax.persistence.Tuple;

public class JiraIssueMacroPerPageTopKQuery
implements GrQuery<JiraIssueMacroPerPageTopKQueryResult>,
L1AssessmentQuery<JiraIssueMacroPerPageTopKQueryResult> {
    private static final int K = 10000;
    private static final String CONTENT_TYPE = "contentType";
    private static final String MACRO_PREFIX = "'macro-count.jira%'";
    private final EntityManagerTemplate tmpl;

    public JiraIssueMacroPerPageTopKQuery(EntityManagerTemplate tmpl) {
        this.tmpl = tmpl;
    }

    @Override
    public String getQueryId() {
        return QueryIds.JIRA_ISSUE_MACRO_TOP_K.name();
    }

    @Override
    public JiraIssueMacroPerPageTopKQueryResult execute() {
        String query = "select c.id as id, c.status as status, cp.name as name, cp.stringval as value from Content as c inner join ContentProperty as cp on c.id=cp.content.id where c.type =:contentType and c.status in ('current', 'draft') and c.previousVersion is null and cp.name like 'macro-count.jira%'";
        List<Tuple> result = this.tmpl.query(Tuple.class, query).param(CONTENT_TYPE, (Object)"PAGE").list();
        return new JiraIssueMacroPerPageTopKQueryResult(result, 10000);
    }
}

