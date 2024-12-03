/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.web.descriptors.WebFragmentModuleDescriptor
 *  com.atlassian.plugin.web.model.WebParam
 */
package com.atlassian.confluence.impl.plugin.web.readonly;

import com.atlassian.confluence.impl.plugin.web.readonly.ReadOnlyWebFragmentModuleDescriptor;
import com.atlassian.confluence.util.GeneralUtil;
import com.atlassian.plugin.web.descriptors.WebFragmentModuleDescriptor;
import com.atlassian.plugin.web.model.WebParam;
import java.util.Map;
import java.util.SortedMap;

public class ReadOnlyWebParam
implements WebParam {
    private final WebParam delegate;

    public ReadOnlyWebParam(WebParam delegate) {
        this.delegate = delegate;
    }

    public SortedMap<String, String> getParams() {
        return this.delegate.getParams();
    }

    public Object get(String s) {
        return this.delegate.get(s);
    }

    public String getRenderedParam(String s, Map<String, Object> map) {
        return this.delegate.getRenderedParam(s, map);
    }

    public WebFragmentModuleDescriptor getDescriptor() {
        return GeneralUtil.applyIfNonNull(this.delegate.getDescriptor(), ReadOnlyWebFragmentModuleDescriptor::new);
    }
}

