/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.EntityType
 */
package com.atlassian.applinks.host.spi;

import com.atlassian.applinks.api.EntityType;

public interface EntityReference {
    public String getKey();

    public EntityType getType();

    public String getName();
}

