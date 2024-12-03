/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.activeobjects.spi.DataSourceProvider
 *  com.atlassian.activeobjects.spi.DatabaseType
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.sal.api.rdbms.TransactionalExecutor
 *  com.atlassian.sal.api.rdbms.TransactionalExecutorFactory
 *  com.atlassian.util.profiling.UtilTimerStack
 *  com.querydsl.sql.Configuration
 *  com.querydsl.sql.DB2Templates
 *  com.querydsl.sql.DerbyTemplates
 *  com.querydsl.sql.HSQLDBTemplates
 *  com.querydsl.sql.SQLQueryFactory
 *  com.querydsl.sql.SQLTemplates
 *  com.querydsl.sql.SQLTemplates$Builder
 *  com.querydsl.sql.namemapping.NameMapping
 *  com.querydsl.sql.namemapping.PreConfiguredNameMapping
 *  javax.inject.Inject
 *  javax.inject.Named
 *  kotlin.Metadata
 *  kotlin.NoWhenBranchMatchedException
 *  kotlin.jvm.functions.Function1
 *  kotlin.jvm.internal.Intrinsics
 *  kotlin.jvm.internal.Reflection
 *  kotlin.jvm.internal.SourceDebugExtension
 *  kotlin.reflect.KClass
 *  org.jetbrains.annotations.NotNull
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.addonengine.addons.analytics.store.server.querydsl;

