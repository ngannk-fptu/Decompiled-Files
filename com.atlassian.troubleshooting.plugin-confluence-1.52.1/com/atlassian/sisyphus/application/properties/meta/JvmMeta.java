/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 */
package com.atlassian.sisyphus.application.properties.meta;

import com.atlassian.sisyphus.application.properties.meta.AbstractParseMeta;
import com.google.common.collect.Lists;

public class JvmMeta
extends AbstractParseMeta {
    public JvmMeta() {
        super("title.jvm");
    }

    @Override
    protected void fillParseData() {
        this.pathMap.put("jvm.version", Lists.newArrayList((Object[])new String[]{".//java.runtime.version", ".//java-runtime-version"}));
        this.pathMap.put("jvm.vendor", Lists.newArrayList((Object[])new String[]{".//java-vm-vendor", ".//java.vm.vendor"}));
        this.pathMap.put("jvm.home", Lists.newArrayList((Object[])new String[]{".//java-home", ".//java.home"}));
        this.pathMap.put("jvm.max.memory", Lists.newArrayList((Object[])new String[]{".//stp-properties-java-heap-max", ".//max-heap"}));
        this.pathMap.put("jvm.free.memory", Lists.newArrayList((Object[])new String[]{".//stp-properties-java-heap-available", ".//heap-available"}));
        this.pathMap.put("jvm.used.memory", Lists.newArrayList((Object[])new String[]{".//stp-properties-java-heap-used", ".//heap-used"}));
        this.pathMap.put("jvm.max.permgen", Lists.newArrayList((Object[])new String[]{".//stp-properties-java-permgen-max", ".//max-permgen"}));
        this.pathMap.put("jvm.used.permgen", Lists.newArrayList((Object[])new String[]{".//stp-properties-java-permgen-used", ".//permgen-used"}));
        this.pathMap.put("jvm.arguments", Lists.newArrayList((Object[])new String[]{".//stp-properties-java-vm-arguments", ".//virtual-machine-arguments"}));
    }

    @Override
    protected void setGroupNode() {
    }
}

