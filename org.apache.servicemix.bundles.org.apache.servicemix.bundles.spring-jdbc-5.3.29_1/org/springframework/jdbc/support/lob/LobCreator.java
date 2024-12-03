/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.lang.Nullable
 */
package org.springframework.jdbc.support.lob;

import java.io.Closeable;
import java.io.InputStream;
import java.io.Reader;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import org.springframework.lang.Nullable;

public interface LobCreator
extends Closeable {
    public void setBlobAsBytes(PreparedStatement var1, int var2, @Nullable byte[] var3) throws SQLException;

    public void setBlobAsBinaryStream(PreparedStatement var1, int var2, @Nullable InputStream var3, int var4) throws SQLException;

    public void setClobAsString(PreparedStatement var1, int var2, @Nullable String var3) throws SQLException;

    public void setClobAsAsciiStream(PreparedStatement var1, int var2, @Nullable InputStream var3, int var4) throws SQLException;

    public void setClobAsCharacterStream(PreparedStatement var1, int var2, @Nullable Reader var3, int var4) throws SQLException;

    @Override
    public void close();
}

