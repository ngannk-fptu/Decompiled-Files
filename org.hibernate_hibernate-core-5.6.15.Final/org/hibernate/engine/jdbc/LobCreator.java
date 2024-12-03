/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.engine.jdbc;

import java.io.InputStream;
import java.io.Reader;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.NClob;

public interface LobCreator {
    public Blob wrap(Blob var1);

    public Clob wrap(Clob var1);

    public NClob wrap(NClob var1);

    public Blob createBlob(byte[] var1);

    public Blob createBlob(InputStream var1, long var2);

    public Clob createClob(String var1);

    public Clob createClob(Reader var1, long var2);

    public NClob createNClob(String var1);

    public NClob createNClob(Reader var1, long var2);
}

