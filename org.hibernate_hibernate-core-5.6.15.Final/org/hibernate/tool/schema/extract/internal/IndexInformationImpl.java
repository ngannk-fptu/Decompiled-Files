/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.tool.schema.extract.internal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.hibernate.boot.model.naming.Identifier;
import org.hibernate.tool.schema.extract.spi.ColumnInformation;
import org.hibernate.tool.schema.extract.spi.IndexInformation;
import org.hibernate.tool.schema.spi.SchemaManagementException;

public class IndexInformationImpl
implements IndexInformation {
    private final Identifier indexIdentifier;
    private final List<ColumnInformation> columnList;

    public IndexInformationImpl(Identifier indexIdentifier, List<ColumnInformation> columnList) {
        this.indexIdentifier = indexIdentifier;
        this.columnList = columnList;
    }

    @Override
    public Identifier getIndexIdentifier() {
        return this.indexIdentifier;
    }

    @Override
    public List<ColumnInformation> getIndexedColumns() {
        return this.columnList;
    }

    public static Builder builder(Identifier indexIdentifier) {
        return new Builder(indexIdentifier);
    }

    public static class Builder {
        private final Identifier indexIdentifier;
        private final List<ColumnInformation> columnList = new ArrayList<ColumnInformation>();

        public Builder(Identifier indexIdentifier) {
            this.indexIdentifier = indexIdentifier;
        }

        public Builder addColumn(ColumnInformation columnInformation) {
            this.columnList.add(columnInformation);
            return this;
        }

        public IndexInformationImpl build() {
            if (this.columnList.isEmpty()) {
                throw new SchemaManagementException("Attempt to resolve JDBC metadata failed to find columns for index [" + this.indexIdentifier.getText() + "]");
            }
            return new IndexInformationImpl(this.indexIdentifier, Collections.unmodifiableList(this.columnList));
        }
    }
}

