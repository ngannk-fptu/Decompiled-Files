/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.activeobjects.external.ActiveObjects
 *  com.atlassian.confluence.user.ConfluenceUser
 *  net.java.ao.DBParam
 */
package com.atlassian.confluence.plugins.createjiracontent.services;

import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.confluence.plugins.createjiracontent.entities.FeatureDiscovery;
import com.atlassian.confluence.plugins.createjiracontent.services.FeatureDiscoveryService;
import com.atlassian.confluence.user.ConfluenceUser;
import net.java.ao.DBParam;

public class DefaultFeatureDiscoveryService
implements FeatureDiscoveryService {
    private final ActiveObjects ao;

    public DefaultFeatureDiscoveryService(ActiveObjects ao) {
        this.ao = ao;
    }

    @Override
    public boolean hasUserDiscovered(ConfluenceUser user) {
        FeatureDiscovery discovery = this.findForUser(user);
        return discovery != null ? discovery.getDiscovered() : false;
    }

    @Override
    public void setUserDiscovered(ConfluenceUser user, boolean discovered) {
        FeatureDiscovery entity = this.findForUser(user);
        if (entity == null) {
            entity = (FeatureDiscovery)this.ao.create(FeatureDiscovery.class, new DBParam[0]);
            entity.setUserKey(user.getKey().toString());
        }
        entity.setDiscovered(discovered);
        entity.save();
    }

    private FeatureDiscovery findForUser(ConfluenceUser user) {
        FeatureDiscovery[] featureDiscovery = (FeatureDiscovery[])this.ao.find(FeatureDiscovery.class, "USER_KEY = ?", new Object[]{user.getKey().toString()});
        if (featureDiscovery != null && featureDiscovery.length > 0) {
            return featureDiscovery[0];
        }
        return null;
    }
}

