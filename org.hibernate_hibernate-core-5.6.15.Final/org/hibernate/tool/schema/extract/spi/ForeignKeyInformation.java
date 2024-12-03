/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.tool.schema.extract.spi;

import org.hibernate.boot.model.naming.Identifier;
import org.hibernate.tool.schema.extract.spi.ColumnInformation;

public interface ForeignKeyInformation {
    public Identifier getForeignKeyIdentifier();

    public Iterable<ColumnReferenceMapping> getColumnReferenceMappings();

    public static interface ColumnReferenceMapping {
        public ColumnInformation getReferencingColumnMetadata();

        public ColumnInformation getReferencedColumnMetadata();
    }
}

