/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jboss.logging.Logger
 */
package org.hibernate.cfg;

import java.util.Map;
import org.hibernate.ConnectionReleaseMode;
import org.hibernate.EntityMode;
import org.hibernate.MultiTenancyStrategy;
import org.hibernate.NullPrecedence;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.SchemaAutoTooling;
import org.hibernate.boot.model.naming.Identifier;
import org.hibernate.boot.spi.SessionFactoryOptions;
import org.hibernate.cache.spi.RegionFactory;
import org.hibernate.cache.spi.TimestampsCacheFactory;
import org.hibernate.cfg.BaselineSessionEventsListenerBuilder;
import org.hibernate.engine.transaction.jta.platform.spi.JtaPlatform;
import org.hibernate.hql.spi.QueryTranslatorFactory;
import org.hibernate.hql.spi.id.MultiTableBulkIdStrategy;
import org.hibernate.loader.BatchFetchStyle;
import org.hibernate.tuple.entity.EntityTuplizerFactory;
import org.jboss.logging.Logger;

@Deprecated
public final class Settings {
    private static final Logger LOG = Logger.getLogger(Settings.class);
    private final SessionFactoryOptions sessionFactoryOptions;
    private final String defaultCatalogName;
    private final String defaultSchemaName;

    public Settings(SessionFactoryOptions sessionFactoryOptions) {
        this(sessionFactoryOptions, null, null);
    }

    public Settings(SessionFactoryOptions sessionFactoryOptions, Metadata metadata) {
        this(sessionFactoryOptions, Settings.extractName(metadata.getDatabase().getPhysicalImplicitNamespaceName().getCatalog()), Settings.extractName(metadata.getDatabase().getPhysicalImplicitNamespaceName().getSchema()));
    }

    private static String extractName(Identifier identifier) {
        return identifier == null ? null : identifier.render();
    }

