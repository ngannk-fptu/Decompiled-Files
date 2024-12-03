/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Maps
 */
package com.atlassian.plugins.navlink.consumer.menu.services;

import com.atlassian.plugins.custom_apps.api.CustomApp;
import com.atlassian.plugins.navlink.producer.navigation.links.SourceType;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class DuplicateLinkFilter {
    public List<CustomApp> filter(Iterable<CustomApp> localAndRemoteLinks) {
        LinkedHashMap appByUrl = Maps.newLinkedHashMap();
        for (CustomApp customApp : localAndRemoteLinks) {
            String customAppUrl = customApp.getUrl();
            if (appByUrl.containsKey(customAppUrl)) {
                if (!this.shouldReplace((CustomApp)appByUrl.get(customAppUrl), customApp)) continue;
                appByUrl.put(customAppUrl, customApp);
                continue;
            }
            appByUrl.put(customAppUrl, customApp);
        }
        ArrayList list = Lists.newArrayList();
        for (Map.Entry entry : appByUrl.entrySet()) {
            list.add(entry.getValue());
        }
        return list;
    }

    private boolean shouldReplace(CustomApp existing, CustomApp next) {
        if (next.isSelf()) {
            return true;
        }
        if (this.isHome(next) && this.isRemote(next) && this.isCustomApp(existing) && this.isLocal(existing)) {
            return true;
        }
        if (this.isCustomApp(existing) && this.isCustomApp(next)) {
            if (this.isLocal(next) && this.isRemote(existing)) {
                return true;
            }
            if (this.isRemote(next) && this.isRemote(existing)) {
                return existing.getDisplayName().compareTo(next.getDisplayName()) > 0;
            }
        }
        return false;
    }

    private boolean isHome(CustomApp app) {
        return "home".equals(app.getNavigationLink().getKey());
    }

    private boolean isCustomApp(CustomApp app) {
        return "custom-apps".equals(app.getNavigationLink().getKey());
    }

    private boolean isRemote(CustomApp app) {
        return app.getNavigationLink().getSource().type() == SourceType.REMOTE;
    }

    private boolean isLocal(CustomApp app) {
        return app.getNavigationLink().getSource().type() == SourceType.LOCAL;
    }
}

