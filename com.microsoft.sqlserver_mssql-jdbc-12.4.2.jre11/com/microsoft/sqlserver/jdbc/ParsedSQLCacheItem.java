/*
 * Decompiled with CFR 0.152.
 */
package com.microsoft.sqlserver.jdbc;

final class ParsedSQLCacheItem {
    String processedSQL;
    int[] parameterPositions;
    String procedureName;
    boolean bReturnValueSyntax;

    ParsedSQLCacheItem(String processedSQL, int[] parameterPositions, String procedureName, boolean bReturnValueSyntax) {
        this.processedSQL = processedSQL;
        this.parameterPositions = parameterPositions;
        this.procedureName = procedureName;
        this.bReturnValueSyntax = bReturnValueSyntax;
    }
}

