/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.web.descriptors.WebFragmentModuleDescriptor
 *  com.atlassian.plugin.web.model.WebLabel
 *  com.atlassian.velocity.htmlsafe.HtmlSafe
 *  javax.servlet.http.HttpServletRequest
 */
package com.atlassian.confluence.impl.plugin.web.readonly;

import com.atlassian.confluence.impl.plugin.web.readonly.ReadOnlyWebFragmentModuleDescriptor;
import com.atlassian.confluence.util.GeneralUtil;
import com.atlassian.plugin.web.descriptors.WebFragmentModuleDescriptor;
import com.atlassian.plugin.web.model.WebLabel;
import com.atlassian.velocity.htmlsafe.HtmlSafe;
import java.util.Map;
import java.util.SortedMap;
import javax.servlet.http.HttpServletRequest;

public class ReadOnlyWebLabel
implements WebLabel {
    private final WebLabel delegate;

    public ReadOnlyWebLabel(WebLabel delegate) {
        this.delegate = delegate;
    }

    public String getKey() {
        return this.delegate.getKey();
    }

    public String getNoKeyValue() {
        return this.delegate.getNoKeyValue();
    }

    @HtmlSafe
    public String getDisplayableLabel(HttpServletRequest httpServletRequest, Map<String, Object> map) {
        return this.delegate.getDisplayableLabel(httpServletRequest, map);
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

