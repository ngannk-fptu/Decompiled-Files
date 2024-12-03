/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.tool.schema.extract.spi;

import org.hibernate.Incubating;
import org.hibernate.boot.model.naming.Identifier;
import org.hibernate.tool.schema.extract.internal.TableInformationImpl;
import org.hibernate.tool.schema.extract.spi.ForeignKeyInformation;
import org.hibernate.tool.schema.extract.spi.IndexInformation;
import org.hibernate.tool.schema.extract.spi.NameSpaceTablesInformation;
import org.hibernate.tool.schema.extract.spi.PrimaryKeyInformation;
import org.hibernate.tool.schema.extract.spi.TableInformation;

@Incubating
public interface InformationExtractor {
    public boolean catalogExists(Identifier var1);

    public boolean schemaExists(Identifier var1, Identifier var2);

    public TableInformation getTable(Identifier var1, Identifier var2, Identifier var3);

    public NameSpaceTablesInformation getTables(Identifier var1, Identifier var2);

    public PrimaryKeyInformation getPrimaryKey(TableInformationImpl var1);

    public Iterable<IndexInformation> getIndexes(TableInformation var1);

    public Iterable<ForeignKeyInformation> getForeignKeys(TableInformation var1);
}