import com.addonengine.addons.analytics.store.server.querydsl.template.ExtendedH2Template;
import com.addonengine.addons.analytics.store.server.querydsl.template.ExtendedMysqlTemplate;
import com.addonengine.addons.analytics.store.server.querydsl.template.ExtendedOracleTemplate;
import com.addonengine.addons.analytics.store.server.querydsl.template.ExtendedPostgresTemplate;
import com.addonengine.addons.analytics.store.server.querydsl.template.ExtendedSqlServerTemplate;
import com.atlassian.activeobjects.spi.DataSourceProvider;
import com.atlassian.activeobjects.spi.DatabaseType;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.sal.api.rdbms.TransactionalExecutor;
import com.atlassian.sal.api.rdbms.TransactionalExecutorFactory;
import com.atlassian.util.profiling.UtilTimerStack;
import com.querydsl.sql.Configuration;
import com.querydsl.sql.DB2Templates;
import com.querydsl.sql.DerbyTemplates;
import com.querydsl.sql.HSQLDBTemplates;
import com.querydsl.sql.SQLQueryFactory;
import com.querydsl.sql.SQLTemplates;
import com.querydsl.sql.namemapping.NameMapping;
import com.querydsl.sql.namemapping.PreConfiguredNameMapping;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import javax.inject.Inject;
import javax.inject.Named;
import kotlin.Metadata;
import kotlin.NoWhenBranchMatchedException;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.Reflection;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlin.reflect.KClass;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Named
@Metadata(mv={1, 9, 0}, k=1, xi=48, d1={"\u0000R\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\b\u0007\u0018\u00002\u00020\u0001B\u001b\b\u0007\u0012\b\b\u0001\u0010\u0002\u001a\u00020\u0003\u0012\b\b\u0001\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\u0002\u0010\u0006J/\u0010\u000e\u001a\u0002H\u000f\"\u0004\b\u0000\u0010\u000f2\b\b\u0002\u0010\u0010\u001a\u00020\u00112\u0012\u0010\u0012\u001a\u000e\u0012\u0004\u0012\u00020\u0014\u0012\u0004\u0012\u0002H\u000f0\u0013\u00a2\u0006\u0002\u0010\u0015J\u0010\u0010\u0016\u001a\u00020\u00172\u0006\u0010\u0018\u001a\u00020\u0019H\u0002J\u0010\u0010\u001a\u001a\u00020\u001b2\u0006\u0010\u001c\u001a\u00020\u001dH\u0002R\u000e\u0010\u0007\u001a\u00020\bX\u0082D\u00a2\u0006\u0002\n\u0000R\u000e\u0010\t\u001a\u00020\bX\u0082D\u00a2\u0006\u0002\n\u0000R\u000e\u0010\n\u001a\u00020\bX\u0082D\u00a2\u0006\u0002\n\u0000R\u0016\u0010\u000b\u001a\n \r*\u0004\u0018\u00010\f0\fX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u001e"}, d2={"Lcom/addonengine/addons/analytics/store/server/querydsl/QueryDslDbConnectionManager;", "", "provider", "Lcom/atlassian/activeobjects/spi/DataSourceProvider;", "transactionalExecutorFactory", "Lcom/atlassian/sal/api/rdbms/TransactionalExecutorFactory;", "(Lcom/atlassian/activeobjects/spi/DataSourceProvider;Lcom/atlassian/sal/api/rdbms/TransactionalExecutorFactory;)V", "SQLSERVER_2005", "", "SQLSERVER_2008", "SQLSERVER_2012", "logger", "Lorg/slf4j/Logger;", "kotlin.jvm.PlatformType", "execute", "T", "readOnly", "", "callback", "Lkotlin/Function1;", "Lcom/querydsl/sql/SQLQueryFactory;", "(ZLkotlin/jvm/functions/Function1;)Ljava/lang/Object;", "getDialect", "Lcom/querydsl/sql/SQLTemplates;", "metaData", "Ljava/sql/DatabaseMetaData;", "setCasingRulesBasedOnDbms", "", "configuration", "Lcom/querydsl/sql/Configuration;", "analytics"})
@SourceDebugExtension(value={"SMAP\nQueryDslDbConnectionManager.kt\nKotlin\n*S Kotlin\n*F\n+ 1 QueryDslDbConnectionManager.kt\ncom/addonengine/addons/analytics/store/server/querydsl/QueryDslDbConnectionManager\n+ 2 utils.kt\ncom/addonengine/addons/analytics/util/UtilsKt\n*L\n1#1,125:1\n11#2,11:126\n*S KotlinDebug\n*F\n+ 1 QueryDslDbConnectionManager.kt\ncom/addonengine/addons/analytics/store/server/querydsl/QueryDslDbConnectionManager\n*L\n55#1:126,11\n*E\n"})
public final class QueryDslDbConnectionManager {
    @NotNull
    private final DataSourceProvider provider;
    @NotNull
    private final TransactionalExecutorFactory transactionalExecutorFactory;
    private final Logger logger;
    private final int SQLSERVER_2005;
    private final int SQLSERVER_2008;
    private final int SQLSERVER_2012;

    @Inject
    public QueryDslDbConnectionManager(@ComponentImport @NotNull DataSourceProvider provider, @ComponentImport @NotNull TransactionalExecutorFactory transactionalExecutorFactory) {
        Intrinsics.checkNotNullParameter((Object)provider, (String)"provider");
        Intrinsics.checkNotNullParameter((Object)transactionalExecutorFactory, (String)"transactionalExecutorFactory");
        this.provider = provider;
        this.transactionalExecutorFactory = transactionalExecutorFactory;
        this.logger = LoggerFactory.getLogger(this.getClass());
        this.SQLSERVER_2005 = 9;
        this.SQLSERVER_2008 = 10;
        this.SQLSERVER_2012 = 11;
    }

    public final <T> T execute(boolean readOnly, @NotNull Function1<? super SQLQueryFactory, ? extends T> callback) {
        Intrinsics.checkNotNullParameter(callback, (String)"callback");
        TransactionalExecutor executor = this.transactionalExecutorFactory.createExecutor(readOnly, false);
        return (T)executor.execute(arg_0 -> QueryDslDbConnectionManager.execute$lambda$2(this, callback, arg_0));
    }

    public static /* synthetic */ Object execute$default(QueryDslDbConnectionManager queryDslDbConnectionManager, boolean bl, Function1 function1, int n, Object object) {
        if ((n & 1) != 0) {
            bl = true;
        }
        return queryDslDbConnectionManager.execute(bl, function1);
    }

