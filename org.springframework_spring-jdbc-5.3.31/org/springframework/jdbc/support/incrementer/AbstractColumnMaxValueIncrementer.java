/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.util.Assert
 */
package org.springframework.jdbc.support.incrementer;

import javax.sql.DataSource;
import org.springframework.jdbc.support.incrementer.AbstractDataFieldMaxValueIncrementer;
import org.springframework.util.Assert;

public abstract class AbstractColumnMaxValueIncrementer
extends AbstractDataFieldMaxValueIncrementer {
    private String columnName;
    private int cacheSize = 1;

    public AbstractColumnMaxValueIncrementer() {
    }

    public AbstractColumnMaxValueIncrementer(DataSource dataSource, String incrementerName, String columnName) {
        super(dataSource, incrementerName);
        Assert.notNull((Object)columnName, (String)"Column name must not be null");
        this.columnName = columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public String getColumnName() {
        return this.columnName;
    }

    public void setCacheSize(int cacheSize) {
        this.cacheSize = cacheSize;
    }

    public int getCacheSize() {
        return this.cacheSize;
    }

    @Override
    public void afterPropertiesSet() {
        super.afterPropertiesSet();
        if (this.columnName == null) {
            throw new IllegalArgumentException("Property 'columnName' is required");
        }
    }
}

