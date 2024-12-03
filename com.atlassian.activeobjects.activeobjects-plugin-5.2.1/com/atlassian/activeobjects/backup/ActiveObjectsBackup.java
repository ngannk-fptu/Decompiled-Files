/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.activeobjects.spi.Backup
 *  com.atlassian.activeobjects.spi.BackupProgressMonitor
 *  com.atlassian.activeobjects.spi.DataSourceProvider
 *  com.atlassian.activeobjects.spi.RestoreProgressMonitor
 */
package com.atlassian.activeobjects.backup;

import com.atlassian.activeobjects.ao.ConverterUtils;
import com.atlassian.activeobjects.ao.PrefixedSchemaConfiguration;
import com.atlassian.activeobjects.backup.ActiveObjectsBackupProgressMonitor;
import com.atlassian.activeobjects.backup.ActiveObjectsDatabaseCleaner;
import com.atlassian.activeobjects.backup.ActiveObjectsForeignKeyCreator;
import com.atlassian.activeobjects.backup.ActiveObjectsRestoreProgressMonitor;
import com.atlassian.activeobjects.backup.ActiveObjectsTableCreator;
import com.atlassian.activeobjects.backup.ActiveObjectsTableReader;
import com.atlassian.activeobjects.backup.DatabaseProviderConnectionProvider;
import com.atlassian.activeobjects.backup.ForeignKeyAroundImporter;
import com.atlassian.activeobjects.backup.OracleSequencesAroundImporter;
import com.atlassian.activeobjects.backup.PostgresSequencesAroundImporter;
import com.atlassian.activeobjects.internal.DatabaseProviderFactory;
import com.atlassian.activeobjects.internal.Prefix;
import com.atlassian.activeobjects.internal.SimplePrefix;
import com.atlassian.activeobjects.osgi.ActiveObjectsServiceFactory;
import com.atlassian.activeobjects.spi.Backup;
import com.atlassian.activeobjects.spi.BackupProgressMonitor;
import com.atlassian.activeobjects.spi.DataSourceProvider;
import com.atlassian.activeobjects.spi.RestoreProgressMonitor;
import com.atlassian.dbexporter.BatchMode;
import com.atlassian.dbexporter.CleanupMode;
import com.atlassian.dbexporter.ConnectionProvider;
import com.atlassian.dbexporter.DatabaseInformation;
import com.atlassian.dbexporter.DbExporter;
import com.atlassian.dbexporter.DbImporter;
import com.atlassian.dbexporter.EntityNameProcessor;
import com.atlassian.dbexporter.ImportExportConfiguration;
import com.atlassian.dbexporter.ImportExportErrorService;
import com.atlassian.dbexporter.exporter.ConnectionProviderInformationReader;
import com.atlassian.dbexporter.exporter.DataExporter;
import com.atlassian.dbexporter.exporter.DatabaseInformationExporter;
import com.atlassian.dbexporter.exporter.ExportConfiguration;
import com.atlassian.dbexporter.exporter.TableDefinitionExporter;
import com.atlassian.dbexporter.importer.DataImporter;
import com.atlassian.dbexporter.importer.DatabaseInformationImporter;
import com.atlassian.dbexporter.importer.ImportConfiguration;
import com.atlassian.dbexporter.importer.SqlServerAroundTableImporter;
import com.atlassian.dbexporter.importer.TableDefinitionImporter;
import com.atlassian.dbexporter.node.stax.StaxStreamReader;
import com.atlassian.dbexporter.node.stax.StaxStreamWriter;
import com.atlassian.dbexporter.progress.ProgressMonitor;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.function.Supplier;
import net.java.ao.DatabaseProvider;
import net.java.ao.SchemaConfiguration;
import net.java.ao.schema.NameConverters;

