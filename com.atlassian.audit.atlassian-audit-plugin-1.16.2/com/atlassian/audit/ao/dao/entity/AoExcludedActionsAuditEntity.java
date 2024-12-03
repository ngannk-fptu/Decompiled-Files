/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.java.ao.Accessor
 *  net.java.ao.Preload
 *  net.java.ao.RawEntity
 *  net.java.ao.schema.AutoIncrement
 *  net.java.ao.schema.Index
 *  net.java.ao.schema.Indexes
 *  net.java.ao.schema.NotNull
 *  net.java.ao.schema.PrimaryKey
 *  net.java.ao.schema.Table
 */
package com.atlassian.audit.ao.dao.entity;

import net.java.ao.Accessor;
import net.java.ao.Preload;
import net.java.ao.RawEntity;
import net.java.ao.schema.AutoIncrement;
import net.java.ao.schema.Index;
import net.java.ao.schema.Indexes;
import net.java.ao.schema.NotNull;
import net.java.ao.schema.PrimaryKey;
import net.java.ao.schema.Table;

@Table(value="AUDIT_DENY_LISTED")
@Preload(value={"ACTION"})
@Indexes(value={@Index(name="action", methodNames={"getAction"})})
public interface AoExcludedActionsAuditEntity
extends RawEntity<Integer> {
    public static final String TABLE_NAME = "AUDIT_DENY_LISTED";
    public static final String ID_COLUMN = "ID";
    public static final String ACTION_COLUMN = "ACTION";

    @AutoIncrement
    @NotNull
    @PrimaryKey(value="ID")
    public Long getId();

    @Accessor(value="ACTION")
    public String getAction();
}

