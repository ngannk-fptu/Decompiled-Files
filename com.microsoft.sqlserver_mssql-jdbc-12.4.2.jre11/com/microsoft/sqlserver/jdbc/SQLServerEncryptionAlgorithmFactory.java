/*
 * Decompiled with CFR 0.152.
 */
package com.microsoft.sqlserver.jdbc;

import com.microsoft.sqlserver.jdbc.SQLServerEncryptionAlgorithm;
import com.microsoft.sqlserver.jdbc.SQLServerEncryptionType;
import com.microsoft.sqlserver.jdbc.SQLServerException;
import com.microsoft.sqlserver.jdbc.SQLServerSymmetricKey;

abstract class SQLServerEncryptionAlgorithmFactory {
    SQLServerEncryptionAlgorithmFactory() {
    }

    abstract SQLServerEncryptionAlgorithm create(SQLServerSymmetricKey var1, SQLServerEncryptionType var2, String var3) throws SQLServerException;
}

