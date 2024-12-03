/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.tool.schema.extract.internal;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.StringTokenizer;
import org.hibernate.boot.model.naming.DatabaseIdentifier;
import org.hibernate.boot.model.naming.Identifier;
import org.hibernate.tool.schema.extract.internal.AbstractInformationExtractorImpl;
import org.hibernate.tool.schema.extract.internal.ColumnInformationImpl;
import org.hibernate.tool.schema.extract.spi.ExtractionContext;
import org.hibernate.tool.schema.extract.spi.TableInformation;

public class InformationExtractorJdbcDatabaseMetaDataImpl
extends AbstractInformationExtractorImpl {
    public InformationExtractorJdbcDatabaseMetaDataImpl(ExtractionContext extractionContext) {
        super(extractionContext);
    }

    @Override
    protected String getResultSetTableTypesPhysicalTableConstant() {
        return "TABLE";
    }

    @Override
    public <T> T processCatalogsResultSet(ExtractionContext.ResultSetProcessor<T> processor) throws SQLException {
        try (ResultSet resultSet = this.getExtractionContext().getJdbcDatabaseMetaData().getCatalogs();){
            T t = processor.process(resultSet);
            return t;
        }
    }

    @Override
    protected <T> T processSchemaResultSet(String catalog, String schemaPattern, ExtractionContext.ResultSetProcessor<T> processor) throws SQLException {
        try (ResultSet resultSet = this.getExtractionContext().getJdbcDatabaseMetaData().getSchemas(catalog, schemaPattern);){
            T t = processor.process(resultSet);
            return t;
        }
    }

    @Override
    protected <T> T processTableResultSet(String catalog, String schemaPattern, String tableNamePattern, String[] types, ExtractionContext.ResultSetProcessor<T> processor) throws SQLException {
        try (ResultSet resultSet = this.getExtractionContext().getJdbcDatabaseMetaData().getTables(catalog, schemaPattern, tableNamePattern, types);){
            T t = processor.process(resultSet);
            return t;
        }
    }

    @Override
    protected <T> T processColumnsResultSet(String catalog, String schemaPattern, String tableNamePattern, String columnNamePattern, ExtractionContext.ResultSetProcessor<T> processor) throws SQLException {
        try (ResultSet resultSet = this.getExtractionContext().getJdbcDatabaseMetaData().getColumns(catalog, schemaPattern, tableNamePattern, columnNamePattern);){
            T t = processor.process(resultSet);
            return t;
        }
    }

    @Override
    protected <T> T processPrimaryKeysResultSet(String catalogFilter, String schemaFilter, Identifier tableName, ExtractionContext.ResultSetProcessor<T> processor) throws SQLException {
        try (ResultSet resultSet = this.getExtractionContext().getJdbcDatabaseMetaData().getPrimaryKeys(catalogFilter, schemaFilter, tableName.getText());){
            T t = processor.process(resultSet);
            return t;
        }
    }

    @Override
    protected <T> T processIndexInfoResultSet(String catalog, String schema, String table, boolean unique, boolean approximate, ExtractionContext.ResultSetProcessor<T> processor) throws SQLException {
        try (ResultSet resultSet = this.getExtractionContext().getJdbcDatabaseMetaData().getIndexInfo(catalog, schema, table, unique, approximate);){
            T t = processor.process(resultSet);
            return t;
        }
    }

    @Override
    protected <T> T processImportedKeysResultSet(String catalog, String schema, String table, ExtractionContext.ResultSetProcessor<T> processor) throws SQLException {
        try (ResultSet resultSet = this.getExtractionContext().getJdbcDatabaseMetaData().getImportedKeys(catalog, schema, table);){
            T t = processor.process(resultSet);
            return t;
        }
    }

    @Override
    protected void addColumns(TableInformation tableInformation) {
        ExtractionContext extractionContext = this.getExtractionContext();
        String tableName = extractionContext.getSqlStringGenerationContext().format(tableInformation.getName().quote());
        try {
            extractionContext.getQueryResults("select * from " + tableName + " where 1=0", null, resultSet -> {
                ResultSetMetaData metaData = resultSet.getMetaData();
                int columnCount = metaData.getColumnCount();
                for (int i = 1; i <= columnCount; ++i) {
                    String columnName = metaData.getColumnName(i);
                    ColumnInformationImpl columnInformation = new ColumnInformationImpl(tableInformation, DatabaseIdentifier.toIdentifier(columnName), metaData.getColumnType(i), new StringTokenizer(metaData.getColumnTypeName(i), "() ").nextToken(), metaData.getPrecision(i), metaData.getScale(i), this.interpretNullable(metaData.isNullable(i)));
                    tableInformation.addColumn(columnInformation);
                }
                return null;
            });
        }
        catch (SQLException e) {
            throw this.convertSQLException(e, "Error accessing column metadata: " + tableInformation.getName().toString());
        }
    }
}

