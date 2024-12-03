/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.java.ao.Accessor
 *  net.java.ao.Entity
 *  net.java.ao.Preload
 *  net.java.ao.schema.AutoIncrement
 *  net.java.ao.schema.NotNull
 *  net.java.ao.schema.PrimaryKey
 *  net.java.ao.schema.StringLength
 *  net.java.ao.schema.Table
 *  net.java.ao.schema.Unique
 */
package com.atlassian.business.insights.core.ao.dao.entity;

import net.java.ao.Accessor;
import net.java.ao.Entity;
import net.java.ao.Preload;
import net.java.ao.schema.AutoIncrement;
import net.java.ao.schema.NotNull;
import net.java.ao.schema.PrimaryKey;
import net.java.ao.schema.StringLength;
import net.java.ao.schema.Table;
import net.java.ao.schema.Unique;

@Table(value="DATA_PIPELINE_CONFIG")
@Preload
public interface AoDataPipelineConfig
extends Entity {
    public static final String TABLE_NAME = "DATA_PIPELINE_CONFIG";
    public static final String ID_COLUMN = "ID";
    public static final String KEY_COLUMN = "KEY";
    public static final String VALUE_COLUMN = "VALUE";
    public static final int KEY_COLUMN_LENGTH = 250;

    @NotNull
    @PrimaryKey(value="ID")
    @AutoIncrement
    public int getID();

    @Unique
    @StringLength(value=250)
    @Accessor(value="KEY")
    public String getKey();

    @StringLength(value=-1)
    @Accessor(value="VALUE")
    public String getValue();

    public void setValue(String var1);
}

