/*
 * Decompiled with CFR 0.152.
 */
package com.microsoft.sqlserver.jdbc;

enum DescribeParameterEncryptionResultSet1 {
    KEYORDINAL,
    DBID,
    KEYID,
    KEYVERSION,
    KEYMDVERSION,
    ENCRYPTEDKEY,
    PROVIDERNAME,
    KEYPATH,
    KEYENCRYPTIONALGORITHM,
    ISREQUESTEDBYENCLAVE,
    ENCLAVECMKSIGNATURE;


    int value() {
        return this.ordinal() + 1;
    }
}

