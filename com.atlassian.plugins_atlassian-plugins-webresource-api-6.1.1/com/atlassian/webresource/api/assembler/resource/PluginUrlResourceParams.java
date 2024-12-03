/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.ExperimentalApi
 */
package com.atlassian.webresource.api.assembler.resource;

import com.atlassian.annotations.ExperimentalApi;
import java.util.Map;

@ExperimentalApi
public interface PluginUrlResourceParams {
    @Deprecated
    public String conditionalComment();

    @Deprecated
    public boolean ieOnly();

    public Map<String, String> other();

    public Map<String, String> all();
}