    private final void setCasingRulesBasedOnDbms(Configuration configuration) {
        DatabaseType databaseType = this.provider.getDatabaseType();
        if (databaseType == DatabaseType.POSTGRESQL) {
            PreConfiguredNameMapping lowerCaseMapping = new PreConfiguredNameMapping();
            lowerCaseMapping.registerTableOverride("SPACES", "spaces");
            lowerCaseMapping.registerColumnOverride("SPACES", "SPACEKEY", "spacekey");
            lowerCaseMapping.registerColumnOverride("SPACES", "SPACENAME", "spacename");
            lowerCaseMapping.registerColumnOverride("SPACES", "SPACETYPE", "spacetype");
            lowerCaseMapping.registerColumnOverride("SPACES", "SPACESTATUS", "spacestatus");
            lowerCaseMapping.registerTableOverride("CONTENT", "content");
            lowerCaseMapping.registerColumnOverride("CONTENT", "CONTENTID", "contentid");
            lowerCaseMapping.registerColumnOverride("CONTENT", "TITLE", "title");
            lowerCaseMapping.registerColumnOverride("CONTENT", "CONTENTTYPE", "contenttype");
            lowerCaseMapping.registerColumnOverride("CONTENT", "CONTENT_STATUS", "content_status");
            lowerCaseMapping.registerColumnOverride("CONTENT", "PREVVER", "prevver");
            lowerCaseMapping.registerColumnOverride("CONTENT", "SPACEID", "spaceid");
            lowerCaseMapping.registerColumnOverride("CONTENT", "CREATIONDATE", "creationdate");
            lowerCaseMapping.registerColumnOverride("CONTENT", "LASTMODDATE", "lastmoddate");
            configuration.setDynamicNameMapping((NameMapping)lowerCaseMapping);
        }
    }

    private final SQLTemplates getDialect(DatabaseMetaData metaData) {
        SQLTemplates.Builder builder2;
        DatabaseType databaseType = this.provider.getDatabaseType();
        this.logger.debug("Database Type: " + databaseType);
        DatabaseType databaseType2 = databaseType;
        switch (databaseType2 == null ? -1 : WhenMappings.$EnumSwitchMapping$0[databaseType2.ordinal()]) {
            case 1: {
                builder2 = ExtendedOracleTemplate.Companion.builder();
                break;
            }
            case 2: {
                builder2 = ExtendedMysqlTemplate.Companion.builder();
                break;
            }
            case 3: {
                builder2 = ExtendedPostgresTemplate.Companion.builder();
                break;
            }
            case 4: {
                builder2 = ExtendedSqlServerTemplate.Companion.builder();
                break;
            }
            case 5: {
                builder2 = ExtendedH2Template.Companion.builder();
                break;
            }
            case 6: {
                builder2 = HSQLDBTemplates.builder();
                break;
            }
            case 7: {
                builder2 = DerbyTemplates.builder();
                break;
            }
            case 8: {
                builder2 = DerbyTemplates.builder();
                break;
            }
            case 9: {
                builder2 = DB2Templates.builder();
                break;
            }
            case 10: {
                throw new UnsupportedOperationException("Unsupported database type '" + databaseType + '\'');
            }
            case 11: {
                this.logger.warn("Unknown database detected. Falling back to use the H2Templates templates for QueryDSL.");
                builder2 = ExtendedH2Template.Companion.builder();
                break;
            }
            default: {
                throw new NoWhenBranchMatchedException();
            }
        }
        SQLTemplates.Builder builder3 = builder2;
        builder3.newLineToSingleSpace().quote();
        if (this.provider.getSchema() != null) {
            builder3.printSchema();
        }
        SQLTemplates sQLTemplates = builder3.build();
        Intrinsics.checkNotNullExpressionValue((Object)sQLTemplates, (String)"build(...)");
        return sQLTemplates;
    }

