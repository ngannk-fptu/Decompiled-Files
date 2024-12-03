/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.tool.schema.extract.internal;

import org.hibernate.boot.model.TruthValue;
import org.hibernate.boot.model.naming.Identifier;
import org.hibernate.tool.schema.extract.spi.ColumnInformation;
import org.hibernate.tool.schema.extract.spi.TableInformation;

public class ColumnInformationImpl
implements ColumnInformation {
    private final TableInformation containingTableInformation;
    private final Identifier columnIdentifier;
    private final int typeCode;
    private final String typeName;
    private final int columnSize;
    private final int decimalDigits;
    private final TruthValue nullable;

    public ColumnInformationImpl(TableInformation containingTableInformation, Identifier columnIdentifier, int typeCode, String typeName, int columnSize, int decimalDigits, TruthValue nullable) {
        this.containingTableInformation = containingTableInformation;
        this.columnIdentifier = columnIdentifier;
        this.typeCode = typeCode;
        this.typeName = typeName;
        this.columnSize = columnSize;
        this.decimalDigits = decimalDigits;
        this.nullable = nullable;
    }

    @Override
    public TableInformation getContainingTableInformation() {
        return this.containingTableInformation;
    }

    @Override
    public Identifier getColumnIdentifier() {
        return this.columnIdentifier;
    }

    @Override
    public int getTypeCode() {
        return this.typeCode;
    }

    @Override
    public String getTypeName() {
        return this.typeName;
    }

    @Override
    public int getColumnSize() {
        return this.columnSize;
    }

    @Override
    public int getDecimalDigits() {
        return this.decimalDigits;
    }

    @Override
    public TruthValue getNullable() {
        return this.nullable;
    }

    public String toString() {
        return "ColumnInformation(" + this.columnIdentifier + ')';
    }
}

