/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.PublicApi
 *  org.apache.commons.lang.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.pocketknife.api.querydsl.util;

import com.atlassian.annotations.PublicApi;
import com.querydsl.core.QueryMetadata;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.SubQueryExpression;
import com.querydsl.sql.Configuration;
import com.querydsl.sql.RelationalPath;
import com.querydsl.sql.SQLListener;
import com.querydsl.sql.SQLSerializer;
import com.querydsl.sql.dml.SQLInsertBatch;
import com.querydsl.sql.dml.SQLMergeBatch;
import com.querydsl.sql.dml.SQLUpdateBatch;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@PublicApi
public class LoggingSqlListener
implements SQLListener {
    private static final Logger log = LoggerFactory.getLogger(LoggingSqlListener.class);
    private final Configuration configuration;

    public LoggingSqlListener(Configuration configuration) {
        this.configuration = configuration;
    }

    @Override
    public void notifyQuery(QueryMetadata md) {
        if (this.weShouldLog()) {
            this.log(this.getSelectSql(md));
        }
    }

    @Override
    public void notifyDelete(RelationalPath<?> entity, QueryMetadata md) {
        if (this.weShouldLog()) {
            this.log(this.getDeleteSql(entity, md));
        }
    }

    @Override
    public void notifyDeletes(RelationalPath<?> entity, List<QueryMetadata> batches) {
        if (this.weShouldLog()) {
            this.log(this.getBatchDeleteSql(entity, batches));
        }
    }

    @Override
    public void notifyMerge(RelationalPath<?> entity, QueryMetadata md, List<Path<?>> keys, List<Path<?>> columns, List<Expression<?>> values, SubQueryExpression<?> subQuery) {
        if (this.weShouldLog()) {
            this.log(this.getMergeSql(entity, md, keys, columns, values, subQuery));
        }
    }

    @Override
    public void notifyMerges(RelationalPath<?> entity, QueryMetadata md, List<SQLMergeBatch> batches) {
        if (this.weShouldLog()) {
            this.log(this.getBatchMergeSql(entity, md, batches));
        }
    }

    @Override
    public void notifyInsert(RelationalPath<?> entity, QueryMetadata md, List<Path<?>> columns, List<Expression<?>> values, SubQueryExpression<?> subQuery) {
        if (this.weShouldLog()) {
            this.log(this.getInsertSql(entity, md, columns, values, subQuery));
        }
    }

    @Override
    public void notifyInserts(RelationalPath<?> entity, QueryMetadata md, List<SQLInsertBatch> batches) {
        if (this.weShouldLog()) {
            this.log(this.getBatchInsertSql(entity, md, batches));
        }
    }

    @Override
    public void notifyUpdate(RelationalPath<?> entity, QueryMetadata md, Map<Path<?>, Expression<?>> updates) {
        if (this.weShouldLog()) {
            this.log(this.getUpdateSql(entity, md, updates));
        }
    }

    @Override
    public void notifyUpdates(RelationalPath<?> entity, List<SQLUpdateBatch> batches) {
        if (this.weShouldLog()) {
            this.log(this.getBatchUpdateSql(entity, batches));
        }
    }

    private boolean weShouldLog() {
        return log.isDebugEnabled();
    }

    private void log(String sql) {
        if (StringUtils.isNotBlank((String)sql)) {
            log.debug(sql);
        }
    }

    private String getSelectSql(QueryMetadata queryMetadata) {
        SQLSerializer serializer = this.newLiteralPrintingSerializer();
        serializer.serialize(queryMetadata, false);
        return serializer.toString();
    }

    private String getDeleteSql(RelationalPath<?> entity, QueryMetadata queryMetadata) {
        SQLSerializer serializer = this.newLiteralPrintingSerializer();
        serializer.serializeDelete(queryMetadata, entity);
        return serializer.toString();
    }

    private String getBatchDeleteSql(RelationalPath<?> entity, List<QueryMetadata> deleteQueriesMetadata) {
        StringBuilder batchDeleteQueries = new StringBuilder();
        batchDeleteQueries.append("<Start batch delete> ");
        for (QueryMetadata queryMetadata : deleteQueriesMetadata) {
            batchDeleteQueries.append(this.getDeleteSql(entity, queryMetadata));
            batchDeleteQueries.append("; ");
        }
        batchDeleteQueries.append(" <End batch delete>");
        return batchDeleteQueries.toString();
    }

    private String getMergeSql(RelationalPath<?> entity, QueryMetadata md, List<Path<?>> keys, List<Path<?>> columns, List<Expression<?>> values, SubQueryExpression<?> subQuery) {
        SQLSerializer serializer = this.newLiteralPrintingSerializer();
        serializer.serializeMerge(md, entity, keys, columns, values, subQuery);
        return serializer.toString();
    }

    private String getBatchMergeSql(RelationalPath<?> entity, QueryMetadata md, List<SQLMergeBatch> mergeBatches) {
        StringBuilder batchMergeQueries = new StringBuilder();
        batchMergeQueries.append("<Start batch merge> ");
        for (SQLMergeBatch mergeBatch : mergeBatches) {
            batchMergeQueries.append(this.getMergeSql(entity, md, mergeBatch.getKeys(), mergeBatch.getColumns(), mergeBatch.getValues(), mergeBatch.getSubQuery()));
            batchMergeQueries.append("; ");
        }
        batchMergeQueries.append(" <End batch merge>");
        return batchMergeQueries.toString();
    }

    private String getInsertSql(RelationalPath<?> entity, QueryMetadata md, List<Path<?>> columns, List<Expression<?>> values, SubQueryExpression<?> subQuery) {
        SQLSerializer serializer = this.newLiteralPrintingSerializer();
        serializer.serializeInsert(md, entity, columns, values, subQuery);
        return serializer.toString();
    }

    private String getBatchInsertSql(RelationalPath<?> entity, QueryMetadata md, List<SQLInsertBatch> insertBatches) {
        StringBuilder batchInsertQueries = new StringBuilder();
        batchInsertQueries.append("<Start batch insert> ");
        for (SQLInsertBatch insertBatch : insertBatches) {
            batchInsertQueries.append(this.getInsertSql(entity, md, insertBatch.getColumns(), insertBatch.getValues(), insertBatch.getSubQuery()));
            batchInsertQueries.append("; ");
        }
        batchInsertQueries.append(" <End batch insert>");
        return batchInsertQueries.toString();
    }

    private String getUpdateSql(RelationalPath<?> entity, QueryMetadata md, Map<Path<?>, Expression<?>> updates) {
        SQLSerializer serializer = this.newLiteralPrintingSerializer();
        serializer.serializeUpdate(md, entity, updates);
        return serializer.toString();
    }

    private String getBatchUpdateSql(RelationalPath<?> entity, List<SQLUpdateBatch> updateBatches) {
        StringBuilder batchUpdateQueries = new StringBuilder();
        batchUpdateQueries.append("<Start batch update> ");
        for (SQLUpdateBatch updateBatch : updateBatches) {
            batchUpdateQueries.append(this.getUpdateSql(entity, updateBatch.getMetadata(), updateBatch.getUpdates()));
            batchUpdateQueries.append("; ");
        }
        batchUpdateQueries.append(" <End batch update>");
        return batchUpdateQueries.toString();
    }

    private SQLSerializer newLiteralPrintingSerializer() {
        SQLSerializer literalPrintingSerializer = new SQLSerializer(this.configuration);
        literalPrintingSerializer.setUseLiterals(true);
        return literalPrintingSerializer;
    }
}

