/*
 * Decompiled with CFR 0.152.
 */
package com.mchange.v1.db.sql.xmlpropsschema;

import com.mchange.v1.xmlprops.DomXmlPropsParser;
import com.mchange.v1.xmlprops.XmlPropsException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;
import java.util.StringTokenizer;

public class XmlPropsSchemaHelper {
    Properties props;

    public XmlPropsSchemaHelper(InputStream inputStream) throws XmlPropsException {
        DomXmlPropsParser domXmlPropsParser = new DomXmlPropsParser();
        this.props = domXmlPropsParser.parseXmlProps(inputStream);
    }

    public PreparedStatement prepareXmlStatement(Connection connection, String string) throws SQLException {
        return connection.prepareStatement(this.getKey(string));
    }

    public void executeViaStatement(Statement statement, String string) throws SQLException {
        statement.executeUpdate(this.getKey(string));
    }

    public StringTokenizer getItems(String string) {
        String string2 = this.getKey(string);
        return new StringTokenizer(string2, ", \t\r\n");
    }

    public String getKey(String string) {
        return this.props.getProperty(string).trim();
    }
}