    private static final Connection execute$lambda$2$lambda$0(Connection $it) {
        return $it;
    }

    /*
     * WARNING - void declaration
     */
    private static final Object execute$lambda$2(QueryDslDbConnectionManager this$0, Function1 $callback, Connection it) {
        void klass$iv;
        Intrinsics.checkNotNullParameter((Object)this$0, (String)"this$0");
        Intrinsics.checkNotNullParameter((Object)$callback, (String)"$callback");
        DatabaseMetaData databaseMetaData = it.getMetaData();
        Intrinsics.checkNotNullExpressionValue((Object)databaseMetaData, (String)"getMetaData(...)");
        Configuration configuration = new Configuration(this$0.getDialect(databaseMetaData));
        this$0.setCasingRulesBasedOnDbms(configuration);
        SQLQueryFactory queryFactory = new SQLQueryFactory(configuration, () -> QueryDslDbConnectionManager.execute$lambda$2$lambda$0(it));
        KClass kClass = Reflection.getOrCreateKotlinClass(this$0.getClass());
        String name$iv = "callback";
        boolean $i$f$atlassianProfilingTimer = false;
        if (UtilTimerStack.isActive()) {
            UtilTimerStack.push((String)(klass$iv.getQualifiedName() + '_' + name$iv));
        }
        boolean bl = false;
        Object result$iv = $callback.invoke((Object)queryFactory);
        if (UtilTimerStack.isActive()) {
            UtilTimerStack.pop((String)(klass$iv.getQualifiedName() + '_' + name$iv));
        }
        return result$iv;
    }

    @Metadata(mv={1, 9, 0}, k=3, xi=48)
    public final class WhenMappings {
        public static final /* synthetic */ int[] $EnumSwitchMapping$0;

        static {
            int[] nArray = new int[DatabaseType.values().length];
            try {
                nArray[DatabaseType.ORACLE.ordinal()] = 1;
            }
            catch (NoSuchFieldError noSuchFieldError) {
                // empty catch block
            }
            try {
                nArray[DatabaseType.MYSQL.ordinal()] = 2;
            }
            catch (NoSuchFieldError noSuchFieldError) {
                // empty catch block
            }
            try {
                nArray[DatabaseType.POSTGRESQL.ordinal()] = 3;
            }
            catch (NoSuchFieldError noSuchFieldError) {
                // empty catch block
            }
            try {
                nArray[DatabaseType.MS_SQL.ordinal()] = 4;
            }
            catch (NoSuchFieldError noSuchFieldError) {
                // empty catch block
            }
            try {
                nArray[DatabaseType.H2.ordinal()] = 5;
            }
            catch (NoSuchFieldError noSuchFieldError) {
                // empty catch block
            }
            try {
                nArray[DatabaseType.HSQL.ordinal()] = 6;
            }
            catch (NoSuchFieldError noSuchFieldError) {
                // empty catch block
            }
            try {
                nArray[DatabaseType.DERBY_EMBEDDED.ordinal()] = 7;
            }
            catch (NoSuchFieldError noSuchFieldError) {
                // empty catch block
            }
            try {
                nArray[DatabaseType.DERBY_NETWORK.ordinal()] = 8;
            }
            catch (NoSuchFieldError noSuchFieldError) {
                // empty catch block
            }
            try {
                nArray[DatabaseType.DB2.ordinal()] = 9;
            }
            catch (NoSuchFieldError noSuchFieldError) {
                // empty catch block
            }
            try {
                nArray[DatabaseType.NUODB.ordinal()] = 10;
            }
            catch (NoSuchFieldError noSuchFieldError) {
                // empty catch block
            }
            try {
                nArray[DatabaseType.UNKNOWN.ordinal()] = 11;
            }
            catch (NoSuchFieldError noSuchFieldError) {
                // empty catch block
            }
            $EnumSwitchMapping$0 = nArray;
        }
    }
}

