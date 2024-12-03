/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.mywork.model.Status
 *  net.java.ao.Accessor
 *  net.java.ao.Mutator
 *  net.java.ao.Preload
 *  net.java.ao.RawEntity
 *  net.java.ao.schema.AutoIncrement
 *  net.java.ao.schema.Default
 *  net.java.ao.schema.Indexed
 *  net.java.ao.schema.NotNull
 *  net.java.ao.schema.PrimaryKey
 *  net.java.ao.schema.StringLength
 */
package com.atlassian.mywork.host.dao.ao;

import com.atlassian.mywork.model.Status;
import java.util.Date;
import net.java.ao.Accessor;
import net.java.ao.Mutator;
import net.java.ao.Preload;
import net.java.ao.RawEntity;
import net.java.ao.schema.AutoIncrement;
import net.java.ao.schema.Default;
import net.java.ao.schema.Indexed;
import net.java.ao.schema.NotNull;
import net.java.ao.schema.PrimaryKey;
import net.java.ao.schema.StringLength;

@Preload
public interface AONotification
extends RawEntity<Long> {
    @AutoIncrement
    @NotNull
    @PrimaryKey(value="ID")
    public long getId();

    @Indexed
    @Accessor(value="User")
    public String getUserKey();

    @Mutator(value="User")
    public void setUserKey(String var1);

    @StringLength(value=-1)
    public String getIconUrl();

    public void setIconUrl(String var1);

    @StringLength(value=-1)
    public String getTitle();

    public void setTitle(String var1);

    @StringLength(value=-1)
    public String getDescription();

    public void setDescription(String var1);

    @Indexed
    public String getGlobalId();

    public void setGlobalId(String var1);

    public String getGroupingId();

    public void setGroupingId(String var1);

    @StringLength(value=-1)
    public String getItemIconUrl();

    public void setItemIconUrl(String var1);

    @StringLength(value=-1)
    public String getItemTitle();

    public void setItemTitle(String var1);

    @StringLength(value=-1)
    public String getUrl();

    public void setUrl(String var1);

    @StringLength(value=-1)
    public String getItemUrl();

    public void setItemUrl(String var1);

    @StringLength(value=-1)
    public String getMetadata();

    public void setMetadata(String var1);

    @Indexed
    public Date getCreated();

    public void setCreated(Date var1);

    public Date getUpdated();

    public void setUpdated(Date var1);

    public Status getStatus();

    public void setStatus(Status var1);

    public String getApplication();

    public void setApplication(String var1);

    public String getEntity();

    public void setEntity(String var1);

    public String getAction();

    public void setAction(String var1);

    @StringLength(value=-1)
    public String getActionIconUrl();

    public void setActionIconUrl(String var1);

    public String getApplicationLinkId();

    public void setApplicationLinkId(String var1);

    @Default(value="false")
    public boolean isRead();

    public void setRead(boolean var1);

    @Default(value="false")
    public boolean isPinned();

    public void setPinned(boolean var1);
}

