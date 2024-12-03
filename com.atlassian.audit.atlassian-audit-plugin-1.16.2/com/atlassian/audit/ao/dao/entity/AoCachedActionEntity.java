/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
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

import javax.annotation.Nullable;
import net.java.ao.Accessor;
import net.java.ao.Preload;
import net.java.ao.RawEntity;
import net.java.ao.schema.AutoIncrement;
import net.java.ao.schema.Index;
import net.java.ao.schema.Indexes;
import net.java.ao.schema.NotNull;
import net.java.ao.schema.PrimaryKey;
import net.java.ao.schema.Table;

@Table(value="AUDIT_ACTION_CACHE")
@Preload(value={"*"})
@Indexes(value={@Index(name="action", methodNames={"getAction"}), @Index(name="actionI18nKey", methodNames={"getActionI18nKey"})})
public interface AoCachedActionEntity
extends RawEntity<Integer> {
    public static final String CACHED_ACTION_TABLE_NAME = "AUDIT_ACTION_CACHE";
    public static final String CACHED_ACTION_COLUMN = "ACTION";
    public static final String CACHED_ACTION_I18N_KEY_COLUMN = "ACTION_T_KEY";
    public static final String CACHED_ACTION_ID_COLUMN = "ID";

    @AutoIncrement
    @NotNull
    @PrimaryKey(value="ID")
    public Integer getId();

    @NotNull
    @Accessor(value="ACTION")
    public String getAction();

    @Nullable
    @Accessor(value="ACTION_T_KEY")
    public String getActionI18nKey();
}

