/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.tool.schema.extract.internal;

import org.hibernate.boot.model.naming.Identifier;
import org.hibernate.tool.schema.extract.spi.ColumnInformation;
import org.hibernate.tool.schema.extract.spi.PrimaryKeyInformation;

public class PrimaryKeyInformationImpl
implements PrimaryKeyInformation {
    private final Identifier identifier;
    private final Iterable<ColumnInformation> columns;

    public PrimaryKeyInformationImpl(Identifier identifier, Iterable<ColumnInformation> columns) {
        this.identifier = identifier;
        this.columns = columns;
    }

    @Override
    public Identifier getPrimaryKeyIdentifier() {
        return this.identifier;
    }

    @Override
    public Iterable<ColumnInformation> getColumns() {
        return this.columns;
    }
}

