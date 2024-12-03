/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 */
package com.atlassian.sisyphus.application.properties.meta;

import com.atlassian.sisyphus.application.properties.meta.AbstractParseMeta;
import com.google.common.collect.Lists;

public class ServiceMeta
extends AbstractParseMeta {
    public ServiceMeta() {
        super("title.services");
    }

    @Override
    protected void fillParseData() {
        this.pathMap.put("service.lastrun", Lists.newArrayList((Object[])new String[]{".//stp-properties-services-service-last-run", ".//last-run"}));
        this.pathMap.put("service.delay", Lists.newArrayList((Object[])new String[]{".//stp-properties-services-service-delay", ".//delay"}));
        this.pathMap.put("service.description", Lists.newArrayList((Object[])new String[]{".//stp-properties-services-service-description", ".//description"}));
        this.pathMap.put("service.name", Lists.newArrayList((Object[])new String[]{".//stp-properties-services-service-name", ".//name"}));
    }

    @Override
    protected void setGroupNode() {
        this.groupNode.add("//services/service");
    }
}

