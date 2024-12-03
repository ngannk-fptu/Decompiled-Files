/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 */
package com.atlassian.sisyphus.application.properties.meta;

import com.atlassian.sisyphus.application.properties.meta.AbstractParseMeta;
import com.google.common.collect.Lists;

public class PluginMeta
extends AbstractParseMeta {
    public PluginMeta() {
        super("title.plugin");
    }

    @Override
    protected void fillParseData() {
        this.pathMap.put("plugin.status", Lists.newArrayList((Object[])new String[]{".//stp-properties-plugins-plugin-status", ".//status"}));
        this.pathMap.put("plugin.name", Lists.newArrayList((Object[])new String[]{".//stp-properties-plugins-plugin-name", ".//name"}));
        this.pathMap.put("plugin.version", Lists.newArrayList((Object[])new String[]{".//stp-properties-plugins-plugin-version", ".//version"}));
        this.pathMap.put("plugin.vendor.url", Lists.newArrayList((Object[])new String[]{".//stp-properties-plugins-plugin-vendor-url", ".//vendor-url"}));
        this.pathMap.put("plugin.userinstalled", Lists.newArrayList((Object[])new String[]{".//stp-properties-plugins-plugin-user-installed", ".//user-installed"}));
        this.pathMap.put("plugin.key", Lists.newArrayList((Object[])new String[]{".//stp-properties-plugins-plugin-key", ".//key"}));
        this.pathMap.put("plugin.vendor", Lists.newArrayList((Object[])new String[]{".//stp-properties-plugins-plugin-vendor", ".//vendor"}));
    }

    @Override
    protected void setGroupNode() {
        this.groupNode.add("//plugins/plugin");
        this.groupNode.add("//enabled-plugins/plugin");
    }
}

