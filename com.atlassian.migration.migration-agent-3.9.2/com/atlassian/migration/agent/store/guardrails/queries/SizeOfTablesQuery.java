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
import com.atlassian.migration.agent.store.guardrails.results.SizeOfTablesQueryResult;
import com.atlassian.migration.agent.store.jpa.EntityManagerTemplate;
import com.atlassian.migration.agent.store.jpa.impl.DialectResolver;
import java.util.List;
import javax.persistence.Tuple;
import org.slf4j.Logger;

public class SizeOfTablesQuery
implements GrQuery<SizeOfTablesQueryResult>,
L1AssessmentQuery<SizeOfTablesQueryResult> {
    private static final Logger log = ContextLoggerFactory.getLogger(SizeOfTablesQuery.class);
    private static final int LIMIT = 100;
    private final EntityManagerTemplate tmpl;
    private final DialectResolver dialectResolver;

    public SizeOfTablesQuery(EntityManagerTemplate tmpl, DialectResolver dialectResolver) {
        this.tmpl = tmpl;
        this.dialectResolver = dialectResolver;
    }

    @Override
    public String getQueryId() {
        return QueryIds.SIZE_OF_TABLES.name();
    }

    @Override
    public SizeOfTablesQueryResult execute() {
        String query = this.getSizeOfTablesQuery();
        List<Tuple> result = this.tmpl.nativeQuery(Tuple.class, query).list();
        return new SizeOfTablesQueryResult(result);
    }

    String getSizeOfTablesQuery() {
        try {
            DbType dbType = this.dialectResolver.getDbType();
            log.info("Resolved hibernate dialect: {}", (Object)dbType);
            return GuardrailsQueries.SIZE_OF_TABLES_QUERY.query(dbType);
        }
        catch (Exception e) {
            log.debug("{} query failed to retrieve dialect, exception {}", (Object)QueryIds.SIZE_OF_TABLES.name(), (Object)e);
            return GuardrailsQueries.SIZE_OF_TABLES_QUERY.query();
        }
    }
}

