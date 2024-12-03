/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.engine.jdbc.env.spi;

public enum NameQualifierSupport {
    CATALOG,
    SCHEMA,
    BOTH,
    NONE;


    public boolean supportsCatalogs() {
        return this == CATALOG || this == BOTH;
    }

    public boolean supportsSchemas() {
        return this == SCHEMA || this == BOTH;
    }
}

