/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.business.insights.api.filter.OptOutEntityType
 *  net.java.ao.Accessor
 *  net.java.ao.Entity
 *  net.java.ao.Preload
 *  net.java.ao.schema.AutoIncrement
 *  net.java.ao.schema.NotNull
 *  net.java.ao.schema.PrimaryKey
 *  net.java.ao.schema.Table
 */
package com.atlassian.business.insights.core.ao.dao.entity;

import com.atlassian.business.insights.api.filter.OptOutEntityType;
import net.java.ao.Accessor;
import net.java.ao.Entity;
import net.java.ao.Preload;
import net.java.ao.schema.AutoIncrement;
import net.java.ao.schema.NotNull;
import net.java.ao.schema.PrimaryKey;
import net.java.ao.schema.Table;

@Table(value="DATA_PIPELINE_EOO")
@Preload
public interface AoEntityOptOutIdentifier
extends Entity {
    public static final String TABLE_NAME = "DATA_PIPELINE_EOO";
    public static final String ID_COLUMN = "ID";
    public static final String ENTITY_IDENTIFIER_COLUMN = "ENTITY_IDENTIFIER";
    public static final String ENTITY_TYPE_COLUMN = "ENTITY_TYPE";

    @AutoIncrement
    @NotNull
    @PrimaryKey(value="ID")
    public int getID();

    @NotNull
    @Accessor(value="ENTITY_IDENTIFIER")
    public String getResourceIdentifier();

    @NotNull
    @Accessor(value="ENTITY_TYPE")
    public OptOutEntityType getResourceType();
}

