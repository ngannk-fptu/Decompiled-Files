/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.java.ao.Entity
 *  net.java.ao.Preload
 */
package com.atlassian.confluence.plugins.createjiracontent.entities;

import net.java.ao.Entity;
import net.java.ao.Preload;

@Preload
public interface FeatureDiscovery
extends Entity {
    public String getUserKey();

    public void setUserKey(String var1);

    public Boolean getDiscovered();

    public void setDiscovered(Boolean var1);
}