    public Settings(SessionFactoryOptions sessionFactoryOptions, String defaultCatalogName, String defaultSchemaName) {
        this.sessionFactoryOptions = sessionFactoryOptions;
        this.defaultCatalogName = sessionFactoryOptions.getDefaultCatalog() != null ? sessionFactoryOptions.getDefaultCatalog() : defaultCatalogName;
        String string = this.defaultSchemaName = sessionFactoryOptions.getDefaultSchema() != null ? sessionFactoryOptions.getDefaultSchema() : defaultSchemaName;
        if (LOG.isDebugEnabled()) {
            LOG.debugf("SessionFactory name : %s", (Object)sessionFactoryOptions.getSessionFactoryName());
            LOG.debugf("Automatic flush during beforeCompletion(): %s", (Object)Settings.enabledDisabled(sessionFactoryOptions.isFlushBeforeCompletionEnabled()));
            LOG.debugf("Automatic session close at end of transaction: %s", (Object)Settings.enabledDisabled(sessionFactoryOptions.isAutoCloseSessionEnabled()));
            LOG.debugf("Statistics: %s", (Object)Settings.enabledDisabled(sessionFactoryOptions.isStatisticsEnabled()));
            LOG.debugf("Deleted entity synthetic identifier rollback: %s", (Object)Settings.enabledDisabled(sessionFactoryOptions.isIdentifierRollbackEnabled()));
            LOG.debugf("Default entity-mode: %s", (Object)sessionFactoryOptions.getDefaultEntityMode());
            LOG.debugf("Check Nullability in Core (should be disabled when Bean Validation is on): %s", (Object)Settings.enabledDisabled(sessionFactoryOptions.isCheckNullability()));
            LOG.debugf("Allow initialization of lazy state outside session : %s", (Object)Settings.enabledDisabled(sessionFactoryOptions.isInitializeLazyStateOutsideTransactionsEnabled()));
            LOG.debugf("Using BatchFetchStyle : %s", (Object)sessionFactoryOptions.getBatchFetchStyle().name());
            LOG.debugf("Default batch fetch size: %s", sessionFactoryOptions.getDefaultBatchFetchSize());
            LOG.debugf("Maximum outer join fetch depth: %s", (Object)sessionFactoryOptions.getMaximumFetchDepth());
            LOG.debugf("Default null ordering: %s", (Object)sessionFactoryOptions.getDefaultNullPrecedence());
            LOG.debugf("Order SQL updates by primary key: %s", (Object)Settings.enabledDisabled(sessionFactoryOptions.isOrderUpdatesEnabled()));
            LOG.debugf("Order SQL inserts for batching: %s", (Object)Settings.enabledDisabled(sessionFactoryOptions.isOrderInsertsEnabled()));
            LOG.debugf("multi-tenancy strategy : %s", (Object)sessionFactoryOptions.getMultiTenancyStrategy());
            LOG.debugf("JTA Track by Thread: %s", (Object)Settings.enabledDisabled(sessionFactoryOptions.isJtaTrackByThread()));
            LOG.debugf("Query language substitutions: %s", (Object)sessionFactoryOptions.getQuerySubstitutions());
            LOG.debugf("Named query checking : %s", (Object)Settings.enabledDisabled(sessionFactoryOptions.isNamedQueryStartupCheckingEnabled()));
            LOG.debugf("Second-level cache: %s", (Object)Settings.enabledDisabled(sessionFactoryOptions.isSecondLevelCacheEnabled()));
            LOG.debugf("Second-level query cache: %s", (Object)Settings.enabledDisabled(sessionFactoryOptions.isQueryCacheEnabled()));
            LOG.debugf("Second-level query cache factory: %s", (Object)sessionFactoryOptions.getTimestampsCacheFactory());
            LOG.debugf("Second-level cache region prefix: %s", (Object)sessionFactoryOptions.getCacheRegionPrefix());
            LOG.debugf("Optimize second-level cache for minimal puts: %s", (Object)Settings.enabledDisabled(sessionFactoryOptions.isMinimalPutsEnabled()));
            LOG.debugf("Structured second-level cache entries: %s", (Object)Settings.enabledDisabled(sessionFactoryOptions.isStructuredCacheEntriesEnabled()));
            LOG.debugf("Second-level cache direct-reference entries: %s", (Object)Settings.enabledDisabled(sessionFactoryOptions.isDirectReferenceCacheEntriesEnabled()));
            LOG.debugf("Automatic eviction of collection cache: %s", (Object)Settings.enabledDisabled(sessionFactoryOptions.isAutoEvictCollectionCache()));
            LOG.debugf("JDBC batch size: %s", sessionFactoryOptions.getJdbcBatchSize());
            LOG.debugf("JDBC batch updates for versioned data: %s", (Object)Settings.enabledDisabled(sessionFactoryOptions.isJdbcBatchVersionedData()));
            LOG.debugf("Scrollable result sets: %s", (Object)Settings.enabledDisabled(sessionFactoryOptions.isScrollableResultSetsEnabled()));
            LOG.debugf("Wrap result sets: %s", (Object)Settings.enabledDisabled(sessionFactoryOptions.isWrapResultSetsEnabled()));
            LOG.debugf("JDBC3 getGeneratedKeys(): %s", (Object)Settings.enabledDisabled(sessionFactoryOptions.isGetGeneratedKeysEnabled()));
            LOG.debugf("JDBC result set fetch size: %s", (Object)sessionFactoryOptions.getJdbcFetchSize());
            LOG.debugf("Connection release mode: %s", (Object)sessionFactoryOptions.getConnectionReleaseMode());
            LOG.debugf("Generate SQL with comments: %s", (Object)Settings.enabledDisabled(sessionFactoryOptions.isCommentsEnabled()));
            LOG.debugf("JPA compliance - query : %s", (Object)Settings.enabledDisabled(sessionFactoryOptions.getJpaCompliance().isJpaQueryComplianceEnabled()));
            LOG.debugf("JPA compliance - closed-handling : %s", (Object)Settings.enabledDisabled(sessionFactoryOptions.getJpaCompliance().isJpaClosedComplianceEnabled()));
            LOG.debugf("JPA compliance - lists : %s", (Object)Settings.enabledDisabled(sessionFactoryOptions.getJpaCompliance().isJpaListComplianceEnabled()));
            LOG.debugf("JPA compliance - transactions : %s", (Object)Settings.enabledDisabled(sessionFactoryOptions.getJpaCompliance().isJpaTransactionComplianceEnabled()));
        }
    }

