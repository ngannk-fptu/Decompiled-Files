/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.tool.schema.extract.spi;

import java.util.List;
import org.hibernate.boot.model.naming.Identifier;
import org.hibernate.tool.schema.extract.spi.ColumnInformation;

public interface IndexInformation {
    public Identifier getIndexIdentifier();

    public List<ColumnInformation> getIndexedColumns();
}

