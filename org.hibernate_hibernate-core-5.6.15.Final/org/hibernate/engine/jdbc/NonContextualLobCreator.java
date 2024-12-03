/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.engine.jdbc;

import java.io.InputStream;
import java.io.Reader;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.NClob;
import org.hibernate.engine.jdbc.AbstractLobCreator;
import org.hibernate.engine.jdbc.BlobProxy;
import org.hibernate.engine.jdbc.ClobProxy;
import org.hibernate.engine.jdbc.LobCreator;
import org.hibernate.engine.jdbc.NClobProxy;

public class NonContextualLobCreator
extends AbstractLobCreator
implements LobCreator {
    public static final NonContextualLobCreator INSTANCE = new NonContextualLobCreator();

    private NonContextualLobCreator() {
    }

    @Override
    public Blob createBlob(byte[] bytes) {
        return BlobProxy.generateProxy(bytes);
    }

    @Override
    public Blob createBlob(InputStream stream, long length) {
        return BlobProxy.generateProxy(stream, length);
    }

    @Override
    public Clob createClob(String string) {
        return ClobProxy.generateProxy(string);
    }

    @Override
    public Clob createClob(Reader reader, long length) {
        return ClobProxy.generateProxy(reader, length);
    }

    @Override
    public NClob createNClob(String string) {
        return NClobProxy.generateProxy(string);
    }

    @Override
    public NClob createNClob(Reader reader, long length) {
        return NClobProxy.generateProxy(reader, length);
    }
}

