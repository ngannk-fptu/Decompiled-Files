/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xalan.lib.sql;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.Statement;
import java.util.Vector;
import org.apache.xalan.extensions.ExpressionContext;
import org.apache.xalan.lib.sql.ConnectionPool;
import org.apache.xalan.lib.sql.DTMDocument;
import org.apache.xalan.lib.sql.QueryParameter;
import org.apache.xalan.lib.sql.SQLQueryParser;
import org.apache.xalan.lib.sql.XConnection;
import org.apache.xml.dtm.DTMManager;
import org.apache.xml.dtm.ref.DTMManagerDefault;
import org.apache.xpath.XPathContext;

public class SQLDocument
extends DTMDocument {
    private boolean DEBUG = false;
    private static final String S_NAMESPACE = "http://xml.apache.org/xalan/SQLExtension";
    private static final String S_SQL = "sql";
    private static final String S_ROW_SET = "row-set";
    private static final String S_METADATA = "metadata";
    private static final String S_COLUMN_HEADER = "column-header";
    private static final String S_ROW = "row";
    private static final String S_COL = "col";
    private static final String S_OUT_PARAMETERS = "out-parameters";
    private static final String S_CATALOGUE_NAME = "catalogue-name";
    private static final String S_DISPLAY_SIZE = "column-display-size";
    private static final String S_COLUMN_LABEL = "column-label";
    private static final String S_COLUMN_NAME = "column-name";
    private static final String S_COLUMN_TYPE = "column-type";
    private static final String S_COLUMN_TYPENAME = "column-typename";
    private static final String S_PRECISION = "precision";
    private static final String S_SCALE = "scale";
    private static final String S_SCHEMA_NAME = "schema-name";
    private static final String S_TABLE_NAME = "table-name";
    private static final String S_CASESENSITIVE = "case-sensitive";
    private static final String S_DEFINITELYWRITABLE = "definitely-writable";
    private static final String S_ISNULLABLE = "nullable";
    private static final String S_ISSIGNED = "signed";
    private static final String S_ISWRITEABLE = "writable";
    private static final String S_ISSEARCHABLE = "searchable";
    private int m_SQL_TypeID = 0;
    private int m_MetaData_TypeID = 0;
    private int m_ColumnHeader_TypeID = 0;
    private int m_RowSet_TypeID = 0;
    private int m_Row_TypeID = 0;
    private int m_Col_TypeID = 0;
    private int m_OutParameter_TypeID = 0;
    private int m_ColAttrib_CATALOGUE_NAME_TypeID = 0;
    private int m_ColAttrib_DISPLAY_SIZE_TypeID = 0;
    private int m_ColAttrib_COLUMN_LABEL_TypeID = 0;
    private int m_ColAttrib_COLUMN_NAME_TypeID = 0;
    private int m_ColAttrib_COLUMN_TYPE_TypeID = 0;
    private int m_ColAttrib_COLUMN_TYPENAME_TypeID = 0;
    private int m_ColAttrib_PRECISION_TypeID = 0;
    private int m_ColAttrib_SCALE_TypeID = 0;
    private int m_ColAttrib_SCHEMA_NAME_TypeID = 0;
    private int m_ColAttrib_TABLE_NAME_TypeID = 0;
    private int m_ColAttrib_CASESENSITIVE_TypeID = 0;
    private int m_ColAttrib_DEFINITELYWRITEABLE_TypeID = 0;
    private int m_ColAttrib_ISNULLABLE_TypeID = 0;
    private int m_ColAttrib_ISSIGNED_TypeID = 0;
    private int m_ColAttrib_ISWRITEABLE_TypeID = 0;
    private int m_ColAttrib_ISSEARCHABLE_TypeID = 0;
    private Statement m_Statement = null;
    private ExpressionContext m_ExpressionContext = null;
    private ConnectionPool m_ConnectionPool = null;
    private ResultSet m_ResultSet = null;
    private SQLQueryParser m_QueryParser = null;
    private int[] m_ColHeadersIdx;
    private int m_ColCount;
    private int m_MetaDataIdx = -1;
    private int m_RowSetIdx = -1;
    private int m_SQLIdx = -1;
    private int m_FirstRowIdx = -1;
    private int m_LastRowIdx = -1;
    private boolean m_StreamingMode = true;
    private boolean m_MultipleResults = false;
    private boolean m_HasErrors = false;
    private boolean m_IsStatementCachingEnabled = false;
    private XConnection m_XConnection = null;

    public SQLDocument(DTMManager mgr, int ident) {
        super(mgr, ident);
    }

    public static SQLDocument getNewDocument(ExpressionContext exprContext) {
        DTMManager mgr = ((XPathContext.XPathExpressionContext)exprContext).getDTMManager();
        DTMManagerDefault mgrDefault = (DTMManagerDefault)mgr;
        int dtmIdent = mgrDefault.getFirstFreeDTMID();
        SQLDocument doc = new SQLDocument(mgr, dtmIdent << 16);
        mgrDefault.addDTM(doc, dtmIdent);
        doc.setExpressionContext(exprContext);
        return doc;
    }

    protected void setExpressionContext(ExpressionContext expr) {
        this.m_ExpressionContext = expr;
    }

    public ExpressionContext getExpressionContext() {
        return this.m_ExpressionContext;
    }

    public void execute(XConnection xconn, SQLQueryParser query) throws SQLException {
        try {
            this.m_StreamingMode = "true".equals(xconn.getFeature("streaming"));
            this.m_MultipleResults = "true".equals(xconn.getFeature("multiple-results"));
            this.m_IsStatementCachingEnabled = "true".equals(xconn.getFeature("cache-statements"));
            this.m_XConnection = xconn;
            this.m_QueryParser = query;
            this.executeSQLStatement();
            this.createExpandedNameTable();
            this.m_DocumentIdx = this.addElement(0, this.m_Document_TypeID, -1, -1);
            this.m_SQLIdx = this.addElement(1, this.m_SQL_TypeID, this.m_DocumentIdx, -1);
            if (!this.m_MultipleResults) {
                this.extractSQLMetaData(this.m_ResultSet.getMetaData());
            }
        }
        catch (SQLException e) {
            this.m_HasErrors = true;
            throw e;
        }
    }

    private void executeSQLStatement() throws SQLException {
        this.m_ConnectionPool = this.m_XConnection.getConnectionPool();
        Connection conn = this.m_ConnectionPool.getConnection();
        if (!this.m_QueryParser.hasParameters()) {
            this.m_Statement = conn.createStatement();
            this.m_ResultSet = this.m_Statement.executeQuery(this.m_QueryParser.getSQLQuery());
        } else if (this.m_QueryParser.isCallable()) {
            CallableStatement cstmt = conn.prepareCall(this.m_QueryParser.getSQLQuery());
            this.m_QueryParser.registerOutputParameters(cstmt);
            this.m_QueryParser.populateStatement(cstmt, this.m_ExpressionContext);
            this.m_Statement = cstmt;
            if (!cstmt.execute()) {
                throw new SQLException("Error in Callable Statement");
            }
            this.m_ResultSet = this.m_Statement.getResultSet();
        } else {
            PreparedStatement stmt = conn.prepareStatement(this.m_QueryParser.getSQLQuery());
            this.m_QueryParser.populateStatement(stmt, this.m_ExpressionContext);
            this.m_Statement = stmt;
            this.m_ResultSet = stmt.executeQuery();
        }
    }

    public void skip(int value) {
        try {
            if (this.m_ResultSet != null) {
                this.m_ResultSet.relative(value);
            }
        }
        catch (Exception origEx) {
            try {
                for (int x = 0; x < value && this.m_ResultSet.next(); ++x) {
                }
            }
            catch (Exception e) {
                this.m_XConnection.setError(origEx, this, this.checkWarnings());
                this.m_XConnection.setError(e, this, this.checkWarnings());
            }
        }
    }

    private void extractSQLMetaData(ResultSetMetaData meta) {
        this.m_MetaDataIdx = this.addElement(1, this.m_MetaData_TypeID, this.m_MultipleResults ? this.m_RowSetIdx : this.m_SQLIdx, -1);
        try {
            this.m_ColCount = meta.getColumnCount();
            this.m_ColHeadersIdx = new int[this.m_ColCount];
        }
        catch (Exception e) {
            this.m_XConnection.setError(e, this, this.checkWarnings());
        }
        int lastColHeaderIdx = -1;
        int i = 1;
        for (i = 1; i <= this.m_ColCount; ++i) {
            this.m_ColHeadersIdx[i - 1] = this.addElement(2, this.m_ColumnHeader_TypeID, this.m_MetaDataIdx, lastColHeaderIdx);
            lastColHeaderIdx = this.m_ColHeadersIdx[i - 1];
            try {
                this.addAttributeToNode(meta.getColumnName(i), this.m_ColAttrib_COLUMN_NAME_TypeID, lastColHeaderIdx);
            }
            catch (Exception e) {
                this.addAttributeToNode("Not Supported", this.m_ColAttrib_COLUMN_NAME_TypeID, lastColHeaderIdx);
            }
            try {
                this.addAttributeToNode(meta.getColumnLabel(i), this.m_ColAttrib_COLUMN_LABEL_TypeID, lastColHeaderIdx);
            }
            catch (Exception e) {
                this.addAttributeToNode("Not Supported", this.m_ColAttrib_COLUMN_LABEL_TypeID, lastColHeaderIdx);
            }
            try {
                this.addAttributeToNode(meta.getCatalogName(i), this.m_ColAttrib_CATALOGUE_NAME_TypeID, lastColHeaderIdx);
            }
            catch (Exception e) {
                this.addAttributeToNode("Not Supported", this.m_ColAttrib_CATALOGUE_NAME_TypeID, lastColHeaderIdx);
            }
            try {
                this.addAttributeToNode(new Integer(meta.getColumnDisplaySize(i)), this.m_ColAttrib_DISPLAY_SIZE_TypeID, lastColHeaderIdx);
            }
            catch (Exception e) {
                this.addAttributeToNode("Not Supported", this.m_ColAttrib_DISPLAY_SIZE_TypeID, lastColHeaderIdx);
            }
            try {
                this.addAttributeToNode(new Integer(meta.getColumnType(i)), this.m_ColAttrib_COLUMN_TYPE_TypeID, lastColHeaderIdx);
            }
            catch (Exception e) {
                this.addAttributeToNode("Not Supported", this.m_ColAttrib_COLUMN_TYPE_TypeID, lastColHeaderIdx);
            }
            try {
                this.addAttributeToNode(meta.getColumnTypeName(i), this.m_ColAttrib_COLUMN_TYPENAME_TypeID, lastColHeaderIdx);
            }
            catch (Exception e) {
                this.addAttributeToNode("Not Supported", this.m_ColAttrib_COLUMN_TYPENAME_TypeID, lastColHeaderIdx);
            }
            try {
                this.addAttributeToNode(new Integer(meta.getPrecision(i)), this.m_ColAttrib_PRECISION_TypeID, lastColHeaderIdx);
            }
            catch (Exception e) {
                this.addAttributeToNode("Not Supported", this.m_ColAttrib_PRECISION_TypeID, lastColHeaderIdx);
            }
            try {
                this.addAttributeToNode(new Integer(meta.getScale(i)), this.m_ColAttrib_SCALE_TypeID, lastColHeaderIdx);
            }
            catch (Exception e) {
                this.addAttributeToNode("Not Supported", this.m_ColAttrib_SCALE_TypeID, lastColHeaderIdx);
            }
            try {
                this.addAttributeToNode(meta.getSchemaName(i), this.m_ColAttrib_SCHEMA_NAME_TypeID, lastColHeaderIdx);
            }
            catch (Exception e) {
                this.addAttributeToNode("Not Supported", this.m_ColAttrib_SCHEMA_NAME_TypeID, lastColHeaderIdx);
            }
            try {
                this.addAttributeToNode(meta.getTableName(i), this.m_ColAttrib_TABLE_NAME_TypeID, lastColHeaderIdx);
            }
            catch (Exception e) {
                this.addAttributeToNode("Not Supported", this.m_ColAttrib_TABLE_NAME_TypeID, lastColHeaderIdx);
            }
            try {
                this.addAttributeToNode(meta.isCaseSensitive(i) ? "true" : "false", this.m_ColAttrib_CASESENSITIVE_TypeID, lastColHeaderIdx);
            }
            catch (Exception e) {
                this.addAttributeToNode("Not Supported", this.m_ColAttrib_CASESENSITIVE_TypeID, lastColHeaderIdx);
            }
            try {
                this.addAttributeToNode(meta.isDefinitelyWritable(i) ? "true" : "false", this.m_ColAttrib_DEFINITELYWRITEABLE_TypeID, lastColHeaderIdx);
            }
            catch (Exception e) {
                this.addAttributeToNode("Not Supported", this.m_ColAttrib_DEFINITELYWRITEABLE_TypeID, lastColHeaderIdx);
            }
            try {
                this.addAttributeToNode(meta.isNullable(i) != 0 ? "true" : "false", this.m_ColAttrib_ISNULLABLE_TypeID, lastColHeaderIdx);
            }
            catch (Exception e) {
                this.addAttributeToNode("Not Supported", this.m_ColAttrib_ISNULLABLE_TypeID, lastColHeaderIdx);
            }
            try {
                this.addAttributeToNode(meta.isSigned(i) ? "true" : "false", this.m_ColAttrib_ISSIGNED_TypeID, lastColHeaderIdx);
            }
            catch (Exception e) {
                this.addAttributeToNode("Not Supported", this.m_ColAttrib_ISSIGNED_TypeID, lastColHeaderIdx);
            }
            try {
                this.addAttributeToNode(meta.isWritable(i) ? "true" : "false", this.m_ColAttrib_ISWRITEABLE_TypeID, lastColHeaderIdx);
            }
            catch (Exception e) {
                this.addAttributeToNode("Not Supported", this.m_ColAttrib_ISWRITEABLE_TypeID, lastColHeaderIdx);
            }
            try {
                this.addAttributeToNode(meta.isSearchable(i) ? "true" : "false", this.m_ColAttrib_ISSEARCHABLE_TypeID, lastColHeaderIdx);
                continue;
            }
            catch (Exception e) {
                this.addAttributeToNode("Not Supported", this.m_ColAttrib_ISSEARCHABLE_TypeID, lastColHeaderIdx);
            }
        }
    }

    @Override
    protected void createExpandedNameTable() {
        super.createExpandedNameTable();
        this.m_SQL_TypeID = this.m_expandedNameTable.getExpandedTypeID(S_NAMESPACE, S_SQL, 1);
        this.m_MetaData_TypeID = this.m_expandedNameTable.getExpandedTypeID(S_NAMESPACE, S_METADATA, 1);
        this.m_ColumnHeader_TypeID = this.m_expandedNameTable.getExpandedTypeID(S_NAMESPACE, S_COLUMN_HEADER, 1);
        this.m_RowSet_TypeID = this.m_expandedNameTable.getExpandedTypeID(S_NAMESPACE, S_ROW_SET, 1);
        this.m_Row_TypeID = this.m_expandedNameTable.getExpandedTypeID(S_NAMESPACE, S_ROW, 1);
        this.m_Col_TypeID = this.m_expandedNameTable.getExpandedTypeID(S_NAMESPACE, S_COL, 1);
        this.m_OutParameter_TypeID = this.m_expandedNameTable.getExpandedTypeID(S_NAMESPACE, S_OUT_PARAMETERS, 1);
        this.m_ColAttrib_CATALOGUE_NAME_TypeID = this.m_expandedNameTable.getExpandedTypeID(S_NAMESPACE, S_CATALOGUE_NAME, 2);
        this.m_ColAttrib_DISPLAY_SIZE_TypeID = this.m_expandedNameTable.getExpandedTypeID(S_NAMESPACE, S_DISPLAY_SIZE, 2);
        this.m_ColAttrib_COLUMN_LABEL_TypeID = this.m_expandedNameTable.getExpandedTypeID(S_NAMESPACE, S_COLUMN_LABEL, 2);
        this.m_ColAttrib_COLUMN_NAME_TypeID = this.m_expandedNameTable.getExpandedTypeID(S_NAMESPACE, S_COLUMN_NAME, 2);
        this.m_ColAttrib_COLUMN_TYPE_TypeID = this.m_expandedNameTable.getExpandedTypeID(S_NAMESPACE, S_COLUMN_TYPE, 2);
        this.m_ColAttrib_COLUMN_TYPENAME_TypeID = this.m_expandedNameTable.getExpandedTypeID(S_NAMESPACE, S_COLUMN_TYPENAME, 2);
        this.m_ColAttrib_PRECISION_TypeID = this.m_expandedNameTable.getExpandedTypeID(S_NAMESPACE, S_PRECISION, 2);
        this.m_ColAttrib_SCALE_TypeID = this.m_expandedNameTable.getExpandedTypeID(S_NAMESPACE, S_SCALE, 2);
        this.m_ColAttrib_SCHEMA_NAME_TypeID = this.m_expandedNameTable.getExpandedTypeID(S_NAMESPACE, S_SCHEMA_NAME, 2);
        this.m_ColAttrib_TABLE_NAME_TypeID = this.m_expandedNameTable.getExpandedTypeID(S_NAMESPACE, S_TABLE_NAME, 2);
        this.m_ColAttrib_CASESENSITIVE_TypeID = this.m_expandedNameTable.getExpandedTypeID(S_NAMESPACE, S_CASESENSITIVE, 2);
        this.m_ColAttrib_DEFINITELYWRITEABLE_TypeID = this.m_expandedNameTable.getExpandedTypeID(S_NAMESPACE, S_DEFINITELYWRITABLE, 2);
        this.m_ColAttrib_ISNULLABLE_TypeID = this.m_expandedNameTable.getExpandedTypeID(S_NAMESPACE, S_ISNULLABLE, 2);
        this.m_ColAttrib_ISSIGNED_TypeID = this.m_expandedNameTable.getExpandedTypeID(S_NAMESPACE, S_ISSIGNED, 2);
        this.m_ColAttrib_ISWRITEABLE_TypeID = this.m_expandedNameTable.getExpandedTypeID(S_NAMESPACE, S_ISWRITEABLE, 2);
        this.m_ColAttrib_ISSEARCHABLE_TypeID = this.m_expandedNameTable.getExpandedTypeID(S_NAMESPACE, S_ISSEARCHABLE, 2);
    }

    private boolean addRowToDTMFromResultSet() {
        try {
            if (this.m_FirstRowIdx == -1) {
                this.m_RowSetIdx = this.addElement(1, this.m_RowSet_TypeID, this.m_SQLIdx, this.m_MultipleResults ? this.m_RowSetIdx : this.m_MetaDataIdx);
                if (this.m_MultipleResults) {
                    this.extractSQLMetaData(this.m_ResultSet.getMetaData());
                }
            }
            if (!this.m_ResultSet.next()) {
                if (this.m_StreamingMode && this.m_LastRowIdx != -1) {
                    this.m_nextsib.setElementAt(-1, this.m_LastRowIdx);
                }
                this.m_ResultSet.close();
                if (this.m_MultipleResults) {
                    while (!this.m_Statement.getMoreResults() && this.m_Statement.getUpdateCount() >= 0) {
                    }
                    this.m_ResultSet = this.m_Statement.getResultSet();
                } else {
                    this.m_ResultSet = null;
                }
                if (this.m_ResultSet != null) {
                    this.m_FirstRowIdx = -1;
                    this.addRowToDTMFromResultSet();
                } else {
                    SQLWarning warn;
                    Vector parameters = this.m_QueryParser.getParameters();
                    if (parameters != null) {
                        int outParamIdx = this.addElement(1, this.m_OutParameter_TypeID, this.m_SQLIdx, this.m_RowSetIdx);
                        int lastColID = -1;
                        for (int indx = 0; indx < parameters.size(); ++indx) {
                            QueryParameter parm = (QueryParameter)parameters.elementAt(indx);
                            if (!parm.isOutput()) continue;
                            Object rawobj = ((CallableStatement)this.m_Statement).getObject(indx + 1);
                            lastColID = this.addElementWithData(rawobj, 2, this.m_Col_TypeID, outParamIdx, lastColID);
                            this.addAttributeToNode(parm.getName(), this.m_ColAttrib_COLUMN_NAME_TypeID, lastColID);
                            this.addAttributeToNode(parm.getName(), this.m_ColAttrib_COLUMN_LABEL_TypeID, lastColID);
                            this.addAttributeToNode(new Integer(parm.getType()), this.m_ColAttrib_COLUMN_TYPE_TypeID, lastColID);
                            this.addAttributeToNode(parm.getTypeName(), this.m_ColAttrib_COLUMN_TYPENAME_TypeID, lastColID);
                        }
                    }
                    if ((warn = this.checkWarnings()) != null) {
                        this.m_XConnection.setError(null, null, warn);
                    }
                }
                return false;
            }
            if (this.m_FirstRowIdx == -1) {
                this.m_LastRowIdx = this.m_FirstRowIdx = this.addElement(2, this.m_Row_TypeID, this.m_RowSetIdx, this.m_MultipleResults ? this.m_MetaDataIdx : -1);
                if (this.m_StreamingMode) {
                    this.m_nextsib.setElementAt(this.m_LastRowIdx, this.m_LastRowIdx);
                }
            } else if (!this.m_StreamingMode) {
                this.m_LastRowIdx = this.addElement(2, this.m_Row_TypeID, this.m_RowSetIdx, this.m_LastRowIdx);
            }
            int colID = this._firstch(this.m_LastRowIdx);
            int pcolID = -1;
            for (int i = 1; i <= this.m_ColCount; ++i) {
                Object o = this.m_ResultSet.getObject(i);
                if (colID == -1) {
                    pcolID = this.addElementWithData(o, 3, this.m_Col_TypeID, this.m_LastRowIdx, pcolID);
                    this.cloneAttributeFromNode(pcolID, this.m_ColHeadersIdx[i - 1]);
                } else {
                    int dataIdent = this._firstch(colID);
                    if (dataIdent == -1) {
                        this.error("Streaming Mode, Data Error");
                    } else {
                        this.m_ObjectArray.setAt(dataIdent, o);
                    }
                }
                if (colID == -1) continue;
                colID = this._nextsib(colID);
            }
        }
        catch (Exception e) {
            if (this.DEBUG) {
                System.out.println("SQL Error Fetching next row [" + e.getLocalizedMessage() + "]");
            }
            this.m_XConnection.setError(e, this, this.checkWarnings());
            this.m_HasErrors = true;
        }
        return true;
    }

    public boolean hasErrors() {
        return this.m_HasErrors;
    }

    public void close(boolean flushConnPool) {
        try {
            SQLWarning warn = this.checkWarnings();
            if (warn != null) {
                this.m_XConnection.setError(null, null, warn);
            }
        }
        catch (Exception warn) {
            // empty catch block
        }
        try {
            if (null != this.m_ResultSet) {
                this.m_ResultSet.close();
                this.m_ResultSet = null;
            }
        }
        catch (Exception warn) {
            // empty catch block
        }
        Connection conn = null;
        try {
            if (null != this.m_Statement) {
                conn = this.m_Statement.getConnection();
                this.m_Statement.close();
                this.m_Statement = null;
            }
        }
        catch (Exception exception) {
            // empty catch block
        }
        try {
            if (conn != null) {
                if (this.m_HasErrors) {
                    this.m_ConnectionPool.releaseConnectionOnError(conn);
                } else {
                    this.m_ConnectionPool.releaseConnection(conn);
                }
            }
        }
        catch (Exception exception) {
            // empty catch block
        }
        this.getManager().release(this, true);
    }

    @Override
    protected boolean nextNode() {
        if (this.DEBUG) {
            System.out.println("nextNode()");
        }
        try {
            return false;
        }
        catch (Exception e) {
            return false;
        }
    }

    @Override
    protected int _nextsib(int identity) {
        if (this.m_ResultSet != null) {
            int id = this._exptype(identity);
            if (this.m_FirstRowIdx == -1) {
                this.addRowToDTMFromResultSet();
            }
            if (id == this.m_Row_TypeID && identity >= this.m_LastRowIdx) {
                if (this.DEBUG) {
                    System.out.println("reading from the ResultSet");
                }
                this.addRowToDTMFromResultSet();
            } else if (this.m_MultipleResults && identity == this.m_RowSetIdx) {
                if (this.DEBUG) {
                    System.out.println("reading for next ResultSet");
                }
                int startIdx = this.m_RowSetIdx;
                while (startIdx == this.m_RowSetIdx && this.m_ResultSet != null) {
                    this.addRowToDTMFromResultSet();
                }
            }
        }
        return super._nextsib(identity);
    }

    @Override
    public void documentRegistration() {
        if (this.DEBUG) {
            System.out.println("Document Registration");
        }
    }

    @Override
    public void documentRelease() {
        if (this.DEBUG) {
            System.out.println("Document Release");
        }
    }

    public SQLWarning checkWarnings() {
        SQLWarning warn = null;
        if (this.m_Statement != null) {
            try {
                warn = this.m_Statement.getWarnings();
                this.m_Statement.clearWarnings();
            }
            catch (SQLException sQLException) {
                // empty catch block
            }
        }
        return warn;
    }
}

