/*
 * Decompiled with CFR 0.152.
 */
package com.mchange.v1.db.sql;

import com.mchange.v1.db.sql.ConnectionUtils;
import com.mchange.v1.db.sql.Schema;
import com.mchange.v1.db.sql.StatementUtils;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.AttributeList;
import org.xml.sax.HandlerBase;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

public class XmlSchema
implements Schema {
    private static final int CREATE = 0;
    private static final int DROP = 1;
    List createStmts;
    List dropStmts;
    Map appMap;

    public XmlSchema(URL uRL) throws SAXException, IOException, ParserConfigurationException {
        this.parse(uRL.openStream());
    }

    public XmlSchema(InputStream inputStream) throws SAXException, IOException, ParserConfigurationException {
        this.parse(inputStream);
    }

    public XmlSchema() {
    }

    public void parse(InputStream inputStream) throws SAXException, IOException, ParserConfigurationException {
        this.createStmts = new ArrayList();
        this.dropStmts = new ArrayList();
        this.appMap = new HashMap();
        InputSource inputSource = new InputSource();
        inputSource.setByteStream(inputStream);
        inputSource.setSystemId(XmlSchema.class.getResource("schema.dtd").toExternalForm());
        SAXParser sAXParser = SAXParserFactory.newInstance().newSAXParser();
        MySaxHandler mySaxHandler = new MySaxHandler();
        sAXParser.parse(inputSource, (HandlerBase)mySaxHandler);
    }

    private void doStatementList(List list, Connection connection) throws SQLException {
        if (list != null) {
            Statement statement = null;
            try {
                statement = connection.createStatement();
                Iterator iterator = list.iterator();
                while (iterator.hasNext()) {
                    statement.executeUpdate((String)iterator.next());
                }
                connection.commit();
            }
            catch (SQLException sQLException) {
                ConnectionUtils.attemptRollback(connection);
                sQLException.fillInStackTrace();
                throw sQLException;
            }
            finally {
                StatementUtils.attemptClose(statement);
            }
        }
    }

    @Override
    public String getStatementText(String string, String string2) {
        SqlApp sqlApp = (SqlApp)this.appMap.get(string);
        String string3 = null;
        if (sqlApp != null) {
            string3 = sqlApp.getStatementText(string2);
        }
        return string3;
    }

    @Override
    public void createSchema(Connection connection) throws SQLException {
        this.doStatementList(this.createStmts, connection);
    }

    @Override
    public void dropSchema(Connection connection) throws SQLException {
        this.doStatementList(this.dropStmts, connection);
    }

    public static void main(String[] stringArray) {
        try {
            XmlSchema xmlSchema = new XmlSchema(XmlSchema.class.getResource("/com/mchange/v1/hjug/hjugschema.xml"));
        }
        catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    class SqlApp {
        Map stmtMap = new HashMap();

        SqlApp() {
        }

        public void setStatementText(String string, String string2) {
            this.stmtMap.put(string, string2);
        }

        public String getStatementText(String string) {
            return (String)this.stmtMap.get(string);
        }
    }

    class MySaxHandler
    extends HandlerBase {
        int state = -1;
        boolean in_statement = false;
        boolean in_comment = false;
        StringBuffer charBuff = null;
        SqlApp currentApp = null;
        String currentStmtName = null;

        MySaxHandler() {
        }

        @Override
        public void startElement(String string, AttributeList attributeList) {
            block5: {
                block8: {
                    block7: {
                        block6: {
                            if (!string.equals("create")) break block6;
                            this.state = 0;
                            break block5;
                        }
                        if (!string.equals("drop")) break block7;
                        this.state = 1;
                        break block5;
                    }
                    if (!string.equals("statement")) break block8;
                    this.in_statement = true;
                    this.charBuff = new StringBuffer();
                    if (this.currentApp == null) break block5;
                    int n = attributeList.getLength();
                    for (int i = 0; i < n; ++i) {
                        String string2 = attributeList.getName(i);
                        if (!string2.equals("name")) continue;
                        this.currentStmtName = attributeList.getValue(i);
                        break block5;
                    }
                    break block5;
                }
                if (string.equals("comment")) {
                    this.in_comment = true;
                } else if (string.equals("application")) {
                    int n = attributeList.getLength();
                    for (int i = 0; i < n; ++i) {
                        String string3 = attributeList.getName(i);
                        if (!string3.equals("name")) continue;
                        String string4 = attributeList.getValue(i);
                        this.currentApp = (SqlApp)XmlSchema.this.appMap.get(string4);
                        if (this.currentApp != null) break;
                        this.currentApp = new SqlApp();
                        XmlSchema.this.appMap.put(string4.intern(), this.currentApp);
                        break;
                    }
                }
            }
        }

        @Override
        public void characters(char[] cArray, int n, int n2) throws SAXException {
            if (!this.in_comment && this.in_statement) {
                this.charBuff.append(cArray, n, n2);
            }
        }

        @Override
        public void endElement(String string) {
            if (string.equals("statement")) {
                String string2 = this.charBuff.toString().trim();
                if (this.state == 0) {
                    XmlSchema.this.createStmts.add(string2);
                } else if (this.state == 1) {
                    XmlSchema.this.dropStmts.add(string2);
                } else if (this.currentApp != null && this.currentStmtName != null) {
                    this.currentApp.setStatementText(this.currentStmtName, string2);
                }
            } else if (string.equals("create") || string.equals("drop")) {
                this.state = -1;
            } else if (string.equals("comment")) {
                this.in_comment = false;
            } else if (string.equals("application")) {
                this.currentApp = null;
            }
        }

        @Override
        public void warning(SAXParseException sAXParseException) {
            System.err.println("[Warning] " + sAXParseException.getMessage());
        }

        @Override
        public void error(SAXParseException sAXParseException) {
            System.err.println("[Error] " + sAXParseException.getMessage());
        }

        @Override
        public void fatalError(SAXParseException sAXParseException) throws SAXException {
            System.err.println("[Fatal Error] " + sAXParseException.getMessage());
            throw sAXParseException;
        }
    }
}

