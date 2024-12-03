/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 */
package com.atlassian.plugin.notifications.salext.refapp;

import com.atlassian.plugin.notifications.spi.salext.GroupManager;
import com.google.common.collect.Lists;

public class RefappGroupManager
implements GroupManager {
    @Override
    public Iterable<String> getGroups() {
        return Lists.newArrayList((Object[])new String[]{"administrators", "developers", "users"});
    }
}

