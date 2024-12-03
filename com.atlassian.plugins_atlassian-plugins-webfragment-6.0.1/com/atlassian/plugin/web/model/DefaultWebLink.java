/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.web.ContextProvider
 *  com.atlassian.plugin.web.WebFragmentHelper
 *  com.atlassian.plugin.web.descriptors.WebFragmentModuleDescriptor
 *  com.atlassian.plugin.web.model.WebLink
 *  javax.servlet.http.HttpServletRequest
 *  org.dom4j.Element
 */
package com.atlassian.plugin.web.model;

import com.atlassian.plugin.web.ContextProvider;
import com.atlassian.plugin.web.WebFragmentHelper;
import com.atlassian.plugin.web.descriptors.WebFragmentModuleDescriptor;
import com.atlassian.plugin.web.model.AbstractWebItem;
import com.atlassian.plugin.web.model.WebLink;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.dom4j.Element;

public class DefaultWebLink
extends AbstractWebItem
implements WebLink {
    private final String url;
    private final boolean absoluteUrl;
    private final String accessKey;
    private final String id;

    public DefaultWebLink(Element linkEl, WebFragmentHelper webFragmentHelper, ContextProvider contextProvider, WebFragmentModuleDescriptor descriptor) {
        super(webFragmentHelper, contextProvider, descriptor);
        this.url = linkEl.getTextTrim();
        this.accessKey = linkEl.attributeValue("accessKey");
        this.id = linkEl.attributeValue("linkId");
        this.absoluteUrl = "true".equals(linkEl.attributeValue("absolute"));
    }

    public String getRenderedUrl(Map<String, Object> context) {
        HashMap<String, Object> tmpContext = new HashMap<String, Object>(context);
        tmpContext.putAll(this.getContextMap(tmpContext));
        return this.getWebFragmentHelper().renderVelocityFragment(this.url, tmpContext);
    }

    private boolean isRelativeUrl(String url) {
        return !this.absoluteUrl && !url.startsWith("http://") && !url.startsWith("https://");
    }

    public String getDisplayableUrl(HttpServletRequest req, Map<String, Object> context) {
        String renderedUrl = this.getRenderedUrl(context);
        if (this.isRelativeUrl(renderedUrl)) {
            return req.getContextPath() + renderedUrl;
        }
        return renderedUrl;
    }

    public boolean hasAccessKey() {
        return this.accessKey != null && !"".equals(this.accessKey);
    }

    public String getAccessKey(Map<String, Object> context) {
        context.putAll(this.getContextMap(context));
        return this.getWebFragmentHelper().renderVelocityFragment(this.accessKey, context);
    }

    public String getId() {
        return this.id;
    }
}

