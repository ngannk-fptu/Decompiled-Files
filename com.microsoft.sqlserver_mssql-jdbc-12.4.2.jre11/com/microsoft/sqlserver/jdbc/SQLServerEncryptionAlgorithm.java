/*
 * Decompiled with CFR 0.152.
 */
package com.microsoft.sqlserver.jdbc;

import com.microsoft.sqlserver.jdbc.SQLServerException;

abstract class SQLServerEncryptionAlgorithm {
    SQLServerEncryptionAlgorithm() {
    }

    abstract byte[] encryptData(byte[] var1) throws SQLServerException;

    abstract byte[] decryptData(byte[] var1) throws SQLServerException;
}

