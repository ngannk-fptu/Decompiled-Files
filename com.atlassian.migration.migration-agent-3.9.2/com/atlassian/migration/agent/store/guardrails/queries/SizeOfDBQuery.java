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
import com.atlassian.migration.agent.store.guardrails.L1AssessmentQuery;
import com.atlassian.migration.agent.store.guardrails.QueryIds;
import com.atlassian.migration.agent.store.guardrails.queries.GuardrailsQueries;
import com.atlassian.migration.agent.store.guardrails.results.SizeOfDBQueryResult;
import com.atlassian.migration.agent.store.jpa.EntityManagerTemplate;
import com.atlassian.migration.agent.store.jpa.impl.DialectResolver;
import java.util.List;
import javax.persistence.Tuple;
import org.slf4j.Logger;

public class SizeOfDBQuery
implements GrQuery<SizeOfDBQueryResult>,
L1AssessmentQuery<SizeOfDBQueryResult> {
    private static final Logger log = ContextLoggerFactory.getLogger(SizeOfDBQuery.class);
    private final EntityManagerTemplate tmpl;
    private final DialectResolver dialectResolver;

    public SizeOfDBQuery(EntityManagerTemplate tmpl, DialectResolver dialectResolver) {
        this.tmpl = tmpl;
        this.dialectResolver = dialectResolver;
    }

    @Override
    public String getQueryId() {
        return QueryIds.SIZE_OF_DB.name();
    }

    @Override
    public SizeOfDBQueryResult execute() {
        String query = this.getSizeOfDBQuery();
        List<Tuple> result = this.tmpl.nativeQuery(Tuple.class, query).list();
        return new SizeOfDBQueryResult(result);
    }

    String getSizeOfDBQuery() {
        try {
            DbType dbType = this.dialectResolver.getDbType();
            log.info("Resolved hibernate dialect: {}", (Object)dbType.name());
            return GuardrailsQueries.SIZE_OF_DB_QUERY.query(dbType);
        }
        catch (Exception e) {
            log.debug("{} query failed to retrieve dialect, exception {}", (Object)QueryIds.SIZE_OF_DB.name(), (Object)e);
            return GuardrailsQueries.SIZE_OF_DB_QUERY.query();
        }
    }
}

