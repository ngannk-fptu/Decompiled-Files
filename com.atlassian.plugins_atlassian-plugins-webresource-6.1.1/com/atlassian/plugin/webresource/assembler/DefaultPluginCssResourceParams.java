/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.webresource.api.assembler.resource.PluginCssResourceParams
 *  com.atlassian.webresource.api.assembler.resource.PluginUrlResource$BatchType
 *  com.google.common.base.Predicate
 *  com.google.common.collect.Maps
 *  javax.annotation.Nullable
 */
package com.atlassian.plugin.webresource.assembler;

import com.atlassian.plugin.webresource.assembler.DefaultPluginUrlResourceParams;
import com.atlassian.webresource.api.assembler.resource.PluginCssResourceParams;
import com.atlassian.webresource.api.assembler.resource.PluginUrlResource;
import com.google.common.base.Predicate;
import com.google.common.collect.Maps;
import java.util.Map;
import javax.annotation.Nullable;

class DefaultPluginCssResourceParams
extends DefaultPluginUrlResourceParams
implements PluginCssResourceParams {
    public DefaultPluginCssResourceParams(Map<String, String> params, String key, PluginUrlResource.BatchType batchType) {
        super(params, key, batchType);
    }

    public String media() {
        return (String)this.params.get("media");
    }

    @Override
    public Map<String, String> other() {
        return Maps.filterEntries(super.other(), (Predicate)new Predicate<Map.Entry<String, String>>(){

            public boolean apply(@Nullable Map.Entry<String, String> input) {
                return !input.getKey().equals("media");
            }
        });
    }
}

