/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.NamedNativeQueries
 *  javax.persistence.NamedNativeQuery
 *  javax.persistence.NamedQueries
 *  javax.persistence.NamedQuery
 *  javax.persistence.NamedStoredProcedureQuery
 *  javax.persistence.SqlResultSetMapping
 *  javax.persistence.SqlResultSetMappings
 *  org.jboss.logging.Logger
 */
package org.hibernate.cfg.annotations;

import java.util.HashMap;
import java.util.List;
import javax.persistence.NamedStoredProcedureQuery;
import javax.persistence.SqlResultSetMapping;
import javax.persistence.SqlResultSetMappings;
import org.hibernate.AnnotationException;
import org.hibernate.AssertionFailure;
import org.hibernate.CacheMode;
import org.hibernate.FlushMode;
import org.hibernate.LockMode;
import org.hibernate.annotations.CacheModeType;
import org.hibernate.annotations.FlushModeType;
import org.hibernate.annotations.NamedNativeQueries;
import org.hibernate.annotations.NamedNativeQuery;
import org.hibernate.annotations.NamedQueries;
import org.hibernate.annotations.NamedQuery;
import org.hibernate.boot.spi.MetadataBuildingContext;
import org.hibernate.cfg.BinderHelper;
import org.hibernate.cfg.annotations.NamedProcedureCallDefinition;
import org.hibernate.cfg.annotations.QueryHintDefinition;
import org.hibernate.cfg.annotations.ResultsetMappingSecondPass;
import org.hibernate.engine.query.spi.sql.NativeSQLQueryReturn;
import org.hibernate.engine.query.spi.sql.NativeSQLQueryRootReturn;
import org.hibernate.engine.spi.NamedQueryDefinition;
import org.hibernate.engine.spi.NamedQueryDefinitionBuilder;
import org.hibernate.engine.spi.NamedSQLQueryDefinition;
import org.hibernate.engine.spi.NamedSQLQueryDefinitionBuilder;
import org.hibernate.internal.CoreMessageLogger;
import org.jboss.logging.Logger;

public abstract class QueryBinder {
    private static final CoreMessageLogger LOG = (CoreMessageLogger)Logger.getMessageLogger(CoreMessageLogger.class, (String)QueryBinder.class.getName());

    public static void bindQuery(javax.persistence.NamedQuery queryAnn, MetadataBuildingContext context, boolean isDefault) {
        if (queryAnn == null) {
            return;
        }
        if (BinderHelper.isEmptyAnnotationValue(queryAnn.name())) {
            throw new AnnotationException("A named query must have a name when used in class or package level");
        }
        QueryHintDefinition hints = new QueryHintDefinition(queryAnn.hints());
        String queryName = queryAnn.query();
        NamedQueryDefinition queryDefinition = new NamedQueryDefinitionBuilder(queryAnn.name()).setLockOptions(hints.determineLockOptions(queryAnn)).setQuery(queryName).setCacheable(hints.getBoolean(queryName, "org.hibernate.cacheable")).setCacheRegion(hints.getString(queryName, "org.hibernate.cacheRegion")).setTimeout(hints.getTimeout(queryName)).setFetchSize(hints.getInteger(queryName, "org.hibernate.fetchSize")).setFlushMode(hints.getFlushMode(queryName)).setCacheMode(hints.getCacheMode(queryName)).setReadOnly(hints.getBoolean(queryName, "org.hibernate.readOnly")).setComment(hints.getString(queryName, "org.hibernate.comment")).setParameterTypes(null).setPassDistinctThrough(hints.getPassDistinctThrough(queryName)).createNamedQueryDefinition();
        if (isDefault) {
            context.getMetadataCollector().addDefaultQuery(queryDefinition);
        } else {
            context.getMetadataCollector().addNamedQuery(queryDefinition);
        }
        if (LOG.isDebugEnabled()) {
            LOG.debugf("Binding named query: %s => %s", queryDefinition.getName(), queryDefinition.getQueryString());
        }
    }

