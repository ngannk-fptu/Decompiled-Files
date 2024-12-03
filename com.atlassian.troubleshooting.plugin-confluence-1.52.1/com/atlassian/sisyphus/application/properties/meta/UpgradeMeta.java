/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 */
package com.atlassian.sisyphus.application.properties.meta;

import com.atlassian.sisyphus.application.properties.meta.AbstractParseMeta;
import com.google.common.collect.Lists;

public class UpgradeMeta
extends AbstractParseMeta {
    public UpgradeMeta() {
        super("title.upgrade.history");
    }

    @Override
    protected void fillParseData() {
        this.pathMap.put("upgrade.version", Lists.newArrayList((Object[])new String[]{".//stp-properties-upgrade-version", ".//version"}));
        this.pathMap.put("upgrade.name", Lists.newArrayList((Object[])new String[]{".//stp-properties-upgrade-time", ".//time"}));
        this.pathMap.put("upgrade.build", Lists.newArrayList((Object[])new String[]{".//stp-properties-upgrade-build", ".//build"}));
    }

    @Override
    protected void setGroupNode() {
        this.groupNode.add("//upgrade-history");
    }
}

