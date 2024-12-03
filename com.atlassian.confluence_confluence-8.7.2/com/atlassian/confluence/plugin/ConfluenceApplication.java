/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.Application
 *  com.google.common.collect.ImmutableSet
 */
package com.atlassian.confluence.plugin;

import com.atlassian.confluence.util.GeneralUtil;
import com.atlassian.plugin.Application;
import com.google.common.collect.ImmutableSet;
import java.util.Set;

public class ConfluenceApplication
implements Application {
    private final String key;

    public ConfluenceApplication(String key) {
        this.key = key;
    }

    public String getKey() {
        return this.key;
    }

    public String getVersion() {
        return GeneralUtil.getVersionNumber();
    }

    public String getBuildNumber() {
        return GeneralUtil.getBuildNumber();
    }

    public static Set<ConfluenceApplication> getConfluenceApplications() {
        return ImmutableSet.of((Object)new ConfluenceApplication("confluence"), (Object)new ConfluenceApplication("com.atlassian.confluence"));
    }
}

