/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.java.ao.Entity
 *  net.java.ao.Preload
 *  net.java.ao.schema.Index
 *  net.java.ao.schema.Indexes
 */
package com.atlassian.confluence.plugins.featurediscovery.entity;

import java.util.Date;
import net.java.ao.Entity;
import net.java.ao.Preload;
import net.java.ao.schema.Index;
import net.java.ao.schema.Indexes;

@Indexes(value={@Index(name="metadata_idx", methodNames={"getContext", "getKey"})})
@Preload
public interface FeatureMetadataAo
extends Entity {
    public String getContext();

    public void setContext(String var1);

    public String getKey();

    public void setKey(String var1);

    public Date getInstallationDate();

    public void setInstallationDate(Date var1);
}

