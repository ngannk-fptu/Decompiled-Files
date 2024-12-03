/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.atlassian.fugue.Pair
 *  javax.annotation.Nullable
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.pocketknife.internal.querydsl.dialect;

import com.atlassian.pocketknife.api.querydsl.configuration.ConfigurationEnrichment;
import com.atlassian.pocketknife.api.querydsl.schema.DialectProvider;
import com.atlassian.pocketknife.api.querydsl.util.LoggingSqlListener;
import com.atlassian.pocketknife.internal.querydsl.dialect.DialectConfiguration;
import com.atlassian.pocketknife.internal.querydsl.dialect.DialectHelper;
import com.atlassian.pocketknife.internal.querydsl.schema.SchemaOverrideListener;
import com.atlassian.pocketknife.internal.querydsl.schema.SchemaOverrider;
import com.atlassian.pocketknife.internal.querydsl.schema.SchemaProvider;
import com.atlassian.pocketknife.internal.querydsl.util.MemoizingResettingReference;
import com.querydsl.sql.Configuration;
import com.querydsl.sql.H2Templates;
import com.querydsl.sql.HSQLDBTemplates;
import com.querydsl.sql.MySQLTemplates;
import com.querydsl.sql.OracleTemplates;
import com.querydsl.sql.PostgreSQLTemplates;
import com.querydsl.sql.SQLTemplates;
import io.atlassian.fugue.Pair;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.annotation.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DefaultDialectConfiguration
implements DialectConfiguration {
    private static final Logger log = LoggerFactory.getLogger(DefaultDialectConfiguration.class);
    private static Map<String, Pair<SQLTemplates.Builder, DialectProvider.SupportedDatabase>> support = new LinkedHashMap<String, Pair<SQLTemplates.Builder, DialectProvider.SupportedDatabase>>();
    private final SchemaProvider schemaProvider;
    private final ConfigurationEnrichment configurationEnrichment;
    private final MemoizingResettingReference<Connection, DialectProvider.Config> cachedConfigRef = new MemoizingResettingReference(this::detect);

    @Autowired
    public DefaultDialectConfiguration(SchemaProvider schemaProvider, ConfigurationEnrichment configurationEnrichment) {
        this.schemaProvider = schemaProvider;
        this.configurationEnrichment = configurationEnrichment;
    }

    @Override
    public DialectProvider.Config getDialectConfig(Connection connection) {
        DialectProvider.Config cachedConfig = this.cachedConfigRef.get(connection);
        SQLTemplates templates = cachedConfig.getSqlTemplates();
        Configuration configuration = this.enrich(new Configuration(templates));
        return new DialectProvider.Config(templates, configuration, cachedConfig.getDatabaseInfo());
    }

    private DialectProvider.Config detect(Connection connection) {
        Pair<SQLTemplates, DialectProvider.SupportedDatabase> pair = this.buildTemplates(connection);
        SQLTemplates sqlTemplates = (SQLTemplates)pair.left();
        Configuration configuration = new Configuration(sqlTemplates);
        return new DialectProvider.Config(sqlTemplates, configuration, this.buildDatabaseInfo((DialectProvider.SupportedDatabase)((Object)pair.right()), connection));
    }

    @Override
    public SQLTemplates.Builder enrich(SQLTemplates.Builder builder) {
        if (this.schemaProvider.getProductSchema().isPresent()) {
            builder.printSchema();
        }
        return builder.newLineToSingleSpace().quote();
    }

    @Override
    public Configuration enrich(Configuration configuration) {
        this.configurationEnrichment.getEnricher().enrich(configuration);
        configuration.addListener(new LoggingSqlListener(configuration));
        configuration.addListener(new SchemaOverrideListener(configuration, new SchemaOverrider(this.schemaProvider)));
        return configuration;
    }

    private Pair<SQLTemplates, DialectProvider.SupportedDatabase> buildTemplates(Connection connection) {
        try {
            DatabaseMetaData metaData = connection.getMetaData();
            String connStr = metaData.getURL();
            Pair<SQLTemplates.Builder, DialectProvider.SupportedDatabase> pair = this.getDBTemplate(connStr, metaData);
            if (pair == null) {
                throw new UnsupportedOperationException(String.format("Unable to detect QueryDSL template support for database %s", connStr));
            }
            SQLTemplates templates = this.enrich((SQLTemplates.Builder)pair.left()).build();
            return Pair.pair((Object)templates, (Object)pair.right());
        }
        catch (SQLException e) {
            throw new RuntimeException("Unable to enquire on JDBC metadata to configure QueryDSL", e);
        }
    }

    @Nullable
    Pair<SQLTemplates.Builder, DialectProvider.SupportedDatabase> getDBTemplate(String connStr, DatabaseMetaData metaData) throws SQLException {
        Pair<SQLTemplates.Builder, DialectProvider.SupportedDatabase> sqlTemplatePair = DialectHelper.isSQLServer(connStr) ? DialectHelper.getSQLServerDBTemplate(metaData) : this.getStaticSupportedDBTemplate(connStr);
        if (sqlTemplatePair != null) {
            log.debug("SQL template has been initialized successfully {}", (Object)sqlTemplatePair.toString());
        } else {
            log.warn("System was unable to initialize SQL template for {}", (Object)connStr);
        }
        return sqlTemplatePair;
    }

    @Nullable
    Pair<SQLTemplates.Builder, DialectProvider.SupportedDatabase> getStaticSupportedDBTemplate(String connStr) {
        Pair<SQLTemplates.Builder, DialectProvider.SupportedDatabase> pair = null;
        for (String db : support.keySet()) {
            if (!connStr.contains(db)) continue;
            pair = support.get(db);
            break;
        }
        return pair;
    }

    private DialectProvider.DatabaseInfo buildDatabaseInfo(DialectProvider.SupportedDatabase supportedDatabase, Connection connection) {
        try {
            DatabaseMetaData metaData = connection.getMetaData();
            return new DialectProvider.DatabaseInfo(supportedDatabase, metaData.getDatabaseProductName(), metaData.getDatabaseProductVersion(), metaData.getDatabaseMajorVersion(), metaData.getDatabaseMinorVersion(), metaData.getDriverName(), metaData.getDriverMajorVersion(), metaData.getDriverMinorVersion());
        }
        catch (SQLException e) {
            throw new RuntimeException("Unable to enquire on JDBC metadata to determine DatabaseInfo", e);
        }
    }

    static {
        support.put(":postgresql:", (Pair<SQLTemplates.Builder, DialectProvider.SupportedDatabase>)Pair.pair((Object)PostgreSQLTemplates.builder(), (Object)((Object)DialectProvider.SupportedDatabase.POSTGRESSQL)));
        support.put(":oracle:", (Pair<SQLTemplates.Builder, DialectProvider.SupportedDatabase>)Pair.pair((Object)OracleTemplates.builder(), (Object)((Object)DialectProvider.SupportedDatabase.ORACLE)));
        support.put(":hsqldb:", (Pair<SQLTemplates.Builder, DialectProvider.SupportedDatabase>)Pair.pair((Object)HSQLDBTemplates.builder(), (Object)((Object)DialectProvider.SupportedDatabase.HSQLDB)));
        support.put(":mysql:", (Pair<SQLTemplates.Builder, DialectProvider.SupportedDatabase>)Pair.pair((Object)MySQLTemplates.builder(), (Object)((Object)DialectProvider.SupportedDatabase.MYSQL)));
        support.put(":h2:", (Pair<SQLTemplates.Builder, DialectProvider.SupportedDatabase>)Pair.pair((Object)H2Templates.builder(), (Object)((Object)DialectProvider.SupportedDatabase.H2)));
    }
}

