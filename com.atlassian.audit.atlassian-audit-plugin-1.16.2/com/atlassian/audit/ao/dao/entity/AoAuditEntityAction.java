/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.java.ao.Accessor
 *  net.java.ao.Preload
 *  net.java.ao.RawEntity
 *  net.java.ao.schema.NotNull
 *  net.java.ao.schema.PrimaryKey
 *  net.java.ao.schema.Table
 */
package com.atlassian.audit.ao.dao.entity;

import net.java.ao.Accessor;
import net.java.ao.Preload;
import net.java.ao.RawEntity;
import net.java.ao.schema.NotNull;
import net.java.ao.schema.PrimaryKey;
import net.java.ao.schema.Table;

@Table(value="AUDIT_ENTITY")
@Preload(value={"ACTION", "ACTION_T_KEY"})
public interface AoAuditEntityAction
extends RawEntity<String> {
    public static final String ID_COLUMN = "ID";
    public static final String ACTION_COLUMN = "ACTION";
    public static final String ACTION_TKEY_COLUMN = "ACTION_T_KEY";

    @Accessor(value="ID")
    public Long getId();

    @NotNull
    @Accessor(value="ACTION")
    @PrimaryKey(value="ACTION")
    public String getAction();

    @Accessor(value="ACTION_T_KEY")
    public String getActionI18nKey();
}