    private static String enabledDisabled(boolean value) {
        return value ? "enabled" : "disabled";
    }

    public String getDefaultSchemaName() {
        return this.defaultSchemaName;
    }

    public String getDefaultCatalogName() {
        return this.defaultCatalogName;
    }

    public String getSessionFactoryName() {
        return this.sessionFactoryOptions.getSessionFactoryName();
    }

    public boolean isSessionFactoryNameAlsoJndiName() {
        return this.sessionFactoryOptions.isSessionFactoryNameAlsoJndiName();
    }

    public boolean isFlushBeforeCompletionEnabled() {
        return this.sessionFactoryOptions.isFlushBeforeCompletionEnabled();
    }

    public boolean isAutoCloseSessionEnabled() {
        return this.sessionFactoryOptions.isAutoCloseSessionEnabled();
    }

    public boolean isStatisticsEnabled() {
        return this.sessionFactoryOptions.isStatisticsEnabled();
    }

    public BaselineSessionEventsListenerBuilder getBaselineSessionEventsListenerBuilder() {
        return this.sessionFactoryOptions.getBaselineSessionEventsListenerBuilder();
    }

    public boolean isIdentifierRollbackEnabled() {
        return this.sessionFactoryOptions.isIdentifierRollbackEnabled();
    }

    public EntityMode getDefaultEntityMode() {
        return this.sessionFactoryOptions.getDefaultEntityMode();
    }

    public EntityTuplizerFactory getEntityTuplizerFactory() {
        return this.sessionFactoryOptions.getEntityTuplizerFactory();
    }

    public boolean isCheckNullability() {
        return this.sessionFactoryOptions.isCheckNullability();
    }

    public boolean isInitializeLazyStateOutsideTransactionsEnabled() {
        return this.sessionFactoryOptions.isInitializeLazyStateOutsideTransactionsEnabled();
    }

    public MultiTableBulkIdStrategy getMultiTableBulkIdStrategy() {
        return this.sessionFactoryOptions.getMultiTableBulkIdStrategy();
    }

    public BatchFetchStyle getBatchFetchStyle() {
        return this.sessionFactoryOptions.getBatchFetchStyle();
    }

    public int getDefaultBatchFetchSize() {
        return this.sessionFactoryOptions.getDefaultBatchFetchSize();
    }

    public Integer getMaximumFetchDepth() {
        return this.sessionFactoryOptions.getMaximumFetchDepth();
    }

    public NullPrecedence getDefaultNullPrecedence() {
        return this.sessionFactoryOptions.getDefaultNullPrecedence();
    }

    public boolean isOrderUpdatesEnabled() {
        return this.sessionFactoryOptions.isOrderUpdatesEnabled();
    }

    public boolean isOrderInsertsEnabled() {
        return this.sessionFactoryOptions.isOrderInsertsEnabled();
    }

    public MultiTenancyStrategy getMultiTenancyStrategy() {
        return this.sessionFactoryOptions.getMultiTenancyStrategy();
    }

    public boolean isJtaTrackByThread() {
        return this.sessionFactoryOptions.isJtaTrackByThread();
    }

    public boolean isStrictJPAQLCompliance() {
        return this.sessionFactoryOptions.isStrictJpaQueryLanguageCompliance();
    }

    public Map getQuerySubstitutions() {
        return this.sessionFactoryOptions.getQuerySubstitutions();
    }

