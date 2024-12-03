/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.activeobjects.admin;

import com.atlassian.activeobjects.admin.PluginInfo;
import java.util.List;

public interface PluginToTablesMapping {
    public void add(PluginInfo var1, List<String> var2);

    public PluginInfo get(String var1);
}

