/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.beans.BeanUtils
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 */
package org.springframework.jdbc.object;

import java.util.Map;
import org.springframework.beans.BeanUtils;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.object.SqlQuery;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

public class GenericSqlQuery<T>
extends SqlQuery<T> {
    @Nullable
    private RowMapper<T> rowMapper;
    @Nullable
    private Class<? extends RowMapper> rowMapperClass;

    public void setRowMapper(RowMapper<T> rowMapper) {
        this.rowMapper = rowMapper;
    }

    public void setRowMapperClass(Class<? extends RowMapper> rowMapperClass) {
        this.rowMapperClass = rowMapperClass;
    }

    @Override
    public void afterPropertiesSet() {
        super.afterPropertiesSet();
        Assert.isTrue((this.rowMapper != null || this.rowMapperClass != null ? 1 : 0) != 0, (String)"'rowMapper' or 'rowMapperClass' is required");
    }

    @Override
    protected RowMapper<T> newRowMapper(@Nullable Object[] parameters, @Nullable Map<?, ?> context) {
        if (this.rowMapper != null) {
            return this.rowMapper;
        }
        Assert.state((this.rowMapperClass != null ? 1 : 0) != 0, (String)"No RowMapper set");
        return (RowMapper)BeanUtils.instantiateClass(this.rowMapperClass);
    }
}

