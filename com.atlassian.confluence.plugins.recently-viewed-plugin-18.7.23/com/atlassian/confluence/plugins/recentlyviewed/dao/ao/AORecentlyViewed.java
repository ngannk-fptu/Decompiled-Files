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
 */
package com.atlassian.confluence.plugins.recentlyviewed.dao.ao;

import java.util.Date;
import net.java.ao.Preload;
import net.java.ao.RawEntity;
import net.java.ao.schema.AutoIncrement;
import net.java.ao.schema.Indexed;
import net.java.ao.schema.NotNull;
import net.java.ao.schema.PrimaryKey;

@Preload
public interface AORecentlyViewed
extends RawEntity<Long> {
    @AutoIncrement
    @NotNull
    @PrimaryKey(value="ID")
    public long getId();

    @Indexed
    public String getUserKey();

    public void setUserKey(String var1);

    @Indexed
    public Long getContentId();

    public void setContentId(Long var1);

    @Indexed
    public String getContentType();

    public void setContentType(String var1);

    @Indexed
    public String getSpaceKey();

    public void setSpaceKey(String var1);

    @Indexed
    public Date getLastViewDate();

    public void setLastViewDate(Date var1);
}

