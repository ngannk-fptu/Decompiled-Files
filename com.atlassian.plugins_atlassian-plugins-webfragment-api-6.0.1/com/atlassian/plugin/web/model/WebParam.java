/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.plugin.web.model;

import com.atlassian.plugin.web.descriptors.WebFragmentModuleDescriptor;
import java.util.Map;
import java.util.SortedMap;

public interface WebParam {
    public SortedMap<String, String> getParams();

    public Object get(String var1);

    public String getRenderedParam(String var1, Map<String, Object> var2);

    public WebFragmentModuleDescriptor getDescriptor();
}