    public boolean isNamedQueryStartupCheckingEnabled() {
        return this.sessionFactoryOptions.isNamedQueryStartupCheckingEnabled();
    }

    public boolean isSecondLevelCacheEnabled() {
        return this.sessionFactoryOptions.isSecondLevelCacheEnabled();
    }

    public boolean isQueryCacheEnabled() {
        return this.sessionFactoryOptions.isQueryCacheEnabled();
    }

    public TimestampsCacheFactory getTimestampsCacheFactory() {
        return this.sessionFactoryOptions.getTimestampsCacheFactory();
    }

    public String getCacheRegionPrefix() {
        return this.sessionFactoryOptions.getCacheRegionPrefix();
    }

    public boolean isMinimalPutsEnabled() {
        return this.sessionFactoryOptions.isMinimalPutsEnabled();
    }

    public boolean isStructuredCacheEntriesEnabled() {
        return this.sessionFactoryOptions.isStructuredCacheEntriesEnabled();
    }

    public boolean isDirectReferenceCacheEntriesEnabled() {
        return this.sessionFactoryOptions.isDirectReferenceCacheEntriesEnabled();
    }

    public boolean isAutoEvictCollectionCache() {
        return this.sessionFactoryOptions.isAutoEvictCollectionCache();
    }

    public boolean isAutoCreateSchema() {
        return this.sessionFactoryOptions.getSchemaAutoTooling() == SchemaAutoTooling.CREATE || this.sessionFactoryOptions.getSchemaAutoTooling() == SchemaAutoTooling.CREATE_DROP || this.sessionFactoryOptions.getSchemaAutoTooling() == SchemaAutoTooling.CREATE_ONLY;
    }

    public boolean isAutoDropSchema() {
        return this.sessionFactoryOptions.getSchemaAutoTooling() == SchemaAutoTooling.CREATE_DROP;
    }

    public boolean isAutoUpdateSchema() {
        return this.sessionFactoryOptions.getSchemaAutoTooling() == SchemaAutoTooling.UPDATE;
    }

    public boolean isAutoValidateSchema() {
        return this.sessionFactoryOptions.getSchemaAutoTooling() == SchemaAutoTooling.VALIDATE;
    }

    public int getJdbcBatchSize() {
        return this.sessionFactoryOptions.getJdbcBatchSize();
    }

    public boolean isJdbcBatchVersionedData() {
        return this.sessionFactoryOptions.isJdbcBatchVersionedData();
    }

    public Integer getJdbcFetchSize() {
        return this.sessionFactoryOptions.getJdbcFetchSize();
    }

    public boolean isScrollableResultSetsEnabled() {
        return this.sessionFactoryOptions.isScrollableResultSetsEnabled();
    }

    public boolean isWrapResultSetsEnabled() {
        return this.sessionFactoryOptions.isWrapResultSetsEnabled();
    }

    public boolean isGetGeneratedKeysEnabled() {
        return this.sessionFactoryOptions.isGetGeneratedKeysEnabled();
    }

    public ConnectionReleaseMode getConnectionReleaseMode() {
        return this.sessionFactoryOptions.getConnectionReleaseMode();
    }

    public boolean isCommentsEnabled() {
        return this.sessionFactoryOptions.isCommentsEnabled();
    }

    public RegionFactory getRegionFactory() {
        return this.sessionFactoryOptions.getServiceRegistry().getService(RegionFactory.class);
    }

    public JtaPlatform getJtaPlatform() {
        return this.sessionFactoryOptions.getServiceRegistry().getService(JtaPlatform.class);
    }

    public QueryTranslatorFactory getQueryTranslatorFactory() {
        return this.sessionFactoryOptions.getServiceRegistry().getService(QueryTranslatorFactory.class);
    }

    public void setCheckNullability(boolean enabled) {
        this.sessionFactoryOptions.setCheckNullability(enabled);
    }

    public boolean isPreferUserTransaction() {
        return this.sessionFactoryOptions.isPreferUserTransaction();
    }
}

