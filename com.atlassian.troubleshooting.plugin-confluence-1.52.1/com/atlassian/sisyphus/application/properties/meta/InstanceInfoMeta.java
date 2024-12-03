/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 */
package com.atlassian.sisyphus.application.properties.meta;

import com.atlassian.sisyphus.application.properties.meta.AbstractParseMeta;
import com.google.common.collect.Lists;

public class InstanceInfoMeta
extends AbstractParseMeta {
    public InstanceInfoMeta() {
        super("title.instanceinfo");
    }

    @Override
    protected void fillParseData() {
        this.pathMap.put("info.appversion", Lists.newArrayList((Object[])new String[]{".//Version", ".//system-version"}));
        this.pathMap.put("info.build.number", Lists.newArrayList((Object[])new String[]{".//build-number", ".//Build-Number"}));
        this.pathMap.put("info.system.time", Lists.newArrayList((Object[])new String[]{".//system-time", ".//System-Time", ".//stp-properties-system-time"}));
        this.pathMap.put("info.system.date", Lists.newArrayList((Object[])new String[]{".//system-date", ".//System-Date", ".//stp-properties-system-date"}));
        this.pathMap.put("info.product.name", Lists.newArrayList((Object[])new String[]{".//product[@name]"}));
        this.pathMap.put("info.product.version", Lists.newArrayList((Object[])new String[]{".//product[@version]"}));
        this.pathMap.put("info.sen", Lists.newArrayList((Object[])new String[]{"//sen"}));
        this.pathMap.put("info.db.jndi.address", Lists.newArrayList((Object[])new String[]{".//Database-JNDI-address"}));
        this.pathMap.put("info.db.version", Lists.newArrayList((Object[])new String[]{".//Database-version"}));
        this.pathMap.put("info.db.driver", Lists.newArrayList((Object[])new String[]{".//Database-driver"}));
        this.pathMap.put("info.db.type", Lists.newArrayList((Object[])new String[]{".//Database-type"}));
    }

    @Override
    protected void setGroupNode() {
    }
}

