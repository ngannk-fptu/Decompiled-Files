/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 *  org.springframework.dao.InvalidDataAccessApiUsageException
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 *  org.springframework.util.CollectionUtils
 */
package org.springframework.jdbc.core.metadata;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import javax.sql.DataSource;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.jdbc.core.metadata.TableMetaDataProvider;
import org.springframework.jdbc.core.metadata.TableMetaDataProviderFactory;
import org.springframework.jdbc.core.metadata.TableParameterMetaData;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils;
import org.springframework.jdbc.support.JdbcUtils;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

public class TableMetaDataContext {
    protected final Log logger = LogFactory.getLog(this.getClass());
    @Nullable
    private String tableName;
    @Nullable
    private String catalogName;
    @Nullable
    private String schemaName;
    private List<String> tableColumns = new ArrayList<String>();
    private boolean accessTableColumnMetaData = true;
    private boolean overrideIncludeSynonymsDefault = false;
    @Nullable
    private TableMetaDataProvider metaDataProvider;
    private boolean generatedKeyColumnsUsed = false;

    public void setTableName(@Nullable String tableName) {
        this.tableName = tableName;
    }

    @Nullable
    public String getTableName() {
        return this.tableName;
    }

    public void setCatalogName(@Nullable String catalogName) {
        this.catalogName = catalogName;
    }

    @Nullable
    public String getCatalogName() {
        return this.catalogName;
    }

    public void setSchemaName(@Nullable String schemaName) {
        this.schemaName = schemaName;
    }

    @Nullable
    public String getSchemaName() {
        return this.schemaName;
    }

    public void setAccessTableColumnMetaData(boolean accessTableColumnMetaData) {
        this.accessTableColumnMetaData = accessTableColumnMetaData;
    }

    public boolean isAccessTableColumnMetaData() {
        return this.accessTableColumnMetaData;
    }

    public void setOverrideIncludeSynonymsDefault(boolean override) {
        this.overrideIncludeSynonymsDefault = override;
    }

    public boolean isOverrideIncludeSynonymsDefault() {
        return this.overrideIncludeSynonymsDefault;
    }

    public List<String> getTableColumns() {
        return this.tableColumns;
    }

    public void processMetaData(DataSource dataSource, List<String> declaredColumns, String[] generatedKeyNames) {
        this.metaDataProvider = TableMetaDataProviderFactory.createMetaDataProvider(dataSource, this);
        this.tableColumns = this.reconcileColumnsToUse(declaredColumns, generatedKeyNames);
    }

    private TableMetaDataProvider obtainMetaDataProvider() {
        Assert.state((this.metaDataProvider != null ? 1 : 0) != 0, (String)"No TableMetaDataProvider - call processMetaData first");
        return this.metaDataProvider;
    }

    protected List<String> reconcileColumnsToUse(List<String> declaredColumns, String[] generatedKeyNames) {
        if (generatedKeyNames.length > 0) {
            this.generatedKeyColumnsUsed = true;
        }
        if (!declaredColumns.isEmpty()) {
            return new ArrayList<String>(declaredColumns);
        }
        LinkedHashSet<String> keys = new LinkedHashSet<String>(generatedKeyNames.length);
        for (String key : generatedKeyNames) {
            keys.add(key.toUpperCase());
        }
        ArrayList<String> columns = new ArrayList<String>();
        for (TableParameterMetaData meta : this.obtainMetaDataProvider().getTableParameterMetaData()) {
            if (keys.contains(meta.getParameterName().toUpperCase())) continue;
            columns.add(meta.getParameterName());
        }
        return columns;
    }

    public List<Object> matchInParameterValuesWithInsertColumns(SqlParameterSource parameterSource) {
        ArrayList<Object> values = new ArrayList<Object>();
        Map<String, String> caseInsensitiveParameterNames = SqlParameterSourceUtils.extractCaseInsensitiveParameterNames(parameterSource);
        for (String column : this.tableColumns) {
            if (parameterSource.hasValue(column)) {
                values.add(SqlParameterSourceUtils.getTypedValue(parameterSource, column));
                continue;
            }
            String lowerCaseName = column.toLowerCase();
            if (parameterSource.hasValue(lowerCaseName)) {
                values.add(SqlParameterSourceUtils.getTypedValue(parameterSource, lowerCaseName));
                continue;
            }
            String propertyName = JdbcUtils.convertUnderscoreNameToPropertyName(column);
            if (parameterSource.hasValue(propertyName)) {
                values.add(SqlParameterSourceUtils.getTypedValue(parameterSource, propertyName));
                continue;
            }
            if (caseInsensitiveParameterNames.containsKey(lowerCaseName)) {
                values.add(SqlParameterSourceUtils.getTypedValue(parameterSource, caseInsensitiveParameterNames.get(lowerCaseName)));
                continue;
            }
            values.add(null);
        }
        return values;
    }

