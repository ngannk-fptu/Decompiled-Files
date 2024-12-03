/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.activeobjects.tx.Transactional
 *  com.atlassian.confluence.user.ConfluenceUser
 */
package com.atlassian.confluence.plugins.createjiracontent.services;

import com.atlassian.activeobjects.tx.Transactional;
import com.atlassian.confluence.user.ConfluenceUser;

@Transactional
public interface FeatureDiscoveryService {
    public boolean hasUserDiscovered(ConfluenceUser var1);

    public void setUserDiscovered(ConfluenceUser var1, boolean var2);
}

