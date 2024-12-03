/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.id;

import org.hibernate.id.IdentifierGenerator;

public interface PersistentIdentifierGenerator
extends IdentifierGenerator {
    public static final String SCHEMA = "schema";
    public static final String TABLE = "target_table";
    public static final String TABLES = "identity_tables";
    public static final String PK = "target_column";
    public static final String CATALOG = "catalog";
    public static final String IDENTIFIER_NORMALIZER = "identifier_normalizer";

    @Deprecated
    default public Object generatorKey() {
        return null;
    }
}

