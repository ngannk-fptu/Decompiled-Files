/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.java.ao.Preload
 *  net.java.ao.RawEntity
 *  net.java.ao.schema.AutoIncrement
 *  net.java.ao.schema.Indexed
 *  net.java.ao.schema.NotNull
 *  net.java.ao.schema.PrimaryKey
 *  net.java.ao.schema.Table
 */
package com.atlassian.mywork.host.upgrade.v8;

import com.atlassian.mywork.host.upgrade.v8.AOUser;
import java.util.Date;
import net.java.ao.Preload;
import net.java.ao.RawEntity;
import net.java.ao.schema.AutoIncrement;
import net.java.ao.schema.Indexed;
import net.java.ao.schema.NotNull;
import net.java.ao.schema.PrimaryKey;
import net.java.ao.schema.Table;

@Preload
@Table(value="user_app_link")
public interface AOUserApplicationLink
extends RawEntity<Long> {
    @AutoIncrement
    @NotNull
    @PrimaryKey(value="ID")
    public long getId();

    public AOUser getUser();

    public void setUser(AOUser var1);

    @Indexed
    public String getApplicationLinkId();

    public void setApplicationLinkId(String var1);

    public boolean isAuthVerified();

    public void setAuthVerified(boolean var1);

    public Date getCreated();

    public void setCreated(Date var1);

    public Date getUpdated();

    public void setUpdated(Date var1);
}

