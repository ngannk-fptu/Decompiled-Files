/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.activeobjects.tx.Transactional
 */
package com.atlassian.confluence.plugins.featurediscovery.manager;

import com.atlassian.activeobjects.tx.Transactional;
import com.atlassian.confluence.plugins.featurediscovery.model.DiscoveredFeature;
import java.util.Date;
import java.util.List;

@Transactional
public interface DiscoveredFeatureManager {
    public DiscoveredFeature find(String var1, String var2, String var3);

    public DiscoveredFeature create(String var1, String var2, String var3, Date var4);

    public void delete(String var1, String var2, String var3);

    public List<DiscoveredFeature> listForUser(String var1);
}

