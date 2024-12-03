/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.sal.api.rdbms.TransactionalExecutor
 *  com.atlassian.sal.api.rdbms.TransactionalExecutorFactory
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.extra.masterdetail.persistence.entities.dao;

import com.atlassian.confluence.extra.masterdetail.persistence.entities.BodyContentQuerier;
import com.atlassian.confluence.extra.masterdetail.persistence.entities.BodyContentTable;
import com.atlassian.confluence.extra.masterdetail.persistence.entities.Tables;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.pocketknife.api.querydsl.DatabaseAccessor;
import com.atlassian.sal.api.rdbms.TransactionalExecutor;
import com.atlassian.sal.api.rdbms.TransactionalExecutorFactory;
import com.querydsl.core.support.QueryBase;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.util.ResultSetAdapter;
import com.querydsl.sql.SQLQuery;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class QueryDSLBodyContentQuerier
implements BodyContentQuerier {
    private static final Logger logger = LoggerFactory.getLogger(QueryDSLBodyContentQuerier.class);
    private final BodyContentTable bodyContentTable = Tables.BODY_CONTENT_TABLE;
    private final DatabaseAccessor databaseAccessor;
    private final TransactionalExecutorFactory transactionalExecutorFactory;

    @Autowired
    public QueryDSLBodyContentQuerier(@ComponentImport TransactionalExecutorFactory transactionalExecutorFactory, DatabaseAccessor databaseAccessor) {
        this.transactionalExecutorFactory = transactionalExecutorFactory;
        this.databaseAccessor = databaseAccessor;
    }

    @Override
    public Map<Long, String> retrieveBodyContentForIds(Collection<Long> contentIds) {
        TransactionalExecutor executor = this.transactionalExecutorFactory.createReadOnly();
        return (Map)executor.execute(dbCon -> this.databaseAccessor.run(connection -> {
            HashMap<Long, String> bodyContents = new HashMap<Long, String>(contentIds.size());
            SQLQuery selectBodyContentForIds = (SQLQuery)((QueryBase)((Object)((SQLQuery)connection.query().from((Expression<?>)this.bodyContentTable)).select(new Expression[]{this.bodyContentTable.BODY, this.bodyContentTable.CONTENT_ID, this.bodyContentTable.BODY_TYPE_ID}))).where((Predicate)this.bodyContentTable.CONTENT_ID.in(contentIds).and(this.bodyContentTable.BODY_TYPE_ID.eq(2)));
            try {
                ResultSetAdapter resultSet = (ResultSetAdapter)selectBodyContentForIds.getResults();
                while (resultSet.next()) {
                    long contentId = resultSet.getLong(2);
                    String bodyContent = resultSet.getString(1);
                    bodyContents.put(contentId, bodyContent);
                }
            }
            catch (SQLException e) {
                throw new RuntimeException(e);
            }
            return bodyContents;
        }));
    }
}

