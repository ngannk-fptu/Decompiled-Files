/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.lang.Nullable
 */
package org.springframework.jdbc.core.support;

import java.io.InputStream;
import java.io.Reader;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import org.springframework.jdbc.core.DisposableSqlTypeValue;
import org.springframework.jdbc.support.lob.DefaultLobHandler;
import org.springframework.jdbc.support.lob.LobCreator;
import org.springframework.jdbc.support.lob.LobHandler;
import org.springframework.lang.Nullable;

public class SqlLobValue
implements DisposableSqlTypeValue {
    @Nullable
    private final Object content;
    private final int length;
    private final LobCreator lobCreator;

    public SqlLobValue(@Nullable byte[] bytes) {
        this(bytes, (LobHandler)new DefaultLobHandler());
    }

    public SqlLobValue(@Nullable byte[] bytes, LobHandler lobHandler) {
        this.content = bytes;
        this.length = bytes != null ? bytes.length : 0;
        this.lobCreator = lobHandler.getLobCreator();
    }

    public SqlLobValue(@Nullable String content) {
        this(content, (LobHandler)new DefaultLobHandler());
    }

    public SqlLobValue(@Nullable String content, LobHandler lobHandler) {
        this.content = content;
        this.length = content != null ? content.length() : 0;
        this.lobCreator = lobHandler.getLobCreator();
    }

    public SqlLobValue(InputStream stream, int length) {
        this(stream, length, (LobHandler)new DefaultLobHandler());
    }

    public SqlLobValue(InputStream stream, int length, LobHandler lobHandler) {
        this.content = stream;
        this.length = length;
        this.lobCreator = lobHandler.getLobCreator();
    }

    public SqlLobValue(Reader reader, int length) {
        this(reader, length, (LobHandler)new DefaultLobHandler());
    }

    public SqlLobValue(Reader reader, int length, LobHandler lobHandler) {
        this.content = reader;
        this.length = length;
        this.lobCreator = lobHandler.getLobCreator();
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    @Override
    public void setTypeValue(PreparedStatement ps, int paramIndex, int sqlType, @Nullable String typeName) throws SQLException {
        if (sqlType == 2004) {
            if (this.content instanceof byte[] || this.content == null) {
                this.lobCreator.setBlobAsBytes(ps, paramIndex, (byte[])this.content);
                return;
            } else if (this.content instanceof String) {
                this.lobCreator.setBlobAsBytes(ps, paramIndex, ((String)this.content).getBytes());
                return;
            } else {
                if (!(this.content instanceof InputStream)) throw new IllegalArgumentException("Content type [" + this.content.getClass().getName() + "] not supported for BLOB columns");
                this.lobCreator.setBlobAsBinaryStream(ps, paramIndex, (InputStream)this.content, this.length);
            }
            return;
        } else {
            if (sqlType != 2005) throw new IllegalArgumentException("SqlLobValue only supports SQL types BLOB and CLOB");
            if (this.content instanceof String || this.content == null) {
                this.lobCreator.setClobAsString(ps, paramIndex, (String)this.content);
                return;
            } else if (this.content instanceof InputStream) {
                this.lobCreator.setClobAsAsciiStream(ps, paramIndex, (InputStream)this.content, this.length);
                return;
            } else {
                if (!(this.content instanceof Reader)) throw new IllegalArgumentException("Content type [" + this.content.getClass().getName() + "] not supported for CLOB columns");
                this.lobCreator.setClobAsCharacterStream(ps, paramIndex, (Reader)this.content, this.length);
            }
        }
    }

    @Override
    public void cleanup() {
        this.lobCreator.close();
    }
}

