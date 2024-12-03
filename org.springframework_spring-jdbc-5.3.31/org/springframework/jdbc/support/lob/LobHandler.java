/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.lang.Nullable
 */
package org.springframework.jdbc.support.lob;

import java.io.InputStream;
import java.io.Reader;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.support.lob.LobCreator;
import org.springframework.lang.Nullable;

public interface LobHandler {
    @Nullable
    public byte[] getBlobAsBytes(ResultSet var1, String var2) throws SQLException;

    @Nullable
    public byte[] getBlobAsBytes(ResultSet var1, int var2) throws SQLException;

    @Nullable
    public InputStream getBlobAsBinaryStream(ResultSet var1, String var2) throws SQLException;

    @Nullable
    public InputStream getBlobAsBinaryStream(ResultSet var1, int var2) throws SQLException;

    @Nullable
    public String getClobAsString(ResultSet var1, String var2) throws SQLException;

    @Nullable
    public String getClobAsString(ResultSet var1, int var2) throws SQLException;

    @Nullable
    public InputStream getClobAsAsciiStream(ResultSet var1, String var2) throws SQLException;

    @Nullable
    public InputStream getClobAsAsciiStream(ResultSet var1, int var2) throws SQLException;

    public Reader getClobAsCharacterStream(ResultSet var1, String var2) throws SQLException;

    public Reader getClobAsCharacterStream(ResultSet var1, int var2) throws SQLException;

    public LobCreator getLobCreator();
}

