/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.tool.schema.extract.spi;

import org.hibernate.boot.model.naming.Identifier;
import org.hibernate.tool.schema.extract.spi.ColumnInformation;

public interface PrimaryKeyInformation {
    public Identifier getPrimaryKeyIdentifier();

    public Iterable<ColumnInformation> getColumns();
}

