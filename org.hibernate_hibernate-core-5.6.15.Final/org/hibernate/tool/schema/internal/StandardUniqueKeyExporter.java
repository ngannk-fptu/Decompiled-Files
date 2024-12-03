/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.tool.schema.internal;

import org.hibernate.boot.Metadata;
import org.hibernate.boot.model.relational.SqlStringGenerationContext;
import org.hibernate.dialect.Dialect;
import org.hibernate.mapping.Constraint;
import org.hibernate.mapping.UniqueKey;
import org.hibernate.tool.schema.spi.Exporter;

public class StandardUniqueKeyExporter
implements Exporter<Constraint> {
    private final Dialect dialect;

    public StandardUniqueKeyExporter(Dialect dialect) {
        this.dialect = dialect;
    }

    @Override
    public String[] getSqlCreateStrings(Constraint constraint, Metadata metadata, SqlStringGenerationContext context) {
        return new String[]{this.dialect.getUniqueDelegate().getAlterTableToAddUniqueKeyCommand((UniqueKey)constraint, metadata, context)};
    }

    @Override
    public String[] getSqlDropStrings(Constraint constraint, Metadata metadata, SqlStringGenerationContext context) {
        return new String[]{this.dialect.getUniqueDelegate().getAlterTableToDropUniqueKeyCommand((UniqueKey)constraint, metadata, context)};
    }
}

