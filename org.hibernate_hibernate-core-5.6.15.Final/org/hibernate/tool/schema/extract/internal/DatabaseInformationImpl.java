/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.tool.schema.extract.internal;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import org.hibernate.boot.model.naming.Identifier;
import org.hibernate.boot.model.relational.Namespace;
import org.hibernate.boot.model.relational.QualifiedSequenceName;
import org.hibernate.boot.model.relational.QualifiedTableName;
import org.hibernate.boot.model.relational.SqlStringGenerationContext;
import org.hibernate.engine.jdbc.env.spi.JdbcEnvironment;
import org.hibernate.resource.transaction.spi.DdlTransactionIsolator;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.tool.schema.extract.spi.DatabaseInformation;
import org.hibernate.tool.schema.extract.spi.ExtractionContext;
import org.hibernate.tool.schema.extract.spi.InformationExtractor;
import org.hibernate.tool.schema.extract.spi.NameSpaceTablesInformation;
import org.hibernate.tool.schema.extract.spi.SequenceInformation;
import org.hibernate.tool.schema.extract.spi.TableInformation;
import org.hibernate.tool.schema.spi.SchemaManagementTool;

public class DatabaseInformationImpl
implements DatabaseInformation,
ExtractionContext.DatabaseObjectAccess {
    private final JdbcEnvironment jdbcEnvironment;
    private final SqlStringGenerationContext sqlStringGenerationContext;
    private final ExtractionContext extractionContext;
    private final InformationExtractor extractor;
    private final Map<QualifiedSequenceName, SequenceInformation> sequenceInformationMap = new HashMap<QualifiedSequenceName, SequenceInformation>();

    public DatabaseInformationImpl(ServiceRegistry serviceRegistry, JdbcEnvironment jdbcEnvironment, SqlStringGenerationContext sqlStringGenerationContext, DdlTransactionIsolator ddlTransactionIsolator, SchemaManagementTool tool) throws SQLException {
        this.jdbcEnvironment = jdbcEnvironment;
        this.sqlStringGenerationContext = sqlStringGenerationContext;
        this.extractionContext = tool.getExtractionTool().createExtractionContext(serviceRegistry, jdbcEnvironment, sqlStringGenerationContext, ddlTransactionIsolator, this);
        this.extractor = tool.getExtractionTool().createInformationExtractor(this.extractionContext);
        this.initializeSequences();
    }

    private void initializeSequences() throws SQLException {
        Iterable<SequenceInformation> itr = this.jdbcEnvironment.getDialect().getSequenceInformationExtractor().extractMetadata(this.extractionContext);
        for (SequenceInformation sequenceInformation : itr) {
            this.sequenceInformationMap.put(new QualifiedSequenceName(null, null, sequenceInformation.getSequenceName().getSequenceName()), sequenceInformation);
        }
    }

    @Override
    public boolean catalogExists(Identifier catalog) {
        return this.extractor.catalogExists(this.sqlStringGenerationContext.catalogWithDefault(catalog));
    }

    @Override
    public boolean schemaExists(Namespace.Name namespace) {
        return this.extractor.schemaExists(this.sqlStringGenerationContext.catalogWithDefault(namespace.getCatalog()), this.sqlStringGenerationContext.schemaWithDefault(namespace.getSchema()));
    }

    @Override
    public TableInformation getTableInformation(Identifier catalogName, Identifier schemaName, Identifier tableName) {
        return this.getTableInformation(new QualifiedTableName(catalogName, schemaName, tableName));
    }

    @Override
    public TableInformation getTableInformation(Namespace.Name namespace, Identifier tableName) {
        return this.getTableInformation(new QualifiedTableName(namespace, tableName));
    }

    @Override
    public TableInformation getTableInformation(QualifiedTableName tableName) {
        if (tableName.getObjectName() == null) {
            throw new IllegalArgumentException("Passed table name cannot be null");
        }
        return this.extractor.getTable(this.sqlStringGenerationContext.catalogWithDefault(tableName.getCatalogName()), this.sqlStringGenerationContext.schemaWithDefault(tableName.getSchemaName()), tableName.getTableName());
    }

    @Override
    public NameSpaceTablesInformation getTablesInformation(Namespace namespace) {
        return this.extractor.getTables(this.sqlStringGenerationContext.catalogWithDefault(namespace.getPhysicalName().getCatalog()), this.sqlStringGenerationContext.schemaWithDefault(namespace.getPhysicalName().getSchema()));
    }

    @Override
    public SequenceInformation getSequenceInformation(Identifier catalogName, Identifier schemaName, Identifier sequenceName) {
        return this.getSequenceInformation(new QualifiedSequenceName(catalogName, schemaName, sequenceName));
    }

    @Override
    public SequenceInformation getSequenceInformation(Namespace.Name schemaName, Identifier sequenceName) {
        return this.getSequenceInformation(new QualifiedSequenceName(schemaName, sequenceName));
    }

    @Override
    public SequenceInformation getSequenceInformation(QualifiedSequenceName sequenceName) {
        return this.locateSequenceInformation(sequenceName);
    }

    @Override
    public void cleanup() {
        this.extractionContext.cleanup();
    }

    @Override
    public TableInformation locateTableInformation(QualifiedTableName tableName) {
        return this.getTableInformation(tableName);
    }

    @Override
    public SequenceInformation locateSequenceInformation(QualifiedSequenceName sequenceName) {
        if (sequenceName.getCatalogName() != null || sequenceName.getSchemaName() != null) {
            sequenceName = new QualifiedSequenceName(null, null, sequenceName.getSequenceName());
        }
        return this.sequenceInformationMap.get(sequenceName);
    }
}

