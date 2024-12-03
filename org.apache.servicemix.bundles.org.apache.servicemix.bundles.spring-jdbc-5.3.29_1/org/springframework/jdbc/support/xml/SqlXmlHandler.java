/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.lang.Nullable
 */
package org.springframework.jdbc.support.xml;

import java.io.InputStream;
import java.io.Reader;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import org.springframework.jdbc.support.xml.SqlXmlValue;
import org.springframework.jdbc.support.xml.XmlBinaryStreamProvider;
import org.springframework.jdbc.support.xml.XmlCharacterStreamProvider;
import org.springframework.jdbc.support.xml.XmlResultProvider;
import org.springframework.lang.Nullable;
import org.w3c.dom.Document;

public interface SqlXmlHandler {
    @Nullable
    public String getXmlAsString(ResultSet var1, String var2) throws SQLException;

    @Nullable
    public String getXmlAsString(ResultSet var1, int var2) throws SQLException;

    @Nullable
    public InputStream getXmlAsBinaryStream(ResultSet var1, String var2) throws SQLException;

    @Nullable
    public InputStream getXmlAsBinaryStream(ResultSet var1, int var2) throws SQLException;

    @Nullable
    public Reader getXmlAsCharacterStream(ResultSet var1, String var2) throws SQLException;

    @Nullable
    public Reader getXmlAsCharacterStream(ResultSet var1, int var2) throws SQLException;

    @Nullable
    public Source getXmlAsSource(ResultSet var1, String var2, @Nullable Class<? extends Source> var3) throws SQLException;

    @Nullable
    public Source getXmlAsSource(ResultSet var1, int var2, @Nullable Class<? extends Source> var3) throws SQLException;

    public SqlXmlValue newSqlXmlValue(String var1);

    public SqlXmlValue newSqlXmlValue(XmlBinaryStreamProvider var1);

    public SqlXmlValue newSqlXmlValue(XmlCharacterStreamProvider var1);

    public SqlXmlValue newSqlXmlValue(Class<? extends Result> var1, XmlResultProvider var2);

    public SqlXmlValue newSqlXmlValue(Document var1);
}

