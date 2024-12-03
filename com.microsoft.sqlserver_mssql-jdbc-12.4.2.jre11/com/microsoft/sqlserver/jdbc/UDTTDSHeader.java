/*
 * Decompiled with CFR 0.152.
 */
package com.microsoft.sqlserver.jdbc;

import com.microsoft.sqlserver.jdbc.SQLServerException;
import com.microsoft.sqlserver.jdbc.TDSReader;

final class UDTTDSHeader {
    private final int maxLen;
    private final String databaseName;
    private final String schemaName;
    private final String typeName;
    private final String assemblyQualifiedName;

    UDTTDSHeader(TDSReader tdsReader) throws SQLServerException {
        this.maxLen = tdsReader.readUnsignedShort();
        this.databaseName = tdsReader.readUnicodeString(tdsReader.readUnsignedByte());
        this.schemaName = tdsReader.readUnicodeString(tdsReader.readUnsignedByte());
        this.typeName = tdsReader.readUnicodeString(tdsReader.readUnsignedByte());
        this.assemblyQualifiedName = tdsReader.readUnicodeString(tdsReader.readUnsignedShort());
    }

    int getMaxLen() {
        return this.maxLen;
    }

    String getTypeName() {
        return this.typeName;
    }
}

