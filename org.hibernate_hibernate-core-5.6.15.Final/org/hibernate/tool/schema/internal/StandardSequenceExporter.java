/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.tool.schema.internal;

import org.hibernate.boot.Metadata;
import org.hibernate.boot.model.relational.QualifiedSequenceName;
import org.hibernate.boot.model.relational.Sequence;
import org.hibernate.boot.model.relational.SqlStringGenerationContext;
import org.hibernate.dialect.Dialect;
import org.hibernate.tool.schema.spi.Exporter;

public class StandardSequenceExporter
implements Exporter<Sequence> {
    private final Dialect dialect;

    public StandardSequenceExporter(Dialect dialect) {
        this.dialect = dialect;
    }

    @Override
    public String[] getSqlCreateStrings(Sequence sequence, Metadata metadata, SqlStringGenerationContext context) {
        return this.dialect.getCreateSequenceStrings(this.getFormattedSequenceName(sequence.getName(), metadata, context), sequence.getInitialValue(), sequence.getIncrementSize());
    }

    @Override
    public String[] getSqlDropStrings(Sequence sequence, Metadata metadata, SqlStringGenerationContext context) {
        return this.dialect.getDropSequenceStrings(this.getFormattedSequenceName(sequence.getName(), metadata, context));
    }

    protected String getFormattedSequenceName(QualifiedSequenceName name, Metadata metadata, SqlStringGenerationContext context) {
        return context.format(name);
    }
}

