/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.tool.schema.extract.spi;

import org.hibernate.boot.model.naming.Identifier;
import org.hibernate.boot.model.relational.QualifiedTableName;
import org.hibernate.tool.schema.extract.spi.ColumnInformation;
import org.hibernate.tool.schema.extract.spi.ForeignKeyInformation;
import org.hibernate.tool.schema.extract.spi.IndexInformation;
import org.hibernate.tool.schema.extract.spi.PrimaryKeyInformation;

public interface TableInformation {
    public QualifiedTableName getName();

    public boolean isPhysicalTable();

    public String getComment();

    public ColumnInformation getColumn(Identifier var1);

    public PrimaryKeyInformation getPrimaryKey();

    public Iterable<ForeignKeyInformation> getForeignKeys();

    public ForeignKeyInformation getForeignKey(Identifier var1);

    public Iterable<IndexInformation> getIndexes();

    public IndexInformation getIndex(Identifier var1);

    public void addColumn(ColumnInformation var1);
}

