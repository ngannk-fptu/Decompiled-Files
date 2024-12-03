/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.tool.schema.spi;

import org.hibernate.boot.Metadata;
import org.hibernate.boot.model.relational.Exportable;
import org.hibernate.boot.model.relational.SqlStringGenerationContext;

public interface Exporter<T extends Exportable> {
    public static final String[] NO_COMMANDS = new String[0];

    @Deprecated
    default public String[] getSqlCreateStrings(T exportable, Metadata metadata) {
        throw new IllegalStateException("getSqlCreateStrings() was not implemented!");
    }

    default public String[] getSqlCreateStrings(T exportable, Metadata metadata, SqlStringGenerationContext context) {
        return this.getSqlCreateStrings(exportable, metadata);
    }

    @Deprecated
    default public String[] getSqlDropStrings(T exportable, Metadata metadata) {
        throw new IllegalStateException("getSqlDropStrings() was not implemented!");
    }

    default public String[] getSqlDropStrings(T exportable, Metadata metadata, SqlStringGenerationContext context) {
        return this.getSqlDropStrings(exportable, metadata);
    }
}

