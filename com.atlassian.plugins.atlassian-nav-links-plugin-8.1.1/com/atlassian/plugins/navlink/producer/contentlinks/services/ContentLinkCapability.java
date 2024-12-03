/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.EntityLink
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableList$Builder
 *  com.google.common.collect.Maps
 */
package com.atlassian.plugins.navlink.producer.contentlinks.services;

import com.atlassian.applinks.api.EntityLink;
import com.atlassian.plugins.navlink.producer.capabilities.CapabilityKey;
import com.atlassian.plugins.navlink.producer.capabilities.RemoteApplicationWithCapabilities;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import java.util.HashMap;
import java.util.List;

class ContentLinkCapability {
    private EntityLink entityLink;
    private String contentLinkUrl;

    ContentLinkCapability(EntityLink entityLink, String contentLinkUrl) {
        this.entityLink = entityLink;
        this.contentLinkUrl = contentLinkUrl;
    }

    public static List<ContentLinkCapability> create(Iterable<RemoteApplicationWithCapabilities> applications, Iterable<EntityLink> entityLinks) {
        ImmutableList.Builder contentLinkCapabilities = ImmutableList.builder();
        HashMap appCapabilitiesByAppLinkId = Maps.newHashMap();
        for (RemoteApplicationWithCapabilities app : applications) {
            if (!app.hasCapability(CapabilityKey.CONTENT_LINKS)) continue;
            appCapabilitiesByAppLinkId.put(app.getApplicationLinkId(), app);
        }
        for (EntityLink entityLink : entityLinks) {
            String appLinkId = entityLink.getApplicationLink().getId().toString();
            if (!appCapabilitiesByAppLinkId.containsKey(appLinkId)) continue;
            contentLinkCapabilities.add((Object)new ContentLinkCapability(entityLink, ((RemoteApplicationWithCapabilities)appCapabilitiesByAppLinkId.get(appLinkId)).getCapabilityUrl(CapabilityKey.CONTENT_LINKS)));
        }
        return contentLinkCapabilities.build();
    }

    public String getContentLinkUrl() {
        return this.contentLinkUrl;
    }

    public EntityLink getEntityLink() {
        return this.entityLink;
    }
}

