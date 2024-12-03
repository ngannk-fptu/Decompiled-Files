/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.java.ao.Mutator
 *  net.java.ao.OneToMany
 *  net.java.ao.Preload
 *  net.java.ao.RawEntity
 *  net.java.ao.schema.AutoIncrement
 *  net.java.ao.schema.NotNull
 *  net.java.ao.schema.PrimaryKey
 *  net.java.ao.schema.StringLength
 *  net.java.ao.schema.Unique
 */
package com.atlassian.mywork.host.upgrade.v8;

import com.atlassian.mywork.host.upgrade.v8.AOUserApplicationLink;
import java.util.Date;
import net.java.ao.Mutator;
import net.java.ao.OneToMany;
import net.java.ao.Preload;
import net.java.ao.RawEntity;
import net.java.ao.schema.AutoIncrement;
import net.java.ao.schema.NotNull;
import net.java.ao.schema.PrimaryKey;
import net.java.ao.schema.StringLength;
import net.java.ao.schema.Unique;

@Preload
public interface AOUser
extends RawEntity<Long> {
    @AutoIncrement
    @NotNull
    @PrimaryKey(value="ID")
    public long getId();

    @Unique
    public String getUsername();

    @Mutator(value="Username")
    public void setUserKey(String var1);

    public long getLastReadNotificationId();

    public void setLastReadNotificationId(long var1);

    @OneToMany
    public AOUserApplicationLink[] getUserApplicationLinks();

    public Date getCreated();

    public void setCreated(Date var1);

    public Date getUpdated();

    public void setUpdated(Date var1);

    @StringLength(value=-1)
    public String getTaskOrdering();

    public void setTaskOrdering(String var1);
}

