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
import com.atlassian.migration.agent.store.guardrails.GuardrailsUtil;
import com.atlassian.migration.agent.store.guardrails.L1AssessmentQuery;
import com.atlassian.migration.agent.store.guardrails.QueryIds;
import com.atlassian.migration.agent.store.guardrails.queries.GuardrailsQueries;
import com.atlassian.migration.agent.store.guardrails.results.EmbeddedAttPerPageQueryResult;
import com.atlassian.migration.agent.store.jpa.EntityManagerTemplate;
import com.atlassian.migration.agent.store.jpa.impl.DialectResolver;
import java.util.List;
import javax.persistence.Tuple;
import org.slf4j.Logger;

public class EmbeddedAttPerPageQuery
implements GrQuery<EmbeddedAttPerPageQueryResult>,
L1AssessmentQuery<EmbeddedAttPerPageQueryResult> {
    private static final Logger log = ContextLoggerFactory.getLogger(EmbeddedAttPerPageQuery.class);
    private static final int LIMIT = 100;
    private final EntityManagerTemplate tmpl;
    private final DialectResolver dialectResolver;

    public EmbeddedAttPerPageQuery(EntityManagerTemplate tmpl, DialectResolver dialectResolver) {
        this.tmpl = tmpl;
        this.dialectResolver = dialectResolver;
    }

    @Override
    public String getQueryId() {
        return QueryIds.EMBEDDED_ATT_PER_PAGE.name();
    }

    @Override
    public EmbeddedAttPerPageQueryResult execute() {
        String query = this.getEmbeddedAttPerPageQuery();
        List<Tuple> result = this.dialectResolver.getDbType() == DbType.ORACLE ? GuardrailsUtil.formatResult(this.tmpl.nativeQuery(Tuple.class, query).max(100).list()) : this.tmpl.query(Tuple.class, query).max(100).list();
        return new EmbeddedAttPerPageQueryResult(result);
    }

    String getEmbeddedAttPerPageQuery() {
        try {
            DbType dbType = this.dialectResolver.getDbType();
            log.info("Resolved hibernate dialect: {}", (Object)dbType);
            return GuardrailsQueries.EMBEDDED_ATT_PER_PAGE.query(dbType);
        }
        catch (Exception e) {
            log.debug("{} query failed to retrieve dialect, exception {}", (Object)QueryIds.EMBEDDED_ATT_PER_PAGE.name(), (Object)e);
            return GuardrailsQueries.EMBEDDED_ATT_PER_PAGE.query();
        }
    }
}