public final class ActiveObjectsBackup
implements Backup {
    public static final Prefix PREFIX = new SimplePrefix("AO");
    private static final Charset CHARSET = StandardCharsets.UTF_8;
    private static final String NAMESPACE = "http://www.atlassian.com/ao";
    private final Supplier<DatabaseProvider> databaseProviderSupplier;
    private final NameConverters nameConverters;
    private final ImportExportErrorService errorService;
    private final ActiveObjectsServiceFactory aoServiceFactory;

    private static DatabaseProvider getDatabaseProvider(DatabaseProviderFactory databaseProviderFactory, DataSourceProvider dataSourceProvider) {
        return Objects.requireNonNull(databaseProviderFactory).getDatabaseProvider(dataSourceProvider.getDataSource(), dataSourceProvider.getDatabaseType(), dataSourceProvider.getSchema());
    }

    public ActiveObjectsBackup(DatabaseProviderFactory databaseProviderFactory, DataSourceProvider dataSourceProvider, NameConverters converters, ImportExportErrorService errorService, ActiveObjectsServiceFactory aoServiceFactory) {
        this(() -> ActiveObjectsBackup.getDatabaseProvider(databaseProviderFactory, dataSourceProvider), converters, errorService, aoServiceFactory);
    }

    ActiveObjectsBackup(DatabaseProvider databaseProvider, NameConverters converters, ImportExportErrorService errorService, ActiveObjectsServiceFactory aoServiceFactory) {
        this(() -> Objects.requireNonNull(databaseProvider), converters, errorService, aoServiceFactory);
    }

    private ActiveObjectsBackup(Supplier<DatabaseProvider> databaseProviderSupplier, NameConverters converters, ImportExportErrorService errorService, ActiveObjectsServiceFactory aoServiceFactory) {
        this.aoServiceFactory = Objects.requireNonNull(aoServiceFactory);
        this.databaseProviderSupplier = Objects.requireNonNull(databaseProviderSupplier);
        this.nameConverters = Objects.requireNonNull(converters);
        this.errorService = Objects.requireNonNull(errorService);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void save(OutputStream stream, BackupProgressMonitor monitor) {
        DatabaseProvider provider = this.databaseProviderSupplier.get();
        DatabaseProviderConnectionProvider connectionProvider = ActiveObjectsBackup.getConnectionProvider(provider);
        ActiveObjectsExportConfiguration configuration = new ActiveObjectsExportConfiguration(connectionProvider, this.getProgressMonitor(monitor));
        DbExporter dbExporter = new DbExporter(new DatabaseInformationExporter(new ConnectionProviderInformationReader(this.errorService, connectionProvider)), new TableDefinitionExporter(new ActiveObjectsTableReader(this.errorService, this.nameConverters, provider, ActiveObjectsBackup.schemaConfiguration())), new DataExporter(this.errorService, provider.getSchema()));
        StaxStreamWriter streamWriter = null;
        try {
            streamWriter = new StaxStreamWriter(this.errorService, new OutputStreamWriter(stream, CHARSET), CHARSET, NAMESPACE);
            dbExporter.exportData(streamWriter, configuration);
            streamWriter.flush();
        }
        catch (Throwable throwable) {
            ActiveObjectsBackup.closeCloseable(streamWriter);
            throw throwable;
        }
        ActiveObjectsBackup.closeCloseable(streamWriter);
    }

    public static SchemaConfiguration schemaConfiguration() {
        return new PrefixedSchemaConfiguration(PREFIX);
    }

    public void restore(InputStream stream, RestoreProgressMonitor monitor) {
        DatabaseProvider provider = this.databaseProviderSupplier.get();
        DatabaseProviderConnectionProvider connectionProvider = ActiveObjectsBackup.getConnectionProvider(provider);
        DatabaseInformation databaseInformation = this.getDatabaseInformation(connectionProvider);
        ActiveObjectsImportConfiguration configuration = new ActiveObjectsImportConfiguration(connectionProvider, this.getProgressMonitor(monitor), databaseInformation);
        DbImporter dbImporter = new DbImporter(this.errorService, new DatabaseInformationImporter(this.errorService), new TableDefinitionImporter(this.errorService, new ActiveObjectsTableCreator(this.errorService, provider, this.nameConverters), new ActiveObjectsDatabaseCleaner(provider, this.nameConverters, ActiveObjectsBackup.schemaConfiguration(), this.errorService, this.aoServiceFactory)), new DataImporter(this.errorService, provider.getSchema(), (DataImporter.AroundTableImporter)new SqlServerAroundTableImporter(this.errorService, provider.getSchema()), new PostgresSequencesAroundImporter(this.errorService, provider), new OracleSequencesAroundImporter(this.errorService, provider, this.nameConverters), new ForeignKeyAroundImporter(new ActiveObjectsForeignKeyCreator(this.errorService, this.nameConverters, provider))));
        try (StaxStreamReader streamReader = new StaxStreamReader(this.errorService, new InputStreamReader(stream, CHARSET));){
            dbImporter.importData(streamReader, configuration);
        }
    }

    public void clear() {
        DatabaseProvider provider = this.databaseProviderSupplier.get();
        new ActiveObjectsDatabaseCleaner(provider, this.nameConverters, ActiveObjectsBackup.schemaConfiguration(), this.errorService, this.aoServiceFactory).cleanup(CleanupMode.CLEAN);
    }

    private DatabaseInformation getDatabaseInformation(DatabaseProviderConnectionProvider connectionProvider) {
        return new DatabaseInformation(new ConnectionProviderInformationReader(this.errorService, connectionProvider).get());
    }

    private static DatabaseProviderConnectionProvider getConnectionProvider(DatabaseProvider provider) {
        return new DatabaseProviderConnectionProvider(provider);
    }

    private ProgressMonitor getProgressMonitor(BackupProgressMonitor backupProgressMonitor) {
        return new ActiveObjectsBackupProgressMonitor(backupProgressMonitor);
    }

    private ProgressMonitor getProgressMonitor(RestoreProgressMonitor restoreProgressMonitor) {
        return new ActiveObjectsRestoreProgressMonitor(restoreProgressMonitor);
    }

    private static void closeCloseable(Closeable streamWriter) {
        if (streamWriter != null) {
            try {
                streamWriter.close();
            }
            catch (IOException iOException) {
                // empty catch block
            }
        }
    }

    public static final class UpperCaseEntityNameProcessor
    implements EntityNameProcessor {
        @Override
        public String tableName(String tableName) {
            return ConverterUtils.toUpperCase(tableName);
        }

        @Override
        public String columnName(String columnName) {
            return ConverterUtils.toUpperCase(columnName);
        }
    }

    private static final class ActiveObjectsImportConfiguration
    extends ActiveObjectsImportExportConfiguration
    implements ImportConfiguration {
        private final DatabaseInformation databaseInformation;

        ActiveObjectsImportConfiguration(ConnectionProvider connectionProvider, ProgressMonitor progressMonitor, DatabaseInformation databaseInformation) {
            super(connectionProvider, progressMonitor);
            this.databaseInformation = Objects.requireNonNull(databaseInformation);
        }

        @Override
        public DatabaseInformation getDatabaseInformation() {
            return this.databaseInformation;
        }

        @Override
        public CleanupMode getCleanupMode() {
            return CleanupMode.CLEAN;
        }

        @Override
        public BatchMode getBatchMode() {
            return BatchMode.ON;
        }
    }

    private static final class ActiveObjectsExportConfiguration
    extends ActiveObjectsImportExportConfiguration
    implements ExportConfiguration {
        public ActiveObjectsExportConfiguration(ConnectionProvider connectionProvider, ProgressMonitor progressMonitor) {
            super(connectionProvider, progressMonitor);
        }
    }

    private static abstract class ActiveObjectsImportExportConfiguration
    implements ImportExportConfiguration {
        private final ConnectionProvider connectionProvider;
        private final ProgressMonitor progressMonitor;
        private final EntityNameProcessor entityNameProcessor;

        ActiveObjectsImportExportConfiguration(ConnectionProvider connectionProvider, ProgressMonitor progressMonitor) {
            this.connectionProvider = Objects.requireNonNull(connectionProvider);
            this.progressMonitor = Objects.requireNonNull(progressMonitor);
            this.entityNameProcessor = new UpperCaseEntityNameProcessor();
        }

        @Override
        public final ConnectionProvider getConnectionProvider() {
            return this.connectionProvider;
        }

        @Override
        public final ProgressMonitor getProgressMonitor() {
            return this.progressMonitor;
        }

        @Override
        public final EntityNameProcessor getEntityNameProcessor() {
            return this.entityNameProcessor;
        }
    }
}

