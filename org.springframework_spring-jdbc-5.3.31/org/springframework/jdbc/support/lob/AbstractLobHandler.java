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
import org.springframework.jdbc.support.lob.LobHandler;
import org.springframework.lang.Nullable;

public abstract class AbstractLobHandler
implements LobHandler {
    @Override
    @Nullable
    public byte[] getBlobAsBytes(ResultSet rs, String columnName) throws SQLException {
        return this.getBlobAsBytes(rs, rs.findColumn(columnName));
    }

    @Override
    @Nullable
    public InputStream getBlobAsBinaryStream(ResultSet rs, String columnName) throws SQLException {
        return this.getBlobAsBinaryStream(rs, rs.findColumn(columnName));
    }

    @Override
    @Nullable
    public String getClobAsString(ResultSet rs, String columnName) throws SQLException {
        return this.getClobAsString(rs, rs.findColumn(columnName));
    }

    @Override
    @Nullable
    public InputStream getClobAsAsciiStream(ResultSet rs, String columnName) throws SQLException {
        return this.getClobAsAsciiStream(rs, rs.findColumn(columnName));
    }

    @Override
    public Reader getClobAsCharacterStream(ResultSet rs, String columnName) throws SQLException {
        return this.getClobAsCharacterStream(rs, rs.findColumn(columnName));
    }
}

