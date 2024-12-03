/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.config.db.HibernateConfig
 *  com.google.common.collect.ImmutableMap
 *  net.java.ao.DatabaseProvider
 *  net.java.ao.db.H2DatabaseProvider
 *  net.java.ao.db.HSQLDatabaseProvider
 *  net.java.ao.db.MySQLDatabaseProvider
 *  net.java.ao.db.OracleDatabaseProvider
 *  net.java.ao.db.PostgreSQLDatabaseProvider
 *  net.java.ao.db.SQLServerDatabaseProvider
 */
package com.atlassian.confluence.extra.calendar3.querydsl;

import com.atlassian.config.db.HibernateConfig;
import com.google.common.collect.ImmutableMap;
import com.querydsl.sql.Configuration;
import com.querydsl.sql.H2Templates;
import com.querydsl.sql.HSQLDBTemplates;
import com.querydsl.sql.MySQLTemplates;
import com.querydsl.sql.OracleTemplates;
import com.querydsl.sql.PostgreSQLTemplates;
import com.querydsl.sql.SQLServer2008Templates;
import com.querydsl.sql.SQLServer2012Templates;
import com.querydsl.sql.SQLTemplates;
import java.util.Arrays;
import java.util.Map;
import java.util.function.Predicate;
import net.java.ao.DatabaseProvider;
import net.java.ao.db.H2DatabaseProvider;
import net.java.ao.db.HSQLDatabaseProvider;
import net.java.ao.db.MySQLDatabaseProvider;
import net.java.ao.db.OracleDatabaseProvider;
import net.java.ao.db.PostgreSQLDatabaseProvider;
import net.java.ao.db.SQLServerDatabaseProvider;

public class QueryDslHelper {
    private static final Map<Class<? extends DatabaseProvider>, SQLTemplates.Builder> AO_TO_SQL_TEMPLATE_MAPPING = ImmutableMap.builder().put(HSQLDatabaseProvider.class, (Object)HSQLDBTemplates.builder()).put(MySQLDatabaseProvider.class, (Object)MySQLTemplates.builder()).put(PostgreSQLDatabaseProvider.class, (Object)PostgreSQLTemplates.builder()).put(SQLServerDatabaseProvider.class, (Object)SQLServer2008Templates.builder()).put(OracleDatabaseProvider.class, (Object)OracleTemplates.builder()).put(H2DatabaseProvider.class, (Object)H2Templates.builder()).build();

    public static SQLTemplates.Builder getDatabaseProvider(DatabaseProvider databaseProvider) {
        for (Map.Entry<Class<? extends DatabaseProvider>, SQLTemplates.Builder> entry : AO_TO_SQL_TEMPLATE_MAPPING.entrySet()) {
            if (!entry.getKey().isAssignableFrom(databaseProvider.getClass())) continue;
            return entry.getValue();
        }
        throw new RuntimeException("Unknown database provider.");
    }

    public static Configuration getConfiguration(DatabaseProvider databaseProvider) {
        SQLTemplates.Builder templateBuilder = QueryDslHelper.getDatabaseProvider(databaseProvider).quote();
        Configuration configuration = new Configuration(templateBuilder.build());
        return configuration;
    }

    public static SQLTemplates.Builder getSqlTemplateBuilderByDialect(String dialect) {
        return HibernateDialectToSqlTemplateEnum.findByDialect((String)dialect).sqlTemplatesBuilder;
    }

    public static Configuration getConfiguration(String dialect) {
        SQLTemplates.Builder templateBuilder = QueryDslHelper.getSqlTemplateBuilderByDialect(dialect).quote();
        Configuration configuration = new Configuration(templateBuilder.build());
        configuration.setUseLiterals(true);
        return configuration;
    }

    private static enum HibernateDialectToSqlTemplateEnum {
        HSQL(dialect -> HibernateConfig.isHsqlDialect((String)dialect), HSQLDBTemplates.builder()),
        MySQL(dialect -> HibernateConfig.isMySqlDialect((String)dialect), MySQLTemplates.builder()),
        PostgreSQL(dialect -> HibernateConfig.isPostgreSqlDialect((String)dialect), PostgreSQLTemplates.builder()),
        SQLServer(dialect -> HibernateConfig.isSqlServerDialect((String)dialect), SQLServer2012Templates.builder()),
        Oracle(dialect -> HibernateConfig.isOracleDialect((String)dialect), OracleTemplates.builder()),
        H2(dialect -> HibernateConfig.isH2Dialect((String)dialect), H2Templates.builder());

        private Predicate<String> dialectChecker;
        private SQLTemplates.Builder sqlTemplatesBuilder;

        private HibernateDialectToSqlTemplateEnum(Predicate<String> dialectChecker, SQLTemplates.Builder sqlTemplatesBuilder) {
            this.dialectChecker = dialectChecker;
            this.sqlTemplatesBuilder = sqlTemplatesBuilder;
        }

        public static HibernateDialectToSqlTemplateEnum findByDialect(String dialect) {
            return Arrays.stream(HibernateDialectToSqlTemplateEnum.values()).filter(v -> v.dialectChecker.test(dialect)).findFirst().orElseThrow(() -> new RuntimeException("Unknown dialect."));
        }
    }
}