    public static void bindNativeQuery(javax.persistence.NamedNativeQuery queryAnn, MetadataBuildingContext context, boolean isDefault) {
        if (queryAnn == null) {
            return;
        }
        if (BinderHelper.isEmptyAnnotationValue(queryAnn.name())) {
            throw new AnnotationException("A named query must have a name when used in class or package level");
        }
        String resultSetMapping = queryAnn.resultSetMapping();
        QueryHintDefinition hints = new QueryHintDefinition(queryAnn.hints());
        String queryName = queryAnn.query();
        NamedSQLQueryDefinitionBuilder builder = new NamedSQLQueryDefinitionBuilder(queryAnn.name()).setQuery(queryName).setQuerySpaces((List<String>)null).setCacheable(hints.getBoolean(queryName, "org.hibernate.cacheable")).setCacheRegion(hints.getString(queryName, "org.hibernate.cacheRegion")).setTimeout(hints.getTimeout(queryName)).setFetchSize(hints.getInteger(queryName, "org.hibernate.fetchSize")).setFlushMode(hints.getFlushMode(queryName)).setCacheMode(hints.getCacheMode(queryName)).setReadOnly(hints.getBoolean(queryName, "org.hibernate.readOnly")).setComment(hints.getString(queryName, "org.hibernate.comment")).setParameterTypes(null).setCallable(hints.getBoolean(queryName, "org.hibernate.callable")).setPassDistinctThrough(hints.getPassDistinctThrough(queryName));
        if (!BinderHelper.isEmptyAnnotationValue(resultSetMapping)) {
            builder.setResultSetRef(resultSetMapping).createNamedQueryDefinition();
        } else if (!Void.TYPE.equals(queryAnn.resultClass())) {
            NativeSQLQueryRootReturn entityQueryReturn = new NativeSQLQueryRootReturn("alias1", queryAnn.resultClass().getName(), new HashMap<String, String[]>(), LockMode.READ);
            builder.setQueryReturns(new NativeSQLQueryReturn[]{entityQueryReturn});
        } else {
            builder.setQueryReturns(new NativeSQLQueryReturn[0]);
        }
        NamedSQLQueryDefinition query = builder.createNamedQueryDefinition();
        if (isDefault) {
            context.getMetadataCollector().addDefaultNamedNativeQuery(query);
        } else {
            context.getMetadataCollector().addNamedNativeQuery(query);
        }
        if (LOG.isDebugEnabled()) {
            LOG.debugf("Binding named native query: %s => %s", queryAnn.name(), queryAnn.query());
        }
    }

    public static void bindNativeQuery(NamedNativeQuery queryAnn, MetadataBuildingContext context) {
        if (queryAnn == null) {
            return;
        }
        if (BinderHelper.isEmptyAnnotationValue(queryAnn.name())) {
            throw new AnnotationException("A named query must have a name when used in class or package level");
        }
        String resultSetMapping = queryAnn.resultSetMapping();
        NamedSQLQueryDefinitionBuilder builder = new NamedSQLQueryDefinitionBuilder().setName(queryAnn.name()).setQuery(queryAnn.query()).setCacheable(queryAnn.cacheable()).setCacheRegion(BinderHelper.isEmptyAnnotationValue(queryAnn.cacheRegion()) ? null : queryAnn.cacheRegion()).setTimeout(queryAnn.timeout() < 0 ? null : Integer.valueOf(queryAnn.timeout())).setFetchSize(queryAnn.fetchSize() < 0 ? null : Integer.valueOf(queryAnn.fetchSize())).setFlushMode(QueryBinder.getFlushMode(queryAnn.flushMode())).setCacheMode(QueryBinder.getCacheMode(queryAnn.cacheMode())).setReadOnly(queryAnn.readOnly()).setComment(BinderHelper.isEmptyAnnotationValue(queryAnn.comment()) ? null : queryAnn.comment()).setParameterTypes(null).setCallable(queryAnn.callable());
        if (!BinderHelper.isEmptyAnnotationValue(resultSetMapping)) {
            builder.setResultSetRef(resultSetMapping);
        } else if (!Void.TYPE.equals(queryAnn.resultClass())) {
            NativeSQLQueryRootReturn entityQueryReturn = new NativeSQLQueryRootReturn("alias1", queryAnn.resultClass().getName(), new HashMap<String, String[]>(), LockMode.READ);
            builder.setQueryReturns(new NativeSQLQueryReturn[]{entityQueryReturn});
        } else {
            LOG.debugf("Raw scalar native-query (no explicit result mappings) found : %s", queryAnn.name());
        }
        NamedSQLQueryDefinition query = builder.createNamedQueryDefinition();
        context.getMetadataCollector().addNamedNativeQuery(query);
        if (LOG.isDebugEnabled()) {
            LOG.debugf("Binding named native query: %s => %s", query.getName(), queryAnn.query());
        }
    }

    public static void bindQueries(javax.persistence.NamedQueries queriesAnn, MetadataBuildingContext context, boolean isDefault) {
        if (queriesAnn == null) {
            return;
        }
        for (javax.persistence.NamedQuery q : queriesAnn.value()) {
            QueryBinder.bindQuery(q, context, isDefault);
        }
    }

    public static void bindNativeQueries(javax.persistence.NamedNativeQueries queriesAnn, MetadataBuildingContext context, boolean isDefault) {
        if (queriesAnn == null) {
            return;
        }
        for (javax.persistence.NamedNativeQuery q : queriesAnn.value()) {
            QueryBinder.bindNativeQuery(q, context, isDefault);
        }
    }

