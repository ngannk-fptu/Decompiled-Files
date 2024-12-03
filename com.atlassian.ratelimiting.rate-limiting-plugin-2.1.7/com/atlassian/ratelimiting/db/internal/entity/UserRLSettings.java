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

@Table(value="USER_RL_SETTINGS")
public interface UserRLSettings
extends RawEntity<String> {
    @NotNull
    @PrimaryKey(value="UserId")
    public String getUserId();

    public void setUserId(String var1);

    @NotNull
    public int getCapacity();

    public void setCapacity(int var1);

    @NotNull
    public int getFillRate();

    public void setFillRate(int var1);

    @NotNull
    public int getIntervalFrequency();

    public void setIntervalFrequency(int var1);

    @NotNull
    public String getIntervalTimeUnit();

    public void setIntervalTimeUnit(String var1);

    @NotNull
    public boolean isWhitelisted();

    public void setWhitelisted(boolean var1);
}

