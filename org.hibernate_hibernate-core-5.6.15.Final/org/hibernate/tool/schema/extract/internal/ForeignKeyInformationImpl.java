/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.tool.schema.extract.internal;

import java.util.List;
import org.hibernate.boot.model.naming.Identifier;
import org.hibernate.tool.schema.extract.spi.ColumnInformation;
import org.hibernate.tool.schema.extract.spi.ForeignKeyInformation;

public class ForeignKeyInformationImpl
implements ForeignKeyInformation {
    private final Identifier fkIdentifier;
    private final List<ForeignKeyInformation.ColumnReferenceMapping> columnMappingList;

    public ForeignKeyInformationImpl(Identifier fkIdentifier, List<ForeignKeyInformation.ColumnReferenceMapping> columnMappingList) {
        this.fkIdentifier = fkIdentifier;
        this.columnMappingList = columnMappingList;
    }

    @Override
    public Identifier getForeignKeyIdentifier() {
        return this.fkIdentifier;
    }

    @Override
    public Iterable<ForeignKeyInformation.ColumnReferenceMapping> getColumnReferenceMappings() {
        return this.columnMappingList;
    }

    public static class ColumnReferenceMappingImpl
    implements ForeignKeyInformation.ColumnReferenceMapping {
        private final ColumnInformation referencing;
        private final ColumnInformation referenced;

        public ColumnReferenceMappingImpl(ColumnInformation referencing, ColumnInformation referenced) {
            this.referencing = referencing;
            this.referenced = referenced;
        }

        @Override
        public ColumnInformation getReferencingColumnMetadata() {
            return this.referencing;
        }

        @Override
        public ColumnInformation getReferencedColumnMetadata() {
            return this.referenced;
        }
    }
}

