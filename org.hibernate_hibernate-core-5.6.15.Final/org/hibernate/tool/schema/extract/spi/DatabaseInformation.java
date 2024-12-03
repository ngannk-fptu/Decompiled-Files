/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.tool.schema.extract.spi;

import org.hibernate.Incubating;
import org.hibernate.boot.model.naming.Identifier;
import org.hibernate.boot.model.relational.Namespace;
import org.hibernate.boot.model.relational.QualifiedSequenceName;
import org.hibernate.boot.model.relational.QualifiedTableName;
import org.hibernate.tool.schema.extract.spi.NameSpaceTablesInformation;
import org.hibernate.tool.schema.extract.spi.SequenceInformation;
import org.hibernate.tool.schema.extract.spi.TableInformation;

@Incubating
public interface DatabaseInformation {
    public boolean schemaExists(Namespace.Name var1);

    public TableInformation getTableInformation(Identifier var1, Identifier var2, Identifier var3);

    public TableInformation getTableInformation(Namespace.Name var1, Identifier var2);

    public TableInformation getTableInformation(QualifiedTableName var1);

    public NameSpaceTablesInformation getTablesInformation(Namespace var1);

    public SequenceInformation getSequenceInformation(Identifier var1, Identifier var2, Identifier var3);

    public SequenceInformation getSequenceInformation(Namespace.Name var1, Identifier var2);

    public SequenceInformation getSequenceInformation(QualifiedSequenceName var1);

    public boolean catalogExists(Identifier var1);

    public void cleanup();
}

