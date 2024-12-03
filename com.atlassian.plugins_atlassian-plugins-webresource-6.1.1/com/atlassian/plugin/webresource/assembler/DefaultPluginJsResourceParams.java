/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.webresource.api.assembler.resource.PluginJsResourceParams
 *  com.atlassian.webresource.api.assembler.resource.PluginUrlResource$BatchType
 */
package com.atlassian.plugin.webresource.assembler;

import com.atlassian.plugin.webresource.assembler.DefaultPluginUrlResourceParams;
import com.atlassian.webresource.api.assembler.resource.PluginJsResourceParams;
import com.atlassian.webresource.api.assembler.resource.PluginUrlResource;
import java.util.Map;

class DefaultPluginJsResourceParams
extends DefaultPluginUrlResourceParams
implements PluginJsResourceParams {
    public DefaultPluginJsResourceParams(Map<String, String> params, String key, PluginUrlResource.BatchType batchType) {
        super(params, key, batchType);
    }
}

