/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.dao.DataAccessResourceFailureException
 *  org.springframework.lang.Nullable
 */
package org.springframework.jdbc.support.xml;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLXML;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.jdbc.support.xml.SqlXmlHandler;
import org.springframework.jdbc.support.xml.SqlXmlValue;
import org.springframework.jdbc.support.xml.XmlBinaryStreamProvider;
import org.springframework.jdbc.support.xml.XmlCharacterStreamProvider;
import org.springframework.jdbc.support.xml.XmlResultProvider;
import org.springframework.lang.Nullable;
import org.w3c.dom.Document;

public class Jdbc4SqlXmlHandler
implements SqlXmlHandler {
    @Override
    @Nullable
    public String getXmlAsString(ResultSet rs, String columnName) throws SQLException {
        SQLXML xmlObject = rs.getSQLXML(columnName);
        return xmlObject != null ? xmlObject.getString() : null;
    }

    @Override
    @Nullable
    public String getXmlAsString(ResultSet rs, int columnIndex) throws SQLException {
        SQLXML xmlObject = rs.getSQLXML(columnIndex);
        return xmlObject != null ? xmlObject.getString() : null;
    }

    @Override
    @Nullable
    public InputStream getXmlAsBinaryStream(ResultSet rs, String columnName) throws SQLException {
        SQLXML xmlObject = rs.getSQLXML(columnName);
        return xmlObject != null ? xmlObject.getBinaryStream() : null;
    }

    @Override
    @Nullable
    public InputStream getXmlAsBinaryStream(ResultSet rs, int columnIndex) throws SQLException {
        SQLXML xmlObject = rs.getSQLXML(columnIndex);
        return xmlObject != null ? xmlObject.getBinaryStream() : null;
    }

    @Override
    @Nullable
    public Reader getXmlAsCharacterStream(ResultSet rs, String columnName) throws SQLException {
        SQLXML xmlObject = rs.getSQLXML(columnName);
        return xmlObject != null ? xmlObject.getCharacterStream() : null;
    }

    @Override
    @Nullable
    public Reader getXmlAsCharacterStream(ResultSet rs, int columnIndex) throws SQLException {
        SQLXML xmlObject = rs.getSQLXML(columnIndex);
        return xmlObject != null ? xmlObject.getCharacterStream() : null;
    }

    @Override
    @Nullable
    public Source getXmlAsSource(ResultSet rs, String columnName, @Nullable Class<? extends Source> sourceClass) throws SQLException {
        SQLXML xmlObject = rs.getSQLXML(columnName);
        if (xmlObject == null) {
            return null;
        }
        return sourceClass != null ? xmlObject.getSource(sourceClass) : xmlObject.getSource(DOMSource.class);
    }

    @Override
    @Nullable
    public Source getXmlAsSource(ResultSet rs, int columnIndex, @Nullable Class<? extends Source> sourceClass) throws SQLException {
        SQLXML xmlObject = rs.getSQLXML(columnIndex);
        if (xmlObject == null) {
            return null;
        }
        return sourceClass != null ? xmlObject.getSource(sourceClass) : xmlObject.getSource(DOMSource.class);
    }

    @Override
    public SqlXmlValue newSqlXmlValue(final String value) {
        return new AbstractJdbc4SqlXmlValue(){

            @Override
            protected void provideXml(SQLXML xmlObject) throws SQLException, IOException {
                xmlObject.setString(value);
            }
        };
    }

    @Override
    public SqlXmlValue newSqlXmlValue(final XmlBinaryStreamProvider provider) {
        return new AbstractJdbc4SqlXmlValue(){

            @Override
            protected void provideXml(SQLXML xmlObject) throws SQLException, IOException {
                provider.provideXml(xmlObject.setBinaryStream());
            }
        };
    }

    @Override
    public SqlXmlValue newSqlXmlValue(final XmlCharacterStreamProvider provider) {
        return new AbstractJdbc4SqlXmlValue(){

            @Override
            protected void provideXml(SQLXML xmlObject) throws SQLException, IOException {
                provider.provideXml(xmlObject.setCharacterStream());
            }
        };
    }

    @Override
    public SqlXmlValue newSqlXmlValue(final Class<? extends Result> resultClass, final XmlResultProvider provider) {
        return new AbstractJdbc4SqlXmlValue(){

            @Override
            protected void provideXml(SQLXML xmlObject) throws SQLException, IOException {
                provider.provideXml((Result)xmlObject.setResult(resultClass));
            }
        };
    }

    @Override
    public SqlXmlValue newSqlXmlValue(final Document document) {
        return new AbstractJdbc4SqlXmlValue(){

            @Override
            protected void provideXml(SQLXML xmlObject) throws SQLException, IOException {
                xmlObject.setResult(DOMResult.class).setNode(document);
            }
        };
    }

    private static abstract class AbstractJdbc4SqlXmlValue
    implements SqlXmlValue {
        @Nullable
        private SQLXML xmlObject;

        private AbstractJdbc4SqlXmlValue() {
        }

        @Override
        public void setValue(PreparedStatement ps, int paramIndex) throws SQLException {
            this.xmlObject = ps.getConnection().createSQLXML();
            try {
                this.provideXml(this.xmlObject);
            }
            catch (IOException ex) {
                throw new DataAccessResourceFailureException("Failure encountered while providing XML", (Throwable)ex);
            }
            ps.setSQLXML(paramIndex, this.xmlObject);
        }

        @Override
        public void cleanup() {
            if (this.xmlObject != null) {
                try {
                    this.xmlObject.free();
                }
                catch (SQLException ex) {
                    throw new DataAccessResourceFailureException("Could not free SQLXML object", (Throwable)ex);
                }
            }
        }

        protected abstract void provideXml(SQLXML var1) throws SQLException, IOException;
    }
}

