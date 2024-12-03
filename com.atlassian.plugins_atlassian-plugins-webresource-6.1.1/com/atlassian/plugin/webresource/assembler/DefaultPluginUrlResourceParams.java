/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.webresource.api.assembler.resource.PluginUrlResource$BatchType
 *  com.atlassian.webresource.api.assembler.resource.PluginUrlResourceParams
 *  com.google.common.base.Predicate
 *  com.google.common.collect.Maps
 *  javax.annotation.Nullable
 *  org.apache.commons.lang3.BooleanUtils
 */
package com.atlassian.plugin.webresource.assembler;

import com.atlassian.webresource.api.assembler.resource.PluginUrlResource;
import com.atlassian.webresource.api.assembler.resource.PluginUrlResourceParams;
import com.google.common.base.Predicate;
import com.google.common.collect.Maps;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.annotation.Nullable;
import org.apache.commons.lang3.BooleanUtils;

abstract class DefaultPluginUrlResourceParams
implements PluginUrlResourceParams {
    protected final Map<String, String> params;

    public DefaultPluginUrlResourceParams(Map<String, String> params, String key, PluginUrlResource.BatchType batchType) {
        this.params = new LinkedHashMap<String, String>(params);
        this.params.put("data-wrm-key", key);
        this.params.put("data-wrm-batch-type", batchType.name().toLowerCase());
        if (!this.ieOnly()) {
            this.params.remove("ieonly");
        }
    }

    @Deprecated
    public String conditionalComment() {
        return this.params.get("conditionalComment");
    }

    @Deprecated
    public boolean ieOnly() {
        return BooleanUtils.toBoolean((String)this.params.get("ieonly"));
    }

    public Map<String, String> other() {
        return Maps.filterEntries(this.params, (Predicate)new Predicate<Map.Entry<String, String>>(){

            public boolean apply(@Nullable Map.Entry<String, String> input) {
                String key = input.getKey();
                return !key.equals("conditionalComment") && !key.equals("ieonly");
            }
        });
    }

    public Map<String, String> all() {
        return Collections.unmodifiableMap(this.params);
    }
}

