/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.java.ao.RawEntity
 *  net.java.ao.schema.NotNull
 *  net.java.ao.schema.PrimaryKey
 *  net.java.ao.schema.Table
 */
package com.atlassian.ratelimiting.db.internal.entity;

import net.java.ao.RawEntity;
import net.java.ao.schema.NotNull;
import net.java.ao.schema.PrimaryKey;
import net.java.ao.schema.Table;

@Table(value="SETTINGS_VERSION")
public interface RLSettingsVersion
extends RawEntity<String> {
    @NotNull
    @PrimaryKey(value="TYPE")
    public String getType();

    public void setType(String var1);

    @NotNull
    public Long getVersion();

    public void setVersion(Long var1);
}

