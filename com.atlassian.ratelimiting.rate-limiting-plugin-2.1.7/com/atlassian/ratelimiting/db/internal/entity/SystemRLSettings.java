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

@Table(value="SYSTEM_RL_SETTINGS")
public interface SystemRLSettings
extends RawEntity<String> {
    @NotNull
    @PrimaryKey(value="NAME")
    public String getName();

    public void setName(String var1);

    @NotNull
    public String getMode();

    public void setMode(String var1);

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
    public String getFlushJobDuration();

    public void setFlushJobDuration(String var1);

    @NotNull
    public String getCleanJobDuration();

    public void setCleanJobDuration(String var1);

    @NotNull
    public String getRetentionPeriodDuration();

    public void setRetentionPeriodDuration(String var1);

    @NotNull
    public String getReaperJobDuration();

    public void setReaperJobDuration(String var1);

    @NotNull
    public String getSettingsReloadJobDuration();

    public void setSettingsReloadJobDuration(String var1);
}