    public List<Object> matchInParameterValuesWithInsertColumns(Map<String, ?> inParameters) {
        ArrayList<Object> values = new ArrayList<Object>(inParameters.size());
        for (String column : this.tableColumns) {
            Object value = inParameters.get(column);
            if (value == null && (value = inParameters.get(column.toLowerCase())) == null) {
                for (Map.Entry<String, ?> entry : inParameters.entrySet()) {
                    if (!column.equalsIgnoreCase(entry.getKey())) continue;
                    value = entry.getValue();
                    break;
                }
            }
            values.add(value);
        }
        return values;
    }

    public String createInsertString(String ... generatedKeyNames) {
        LinkedHashSet<String> keys = new LinkedHashSet<String>(generatedKeyNames.length);
        for (String key : generatedKeyNames) {
            keys.add(key.toUpperCase());
        }
        StringBuilder insertStatement = new StringBuilder();
        insertStatement.append("INSERT INTO ");
        if (this.getSchemaName() != null) {
            insertStatement.append(this.getSchemaName());
            insertStatement.append('.');
        }
        insertStatement.append(this.getTableName());
        insertStatement.append(" (");
        int columnCount = 0;
        for (String columnName : this.getTableColumns()) {
            if (keys.contains(columnName.toUpperCase())) continue;
            if (++columnCount > 1) {
                insertStatement.append(", ");
            }
            insertStatement.append(columnName);
        }
        insertStatement.append(") VALUES(");
        if (columnCount < 1) {
            if (this.generatedKeyColumnsUsed) {
                if (this.logger.isDebugEnabled()) {
                    this.logger.debug((Object)("Unable to locate non-key columns for table '" + this.getTableName() + "' so an empty insert statement is generated"));
                }
            } else {
                String message = "Unable to locate columns for table '" + this.getTableName() + "' so an insert statement can't be generated.";
                if (this.isAccessTableColumnMetaData()) {
                    message = message + " Consider specifying explicit column names -- for example, via SimpleJdbcInsert#usingColumns().";
                }
                throw new InvalidDataAccessApiUsageException(message);
            }
        }
        String params = String.join((CharSequence)", ", Collections.nCopies(columnCount, "?"));
        insertStatement.append(params);
        insertStatement.append(')');
        return insertStatement.toString();
    }

    public int[] createInsertTypes() {
        int[] types = new int[this.getTableColumns().size()];
        List<TableParameterMetaData> parameters = this.obtainMetaDataProvider().getTableParameterMetaData();
        LinkedHashMap parameterMap = CollectionUtils.newLinkedHashMap((int)parameters.size());
        for (TableParameterMetaData tpmd : parameters) {
            parameterMap.put(tpmd.getParameterName().toUpperCase(), tpmd);
        }
        int typeIndx = 0;
        for (String column : this.getTableColumns()) {
            TableParameterMetaData tpmd;
            types[typeIndx] = column == null ? Integer.MIN_VALUE : ((tpmd = (TableParameterMetaData)parameterMap.get(column.toUpperCase())) != null ? tpmd.getSqlType() : Integer.MIN_VALUE);
            ++typeIndx;
        }
        return types;
    }

    public boolean isGetGeneratedKeysSupported() {
        return this.obtainMetaDataProvider().isGetGeneratedKeysSupported();
    }

    public boolean isGetGeneratedKeysSimulated() {
        return this.obtainMetaDataProvider().isGetGeneratedKeysSimulated();
    }

    @Deprecated
    @Nullable
    public String getSimulationQueryForGetGeneratedKey(String tableName, String keyColumnName) {
        return this.getSimpleQueryForGetGeneratedKey(tableName, keyColumnName);
    }

    @Nullable
    public String getSimpleQueryForGetGeneratedKey(String tableName, String keyColumnName) {
        return this.obtainMetaDataProvider().getSimpleQueryForGetGeneratedKey(tableName, keyColumnName);
    }

    public boolean isGeneratedKeysColumnNameArraySupported() {
        return this.obtainMetaDataProvider().isGeneratedKeysColumnNameArraySupported();
    }
}

