/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.tool.schema.extract.spi;

import org.hibernate.boot.model.TruthValue;
import org.hibernate.boot.model.naming.Identifier;
import org.hibernate.tool.schema.extract.spi.TableInformation;

public interface ColumnInformation {
    public TableInformation getContainingTableInformation();

    public Identifier getColumnIdentifier();

    public TruthValue getNullable();

    public int getTypeCode();

    public String getTypeName();

    public int getColumnSize();

    public int getDecimalDigits();
}

