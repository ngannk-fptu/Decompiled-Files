/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.velocity.htmlsafe.HtmlSafe
 *  com.atlassian.plugin.web.descriptors.WebFragmentModuleDescriptor
 *  com.atlassian.plugin.web.model.WebLabel
 *  javax.servlet.http.HttpServletRequest
 */
package com.atlassian.confluence.plugin.descriptor.web.model;

import com.atlassian.confluence.plugin.descriptor.web.WebInterfaceContext;
import com.atlassian.confluence.velocity.htmlsafe.HtmlSafe;
import com.atlassian.plugin.web.descriptors.WebFragmentModuleDescriptor;
import com.atlassian.plugin.web.model.WebLabel;
import java.util.Map;
import java.util.SortedMap;
import javax.servlet.http.HttpServletRequest;

public class ConfluenceWebLabel
implements WebLabel {
    final WebLabel decoratedLabel;

    public ConfluenceWebLabel(WebLabel webLabel) {
        this.decoratedLabel = webLabel;
    }

    public String getKey() {
        return this.decoratedLabel.getKey();
    }

    @HtmlSafe
    public String getDisplayableLabel(HttpServletRequest req, Map params) {
        return this.decoratedLabel.getDisplayableLabel(req, params);
    }

    @HtmlSafe
    public String getDisplayableLabel(HttpServletRequest req, WebInterfaceContext context) {
        return this.getDisplayableLabel(req, context.toMap());
    }

    public SortedMap getParams() {
        return this.decoratedLabel.getParams();
    }

    public Object get(String key) {
        return this.decoratedLabel.get(key);
    }

    public String getRenderedParam(String key, Map params) {
        return this.decoratedLabel.getRenderedParam(key, params);
    }

    public WebFragmentModuleDescriptor getDescriptor() {
        return this.decoratedLabel.getDescriptor();
    }

    public String getNoKeyValue() {
        return this.decoratedLabel.getNoKeyValue();
    }
}

