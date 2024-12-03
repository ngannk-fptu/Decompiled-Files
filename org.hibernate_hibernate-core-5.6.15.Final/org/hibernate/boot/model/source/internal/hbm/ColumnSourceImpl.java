/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.boot.model.source.internal.hbm;

import java.util.Set;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmColumnType;
import org.hibernate.boot.model.TruthValue;
import org.hibernate.boot.model.source.internal.hbm.AbstractHbmSourceNode;
import org.hibernate.boot.model.source.internal.hbm.CommaSeparatedStringHelper;
import org.hibernate.boot.model.source.internal.hbm.Helper;
import org.hibernate.boot.model.source.internal.hbm.MappingDocument;
import org.hibernate.boot.model.source.spi.ColumnSource;
import org.hibernate.boot.model.source.spi.JdbcDataType;
import org.hibernate.boot.model.source.spi.RelationalValueSource;
import org.hibernate.boot.model.source.spi.SizeSource;

class ColumnSourceImpl
extends AbstractHbmSourceNode
implements ColumnSource {
    private final String tableName;
    private final JaxbHbmColumnType columnElement;
    private final TruthValue nullable;
    private final Set<String> indexConstraintNames;
    private final Set<String> ukConstraintNames;

    ColumnSourceImpl(MappingDocument mappingDocument, String tableName, JaxbHbmColumnType columnElement, Set<String> indexConstraintNames, Set<String> ukConstraintNames) {
        this(mappingDocument, tableName, columnElement, ColumnSourceImpl.interpretNotNullToNullability(columnElement.isNotNull()), indexConstraintNames, ukConstraintNames);
    }

    private static TruthValue interpretNotNullToNullability(Boolean notNull) {
        if (notNull == null) {
            return TruthValue.UNKNOWN;
        }
        return notNull != false ? TruthValue.FALSE : TruthValue.TRUE;
    }

    ColumnSourceImpl(MappingDocument mappingDocument, String tableName, JaxbHbmColumnType columnElement, TruthValue nullable, Set<String> indexConstraintNames, Set<String> ukConstraintNames) {
        super(mappingDocument);
        this.tableName = tableName;
        this.columnElement = columnElement;
        this.nullable = nullable;
        this.indexConstraintNames = CommaSeparatedStringHelper.splitAndCombine(indexConstraintNames, columnElement.getIndex());
        this.ukConstraintNames = CommaSeparatedStringHelper.splitAndCombine(ukConstraintNames, columnElement.getUniqueKey());
    }

    @Override
    public RelationalValueSource.Nature getNature() {
        return RelationalValueSource.Nature.COLUMN;
    }

    @Override
    public String getName() {
        return this.columnElement.getName();
    }

    @Override
    public TruthValue isNullable() {
        return this.nullable;
    }

    @Override
    public String getDefaultValue() {
        return this.columnElement.getDefault();
    }

    @Override
    public String getSqlType() {
        return this.columnElement.getSqlType();
    }

    @Override
    public JdbcDataType getDatatype() {
        return null;
    }

    @Override
    public SizeSource getSizeSource() {
        return Helper.interpretSizeSource(this.columnElement.getLength(), this.columnElement.getScale(), this.columnElement.getPrecision());
    }

    @Override
    public String getReadFragment() {
        return this.columnElement.getRead();
    }

    @Override
    public String getWriteFragment() {
        return this.columnElement.getWrite();
    }

    @Override
    public boolean isUnique() {
        return this.columnElement.isUnique() != null && this.columnElement.isUnique() != false;
    }

    @Override
    public String getCheckCondition() {
        return this.columnElement.getCheck();
    }

    @Override
    public String getComment() {
        return this.columnElement.getComment();
    }

    @Override
    public String getContainingTableName() {
        return this.tableName;
    }

    @Override
    public Set<String> getIndexConstraintNames() {
        return this.indexConstraintNames;
    }

    @Override
    public Set<String> getUniqueKeyConstraintNames() {
        return this.ukConstraintNames;
    }
}

