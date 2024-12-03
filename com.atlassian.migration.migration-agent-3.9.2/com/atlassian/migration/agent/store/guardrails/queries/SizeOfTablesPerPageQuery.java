/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.plugins.recentlyviewed.RecentlyViewedManager
 *  javax.persistence.Tuple
 *  org.slf4j.Logger
 */
package com.atlassian.migration.agent.store.guardrails.queries;

import com.atlassian.confluence.plugins.recentlyviewed.RecentlyViewedManager;
import com.atlassian.migration.agent.logging.ContextLoggerFactory;
import com.atlassian.migration.agent.newexport.DbType;
import com.atlassian.migration.agent.store.guardrails.GrQuery;
import com.atlassian.migration.agent.store.guardrails.GuardrailsUtil;
import com.atlassian.migration.agent.store.guardrails.L1AssessmentQuery;
import com.atlassian.migration.agent.store.guardrails.QueryIds;
import com.atlassian.migration.agent.store.guardrails.queries.GuardrailsQueries;
import com.atlassian.migration.agent.store.guardrails.results.TablesPerPageQueryResult;
import com.atlassian.migration.agent.store.jpa.EntityManagerTemplate;
import com.atlassian.migration.agent.store.jpa.impl.DialectResolver;
import java.util.List;
import javax.persistence.Tuple;
import org.slf4j.Logger;

public class SizeOfTablesPerPageQuery
implements GrQuery<TablesPerPageQueryResult>,
L1AssessmentQuery<TablesPerPageQueryResult> {
    private static final Logger log = ContextLoggerFactory.getLogger(SizeOfTablesPerPageQuery.class);
    private final DialectResolver dialectResolver;
    private final EntityManagerTemplate tmpl;
    private final RecentlyViewedManager recentlyViewedManager;

    public SizeOfTablesPerPageQuery(EntityManagerTemplate tmpl, RecentlyViewedManager recentlyViewedManager, DialectResolver dialectResolver) {
        this.tmpl = tmpl;
        this.recentlyViewedManager = recentlyViewedManager;
        this.dialectResolver = dialectResolver;
    }

    @Override
    public String getQueryId() {
        return QueryIds.SIZE_OF_TABLES_PER_PAGE.name();
    }

    @Override
    public TablesPerPageQueryResult execute() {
        String query = this.getSizeOfTablesQuery();
        List<Tuple> result = DbType.MSSQL == this.dialectResolver.getDbType() || DbType.ORACLE == this.dialectResolver.getDbType() ? GuardrailsUtil.formatResult(this.tmpl.nativeQuery(Tuple.class, query).list()) : this.tmpl.query(Tuple.class, query).list();
        return new TablesPerPageQueryResult(result, this.recentlyViewedManager);
    }

    String getSizeOfTablesQuery() {
        try {
            DbType dbType = this.dialectResolver.getDbType();
            log.info("Resolved hibernate dialect: {}", (Object)dbType);
            return GuardrailsQueries.SIZE_OF_TABLES_PER_PAGE_QUERY.query(dbType);
        }
        catch (Exception e) {
            log.debug("{} query failed to retrieve dialect, exception {}", (Object)QueryIds.SIZE_OF_TABLES_PER_PAGE.name(), (Object)e);
            return GuardrailsQueries.SIZE_OF_TABLES_PER_PAGE_QUERY.query();
        }
    }
}

