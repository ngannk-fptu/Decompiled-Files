/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.engine.jdbc;

import java.io.InputStream;
import java.io.Reader;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.NClob;
import java.sql.SQLException;
import org.hibernate.JDBCException;
import org.hibernate.engine.jdbc.AbstractLobCreator;
import org.hibernate.engine.jdbc.LobCreationContext;
import org.hibernate.engine.jdbc.LobCreator;
import org.hibernate.engine.jdbc.NonContextualLobCreator;

public class ContextualLobCreator
extends AbstractLobCreator
implements LobCreator {
    private LobCreationContext lobCreationContext;
    public static final LobCreationContext.Callback<Blob> CREATE_BLOB_CALLBACK = new LobCreationContext.Callback<Blob>(){

        @Override
        public Blob executeOnConnection(Connection connection) throws SQLException {
            return connection.createBlob();
        }
    };
    public static final LobCreationContext.Callback<Clob> CREATE_CLOB_CALLBACK = new LobCreationContext.Callback<Clob>(){

        @Override
        public Clob executeOnConnection(Connection connection) throws SQLException {
            return connection.createClob();
        }
    };
    public static final LobCreationContext.Callback<NClob> CREATE_NCLOB_CALLBACK = new LobCreationContext.Callback<NClob>(){

        @Override
        public NClob executeOnConnection(Connection connection) throws SQLException {
            return connection.createNClob();
        }
    };

    public ContextualLobCreator(LobCreationContext lobCreationContext) {
        this.lobCreationContext = lobCreationContext;
    }

    public Blob createBlob() {
        return this.lobCreationContext.execute(CREATE_BLOB_CALLBACK);
    }

    @Override
    public Blob createBlob(byte[] bytes) {
        try {
            Blob blob = this.createBlob();
            blob.setBytes(1L, bytes);
            return blob;
        }
        catch (SQLException e) {
            throw new JDBCException("Unable to set BLOB bytes after creation", e);
        }
    }

    @Override
    public Blob createBlob(InputStream inputStream, long length) {
        return NonContextualLobCreator.INSTANCE.createBlob(inputStream, length);
    }

    public Clob createClob() {
        return this.lobCreationContext.execute(CREATE_CLOB_CALLBACK);
    }

    @Override
    public Clob createClob(String string) {
        try {
            Clob clob = this.createClob();
            clob.setString(1L, string);
            return clob;
        }
        catch (SQLException e) {
            throw new JDBCException("Unable to set CLOB string after creation", e);
        }
    }

    @Override
    public Clob createClob(Reader reader, long length) {
        return NonContextualLobCreator.INSTANCE.createClob(reader, length);
    }

    public NClob createNClob() {
        return this.lobCreationContext.execute(CREATE_NCLOB_CALLBACK);
    }

    @Override
    public NClob createNClob(String string) {
        try {
            NClob nclob = this.createNClob();
            nclob.setString(1L, string);
            return nclob;
        }
        catch (SQLException e) {
            throw new JDBCException("Unable to set NCLOB string after creation", e);
        }
    }

    @Override
    public NClob createNClob(Reader reader, long length) {
        return NonContextualLobCreator.INSTANCE.createNClob(reader, length);
    }
}

