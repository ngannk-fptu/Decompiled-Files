/*
 * Decompiled with CFR 0.152.
 */
package com.microsoft.sqlserver.jdbc;

import com.microsoft.sqlserver.jdbc.SQLServerException;
import java.time.Duration;

public abstract class SQLServerColumnEncryptionKeyStoreProvider {
    public abstract void setName(String var1);

    public abstract String getName();

    public abstract byte[] decryptColumnEncryptionKey(String var1, String var2, byte[] var3) throws SQLServerException;

    public abstract byte[] encryptColumnEncryptionKey(String var1, String var2, byte[] var3) throws SQLServerException;

    public abstract boolean verifyColumnMasterKeyMetadata(String var1, boolean var2, byte[] var3) throws SQLServerException;

    public Duration getColumnEncryptionKeyCacheTtl() {
        return Duration.ZERO;
    }

    public void setColumnEncryptionCacheTtl(Duration duration) {
    }
}

