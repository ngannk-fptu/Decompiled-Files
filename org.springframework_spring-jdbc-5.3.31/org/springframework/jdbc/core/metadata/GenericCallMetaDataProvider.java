/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 *  org.springframework.dao.InvalidDataAccessApiUsageException
 *  org.springframework.lang.Nullable
 *  org.springframework.util.StringUtils
 */
package org.springframework.jdbc.core.metadata;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.jdbc.core.SqlInOutParameter;
import org.springframework.jdbc.core.SqlOutParameter;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.metadata.CallMetaDataProvider;
import org.springframework.jdbc.core.metadata.CallParameterMetaData;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;

public class GenericCallMetaDataProvider
implements CallMetaDataProvider {
    protected static final Log logger = LogFactory.getLog(CallMetaDataProvider.class);
    private final String userName;
    private boolean supportsCatalogsInProcedureCalls = true;
    private boolean supportsSchemasInProcedureCalls = true;
    private boolean storesUpperCaseIdentifiers = true;
    private boolean storesLowerCaseIdentifiers = false;
    private boolean procedureColumnMetaDataUsed = false;
    private final List<CallParameterMetaData> callParameterMetaData = new ArrayList<CallParameterMetaData>();

    protected GenericCallMetaDataProvider(DatabaseMetaData databaseMetaData) throws SQLException {
        this.userName = databaseMetaData.getUserName();
    }

    @Override
    public void initializeWithMetaData(DatabaseMetaData databaseMetaData) throws SQLException {
        block11: {
            block10: {
                block9: {
                    block8: {
                        try {
                            this.setSupportsCatalogsInProcedureCalls(databaseMetaData.supportsCatalogsInProcedureCalls());
                        }
                        catch (SQLException ex) {
                            if (!logger.isWarnEnabled()) break block8;
                            logger.warn((Object)("Error retrieving 'DatabaseMetaData.supportsCatalogsInProcedureCalls': " + ex.getMessage()));
                        }
                    }
                    try {
                        this.setSupportsSchemasInProcedureCalls(databaseMetaData.supportsSchemasInProcedureCalls());
                    }
                    catch (SQLException ex) {
                        if (!logger.isWarnEnabled()) break block9;
                        logger.warn((Object)("Error retrieving 'DatabaseMetaData.supportsSchemasInProcedureCalls': " + ex.getMessage()));
                    }
                }
                try {
                    this.setStoresUpperCaseIdentifiers(databaseMetaData.storesUpperCaseIdentifiers());
                }
                catch (SQLException ex) {
                    if (!logger.isWarnEnabled()) break block10;
                    logger.warn((Object)("Error retrieving 'DatabaseMetaData.storesUpperCaseIdentifiers': " + ex.getMessage()));
                }
            }
            try {
                this.setStoresLowerCaseIdentifiers(databaseMetaData.storesLowerCaseIdentifiers());
            }
            catch (SQLException ex) {
                if (!logger.isWarnEnabled()) break block11;
                logger.warn((Object)("Error retrieving 'DatabaseMetaData.storesLowerCaseIdentifiers': " + ex.getMessage()));
            }
        }
    }

    @Override
    public void initializeWithProcedureColumnMetaData(DatabaseMetaData databaseMetaData, @Nullable String catalogName, @Nullable String schemaName, @Nullable String procedureName) throws SQLException {
        this.procedureColumnMetaDataUsed = true;
        this.processProcedureColumns(databaseMetaData, catalogName, schemaName, procedureName);
    }

    @Override
    public List<CallParameterMetaData> getCallParameterMetaData() {
        return this.callParameterMetaData;
    }

    @Override
    @Nullable
    public String procedureNameToUse(@Nullable String procedureName) {
        return this.identifierNameToUse(procedureName);
    }

    @Override
    @Nullable
    public String catalogNameToUse(@Nullable String catalogName) {
        return this.identifierNameToUse(catalogName);
    }

    @Override
    @Nullable
    public String schemaNameToUse(@Nullable String schemaName) {
        return this.identifierNameToUse(schemaName);
    }

    @Override
    @Nullable
    public String metaDataCatalogNameToUse(@Nullable String catalogName) {
        if (this.isSupportsCatalogsInProcedureCalls()) {
            return this.catalogNameToUse(catalogName);
        }
        return null;
    }

    @Override
    @Nullable
    public String metaDataSchemaNameToUse(@Nullable String schemaName) {
        if (this.isSupportsSchemasInProcedureCalls()) {
            return this.schemaNameToUse(schemaName);
        }
        return null;
    }

    @Override
    @Nullable
    public String parameterNameToUse(@Nullable String parameterName) {
        return this.identifierNameToUse(parameterName);
    }

    @Override
    public boolean byPassReturnParameter(String parameterName) {
        return false;
    }

    @Override
    public SqlParameter createDefaultOutParameter(String parameterName, CallParameterMetaData meta) {
        return new SqlOutParameter(parameterName, meta.getSqlType());
    }

    @Override
    public SqlParameter createDefaultInOutParameter(String parameterName, CallParameterMetaData meta) {
        return new SqlInOutParameter(parameterName, meta.getSqlType());
    }

    @Override
    public SqlParameter createDefaultInParameter(String parameterName, CallParameterMetaData meta) {
        return new SqlParameter(parameterName, meta.getSqlType());
    }

    @Override
    public String getUserName() {
        return this.userName;
    }

    @Override
    public boolean isReturnResultSetSupported() {
        return true;
    }

    @Override
    public boolean isRefCursorSupported() {
        return false;
    }

    @Override
    public int getRefCursorSqlType() {
        return 1111;
    }

    @Override
    public boolean isProcedureColumnMetaDataUsed() {
        return this.procedureColumnMetaDataUsed;
    }

    protected void setSupportsCatalogsInProcedureCalls(boolean supportsCatalogsInProcedureCalls) {
        this.supportsCatalogsInProcedureCalls = supportsCatalogsInProcedureCalls;
    }

    @Override
    public boolean isSupportsCatalogsInProcedureCalls() {
        return this.supportsCatalogsInProcedureCalls;
    }

    protected void setSupportsSchemasInProcedureCalls(boolean supportsSchemasInProcedureCalls) {
        this.supportsSchemasInProcedureCalls = supportsSchemasInProcedureCalls;
    }

    @Override
    public boolean isSupportsSchemasInProcedureCalls() {
        return this.supportsSchemasInProcedureCalls;
    }

    protected void setStoresUpperCaseIdentifiers(boolean storesUpperCaseIdentifiers) {
        this.storesUpperCaseIdentifiers = storesUpperCaseIdentifiers;
    }

    protected boolean isStoresUpperCaseIdentifiers() {
        return this.storesUpperCaseIdentifiers;
    }

    protected void setStoresLowerCaseIdentifiers(boolean storesLowerCaseIdentifiers) {
        this.storesLowerCaseIdentifiers = storesLowerCaseIdentifiers;
    }

    protected boolean isStoresLowerCaseIdentifiers() {
        return this.storesLowerCaseIdentifiers;
    }

    @Nullable
    private String identifierNameToUse(@Nullable String identifierName) {
        if (identifierName == null) {
            return null;
        }
        if (this.isStoresUpperCaseIdentifiers()) {
            return identifierName.toUpperCase();
        }
        if (this.isStoresLowerCaseIdentifiers()) {
            return identifierName.toLowerCase();
        }
        return identifierName;
    }

    private void processProcedureColumns(DatabaseMetaData databaseMetaData, @Nullable String catalogName, @Nullable String schemaName, @Nullable String procedureName) {
        block53: {
            String metaDataCatalogName = this.metaDataCatalogNameToUse(catalogName);
            String metaDataSchemaName = this.metaDataSchemaNameToUse(schemaName);
            String metaDataProcedureName = this.procedureNameToUse(procedureName);
            if (logger.isDebugEnabled()) {
                logger.debug((Object)("Retrieving meta-data for " + metaDataCatalogName + '/' + metaDataSchemaName + '/' + metaDataProcedureName));
            }
            try {
                ArrayList<String> found = new ArrayList<String>();
                boolean function = false;
                try (ResultSet procedures = databaseMetaData.getProcedures(metaDataCatalogName, metaDataSchemaName, metaDataProcedureName);){
                    while (procedures.next()) {
                        found.add(procedures.getString("PROCEDURE_CAT") + '.' + procedures.getString("PROCEDURE_SCHEM") + '.' + procedures.getString("PROCEDURE_NAME"));
                    }
                }
                if (found.isEmpty()) {
                    var11_12 = null;
                    try (ResultSet functions = databaseMetaData.getFunctions(metaDataCatalogName, metaDataSchemaName, metaDataProcedureName);){
                        while (functions.next()) {
                            found.add(functions.getString("FUNCTION_CAT") + '.' + functions.getString("FUNCTION_SCHEM") + '.' + functions.getString("FUNCTION_NAME"));
                            function = true;
                        }
                    }
                    catch (Throwable throwable) {
                        var11_12 = throwable;
                        throw throwable;
                    }
                }
                if (found.size() > 1) {
                    throw new InvalidDataAccessApiUsageException("Unable to determine the correct call signature - multiple signatures for '" + metaDataProcedureName + "': found " + found + " " + (function ? "functions" : "procedures"));
                }
                if (found.isEmpty()) {
                    if (metaDataProcedureName != null && metaDataProcedureName.contains(".") && !StringUtils.hasText((String)metaDataCatalogName)) {
                        String packageName = metaDataProcedureName.substring(0, metaDataProcedureName.indexOf(46));
                        throw new InvalidDataAccessApiUsageException("Unable to determine the correct call signature for '" + metaDataProcedureName + "' - package name should be specified separately using '.withCatalogName(\"" + packageName + "\")'");
                    }
                    if ("Oracle".equals(databaseMetaData.getDatabaseProductName())) {
                        if (logger.isDebugEnabled()) {
                            logger.debug((Object)("Oracle JDBC driver did not return procedure/function/signature for '" + metaDataProcedureName + "' - assuming a non-exposed synonym"));
                        }
                    } else {
                        throw new InvalidDataAccessApiUsageException("Unable to determine the correct call signature - no procedure/function/signature for '" + metaDataProcedureName + "'");
                    }
                }
                if (logger.isDebugEnabled()) {
                    logger.debug((Object)("Retrieving column meta-data for " + (function ? "function" : "procedure") + ' ' + metaDataCatalogName + '/' + metaDataSchemaName + '/' + metaDataProcedureName));
                }
                var11_12 = null;
                try (ResultSet columns = function ? databaseMetaData.getFunctionColumns(metaDataCatalogName, metaDataSchemaName, metaDataProcedureName, null) : databaseMetaData.getProcedureColumns(metaDataCatalogName, metaDataSchemaName, metaDataProcedureName, null);){
                    while (columns.next()) {
                        String columnName = columns.getString("COLUMN_NAME");
                        int columnType = columns.getInt("COLUMN_TYPE");
                        if (columnName == null && GenericCallMetaDataProvider.isInOrOutColumn(columnType, function)) {
                            if (!logger.isDebugEnabled()) continue;
                            logger.debug((Object)("Skipping meta-data for: " + columnType + " " + columns.getInt("DATA_TYPE") + " " + columns.getString("TYPE_NAME") + " " + columns.getInt("NULLABLE") + " (probably a member of a collection)"));
                            continue;
                        }
                        int nullable = function ? 1 : 1;
                        CallParameterMetaData meta = new CallParameterMetaData(function, columnName, columnType, columns.getInt("DATA_TYPE"), columns.getString("TYPE_NAME"), columns.getInt("NULLABLE") == nullable);
                        this.callParameterMetaData.add(meta);
                        if (!logger.isDebugEnabled()) continue;
                        logger.debug((Object)("Retrieved meta-data: " + meta.getParameterName() + " " + meta.getParameterType() + " " + meta.getSqlType() + " " + meta.getTypeName() + " " + meta.isNullable()));
                    }
                }
                catch (Throwable throwable) {
                    var11_12 = throwable;
                    throw throwable;
                }
            }
            catch (SQLException ex) {
                if (!logger.isWarnEnabled()) break block53;
                logger.warn((Object)"Error while retrieving meta-data for procedure columns. Consider declaring explicit parameters -- for example, via SimpleJdbcCall#addDeclaredParameter().", (Throwable)ex);
            }
        }
    }

    private static boolean isInOrOutColumn(int columnType, boolean function) {
        if (function) {
            return columnType == 1 || columnType == 2 || columnType == 3;
        }
        return columnType == 1 || columnType == 2 || columnType == 4;
    }
}

