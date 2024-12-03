/*
 * Decompiled with CFR 0.152.
 */
package net.sourceforge.jtds.jdbc;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.RowIdLifetime;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import net.sourceforge.jtds.jdbc.CachedResultSet;
import net.sourceforge.jtds.jdbc.Driver;
import net.sourceforge.jtds.jdbc.JtdsConnection;
import net.sourceforge.jtds.jdbc.JtdsResultSet;
import net.sourceforge.jtds.jdbc.JtdsStatement;
import net.sourceforge.jtds.jdbc.Messages;
import net.sourceforge.jtds.jdbc.TdsData;
import net.sourceforge.jtds.jdbc.TypeInfo;

public class JtdsDatabaseMetaData
implements DatabaseMetaData {
    static final int sqlStateXOpen = 1;
    private final int tdsVersion;
    private final int serverType;
    private final JtdsConnection connection;
    int sysnameLength = 30;
    Boolean caseSensitive;

    public JtdsDatabaseMetaData(JtdsConnection connection) {
        this.connection = connection;
        this.tdsVersion = connection.getTdsVersion();
        this.serverType = connection.getServerType();
        if (this.tdsVersion >= 3) {
            this.sysnameLength = 128;
        }
    }

    @Override
    public boolean allProceduresAreCallable() throws SQLException {
        return true;
    }

    @Override
    public boolean allTablesAreSelectable() throws SQLException {
        return this.connection.getServerType() == 1;
    }

    @Override
    public boolean dataDefinitionCausesTransactionCommit() throws SQLException {
        return false;
    }

    @Override
    public boolean dataDefinitionIgnoredInTransactions() throws SQLException {
        return false;
    }

    @Override
    public boolean doesMaxRowSizeIncludeBlobs() throws SQLException {
        return false;
    }

    @Override
    public ResultSet getBestRowIdentifier(String catalog, String schema, String table, int scope, boolean nullable) throws SQLException {
        String[] colNames = new String[]{"SCOPE", "COLUMN_NAME", "DATA_TYPE", "TYPE_NAME", "COLUMN_SIZE", "BUFFER_LENGTH", "DECIMAL_DIGITS", "PSEUDO_COLUMN"};
        int[] colTypes = new int[]{5, 12, 4, 12, 4, 4, 5, 5};
        String query = "sp_special_columns ?, ?, ?, ?, ?, ?, ?";
        CallableStatement s = this.connection.prepareCall(this.syscall(catalog, query));
        s.setString(1, table);
        s.setString(2, schema);
        s.setString(3, catalog);
        s.setString(4, "R");
        s.setString(5, "T");
        s.setString(6, "U");
        s.setInt(7, 3);
        JtdsResultSet rs = (JtdsResultSet)s.executeQuery();
        CachedResultSet rsTmp = new CachedResultSet((JtdsStatement)((Object)s), colNames, colTypes);
        rsTmp.moveToInsertRow();
        int colCnt = rs.getMetaData().getColumnCount();
        while (rs.next()) {
            for (int i = 1; i <= colCnt; ++i) {
                if (i == 3) {
                    int type = TypeInfo.normalizeDataType(rs.getInt(i), this.connection.getUseLOBs());
                    rsTmp.updateInt(i, type);
                    continue;
                }
                rsTmp.updateObject(i, rs.getObject(i));
            }
            rsTmp.insertRow();
        }
        rs.close();
        rsTmp.moveToCurrentRow();
        rsTmp.setConcurrency(1007);
        return rsTmp;
    }

    @Override
    public ResultSet getCatalogs() throws SQLException {
        String query = "exec sp_tables '', '', '%', NULL";
        Statement s = this.connection.createStatement();
        JtdsResultSet rs = (JtdsResultSet)s.executeQuery(query);
        rs.setColumnCount(1);
        rs.setColLabel(1, "TABLE_CAT");
        JtdsDatabaseMetaData.upperCaseColumnNames(rs);
        return rs;
    }

    @Override
    public String getCatalogSeparator() throws SQLException {
        return ".";
    }

    @Override
    public String getCatalogTerm() throws SQLException {
        return "database";
    }

    @Override
    public ResultSet getColumnPrivileges(String catalog, String schema, String table, String columnNamePattern) throws SQLException {
        String query = "sp_column_privileges ?, ?, ?, ?";
        CallableStatement s = this.connection.prepareCall(this.syscall(catalog, query));
        s.setString(1, table);
        s.setString(2, schema);
        s.setString(3, catalog);
        s.setString(4, JtdsDatabaseMetaData.processEscapes(columnNamePattern));
        JtdsResultSet rs = (JtdsResultSet)s.executeQuery();
        rs.setColLabel(1, "TABLE_CAT");
        rs.setColLabel(2, "TABLE_SCHEM");
        JtdsDatabaseMetaData.upperCaseColumnNames(rs);
        return rs;
    }

    @Override
    public ResultSet getColumns(String catalog, String schemaPattern, String tableNamePattern, String columnNamePattern) throws SQLException {
        String[] colNames = new String[]{"TABLE_CAT", "TABLE_SCHEM", "TABLE_NAME", "COLUMN_NAME", "DATA_TYPE", "TYPE_NAME", "COLUMN_SIZE", "BUFFER_LENGTH", "DECIMAL_DIGITS", "NUM_PREC_RADIX", "NULLABLE", "REMARKS", "COLUMN_DEF", "SQL_DATA_TYPE", "SQL_DATETIME_SUB", "CHAR_OCTET_LENGTH", "ORDINAL_POSITION", "IS_NULLABLE", "SCOPE_CATALOG", "SCOPE_SCHEMA", "SCOPE_TABLE", "SOURCE_DATA_TYPE", "IS_AUTOINCREMENT"};
        int[] colTypes = new int[]{12, 12, 12, 12, 4, 12, 4, 4, 4, 4, 4, 12, 12, 4, 4, 4, 4, 12, 12, 12, 12, 5, 12};
        String query = "sp_columns ?, ?, ?, ?, ?";
        CallableStatement s = this.connection.prepareCall(this.syscall(catalog, query));
        s.setString(1, JtdsDatabaseMetaData.processEscapes(tableNamePattern));
        s.setString(2, JtdsDatabaseMetaData.processEscapes(schemaPattern));
        s.setString(3, catalog);
        s.setString(4, JtdsDatabaseMetaData.processEscapes(columnNamePattern));
        s.setInt(5, 3);
        JtdsResultSet rs = (JtdsResultSet)s.executeQuery();
        CachedResultSet rsTmp = new CachedResultSet((JtdsStatement)((Object)s), colNames, colTypes);
        rsTmp.moveToInsertRow();
        int colCnt = rs.getMetaData().getColumnCount();
        while (rs.next()) {
            int i;
            String typeName = rs.getString(6);
            if (this.serverType == 2) {
                for (i = 1; i <= 4; ++i) {
                    rsTmp.updateObject(i, rs.getObject(i));
                }
                rsTmp.updateInt(5, TypeInfo.normalizeDataType(rs.getInt(5), this.connection.getUseLOBs()));
                rsTmp.updateString(6, typeName);
                for (i = 8; i <= 12; ++i) {
                    rsTmp.updateObject(i, rs.getObject(i));
                }
                if (colCnt >= 20) {
                    for (i = 13; i <= 18; ++i) {
                        rsTmp.updateObject(i, rs.getObject(i + 2));
                    }
                } else {
                    rsTmp.updateObject(16, rs.getObject(8));
                    rsTmp.updateObject(17, rs.getObject(14));
                }
                if ("image".equals(typeName) || "text".equals(typeName)) {
                    rsTmp.updateInt(7, Integer.MAX_VALUE);
                    rsTmp.updateInt(16, Integer.MAX_VALUE);
                } else if ("univarchar".equals(typeName) || "unichar".equals(typeName)) {
                    rsTmp.updateInt(7, rs.getInt(7) / 2);
                    rsTmp.updateObject(16, rs.getObject(7));
                } else {
                    rsTmp.updateInt(7, rs.getInt(7));
                }
                rsTmp.updateString(23, typeName.toLowerCase().contains("identity") ? "YES" : "NO");
            } else {
                for (i = 1; i <= colCnt; ++i) {
                    if (i == 5) {
                        int type = TypeInfo.normalizeDataType(rs.getInt(i), this.connection.getUseLOBs());
                        rsTmp.updateInt(i, type);
                        continue;
                    }
                    if (i == 19) {
                        rsTmp.updateString(6, TdsData.getMSTypeName(rs.getString(6), rs.getInt(19)));
                        continue;
                    }
                    rsTmp.updateObject(i, rs.getObject(i));
                }
                rsTmp.updateString(23, typeName.toLowerCase().contains("identity") ? "YES" : "NO");
            }
            rsTmp.insertRow();
        }
        rs.close();
        rsTmp.moveToCurrentRow();
        rsTmp.setConcurrency(1007);
        return rsTmp;
    }

    @Override
    public ResultSet getCrossReference(String primaryCatalog, String primarySchema, String primaryTable, String foreignCatalog, String foreignSchema, String foreignTable) throws SQLException {
        String[] colNames = new String[]{"PKTABLE_CAT", "PKTABLE_SCHEM", "PKTABLE_NAME", "PKCOLUMN_NAME", "FKTABLE_CAT", "FKTABLE_SCHEM", "FKTABLE_NAME", "FKCOLUMN_NAME", "KEY_SEQ", "UPDATE_RULE", "DELETE_RULE", "FK_NAME", "PK_NAME", "DEFERRABILITY"};
        int[] colTypes = new int[]{12, 12, 12, 12, 12, 12, 12, 12, 5, 5, 5, 12, 12, 5};
        String query = "sp_fkeys ?, ?, ?, ?, ?, ?";
        query = primaryCatalog != null ? this.syscall(primaryCatalog, query) : (foreignCatalog != null ? this.syscall(foreignCatalog, query) : this.syscall(null, query));
        CallableStatement s = this.connection.prepareCall(query);
        s.setString(1, primaryTable);
        s.setString(2, JtdsDatabaseMetaData.processEscapes(primarySchema));
        s.setString(3, primaryCatalog);
        s.setString(4, foreignTable);
        s.setString(5, JtdsDatabaseMetaData.processEscapes(foreignSchema));
        s.setString(6, foreignCatalog);
        JtdsResultSet rs = (JtdsResultSet)s.executeQuery();
        int colCnt = rs.getMetaData().getColumnCount();
        CachedResultSet rsTmp = new CachedResultSet((JtdsStatement)((Object)s), colNames, colTypes);
        rsTmp.moveToInsertRow();
        while (rs.next()) {
            for (int i = 1; i <= colCnt; ++i) {
                rsTmp.updateObject(i, rs.getObject(i));
            }
            if (colCnt < 14) {
                rsTmp.updateShort(14, (short)7);
            }
            rsTmp.insertRow();
        }
        rs.close();
        rsTmp.moveToCurrentRow();
        rsTmp.setConcurrency(1007);
        return rsTmp;
    }

    @Override
    public String getDatabaseProductName() throws SQLException {
        return this.connection.getDatabaseProductName();
    }

    @Override
    public String getDatabaseProductVersion() throws SQLException {
        return this.connection.getDatabaseProductVersion();
    }

    @Override
    public int getDefaultTransactionIsolation() throws SQLException {
        return 2;
    }

    @Override
    public int getDriverMajorVersion() {
        return 1;
    }

    @Override
    public int getDriverMinorVersion() {
        return 3;
    }

    @Override
    public String getDriverName() throws SQLException {
        return "jTDS Type 4 JDBC Driver for MS SQL Server and Sybase";
    }

    @Override
    public String getDriverVersion() throws SQLException {
        return Driver.getVersion();
    }

    @Override
    public ResultSet getExportedKeys(String catalog, String schema, String table) throws SQLException {
        return this.getCrossReference(catalog, schema, table, null, null, null);
    }

    @Override
    public String getExtraNameCharacters() throws SQLException {
        return "$#@";
    }

    @Override
    public String getIdentifierQuoteString() throws SQLException {
        return "\"";
    }

    @Override
    public ResultSet getImportedKeys(String catalog, String schema, String table) throws SQLException {
        return this.getCrossReference(null, null, null, catalog, schema, table);
    }

    @Override
    public ResultSet getIndexInfo(String catalog, String schema, String table, boolean unique, boolean approximate) throws SQLException {
        String[] colNames = new String[]{"TABLE_CAT", "TABLE_SCHEM", "TABLE_NAME", "NON_UNIQUE", "INDEX_QUALIFIER", "INDEX_NAME", "TYPE", "ORDINAL_POSITION", "COLUMN_NAME", "ASC_OR_DESC", "CARDINALITY", "PAGES", "FILTER_CONDITION"};
        int[] colTypes = new int[]{12, 12, 12, -7, 12, 12, 5, 5, 12, 12, 4, 4, 12};
        String query = "sp_statistics ?, ?, ?, ?, ?, ?";
        CallableStatement s = this.connection.prepareCall(this.syscall(catalog, query));
        s.setString(1, table);
        s.setString(2, schema);
        s.setString(3, catalog);
        s.setString(4, "%");
        s.setString(5, unique ? "Y" : "N");
        s.setString(6, approximate ? "Q" : "E");
        JtdsResultSet rs = (JtdsResultSet)s.executeQuery();
        int colCnt = rs.getMetaData().getColumnCount();
        CachedResultSet rsTmp = new CachedResultSet((JtdsStatement)((Object)s), colNames, colTypes);
        rsTmp.moveToInsertRow();
        while (rs.next()) {
            for (int i = 1; i <= colCnt; ++i) {
                rsTmp.updateObject(i, rs.getObject(i));
            }
            rsTmp.insertRow();
        }
        rs.close();
        rsTmp.moveToCurrentRow();
        rsTmp.setConcurrency(1007);
        return rsTmp;
    }

    @Override
    public int getMaxBinaryLiteralLength() throws SQLException {
        return 131072;
    }

    @Override
    public int getMaxCatalogNameLength() throws SQLException {
        return this.sysnameLength;
    }

    @Override
    public int getMaxCharLiteralLength() throws SQLException {
        return 131072;
    }

    @Override
    public int getMaxColumnNameLength() throws SQLException {
        return this.sysnameLength;
    }

    @Override
    public int getMaxColumnsInGroupBy() throws SQLException {
        return this.tdsVersion >= 3 ? 0 : 16;
    }

    @Override
    public int getMaxColumnsInIndex() throws SQLException {
        return 16;
    }

    @Override
    public int getMaxColumnsInOrderBy() throws SQLException {
        return this.tdsVersion >= 3 ? 0 : 16;
    }

    @Override
    public int getMaxColumnsInSelect() throws SQLException {
        return 4096;
    }

    @Override
    public int getMaxColumnsInTable() throws SQLException {
        return this.tdsVersion >= 3 ? 1024 : 250;
    }

    @Override
    public int getMaxConnections() throws SQLException {
        return Short.MAX_VALUE;
    }

    @Override
    public int getMaxCursorNameLength() throws SQLException {
        return this.sysnameLength;
    }

    @Override
    public int getMaxIndexLength() throws SQLException {
        return this.tdsVersion >= 3 ? 900 : 255;
    }

    @Override
    public int getMaxProcedureNameLength() throws SQLException {
        return this.sysnameLength;
    }

    @Override
    public int getMaxRowSize() throws SQLException {
        return this.tdsVersion >= 3 ? 8060 : 1962;
    }

    @Override
    public int getMaxSchemaNameLength() throws SQLException {
        return this.sysnameLength;
    }

    @Override
    public int getMaxStatementLength() throws SQLException {
        return 0;
    }

    @Override
    public int getMaxStatements() throws SQLException {
        return 0;
    }

    @Override
    public int getMaxTableNameLength() throws SQLException {
        return this.sysnameLength;
    }

    @Override
    public int getMaxTablesInSelect() throws SQLException {
        return this.tdsVersion > 2 ? 256 : 16;
    }

    @Override
    public int getMaxUserNameLength() throws SQLException {
        return this.sysnameLength;
    }

    @Override
    public String getNumericFunctions() throws SQLException {
        return "abs,acos,asin,atan,atan2,ceiling,cos,cot,degrees,exp,floor,log,log10,mod,pi,power,radians,rand,round,sign,sin,sqrt,tan";
    }

    @Override
    public ResultSet getPrimaryKeys(String catalog, String schema, String table) throws SQLException {
        String[] colNames = new String[]{"TABLE_CAT", "TABLE_SCHEM", "TABLE_NAME", "COLUMN_NAME", "KEY_SEQ", "PK_NAME"};
        int[] colTypes = new int[]{12, 12, 12, 12, 5, 12};
        String query = "sp_pkeys ?, ?, ?";
        CallableStatement s = this.connection.prepareCall(this.syscall(catalog, query));
        s.setString(1, table);
        s.setString(2, schema);
        s.setString(3, catalog);
        JtdsResultSet rs = (JtdsResultSet)s.executeQuery();
        CachedResultSet rsTmp = new CachedResultSet((JtdsStatement)((Object)s), colNames, colTypes);
        rsTmp.moveToInsertRow();
        int colCnt = rs.getMetaData().getColumnCount();
        while (rs.next()) {
            for (int i = 1; i <= colCnt; ++i) {
                rsTmp.updateObject(i, rs.getObject(i));
            }
            rsTmp.insertRow();
        }
        rs.close();
        rsTmp.moveToCurrentRow();
        rsTmp.setConcurrency(1007);
        return rsTmp;
    }

    @Override
    public ResultSet getProcedureColumns(String catalog, String schemaPattern, String procedureNamePattern, String columnNamePattern) throws SQLException {
        String[] colNames = new String[]{"PROCEDURE_CAT", "PROCEDURE_SCHEM", "PROCEDURE_NAME", "COLUMN_NAME", "COLUMN_TYPE", "DATA_TYPE", "TYPE_NAME", "PRECISION", "LENGTH", "SCALE", "RADIX", "NULLABLE", "REMARKS"};
        int[] colTypes = new int[]{12, 12, 12, 12, 5, 4, 12, 4, 4, 5, 5, 5, 12};
        String query = "sp_sproc_columns ?, ?, ?, ?, ?";
        CallableStatement s = this.connection.prepareCall(this.syscall(catalog, query));
        s.setString(1, JtdsDatabaseMetaData.processEscapes(procedureNamePattern));
        s.setString(2, JtdsDatabaseMetaData.processEscapes(schemaPattern));
        s.setString(3, catalog);
        s.setString(4, JtdsDatabaseMetaData.processEscapes(columnNamePattern));
        s.setInt(5, 3);
        JtdsResultSet rs = (JtdsResultSet)s.executeQuery();
        ResultSetMetaData rsmd = rs.getMetaData();
        CachedResultSet rsTmp = new CachedResultSet((JtdsStatement)((Object)s), colNames, colTypes);
        rsTmp.moveToInsertRow();
        while (rs.next()) {
            String colName;
            String mode;
            int offset = 0;
            int i = 1;
            while (i + offset <= colNames.length) {
                if (i == 5 && !"column_type".equalsIgnoreCase(rsmd.getColumnName(i))) {
                    String colName2 = rs.getString(4);
                    if ("RETURN_VALUE".equals(colName2)) {
                        rsTmp.updateInt(i, 5);
                    } else {
                        rsTmp.updateInt(i, 0);
                    }
                    offset = 1;
                }
                if (i == 3) {
                    int pos;
                    String name = rs.getString(i);
                    if (name != null && name.length() > 0 && (pos = name.lastIndexOf(59)) >= 0) {
                        name = name.substring(0, pos);
                    }
                    rsTmp.updateString(i + offset, name);
                } else if ("data_type".equalsIgnoreCase(rsmd.getColumnName(i))) {
                    int type = TypeInfo.normalizeDataType(rs.getInt(i), this.connection.getUseLOBs());
                    rsTmp.updateInt(i + offset, type);
                } else {
                    rsTmp.updateObject(i + offset, rs.getObject(i));
                }
                ++i;
            }
            if (this.serverType == 2 && rsmd.getColumnCount() >= 22 && (mode = rs.getString(22)) != null) {
                if (mode.equalsIgnoreCase("in")) {
                    rsTmp.updateInt(5, 1);
                } else if (mode.equalsIgnoreCase("out")) {
                    rsTmp.updateInt(5, 2);
                }
            }
            if ((this.serverType == 2 || this.tdsVersion == 1 || this.tdsVersion == 3) && "RETURN_VALUE".equals(colName = rs.getString(4))) {
                rsTmp.updateString(4, "@RETURN_VALUE");
            }
            rsTmp.insertRow();
        }
        rs.close();
        rsTmp.moveToCurrentRow();
        rsTmp.setConcurrency(1007);
        return rsTmp;
    }

    @Override
    public ResultSet getProcedures(String catalog, String schemaPattern, String procedureNamePattern) throws SQLException {
        String[] colNames = new String[]{"PROCEDURE_CAT", "PROCEDURE_SCHEM", "PROCEDURE_NAME", "RESERVED_1", "RESERVED_2", "RESERVED_3", "REMARKS", "PROCEDURE_TYPE"};
        int[] colTypes = new int[]{12, 12, 12, 4, 4, 4, 12, 5};
        String query = "sp_stored_procedures ?, ?, ?";
        CallableStatement s = this.connection.prepareCall(this.syscall(catalog, query));
        s.setString(1, JtdsDatabaseMetaData.processEscapes(procedureNamePattern));
        s.setString(2, JtdsDatabaseMetaData.processEscapes(schemaPattern));
        s.setString(3, catalog);
        JtdsResultSet rs = (JtdsResultSet)s.executeQuery();
        CachedResultSet rsTmp = new CachedResultSet((JtdsStatement)((Object)s), colNames, colTypes);
        rsTmp.moveToInsertRow();
        int colCnt = rs.getMetaData().getColumnCount();
        while (rs.next()) {
            rsTmp.updateString(1, rs.getString(1));
            rsTmp.updateString(2, rs.getString(2));
            String name = rs.getString(3);
            if (name != null && name.endsWith(";1")) {
                name = name.substring(0, name.length() - 2);
            }
            rsTmp.updateString(3, name);
            for (int i = 4; i <= colCnt; ++i) {
                rsTmp.updateObject(i, rs.getObject(i));
            }
            if (colCnt < 8) {
                rsTmp.updateShort(8, (short)2);
            }
            rsTmp.insertRow();
        }
        rsTmp.moveToCurrentRow();
        rsTmp.setConcurrency(1007);
        rs.close();
        return rsTmp;
    }

    @Override
    public String getProcedureTerm() throws SQLException {
        return "stored procedure";
    }

    @Override
    public ResultSet getSchemas() throws SQLException {
        String sql;
        Statement statement = this.connection.createStatement();
        if (this.connection.getServerType() == 1 && this.connection.getDatabaseMajorVersion() >= 9) {
            sql = "SELECT name AS TABLE_SCHEM, NULL as TABLE_CATALOG FROM sys.schemas";
        } else {
            sql = "SELECT name AS TABLE_SCHEM, NULL as TABLE_CATALOG FROM dbo.sysusers";
            sql = this.tdsVersion >= 3 ? sql + " WHERE islogin=1" : sql + " WHERE uid>0";
        }
        sql = sql + " ORDER BY TABLE_SCHEM";
        return statement.executeQuery(sql);
    }

    @Override
    public String getSchemaTerm() throws SQLException {
        return "owner";
    }

    @Override
    public String getSearchStringEscape() throws SQLException {
        return "\\";
    }

    @Override
    public String getSQLKeywords() throws SQLException {
        return "ARITH_OVERFLOW,BREAK,BROWSE,BULK,CHAR_CONVERT,CHECKPOINT,CLUSTERED,COMPUTE,CONFIRM,CONTROLROW,DATA_PGS,DATABASE,DBCC,DISK,DUMMY,DUMP,ENDTRAN,ERRLVL,ERRORDATA,ERROREXIT,EXIT,FILLFACTOR,HOLDLOCK,IDENTITY_INSERT,IF,INDEX,KILL,LINENO,LOAD,MAX_ROWS_PER_PAGE,MIRROR,MIRROREXIT,NOHOLDLOCK,NONCLUSTERED,NUMERIC_TRUNCATION,OFF,OFFSETS,ONCE,ONLINE,OVER,PARTITION,PERM,PERMANENT,PLAN,PRINT,PROC,PROCESSEXIT,RAISERROR,READ,READTEXT,RECONFIGURE,REPLACE,RESERVED_PGS,RETURN,ROLE,ROWCNT,ROWCOUNT,RULE,SAVE,SETUSER,SHARED,SHUTDOWN,SOME,STATISTICS,STRIPE,SYB_IDENTITY,SYB_RESTREE,SYB_TERMINATE,TEMP,TEXTSIZE,TRAN,TRIGGER,TRUNCATE,TSEQUAL,UNPARTITION,USE,USED_PGS,USER_OPTION,WAITFOR,WHILE,WRITETEXT";
    }

    @Override
    public String getStringFunctions() throws SQLException {
        if (this.connection.getServerType() == 1) {
            return "ascii,char,concat,difference,insert,lcase,left,length,locate,ltrim,repeat,replace,right,rtrim,soundex,space,substring,ucase";
        }
        return "ascii,char,concat,difference,insert,lcase,length,ltrim,repeat,right,rtrim,soundex,space,substring,ucase";
    }

    @Override
    public String getSystemFunctions() throws SQLException {
        return "database,ifnull,user,convert";
    }

    @Override
    public ResultSet getTablePrivileges(String catalog, String schemaPattern, String tableNamePattern) throws SQLException {
        String query = "sp_table_privileges ?, ?, ?";
        CallableStatement s = this.connection.prepareCall(this.syscall(catalog, query));
        s.setString(1, JtdsDatabaseMetaData.processEscapes(tableNamePattern));
        s.setString(2, JtdsDatabaseMetaData.processEscapes(schemaPattern));
        s.setString(3, catalog);
        JtdsResultSet rs = (JtdsResultSet)s.executeQuery();
        rs.setColLabel(1, "TABLE_CAT");
        rs.setColLabel(2, "TABLE_SCHEM");
        JtdsDatabaseMetaData.upperCaseColumnNames(rs);
        return rs;
    }

    @Override
    public ResultSet getTables(String catalog, String schemaPattern, String tableNamePattern, String[] types) throws SQLException {
        String[] colNames = new String[]{"TABLE_CAT", "TABLE_SCHEM", "TABLE_NAME", "TABLE_TYPE", "REMARKS", "TYPE_CAT", "TYPE_SCHEM", "TYPE_NAME", "SELF_REFERENCING_COL_NAME", "REF_GENERATION"};
        int[] colTypes = new int[]{12, 12, 12, 12, 12, 12, 12, 12, 12, 12};
        String query = "sp_tables ?, ?, ?, ?";
        CallableStatement cstmt = this.connection.prepareCall(this.syscall(catalog, query));
        cstmt.setString(1, JtdsDatabaseMetaData.processEscapes(tableNamePattern));
        cstmt.setString(2, JtdsDatabaseMetaData.processEscapes(schemaPattern));
        cstmt.setString(3, catalog);
        if (types == null) {
            cstmt.setString(4, null);
        } else {
            StringBuilder buf = new StringBuilder(64);
            buf.append('\"');
            for (int i = 0; i < types.length; ++i) {
                buf.append('\'').append(types[i]).append("',");
            }
            if (buf.length() > 1) {
                buf.setLength(buf.length() - 1);
            }
            buf.append('\"');
            cstmt.setString(4, buf.toString());
        }
        JtdsResultSet rs = (JtdsResultSet)cstmt.executeQuery();
        CachedResultSet rsTmp = new CachedResultSet((JtdsStatement)((Object)cstmt), colNames, colTypes);
        rsTmp.moveToInsertRow();
        int colCnt = rs.getMetaData().getColumnCount();
        while (rs.next()) {
            for (int i = 1; i <= colCnt; ++i) {
                rsTmp.updateObject(i, rs.getObject(i));
            }
            rsTmp.insertRow();
        }
        rsTmp.moveToCurrentRow();
        rsTmp.setConcurrency(1007);
        rs.close();
        return rsTmp;
    }

    @Override
    public ResultSet getTableTypes() throws SQLException {
        String sql = "select 'SYSTEM TABLE' TABLE_TYPE union select 'TABLE' TABLE_TYPE union select 'VIEW' TABLE_TYPE order by TABLE_TYPE";
        Statement stmt = this.connection.createStatement();
        return stmt.executeQuery(sql);
    }

    @Override
    public String getTimeDateFunctions() throws SQLException {
        return "curdate,curtime,dayname,dayofmonth,dayofweek,dayofyear,hour,minute,month,monthname,now,quarter,timestampadd,timestampdiff,second,week,year";
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public ResultSet getTypeInfo() throws SQLException {
        JtdsResultSet rs;
        Statement s = this.connection.createStatement();
        try {
            rs = (JtdsResultSet)s.executeQuery("exec sp_datatype_info @ODBCVer=3");
        }
        catch (SQLException ex) {
            s.close();
            throw ex;
        }
        try {
            CachedResultSet cachedResultSet = JtdsDatabaseMetaData.createTypeInfoResultSet(rs, this.connection.getUseLOBs());
            return cachedResultSet;
        }
        finally {
            rs.close();
        }
    }

    @Override
    public ResultSet getUDTs(String catalog, String schemaPattern, String typeNamePattern, int[] types) throws SQLException {
        String[] colNames = new String[]{"TYPE_CAT", "TYPE_SCHEM", "TYPE_NAME", "CLASS_NAME", "DATA_TYPE", "REMARKS", "BASE_TYPE"};
        int[] colTypes = new int[]{12, 12, 12, 12, 4, 12, 5};
        JtdsStatement dummyStmt = (JtdsStatement)this.connection.createStatement();
        CachedResultSet rs = new CachedResultSet(dummyStmt, colNames, colTypes);
        rs.setConcurrency(1007);
        return rs;
    }

    @Override
    public String getURL() throws SQLException {
        return this.connection.getURL();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public String getUserName() throws SQLException {
        Statement s = null;
        ResultSet rs = null;
        String result = "";
        try {
            s = this.connection.createStatement();
            rs = this.connection.getServerType() == 2 ? s.executeQuery("select suser_name()") : s.executeQuery("select system_user");
            if (!rs.next()) {
                throw new SQLException(Messages.get("error.dbmeta.nouser"), "HY000");
            }
            result = rs.getString(1);
        }
        finally {
            if (rs != null) {
                rs.close();
            }
            if (s != null) {
                s.close();
            }
        }
        return result;
    }

    @Override
    public ResultSet getVersionColumns(String catalog, String schema, String table) throws SQLException {
        String[] colNames = new String[]{"SCOPE", "COLUMN_NAME", "DATA_TYPE", "TYPE_NAME", "COLUMN_SIZE", "BUFFER_LENGTH", "DECIMAL_DIGITS", "PSEUDO_COLUMN"};
        int[] colTypes = new int[]{5, 12, 4, 12, 4, 4, 5, 5};
        String query = "sp_special_columns ?, ?, ?, ?, ?, ?, ?";
        CallableStatement s = this.connection.prepareCall(this.syscall(catalog, query));
        s.setString(1, table);
        s.setString(2, schema);
        s.setString(3, catalog);
        s.setString(4, "V");
        s.setString(5, "C");
        s.setString(6, "O");
        s.setInt(7, 3);
        JtdsResultSet rs = (JtdsResultSet)s.executeQuery();
        CachedResultSet rsTmp = new CachedResultSet((JtdsStatement)((Object)s), colNames, colTypes);
        rsTmp.moveToInsertRow();
        int colCnt = rs.getMetaData().getColumnCount();
        while (rs.next()) {
            for (int i = 1; i <= colCnt; ++i) {
                rsTmp.updateObject(i, rs.getObject(i));
            }
            rsTmp.insertRow();
        }
        rsTmp.moveToCurrentRow();
        rsTmp.setConcurrency(1007);
        rs.close();
        return rsTmp;
    }

    @Override
    public boolean isCatalogAtStart() throws SQLException {
        return true;
    }

    @Override
    public boolean isReadOnly() throws SQLException {
        return false;
    }

    @Override
    public Connection getConnection() throws SQLException {
        return this.connection;
    }

    @Override
    public boolean nullPlusNonNullIsNull() throws SQLException {
        return true;
    }

    @Override
    public boolean nullsAreSortedAtEnd() throws SQLException {
        return false;
    }

    @Override
    public boolean nullsAreSortedAtStart() throws SQLException {
        return false;
    }

    @Override
    public boolean nullsAreSortedHigh() throws SQLException {
        return false;
    }

    @Override
    public boolean nullsAreSortedLow() throws SQLException {
        return true;
    }

    @Override
    public boolean storesLowerCaseIdentifiers() throws SQLException {
        return false;
    }

    @Override
    public boolean storesLowerCaseQuotedIdentifiers() throws SQLException {
        return false;
    }

    @Override
    public boolean storesMixedCaseIdentifiers() throws SQLException {
        this.setCaseSensitiveFlag();
        return this.caseSensitive == false;
    }

    @Override
    public boolean storesMixedCaseQuotedIdentifiers() throws SQLException {
        this.setCaseSensitiveFlag();
        return this.caseSensitive == false;
    }

    @Override
    public boolean storesUpperCaseIdentifiers() throws SQLException {
        return false;
    }

    @Override
    public boolean storesUpperCaseQuotedIdentifiers() throws SQLException {
        return false;
    }

    @Override
    public boolean supportsAlterTableWithAddColumn() throws SQLException {
        return true;
    }

    @Override
    public boolean supportsAlterTableWithDropColumn() throws SQLException {
        return true;
    }

    @Override
    public boolean supportsANSI92EntryLevelSQL() throws SQLException {
        return true;
    }

    @Override
    public boolean supportsANSI92FullSQL() throws SQLException {
        return false;
    }

    @Override
    public boolean supportsANSI92IntermediateSQL() throws SQLException {
        return false;
    }

    @Override
    public boolean supportsCatalogsInDataManipulation() throws SQLException {
        return true;
    }

    @Override
    public boolean supportsCatalogsInIndexDefinitions() throws SQLException {
        return true;
    }

    @Override
    public boolean supportsCatalogsInPrivilegeDefinitions() throws SQLException {
        return true;
    }

    @Override
    public boolean supportsCatalogsInProcedureCalls() throws SQLException {
        return true;
    }

    @Override
    public boolean supportsCatalogsInTableDefinitions() throws SQLException {
        return true;
    }

    @Override
    public boolean supportsColumnAliasing() throws SQLException {
        return true;
    }

    @Override
    public boolean supportsConvert() throws SQLException {
        return true;
    }

    @Override
    public boolean supportsConvert(int fromType, int toType) throws SQLException {
        if (fromType == toType) {
            return true;
        }
        switch (fromType) {
            case -7: 
            case -6: 
            case -5: 
            case 2: 
            case 3: 
            case 4: 
            case 5: 
            case 6: 
            case 7: 
            case 8: 
            case 91: 
            case 92: 
            case 93: {
                return toType != -1 && toType != -4 && toType != 2004 && toType != 2005;
            }
            case -3: 
            case -2: {
                return toType != 6 && toType != 7 && toType != 8;
            }
            case -4: 
            case 2004: {
                return toType == -2 || toType == -3 || toType == 2004 || toType == -4;
            }
            case -1: 
            case 2005: {
                return toType == 1 || toType == 12 || toType == 2005 || toType == -1;
            }
            case 0: 
            case 1: 
            case 12: {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean supportsCoreSQLGrammar() throws SQLException {
        return true;
    }

    @Override
    public boolean supportsCorrelatedSubqueries() throws SQLException {
        return true;
    }

    @Override
    public boolean supportsDataDefinitionAndDataManipulationTransactions() throws SQLException {
        return true;
    }

    @Override
    public boolean supportsDataManipulationTransactionsOnly() throws SQLException {
        return false;
    }

    @Override
    public boolean supportsDifferentTableCorrelationNames() throws SQLException {
        return false;
    }

    @Override
    public boolean supportsExpressionsInOrderBy() throws SQLException {
        return true;
    }

    @Override
    public boolean supportsExtendedSQLGrammar() throws SQLException {
        return false;
    }

    @Override
    public boolean supportsFullOuterJoins() throws SQLException {
        if (this.connection.getServerType() == 2) {
            return this.getDatabaseMajorVersion() >= 12;
        }
        return true;
    }

    @Override
    public boolean supportsGroupBy() throws SQLException {
        return true;
    }

    @Override
    public boolean supportsGroupByBeyondSelect() throws SQLException {
        return true;
    }

    @Override
    public boolean supportsGroupByUnrelated() throws SQLException {
        return true;
    }

    @Override
    public boolean supportsIntegrityEnhancementFacility() throws SQLException {
        return false;
    }

    @Override
    public boolean supportsLikeEscapeClause() throws SQLException {
        return true;
    }

    @Override
    public boolean supportsLimitedOuterJoins() throws SQLException {
        return true;
    }

    @Override
    public boolean supportsMinimumSQLGrammar() throws SQLException {
        return true;
    }

    @Override
    public boolean supportsMixedCaseIdentifiers() throws SQLException {
        this.setCaseSensitiveFlag();
        return this.caseSensitive;
    }

    @Override
    public boolean supportsMixedCaseQuotedIdentifiers() throws SQLException {
        this.setCaseSensitiveFlag();
        return this.caseSensitive;
    }

    @Override
    public boolean supportsMultipleResultSets() throws SQLException {
        return true;
    }

    @Override
    public boolean supportsMultipleTransactions() throws SQLException {
        return true;
    }

    @Override
    public boolean supportsNonNullableColumns() throws SQLException {
        return true;
    }

    @Override
    public boolean supportsOpenCursorsAcrossCommit() throws SQLException {
        return true;
    }

    @Override
    public boolean supportsOpenCursorsAcrossRollback() throws SQLException {
        return this.connection.getServerType() == 2;
    }

    @Override
    public boolean supportsOpenStatementsAcrossCommit() throws SQLException {
        return true;
    }

    @Override
    public boolean supportsOpenStatementsAcrossRollback() throws SQLException {
        return true;
    }

    @Override
    public boolean supportsOrderByUnrelated() throws SQLException {
        return true;
    }

    @Override
    public boolean supportsOuterJoins() throws SQLException {
        return true;
    }

    @Override
    public boolean supportsPositionedDelete() throws SQLException {
        return true;
    }

    @Override
    public boolean supportsPositionedUpdate() throws SQLException {
        return true;
    }

    @Override
    public boolean supportsSchemasInDataManipulation() throws SQLException {
        return true;
    }

    @Override
    public boolean supportsSchemasInIndexDefinitions() throws SQLException {
        return true;
    }

    @Override
    public boolean supportsSchemasInPrivilegeDefinitions() throws SQLException {
        return true;
    }

    @Override
    public boolean supportsSchemasInProcedureCalls() throws SQLException {
        return true;
    }

    @Override
    public boolean supportsSchemasInTableDefinitions() throws SQLException {
        return true;
    }

    @Override
    public boolean supportsSelectForUpdate() throws SQLException {
        return false;
    }

    @Override
    public boolean supportsStoredProcedures() throws SQLException {
        return true;
    }

    @Override
    public boolean supportsSubqueriesInComparisons() throws SQLException {
        return true;
    }

    @Override
    public boolean supportsSubqueriesInExists() throws SQLException {
        return true;
    }

    @Override
    public boolean supportsSubqueriesInIns() throws SQLException {
        return true;
    }

    @Override
    public boolean supportsSubqueriesInQuantifieds() throws SQLException {
        return true;
    }

    @Override
    public boolean supportsTableCorrelationNames() throws SQLException {
        return true;
    }

    @Override
    public boolean supportsTransactionIsolationLevel(int level) throws SQLException {
        switch (level) {
            case 1: 
            case 2: 
            case 4: 
            case 8: {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean supportsTransactions() throws SQLException {
        return true;
    }

    @Override
    public boolean supportsUnion() throws SQLException {
        return true;
    }

    @Override
    public boolean supportsUnionAll() throws SQLException {
        return true;
    }

    @Override
    public boolean usesLocalFilePerTable() throws SQLException {
        return false;
    }

    @Override
    public boolean usesLocalFiles() throws SQLException {
        return false;
    }

    @Override
    public boolean supportsResultSetType(int type) throws SQLException {
        return type >= 1003 && type <= 1006;
    }

    @Override
    public boolean supportsResultSetConcurrency(int type, int concurrency) throws SQLException {
        if (!this.supportsResultSetType(type)) {
            return false;
        }
        if (concurrency < 1007 || concurrency > 1010) {
            return false;
        }
        return type != 1004 || concurrency == 1007;
    }

    @Override
    public boolean ownUpdatesAreVisible(int type) throws SQLException {
        return true;
    }

    @Override
    public boolean ownDeletesAreVisible(int type) throws SQLException {
        return true;
    }

    @Override
    public boolean ownInsertsAreVisible(int type) throws SQLException {
        return true;
    }

    @Override
    public boolean othersUpdatesAreVisible(int type) throws SQLException {
        return type >= 1005;
    }

    @Override
    public boolean othersDeletesAreVisible(int type) throws SQLException {
        return type >= 1005;
    }

    @Override
    public boolean othersInsertsAreVisible(int type) throws SQLException {
        return type == 1006;
    }

    @Override
    public boolean updatesAreDetected(int type) throws SQLException {
        return false;
    }

    @Override
    public boolean deletesAreDetected(int type) throws SQLException {
        return true;
    }

    @Override
    public boolean insertsAreDetected(int type) throws SQLException {
        return false;
    }

    @Override
    public boolean supportsBatchUpdates() throws SQLException {
        return true;
    }

    private void setCaseSensitiveFlag() throws SQLException {
        if (this.caseSensitive == null) {
            Statement s = this.connection.createStatement();
            ResultSet rs = s.executeQuery("sp_server_info 16");
            rs.next();
            this.caseSensitive = "MIXED".equalsIgnoreCase(rs.getString(3)) ? Boolean.FALSE : Boolean.TRUE;
            s.close();
        }
    }

    @Override
    public ResultSet getAttributes(String catalog, String schemaPattern, String typeNamePattern, String attributeNamePattern) throws SQLException {
        String[] colNames = new String[]{"TYPE_CAT", "TYPE_SCHEM", "TYPE_NAME", "ATTR_NAME", "DATA_TYPE", "ATTR_TYPE_NAME", "ATTR_SIZE", "DECIMAL_DIGITS", "NUM_PREC_RADIX", "NULLABLE", "REMARKS", "ATTR_DEF", "SQL_DATA_TYPE", "SQL_DATETIME_SUB", "CHAR_OCTET_LENGTH", "ORDINAL_POSITION", "IS_NULLABLE", "SCOPE_CATALOG", "SCOPE_SCHEMA", "SCOPE_TABLE", "SOURCE_DATA_TYPE"};
        int[] colTypes = new int[]{12, 12, 12, 12, 4, 12, 4, 4, 4, 4, 12, 12, 4, 4, 4, 4, 12, 12, 12, 12, 5};
        JtdsStatement dummyStmt = (JtdsStatement)this.connection.createStatement();
        CachedResultSet rs = new CachedResultSet(dummyStmt, colNames, colTypes);
        rs.setConcurrency(1007);
        return rs;
    }

    @Override
    public int getDatabaseMajorVersion() throws SQLException {
        return this.connection.getDatabaseMajorVersion();
    }

    @Override
    public int getDatabaseMinorVersion() throws SQLException {
        return this.connection.getDatabaseMinorVersion();
    }

    @Override
    public int getJDBCMajorVersion() throws SQLException {
        return 3;
    }

    @Override
    public int getJDBCMinorVersion() throws SQLException {
        return 0;
    }

    @Override
    public int getResultSetHoldability() throws SQLException {
        return 1;
    }

    @Override
    public int getSQLStateType() throws SQLException {
        return 1;
    }

    @Override
    public ResultSet getSuperTables(String catalog, String schemaPattern, String tableNamePattern) throws SQLException {
        String[] colNames = new String[]{"TABLE_CAT", "TABLE_SCHEM", "TABLE_NAME", "SUPERTABLE_NAME"};
        int[] colTypes = new int[]{12, 12, 12, 12};
        JtdsStatement dummyStmt = (JtdsStatement)this.connection.createStatement();
        CachedResultSet rs = new CachedResultSet(dummyStmt, colNames, colTypes);
        rs.setConcurrency(1007);
        return rs;
    }

    @Override
    public ResultSet getSuperTypes(String catalog, String schemaPattern, String typeNamePattern) throws SQLException {
        String[] colNames = new String[]{"TYPE_CAT", "TYPE_SCHEM", "TYPE_NAME", "SUPERTYPE_CAT", "SUPERTYPE_SCHEM", "SUPERTYPE_NAME"};
        int[] colTypes = new int[]{12, 12, 12, 12, 12, 12};
        JtdsStatement dummyStmt = (JtdsStatement)this.connection.createStatement();
        CachedResultSet rs = new CachedResultSet(dummyStmt, colNames, colTypes);
        rs.setConcurrency(1007);
        return rs;
    }

    @Override
    public boolean locatorsUpdateCopy() throws SQLException {
        return true;
    }

    @Override
    public boolean supportsGetGeneratedKeys() throws SQLException {
        return true;
    }

    @Override
    public boolean supportsMultipleOpenResults() throws SQLException {
        return true;
    }

    @Override
    public boolean supportsNamedParameters() throws SQLException {
        return true;
    }

    @Override
    public boolean supportsResultSetHoldability(int param) throws SQLException {
        return false;
    }

    @Override
    public boolean supportsSavepoints() throws SQLException {
        return true;
    }

    @Override
    public boolean supportsStatementPooling() throws SQLException {
        return true;
    }

    private static String processEscapes(String pattern) {
        int escChar = 92;
        if (pattern == null || pattern.indexOf(92) == -1) {
            return pattern;
        }
        int len = pattern.length();
        StringBuilder buf = new StringBuilder(len + 10);
        for (int i = 0; i < len; ++i) {
            if (pattern.charAt(i) != '\\') {
                buf.append(pattern.charAt(i));
                continue;
            }
            if (i >= len - 1) continue;
            buf.append('[');
            buf.append(pattern.charAt(++i));
            buf.append(']');
        }
        return buf.toString();
    }

    private String syscall(String catalog, String call) {
        StringBuilder sql = new StringBuilder(30 + call.length());
        sql.append("{call ");
        if (catalog != null) {
            if (this.tdsVersion >= 3) {
                sql.append('[').append(catalog).append(']');
            } else {
                sql.append(catalog);
            }
            sql.append("..");
        }
        sql.append(call).append('}');
        return sql.toString();
    }

    private static void upperCaseColumnNames(JtdsResultSet results) throws SQLException {
        ResultSetMetaData rsmd = results.getMetaData();
        int cnt = rsmd.getColumnCount();
        for (int i = 1; i <= cnt; ++i) {
            String name = rsmd.getColumnLabel(i);
            if (name == null || name.length() <= 0) continue;
            results.setColLabel(i, name.toUpperCase());
        }
    }

    private static CachedResultSet createTypeInfoResultSet(JtdsResultSet rs, boolean useLOBs) throws SQLException {
        CachedResultSet result = new CachedResultSet(rs, false);
        if (result.getMetaData().getColumnCount() > 18) {
            result.setColumnCount(18);
        }
        result.setColLabel(3, "PRECISION");
        result.setColLabel(11, "FIXED_PREC_SCALE");
        JtdsDatabaseMetaData.upperCaseColumnNames(result);
        result.setConcurrency(1008);
        result.moveToInsertRow();
        for (TypeInfo ti : JtdsDatabaseMetaData.getSortedTypes(rs, useLOBs)) {
            ti.update(result);
            result.insertRow();
        }
        result.moveToCurrentRow();
        result.setConcurrency(1007);
        return result;
    }

    private static Collection getSortedTypes(ResultSet rs, boolean useLOBs) throws SQLException {
        ArrayList<TypeInfo> types = new ArrayList<TypeInfo>(40);
        while (rs.next()) {
            types.add(new TypeInfo(rs, useLOBs));
        }
        Collections.sort(types);
        return types;
    }

    @Override
    public boolean autoCommitFailureClosesAllResultSets() throws SQLException {
        throw new AbstractMethodError();
    }

    @Override
    public ResultSet getClientInfoProperties() throws SQLException {
        throw new AbstractMethodError();
    }

    @Override
    public ResultSet getFunctionColumns(String catalog, String schemaPattern, String functionNamePattern, String columnNamePattern) throws SQLException {
        throw new AbstractMethodError();
    }

    @Override
    public ResultSet getFunctions(String catalog, String schemaPattern, String functionNamePattern) throws SQLException {
        throw new AbstractMethodError();
    }

    @Override
    public RowIdLifetime getRowIdLifetime() throws SQLException {
        throw new AbstractMethodError();
    }

    @Override
    public ResultSet getSchemas(String catalog, String schemaPattern) throws SQLException {
        throw new AbstractMethodError();
    }

    @Override
    public boolean supportsStoredFunctionsUsingCallSyntax() throws SQLException {
        throw new AbstractMethodError();
    }

    public boolean isWrapperFor(Class arg0) throws SQLException {
        throw new AbstractMethodError();
    }

    public Object unwrap(Class arg0) throws SQLException {
        throw new AbstractMethodError();
    }

    @Override
    public ResultSet getPseudoColumns(String catalog, String schemaPattern, String tableNamePattern, String columnNamePattern) throws SQLException {
        throw new AbstractMethodError();
    }

    @Override
    public boolean generatedKeyAlwaysReturned() throws SQLException {
        throw new AbstractMethodError();
    }
}

