/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.PublicSpi
 */
package com.atlassian.activeobjects.spi;

import com.atlassian.annotations.PublicSpi;

@PublicSpi
public interface ActiveObjectsPluginConfiguration {
    public String getDatabaseBaseDirectory();
}

