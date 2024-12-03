/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  net.java.ao.Entity
 *  net.java.ao.Preload
 *  net.java.ao.schema.Index
 *  net.java.ao.schema.Indexes
 *  net.java.ao.schema.Table
 */
package com.atlassian.confluence.plugins.featurediscovery.entity;

import java.util.Date;
import javax.annotation.Nonnull;
import net.java.ao.Entity;
import net.java.ao.Preload;
import net.java.ao.schema.Index;
import net.java.ao.schema.Indexes;
import net.java.ao.schema.Table;

@Indexes(value={@Index(name="discovered_idx", methodNames={"getUserKey"})})
@Preload
@Table(value="discovered")
public interface DiscoveredFeatureAo
extends Entity {
    @Nonnull
    public String getPluginKey();

    public void setPluginKey(String var1);

    public String getKey();

    public void setKey(String var1);

    public String getUserKey();

    public void setUserKey(String var1);

    public Date getDate();

    public void setDate(Date var1);
}

