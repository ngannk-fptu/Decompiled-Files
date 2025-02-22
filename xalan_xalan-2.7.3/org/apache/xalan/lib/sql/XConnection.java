/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xalan.lib.sql;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.Vector;
import javax.xml.transform.ErrorListener;
import javax.xml.transform.TransformerException;
import org.apache.xalan.extensions.ExpressionContext;
import org.apache.xalan.lib.sql.ConnectionPool;
import org.apache.xalan.lib.sql.ConnectionPoolManager;
import org.apache.xalan.lib.sql.DefaultConnectionPool;
import org.apache.xalan.lib.sql.JNDIConnectionPool;
import org.apache.xalan.lib.sql.QueryParameter;
import org.apache.xalan.lib.sql.SQLDocument;
import org.apache.xalan.lib.sql.SQLErrorDocument;
import org.apache.xalan.lib.sql.SQLQueryParser;
import org.apache.xml.dtm.DTM;
import org.apache.xml.dtm.DTMIterator;
import org.apache.xml.dtm.DTMManager;
import org.apache.xml.dtm.ref.DTMManagerDefault;
import org.apache.xml.dtm.ref.DTMNodeIterator;
import org.apache.xml.dtm.ref.DTMNodeProxy;
import org.apache.xpath.XPathContext;
import org.apache.xpath.objects.XBooleanStatic;
import org.apache.xpath.objects.XNodeSet;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class XConnection {
    private static final boolean DEBUG = false;
    private ConnectionPool m_ConnectionPool = null;
    private Connection m_Connection = null;
    private boolean m_DefaultPoolingEnabled = false;
    private Vector m_OpenSQLDocuments = new Vector();
    private ConnectionPoolManager m_PoolMgr = new ConnectionPoolManager();
    private Vector m_ParameterList = new Vector();
    private Exception m_Error = null;
    private SQLDocument m_LastSQLDocumentWithError = null;
    private boolean m_FullErrors = false;
    private SQLQueryParser m_QueryParser = new SQLQueryParser();
    private boolean m_IsDefaultPool = false;
    private boolean m_IsStreamingEnabled = true;
    private boolean m_InlineVariables = false;
    private boolean m_IsMultipleResultsEnabled = false;
    private boolean m_IsStatementCachingEnabled = false;

    public XConnection() {
    }

    public XConnection(ExpressionContext exprContext, String connPoolName) {
        this.connect(exprContext, connPoolName);
    }

    public XConnection(ExpressionContext exprContext, String driver, String dbURL) {
        this.connect(exprContext, driver, dbURL);
    }

    public XConnection(ExpressionContext exprContext, NodeList list) {
        this.connect(exprContext, list);
    }

    public XConnection(ExpressionContext exprContext, String driver, String dbURL, String user, String password) {
        this.connect(exprContext, driver, dbURL, user, password);
    }

    public XConnection(ExpressionContext exprContext, String driver, String dbURL, Element protocolElem) {
        this.connect(exprContext, driver, dbURL, protocolElem);
    }

    public XBooleanStatic connect(ExpressionContext exprContext, String name) {
        try {
            this.m_ConnectionPool = this.m_PoolMgr.getPool(name);
            if (this.m_ConnectionPool == null) {
                JNDIConnectionPool pool = new JNDIConnectionPool(name);
                if (pool.testConnection()) {
                    this.m_PoolMgr.registerPool(name, pool);
                    this.m_ConnectionPool = pool;
                    this.m_IsDefaultPool = false;
                    return new XBooleanStatic(true);
                }
                throw new IllegalArgumentException("Invalid ConnectionPool name or JNDI Datasource path: " + name);
            }
            this.m_IsDefaultPool = false;
            return new XBooleanStatic(true);
        }
        catch (Exception e) {
            this.setError(e, exprContext);
            return new XBooleanStatic(false);
        }
    }

    public XBooleanStatic connect(ExpressionContext exprContext, String driver, String dbURL) {
        try {
            this.init(driver, dbURL, new Properties());
            return new XBooleanStatic(true);
        }
        catch (SQLException e) {
            this.setError(e, exprContext);
            return new XBooleanStatic(false);
        }
        catch (Exception e) {
            this.setError(e, exprContext);
            return new XBooleanStatic(false);
        }
    }

    public XBooleanStatic connect(ExpressionContext exprContext, Element protocolElem) {
        try {
            this.initFromElement(protocolElem);
            return new XBooleanStatic(true);
        }
        catch (SQLException e) {
            this.setError(e, exprContext);
            return new XBooleanStatic(false);
        }
        catch (Exception e) {
            this.setError(e, exprContext);
            return new XBooleanStatic(false);
        }
    }

    public XBooleanStatic connect(ExpressionContext exprContext, NodeList list) {
        try {
            this.initFromElement((Element)list.item(0));
            return new XBooleanStatic(true);
        }
        catch (SQLException e) {
            this.setError(e, exprContext);
            return new XBooleanStatic(false);
        }
        catch (Exception e) {
            this.setError(e, exprContext);
            return new XBooleanStatic(false);
        }
    }

    public XBooleanStatic connect(ExpressionContext exprContext, String driver, String dbURL, String user, String password) {
        try {
            Properties prop = new Properties();
            prop.put("user", user);
            prop.put("password", password);
            this.init(driver, dbURL, prop);
            return new XBooleanStatic(true);
        }
        catch (SQLException e) {
            this.setError(e, exprContext);
            return new XBooleanStatic(false);
        }
        catch (Exception e) {
            this.setError(e, exprContext);
            return new XBooleanStatic(false);
        }
    }

    public XBooleanStatic connect(ExpressionContext exprContext, String driver, String dbURL, Element protocolElem) {
        try {
            Properties prop = new Properties();
            NamedNodeMap atts = protocolElem.getAttributes();
            for (int i = 0; i < atts.getLength(); ++i) {
                prop.put(atts.item(i).getNodeName(), atts.item(i).getNodeValue());
            }
            this.init(driver, dbURL, prop);
            return new XBooleanStatic(true);
        }
        catch (SQLException e) {
            this.setError(e, exprContext);
            return new XBooleanStatic(false);
        }
        catch (Exception e) {
            this.setError(e, exprContext);
            return new XBooleanStatic(false);
        }
    }

    private void initFromElement(Element e) throws SQLException {
        Properties prop = new Properties();
        String driver = "";
        String dbURL = "";
        Node n = e.getFirstChild();
        if (null == n) {
            return;
        }
        do {
            Node n1;
            String s;
            Node n12;
            String nName;
            if ((nName = n.getNodeName()).equalsIgnoreCase("dbdriver")) {
                driver = "";
                n12 = n.getFirstChild();
                if (null != n12) {
                    driver = n12.getNodeValue();
                }
            }
            if (nName.equalsIgnoreCase("dburl")) {
                dbURL = "";
                n12 = n.getFirstChild();
                if (null != n12) {
                    dbURL = n12.getNodeValue();
                }
            }
            if (nName.equalsIgnoreCase("password")) {
                s = "";
                n1 = n.getFirstChild();
                if (null != n1) {
                    s = n1.getNodeValue();
                }
                prop.put("password", s);
            }
            if (nName.equalsIgnoreCase("user")) {
                s = "";
                n1 = n.getFirstChild();
                if (null != n1) {
                    s = n1.getNodeValue();
                }
                prop.put("user", s);
            }
            if (!nName.equalsIgnoreCase("protocol")) continue;
            String Name2 = "";
            NamedNodeMap attrs = n.getAttributes();
            Node n13 = attrs.getNamedItem("name");
            if (null == n13) continue;
            String s2 = "";
            Name2 = n13.getNodeValue();
            Node n2 = n.getFirstChild();
            if (null != n2) {
                s2 = n2.getNodeValue();
            }
            prop.put(Name2, s2);
        } while ((n = n.getNextSibling()) != null);
        this.init(driver, dbURL, prop);
    }

    private void init(String driver, String dbURL, Properties prop) throws SQLException {
        String poolName;
        ConnectionPool cpool;
        String passwd;
        Connection con = null;
        String user = prop.getProperty("user");
        if (user == null) {
            user = "";
        }
        if ((passwd = prop.getProperty("password")) == null) {
            passwd = "";
        }
        if ((cpool = this.m_PoolMgr.getPool(poolName = driver + dbURL + user + passwd)) == null) {
            DefaultConnectionPool defpool = new DefaultConnectionPool();
            defpool.setDriver(driver);
            defpool.setURL(dbURL);
            defpool.setProtocol(prop);
            if (this.m_DefaultPoolingEnabled) {
                defpool.setPoolEnabled(true);
            }
            this.m_PoolMgr.registerPool(poolName, defpool);
            this.m_ConnectionPool = defpool;
        } else {
            this.m_ConnectionPool = cpool;
        }
        this.m_IsDefaultPool = true;
        try {
            con = this.m_ConnectionPool.getConnection();
        }
        catch (SQLException e) {
            if (con != null) {
                this.m_ConnectionPool.releaseConnectionOnError(con);
                con = null;
            }
            throw e;
        }
        finally {
            if (con != null) {
                this.m_ConnectionPool.releaseConnection(con);
            }
        }
    }

    public ConnectionPool getConnectionPool() {
        return this.m_ConnectionPool;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public DTM query(ExpressionContext exprContext, String queryString) {
        SQLDocument doc;
        block7: {
            doc = null;
            if (null != this.m_ConnectionPool) break block7;
            DTM dTM = null;
            return dTM;
        }
        try {
            SQLQueryParser query = this.m_QueryParser.parse(this, queryString, 1);
            doc = SQLDocument.getNewDocument(exprContext);
            doc.execute(this, query);
            this.m_OpenSQLDocuments.addElement(doc);
        }
        catch (Exception e) {
            if (doc != null) {
                if (doc.hasErrors()) {
                    this.setError(e, doc, doc.checkWarnings());
                }
                doc.close(this.m_IsDefaultPool);
                doc = null;
            }
        }
        return doc;
    }

    public DTM pquery(ExpressionContext exprContext, String queryString) {
        return this.pquery(exprContext, queryString, null);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public DTM pquery(ExpressionContext exprContext, String queryString, String typeInfo) {
        SQLDocument doc;
        block8: {
            doc = null;
            if (null != this.m_ConnectionPool) break block8;
            DTM dTM = null;
            return dTM;
        }
        try {
            SQLQueryParser query = this.m_QueryParser.parse(this, queryString, 0);
            if (!this.m_InlineVariables) {
                this.addTypeToData(typeInfo);
                query.setParameters(this.m_ParameterList);
            }
            doc = SQLDocument.getNewDocument(exprContext);
            doc.execute(this, query);
            this.m_OpenSQLDocuments.addElement(doc);
        }
        catch (Exception e) {
            if (doc != null) {
                if (doc.hasErrors()) {
                    this.setError(e, doc, doc.checkWarnings());
                }
                doc.close(this.m_IsDefaultPool);
                doc = null;
            }
        }
        return doc;
    }

    public void skipRec(ExpressionContext exprContext, Object o, int value) {
        SQLDocument sqldoc = null;
        Object nodei = null;
        sqldoc = this.locateSQLDocument(exprContext, o);
        if (sqldoc != null) {
            sqldoc.skip(value);
        }
    }

    private void addTypeToData(String typeInfo) {
        if (typeInfo != null && this.m_ParameterList != null) {
            StringTokenizer plist = new StringTokenizer(typeInfo);
            int indx = 0;
            while (plist.hasMoreTokens()) {
                String value = plist.nextToken();
                QueryParameter qp = (QueryParameter)this.m_ParameterList.elementAt(indx);
                if (null != qp) {
                    qp.setTypeName(value);
                }
                ++indx;
            }
        }
    }

    public void addParameter(String value) {
        this.addParameterWithType(value, null);
    }

    public void addParameterWithType(String value, String Type2) {
        this.m_ParameterList.addElement(new QueryParameter(value, Type2));
    }

    public void addParameterFromElement(Element e) {
        NamedNodeMap attrs = e.getAttributes();
        Node Type2 = attrs.getNamedItem("type");
        Node n1 = e.getFirstChild();
        if (null != n1) {
            String value = n1.getNodeValue();
            if (value == null) {
                value = "";
            }
            this.m_ParameterList.addElement(new QueryParameter(value, Type2.getNodeValue()));
        }
    }

    public void addParameterFromElement(NodeList nl) {
        int count = nl.getLength();
        for (int x = 0; x < count; ++x) {
            this.addParameters((Element)nl.item(x));
        }
    }

    private void addParameters(Element elem) {
        Node n = elem.getFirstChild();
        if (null == n) {
            return;
        }
        do {
            if (n.getNodeType() != 1) continue;
            NamedNodeMap attrs = n.getAttributes();
            Node Type2 = attrs.getNamedItem("type");
            String TypeStr = Type2 == null ? "string" : Type2.getNodeValue();
            Node n1 = n.getFirstChild();
            if (null == n1) continue;
            String value = n1.getNodeValue();
            if (value == null) {
                value = "";
            }
            this.m_ParameterList.addElement(new QueryParameter(value, TypeStr));
        } while ((n = n.getNextSibling()) != null);
    }

    public void clearParameters() {
        this.m_ParameterList.removeAllElements();
    }

    public void enableDefaultConnectionPool() {
        this.m_DefaultPoolingEnabled = true;
        if (this.m_ConnectionPool == null) {
            return;
        }
        if (this.m_IsDefaultPool) {
            return;
        }
        this.m_ConnectionPool.setPoolEnabled(true);
    }

    public void disableDefaultConnectionPool() {
        this.m_DefaultPoolingEnabled = false;
        if (this.m_ConnectionPool == null) {
            return;
        }
        if (!this.m_IsDefaultPool) {
            return;
        }
        this.m_ConnectionPool.setPoolEnabled(false);
    }

    public void enableStreamingMode() {
        this.m_IsStreamingEnabled = true;
    }

    public void disableStreamingMode() {
        this.m_IsStreamingEnabled = false;
    }

    public DTM getError() {
        if (this.m_FullErrors) {
            for (int idx = 0; idx < this.m_OpenSQLDocuments.size(); ++idx) {
                SQLDocument doc = (SQLDocument)this.m_OpenSQLDocuments.elementAt(idx);
                SQLWarning warn = doc.checkWarnings();
                if (warn == null) continue;
                this.setError(null, doc, warn);
            }
        }
        return this.buildErrorDocument();
    }

    public void close() throws SQLException {
        while (this.m_OpenSQLDocuments.size() != 0) {
            SQLDocument d = (SQLDocument)this.m_OpenSQLDocuments.elementAt(0);
            try {
                d.close(this.m_IsDefaultPool);
            }
            catch (Exception exception) {
                // empty catch block
            }
            this.m_OpenSQLDocuments.removeElementAt(0);
        }
        if (null != this.m_Connection) {
            this.m_ConnectionPool.releaseConnection(this.m_Connection);
            this.m_Connection = null;
        }
    }

    public void close(ExpressionContext exprContext, Object doc) throws SQLException {
        SQLDocument sqlDoc = this.locateSQLDocument(exprContext, doc);
        if (sqlDoc != null) {
            sqlDoc.close(this.m_IsDefaultPool);
            this.m_OpenSQLDocuments.remove(sqlDoc);
        }
    }

    private SQLDocument locateSQLDocument(ExpressionContext exprContext, Object doc) {
        try {
            if (doc instanceof DTMNodeIterator) {
                DTMNodeIterator dtmIter = (DTMNodeIterator)doc;
                try {
                    DTMNodeProxy root = (DTMNodeProxy)dtmIter.getRoot();
                    return (SQLDocument)root.getDTM();
                }
                catch (Exception e) {
                    XNodeSet xNS = (XNodeSet)dtmIter.getDTMIterator();
                    DTMIterator iter = xNS.getContainedIter();
                    DTM dtm = iter.getDTM(xNS.nextNode());
                    return (SQLDocument)dtm;
                }
            }
            this.setError(new Exception("SQL Extension:close - Can Not Identify SQLDocument"), exprContext);
            return null;
        }
        catch (Exception e) {
            this.setError(e, exprContext);
            return null;
        }
    }

    private SQLErrorDocument buildErrorDocument() {
        SQLErrorDocument eDoc = null;
        if (this.m_LastSQLDocumentWithError != null) {
            ExpressionContext ctx = this.m_LastSQLDocumentWithError.getExpressionContext();
            SQLWarning warn = this.m_LastSQLDocumentWithError.checkWarnings();
            try {
                DTMManager mgr = ((XPathContext.XPathExpressionContext)ctx).getDTMManager();
                DTMManagerDefault mgrDefault = (DTMManagerDefault)mgr;
                int dtmIdent = mgrDefault.getFirstFreeDTMID();
                eDoc = new SQLErrorDocument(mgr, dtmIdent << 16, this.m_Error, warn, this.m_FullErrors);
                mgrDefault.addDTM(eDoc, dtmIdent);
                this.m_Error = null;
                this.m_LastSQLDocumentWithError = null;
            }
            catch (Exception e) {
                eDoc = null;
            }
        }
        return eDoc;
    }

    public void setError(Exception excp, ExpressionContext expr) {
        try {
            ErrorListener listen = expr.getErrorListener();
            if (listen != null && excp != null) {
                listen.warning(new TransformerException(excp.toString(), expr.getXPathContext().getSAXLocator(), excp));
            }
        }
        catch (Exception exception) {
            // empty catch block
        }
    }

    public void setError(Exception excp, SQLDocument doc, SQLWarning warn) {
        ExpressionContext cont = doc.getExpressionContext();
        this.m_LastSQLDocumentWithError = doc;
        try {
            ErrorListener listen = cont.getErrorListener();
            if (listen != null && excp != null) {
                listen.warning(new TransformerException(excp.toString(), cont.getXPathContext().getSAXLocator(), excp));
            }
            if (listen != null && warn != null) {
                listen.warning(new TransformerException(warn.toString(), cont.getXPathContext().getSAXLocator(), warn));
            }
            if (excp != null) {
                this.m_Error = excp;
            }
            if (warn != null) {
                SQLWarning tw = new SQLWarning(warn.getMessage(), warn.getSQLState(), warn.getErrorCode());
                for (SQLWarning nw = warn.getNextWarning(); nw != null; nw = nw.getNextWarning()) {
                    tw.setNextWarning(new SQLWarning(nw.getMessage(), nw.getSQLState(), nw.getErrorCode()));
                }
                tw.setNextWarning(new SQLWarning(warn.getMessage(), warn.getSQLState(), warn.getErrorCode()));
            }
        }
        catch (Exception exception) {
            // empty catch block
        }
    }

    public void setFeature(String feature, String setting) {
        boolean value = false;
        if ("true".equalsIgnoreCase(setting)) {
            value = true;
        }
        if ("streaming".equalsIgnoreCase(feature)) {
            this.m_IsStreamingEnabled = value;
        } else if ("inline-variables".equalsIgnoreCase(feature)) {
            this.m_InlineVariables = value;
        } else if ("multiple-results".equalsIgnoreCase(feature)) {
            this.m_IsMultipleResultsEnabled = value;
        } else if ("cache-statements".equalsIgnoreCase(feature)) {
            this.m_IsStatementCachingEnabled = value;
        } else if ("default-pool-enabled".equalsIgnoreCase(feature)) {
            this.m_DefaultPoolingEnabled = value;
            if (this.m_ConnectionPool == null) {
                return;
            }
            if (this.m_IsDefaultPool) {
                return;
            }
            this.m_ConnectionPool.setPoolEnabled(value);
        } else if ("full-errors".equalsIgnoreCase(feature)) {
            this.m_FullErrors = value;
        }
    }

    public String getFeature(String feature) {
        String value = null;
        if ("streaming".equalsIgnoreCase(feature)) {
            value = this.m_IsStreamingEnabled ? "true" : "false";
        } else if ("inline-variables".equalsIgnoreCase(feature)) {
            value = this.m_InlineVariables ? "true" : "false";
        } else if ("multiple-results".equalsIgnoreCase(feature)) {
            value = this.m_IsMultipleResultsEnabled ? "true" : "false";
        } else if ("cache-statements".equalsIgnoreCase(feature)) {
            value = this.m_IsStatementCachingEnabled ? "true" : "false";
        } else if ("default-pool-enabled".equalsIgnoreCase(feature)) {
            value = this.m_DefaultPoolingEnabled ? "true" : "false";
        } else if ("full-errors".equalsIgnoreCase(feature)) {
            value = this.m_FullErrors ? "true" : "false";
        }
        return value;
    }

    protected void finalize() {
        try {
            this.close();
        }
        catch (Exception exception) {
            // empty catch block
        }
    }
}

