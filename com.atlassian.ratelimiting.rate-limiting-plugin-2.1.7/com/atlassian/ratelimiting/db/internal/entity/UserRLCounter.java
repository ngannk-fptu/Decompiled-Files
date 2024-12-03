/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.java.ao.RawEntity
 *  net.java.ao.schema.AutoIncrement
 *  net.java.ao.schema.Indexed
 *  net.java.ao.schema.NotNull
 *  net.java.ao.schema.PrimaryKey
 *  net.java.ao.schema.Table
 */
package com.atlassian.ratelimiting.db.internal.entity;

import java.util.Date;
import net.java.ao.RawEntity;
import net.java.ao.schema.AutoIncrement;
import net.java.ao.schema.Indexed;
import net.java.ao.schema.NotNull;
import net.java.ao.schema.PrimaryKey;
import net.java.ao.schema.Table;

@Table(value="RL_USER_COUNTER")
public interface UserRLCounter
extends RawEntity<Long> {
    @NotNull
    @AutoIncrement
    @PrimaryKey(value="ID")
    public long getId();

    public void setId(long var1);

    @NotNull
    public String getNodeId();

    public void setNodeId(String var1);

    @NotNull
    @Indexed
    public String getUserId();

    public void setUserId(String var1);

    @NotNull
    @Indexed
    public Date getIntervalStart();

    public void setIntervalStart(Date var1);

    @NotNull
    public long getRejectCount();

    public void setRejectCount(long var1);
}

