/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.web.descriptors.WebFragmentModuleDescriptor
 *  com.atlassian.plugin.web.model.WebLink
 *  javax.servlet.http.HttpServletRequest
 */
package com.atlassian.confluence.impl.plugin.web.readonly;

import com.atlassian.confluence.impl.plugin.web.readonly.ReadOnlyWebFragmentModuleDescriptor;
import com.atlassian.confluence.themes.GlobalHelper;
import com.atlassian.confluence.util.GeneralUtil;
import com.atlassian.plugin.web.descriptors.WebFragmentModuleDescriptor;
import com.atlassian.plugin.web.model.WebLink;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;

public class ReadOnlyWebLink
implements WebLink {
    private final WebLink delegate;

    public ReadOnlyWebLink(WebLink delegate) {
        this.delegate = delegate;
    }

    public String getRenderedUrl(Map<String, Object> map) {
        return this.delegate.getRenderedUrl(map);
    }

    public String getDisplayableUrl(HttpServletRequest httpServletRequest, Map<String, Object> map) {
        return this.delegate.getDisplayableUrl(httpServletRequest, map);
    }

    public boolean hasAccessKey() {
        return this.delegate.hasAccessKey();
    }

    public String getAccessKey(Map<String, Object> map) {
        return this.delegate.getAccessKey(map);
    }

    @Deprecated
    public String getAccessKey(GlobalHelper helper) {
        return this.getAccessKey(new HashMap<String, Object>(Map.of("helper", helper)));
    }

    public String getId() {
        return this.delegate.getId();
    }

    public WebFragmentModuleDescriptor getDescriptor() {
        return GeneralUtil.applyIfNonNull(this.delegate.getDescriptor(), ReadOnlyWebFragmentModuleDescriptor::new);
    }
}

