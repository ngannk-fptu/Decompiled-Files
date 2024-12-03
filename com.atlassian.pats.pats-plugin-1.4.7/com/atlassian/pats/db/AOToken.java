/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.java.ao.RawEntity
 *  net.java.ao.schema.AutoIncrement
 *  net.java.ao.schema.Index
 *  net.java.ao.schema.Indexes
 *  net.java.ao.schema.NotNull
 *  net.java.ao.schema.PrimaryKey
 *  net.java.ao.schema.Table
 */
package com.atlassian.pats.db;

import com.atlassian.pats.db.NotificationState;
import java.util.Date;
import net.java.ao.RawEntity;
import net.java.ao.schema.AutoIncrement;
import net.java.ao.schema.Index;
import net.java.ao.schema.Indexes;
import net.java.ao.schema.NotNull;
import net.java.ao.schema.PrimaryKey;
import net.java.ao.schema.Table;

@Table(value="PERSONAL_TOKEN")
@Indexes(value={@Index(name="filterIndex", methodNames={"getTokenId", "getExpiringAt"})})
public interface AOToken
extends RawEntity<Long> {
    @AutoIncrement
    @NotNull
    @PrimaryKey(value="ID")
    public long getId();

    @NotNull
    public String getName();

    public void setName(String var1);

    @NotNull
    public String getUserKey();

    public void setUserKey(String var1);

    @NotNull
    public Date getCreatedAt();

    public void setCreatedAt(Date var1);

    public Date getLastAccessedAt();

    public void setLastAccessedAt(Date var1);

    @NotNull
    public String getHashedToken();

    public void setHashedToken(String var1);

    @NotNull
    public String getTokenId();

    public void setTokenId(String var1);

    @NotNull
    public Date getExpiringAt();

    public void setExpiringAt(Date var1);

    @NotNull
    public NotificationState getNotificationState();

    public NotificationState setNotificationState(NotificationState var1);
}