    public static void bindNativeQueries(NamedNativeQueries queriesAnn, MetadataBuildingContext context) {
        if (queriesAnn == null) {
            return;
        }
        for (NamedNativeQuery q : queriesAnn.value()) {
            QueryBinder.bindNativeQuery(q, context);
        }
    }

    public static void bindQuery(NamedQuery queryAnn, MetadataBuildingContext context) {
        if (queryAnn == null) {
            return;
        }
        if (BinderHelper.isEmptyAnnotationValue(queryAnn.name())) {
            throw new AnnotationException("A named query must have a name when used in class or package level");
        }
        FlushMode flushMode = QueryBinder.getFlushMode(queryAnn.flushMode());
        NamedQueryDefinition query = new NamedQueryDefinitionBuilder().setName(queryAnn.name()).setQuery(queryAnn.query()).setCacheable(queryAnn.cacheable()).setCacheRegion(BinderHelper.isEmptyAnnotationValue(queryAnn.cacheRegion()) ? null : queryAnn.cacheRegion()).setTimeout(queryAnn.timeout() < 0 ? null : Integer.valueOf(queryAnn.timeout())).setFetchSize(queryAnn.fetchSize() < 0 ? null : Integer.valueOf(queryAnn.fetchSize())).setFlushMode(flushMode).setCacheMode(QueryBinder.getCacheMode(queryAnn.cacheMode())).setReadOnly(queryAnn.readOnly()).setComment(BinderHelper.isEmptyAnnotationValue(queryAnn.comment()) ? null : queryAnn.comment()).setParameterTypes(null).createNamedQueryDefinition();
        context.getMetadataCollector().addNamedQuery(query);
        if (LOG.isDebugEnabled()) {
            LOG.debugf("Binding named query: %s => %s", query.getName(), query.getQueryString());
        }
    }

    private static FlushMode getFlushMode(FlushModeType flushModeType) {
        FlushMode flushMode;
        switch (flushModeType) {
            case ALWAYS: {
                flushMode = FlushMode.ALWAYS;
                break;
            }
            case AUTO: {
                flushMode = FlushMode.AUTO;
                break;
            }
            case COMMIT: {
                flushMode = FlushMode.COMMIT;
                break;
            }
            case NEVER: {
                flushMode = FlushMode.MANUAL;
                break;
            }
            case MANUAL: {
                flushMode = FlushMode.MANUAL;
                break;
            }
            case PERSISTENCE_CONTEXT: {
                flushMode = null;
                break;
            }
            default: {
                throw new AssertionFailure("Unknown flushModeType: " + (Object)((Object)flushModeType));
            }
        }
        return flushMode;
    }

    private static CacheMode getCacheMode(CacheModeType cacheModeType) {
        switch (cacheModeType) {
            case GET: {
                return CacheMode.GET;
            }
            case IGNORE: {
                return CacheMode.IGNORE;
            }
            case NORMAL: {
                return CacheMode.NORMAL;
            }
            case PUT: {
                return CacheMode.PUT;
            }
            case REFRESH: {
                return CacheMode.REFRESH;
            }
        }
        throw new AssertionFailure("Unknown cacheModeType: " + (Object)((Object)cacheModeType));
    }

    public static void bindQueries(NamedQueries queriesAnn, MetadataBuildingContext context) {
        if (queriesAnn == null) {
            return;
        }
        for (NamedQuery q : queriesAnn.value()) {
            QueryBinder.bindQuery(q, context);
        }
    }

    public static void bindNamedStoredProcedureQuery(NamedStoredProcedureQuery annotation, MetadataBuildingContext context, boolean isDefault) {
        if (annotation == null) {
            return;
        }
        if (BinderHelper.isEmptyAnnotationValue(annotation.name())) {
            throw new AnnotationException("A named query must have a name when used in class or package level");
        }
        NamedProcedureCallDefinition def = new NamedProcedureCallDefinition(annotation);
        if (isDefault) {
            context.getMetadataCollector().addDefaultNamedProcedureCallDefinition(def);
        } else {
            context.getMetadataCollector().addNamedProcedureCallDefinition(def);
        }
        LOG.debugf("Bound named stored procedure query : %s => %s", def.getRegisteredName(), def.getProcedureName());
    }

    public static void bindSqlResultSetMappings(SqlResultSetMappings ann, MetadataBuildingContext context, boolean isDefault) {
        if (ann == null) {
            return;
        }
        for (SqlResultSetMapping rs : ann.value()) {
            context.getMetadataCollector().addSecondPass(new ResultsetMappingSecondPass(rs, context, true));
        }
    }

    public static void bindSqlResultSetMapping(SqlResultSetMapping ann, MetadataBuildingContext context, boolean isDefault) {
        context.getMetadataCollector().addSecondPass(new ResultsetMappingSecondPass(ann, context, isDefault));
    }
}

