/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.dbexporter.ConnectionProvider
 *  com.atlassian.dbexporter.DbExporter
 *  com.atlassian.dbexporter.ImportExportErrorService
 *  com.atlassian.dbexporter.exporter.ConnectionProviderInformationReader
 *  com.atlassian.dbexporter.exporter.DataExporter
 *  com.atlassian.dbexporter.exporter.DatabaseInformationExporter
 *  com.atlassian.dbexporter.exporter.DatabaseInformationReader
 *  com.atlassian.dbexporter.exporter.ExportConfiguration
 *  com.atlassian.dbexporter.exporter.Exporter
 *  com.atlassian.dbexporter.exporter.TableDefinitionExporter
 *  com.atlassian.dbexporter.exporter.TableReader
 *  com.atlassian.dbexporter.node.NodeStreamWriter
 *  com.atlassian.dbexporter.node.stax.StaxStreamWriter
 *  com.atlassian.sal.spring.connection.SpringHostConnectionAccessor$ConnectionProvider
 *  com.atlassian.spring.container.ContainerManager
 *  com.google.common.base.Charsets
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.transaction.PlatformTransactionManager
 *  org.springframework.transaction.support.TransactionTemplate
 */
package com.atlassian.confluence.upgrade.recovery;

import com.atlassian.confluence.upgrade.recovery.ConfluenceExportConfiguration;
import com.atlassian.confluence.upgrade.recovery.ConfluenceProgressMonitor;
import com.atlassian.confluence.upgrade.recovery.ConfluenceTableReader;
import com.atlassian.confluence.upgrade.recovery.DbDumpException;
import com.atlassian.confluence.upgrade.recovery.EchoEntityNameProcessor;
import com.atlassian.confluence.upgrade.recovery.HibernateConnectionProvider;
import com.atlassian.confluence.upgrade.recovery.RecoveryErrorService;
import com.atlassian.confluence.upgrade.recovery.RecoveryFileGenerator;
import com.atlassian.dbexporter.ConnectionProvider;
import com.atlassian.dbexporter.DbExporter;
import com.atlassian.dbexporter.ImportExportErrorService;
import com.atlassian.dbexporter.exporter.ConnectionProviderInformationReader;
import com.atlassian.dbexporter.exporter.DataExporter;
import com.atlassian.dbexporter.exporter.DatabaseInformationExporter;
import com.atlassian.dbexporter.exporter.DatabaseInformationReader;
import com.atlassian.dbexporter.exporter.ExportConfiguration;
import com.atlassian.dbexporter.exporter.Exporter;
import com.atlassian.dbexporter.exporter.TableDefinitionExporter;
import com.atlassian.dbexporter.exporter.TableReader;
import com.atlassian.dbexporter.node.NodeStreamWriter;
import com.atlassian.dbexporter.node.stax.StaxStreamWriter;
import com.atlassian.sal.spring.connection.SpringHostConnectionAccessor;
import com.atlassian.spring.container.ContainerManager;
import com.google.common.base.Charsets;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.sql.Connection;
import java.util.zip.GZIPOutputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

public class DefaultRecoveryFileGenerator
implements RecoveryFileGenerator {
    private static final Logger log = LoggerFactory.getLogger(DefaultRecoveryFileGenerator.class);
    private static final String NAMESPACE = "http://www.atlassian.com/confluence/upgrade/recovery";
    private final PlatformTransactionManager transactionManager;

    public DefaultRecoveryFileGenerator(PlatformTransactionManager transactionManager) {
        this.transactionManager = transactionManager;
    }

    @Override
    public void generate(File upgradeRecoveryfile) {
        TransactionTemplate transactionTemplate = new TransactionTemplate(this.transactionManager);
        transactionTemplate.setPropagationBehavior(3);
        transactionTemplate.execute(status -> {
            SpringHostConnectionAccessor.ConnectionProvider salConnectionProvider = (SpringHostConnectionAccessor.ConnectionProvider)ContainerManager.getComponent((String)"salConnectionProvider");
            Connection connection = salConnectionProvider.getConnection();
            RecoveryErrorService errorService = new RecoveryErrorService();
            HibernateConnectionProvider connectionProvider = new HibernateConnectionProvider(connection);
            DbExporter dbExporter = new DbExporter(new Exporter[]{new DatabaseInformationExporter((DatabaseInformationReader)new ConnectionProviderInformationReader((ImportExportErrorService)errorService, (ConnectionProvider)connectionProvider)), new TableDefinitionExporter((TableReader)new ConfluenceTableReader(connection)), new DataExporter((ImportExportErrorService)errorService, null)});
            ConfluenceProgressMonitor progressMonitor = new ConfluenceProgressMonitor();
            EchoEntityNameProcessor entityNameProcessor = new EchoEntityNameProcessor();
            ConfluenceExportConfiguration configuration = new ConfluenceExportConfiguration(connectionProvider, progressMonitor, entityNameProcessor);
            try (FileOutputStream fos = new FileOutputStream(upgradeRecoveryfile);
                 OutputStreamWriter writer = new OutputStreamWriter((OutputStream)new GZIPOutputStream(fos), Charsets.UTF_8);
                 StaxStreamWriter streamWriter = new StaxStreamWriter((ImportExportErrorService)errorService, (Writer)writer, Charsets.UTF_8, NAMESPACE);){
                dbExporter.exportData((NodeStreamWriter)streamWriter, (ExportConfiguration)configuration);
            }
            catch (RuntimeException e) {
                throw new DbDumpException("Failed to generate recovery file", e);
            }
            catch (Exception e) {
                throw new DbDumpException("Failed to generate recovery file", e);
            }
            return true;
        });
    }
}

