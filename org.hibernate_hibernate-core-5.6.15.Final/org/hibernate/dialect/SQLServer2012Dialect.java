/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.dialect;

import org.hibernate.boot.Metadata;
import org.hibernate.boot.model.relational.QualifiedSequenceName;
import org.hibernate.boot.model.relational.Sequence;
import org.hibernate.boot.model.relational.SqlStringGenerationContext;
import org.hibernate.dialect.Dialect;
import org.hibernate.dialect.SQLServer2008Dialect;
import org.hibernate.dialect.pagination.LimitHandler;
import org.hibernate.dialect.pagination.SQLServer2012LimitHandler;
import org.hibernate.tool.schema.internal.StandardSequenceExporter;
import org.hibernate.tool.schema.spi.Exporter;

public class SQLServer2012Dialect
extends SQLServer2008Dialect {
    private final Exporter<Sequence> sequenceExporter = new SqlServerSequenceExporter(this);

    @Override
    public boolean supportsSequences() {
        return true;
    }

    @Override
    public boolean supportsPooledSequences() {
        return true;
    }

    @Override
    public Exporter<Sequence> getSequenceExporter() {
        return this.sequenceExporter;
    }

    @Override
    public String getCreateSequenceString(String sequenceName) {
        return "create sequence " + sequenceName;
    }

    @Override
    public String getDropSequenceString(String sequenceName) {
        return "drop sequence " + sequenceName;
    }

    @Override
    public String getSelectSequenceNextValString(String sequenceName) {
        return "next value for " + sequenceName;
    }

    @Override
    public String getSequenceNextValString(String sequenceName) {
        return "select " + this.getSelectSequenceNextValString(sequenceName);
    }

    @Override
    public String getQuerySequencesString() {
        return "select sequence_name, sequence_catalog, sequence_schema, convert( bigint, start_value ) as start_value, convert( bigint, minimum_value ) as minimum_value, convert( bigint, maximum_value ) as maximum_value, convert( bigint, increment ) as increment from INFORMATION_SCHEMA.SEQUENCES";
    }

    @Override
    public String getQueryHintString(String sql, String hints) {
        StringBuilder buffer = new StringBuilder(sql.length() + hints.length() + 12);
        int pos = sql.indexOf(59);
        if (pos > -1) {
            buffer.append(sql.substring(0, pos));
        } else {
            buffer.append(sql);
        }
        buffer.append(" OPTION (").append(hints).append(")");
        if (pos > -1) {
            buffer.append(";");
        }
        sql = buffer.toString();
        return sql;
    }

    @Override
    public boolean supportsLimitOffset() {
        return true;
    }

    @Override
    protected LimitHandler getDefaultLimitHandler() {
        return new SQLServer2012LimitHandler();
    }

    private class SqlServerSequenceExporter
    extends StandardSequenceExporter {
        public SqlServerSequenceExporter(Dialect dialect) {
            super(dialect);
        }

        @Override
        protected String getFormattedSequenceName(QualifiedSequenceName name, Metadata metadata, SqlStringGenerationContext context) {
            return context.formatWithoutCatalog(name);
        }
    }
}

