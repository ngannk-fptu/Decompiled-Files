/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.procedure;

import org.hibernate.MappingException;

public class UnknownSqlResultSetMappingException
extends MappingException {
    private final String unknownSqlResultSetMappingName;

    public UnknownSqlResultSetMappingException(String unknownSqlResultSetMappingName) {
        super("The given SqlResultSetMapping name [" + unknownSqlResultSetMappingName + "] is unknown");
        this.unknownSqlResultSetMappingName = unknownSqlResultSetMappingName;
    }

    public String getUnknownSqlResultSetMappingName() {
        return this.unknownSqlResultSetMappingName;
    }
}

