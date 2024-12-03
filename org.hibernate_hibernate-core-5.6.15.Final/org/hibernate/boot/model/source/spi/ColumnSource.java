/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.boot.model.source.spi;

import java.util.Set;
import org.hibernate.boot.model.TruthValue;
import org.hibernate.boot.model.source.spi.JdbcDataType;
import org.hibernate.boot.model.source.spi.RelationalValueSource;
import org.hibernate.boot.model.source.spi.SizeSource;

public interface ColumnSource
extends RelationalValueSource {
    public String getName();

    public String getReadFragment();

    public String getWriteFragment();

    public TruthValue isNullable();

    public String getDefaultValue();

    public String getSqlType();

    public JdbcDataType getDatatype();

    public SizeSource getSizeSource();

    public boolean isUnique();

    public String getCheckCondition();

    public String getComment();

    public Set<String> getIndexConstraintNames();

    public Set<String> getUniqueKeyConstraintNames();
}

