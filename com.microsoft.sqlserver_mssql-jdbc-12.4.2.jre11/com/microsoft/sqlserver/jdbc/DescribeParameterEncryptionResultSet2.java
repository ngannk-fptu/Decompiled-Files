/*
 * Decompiled with CFR 0.152.
 */
package com.microsoft.sqlserver.jdbc;

enum DescribeParameterEncryptionResultSet2 {
    PARAMETERORDINAL,
    PARAMETERNAME,
    COLUMNENCRYPTIONALGORITHM,
    COLUMNENCRYPTIONTYPE,
    COLUMNENCRYPTIONKEYORDINAL,
    NORMALIZATIONRULEVERSION;


    int value() {
        return this.ordinal() + 1;
    }
}

