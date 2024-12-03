/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.core.util.map.EasyMap
 *  com.atlassian.plugin.web.descriptors.WebFragmentModuleDescriptor
 *  com.atlassian.plugin.web.model.WebLink
 *  javax.servlet.http.HttpServletRequest
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.plugin.descriptor.web.model;

import com.atlassian.confluence.plugin.descriptor.web.WebInterfaceContext;
import com.atlassian.confluence.plugin.descriptor.web.model.SettableWebLink;
import com.atlassian.confluence.themes.GlobalHelper;
import com.atlassian.core.util.map.EasyMap;
import com.atlassian.plugin.web.descriptors.WebFragmentModuleDescriptor;
import com.atlassian.plugin.web.model.WebLink;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConfluenceWebLink
implements SettableWebLink {
    private static final Logger log = LoggerFactory.getLogger(ConfluenceWebLink.class);
    WebLink webLink;

    public ConfluenceWebLink() {
    }

    public ConfluenceWebLink(WebLink webLink) {
        this.webLink = webLink;
    }

    public String getRenderedUrl(WebInterfaceContext context) {
        return this.webLink.getRenderedUrl(context.toMap());
    }

    public String getRenderedUrl(Map params) {
        return this.webLink.getRenderedUrl(params);
    }

    public String getDisplayableUrl(HttpServletRequest req, WebInterfaceContext context) {
        Map<String, Object> params;
        if (context == null) {
            log.error("No context defined", (Throwable)new NullPointerException());
            params = new HashMap<String, Object>();
        } else {
            params = context.toMap();
        }
        return this.getDisplayableUrl(req, params);
    }

    public String getDisplayableUrl(HttpServletRequest req, Map params) {
        try {
            if (this.webLink == null) {
                log.error("No weblink defined", (Throwable)new NullPointerException());
                return "";
            }
            return this.webLink.getDisplayableUrl(req, params);
        }
        catch (Exception e) {
            log.error("Error getting displayable url from weblink {}", (Object)(this.webLink != null ? this.webLink.getId() : "null"), (Object)e);
            return "";
        }
    }

    public WebFragmentModuleDescriptor getDescriptor() {
        return this.webLink.getDescriptor();
    }

    public boolean hasAccessKey() {
        return this.webLink.hasAccessKey();
    }

    public String getAccessKey(GlobalHelper helper) {
        return this.webLink.getAccessKey(EasyMap.build((Object)"helper", (Object)helper));
    }

    public String getAccessKey(Map params) {
        return this.webLink.getAccessKey(params);
    }

    public String getId() {
        return this.webLink.getId();
    }

    @Override
    public void setLink(WebLink webLink) {
        this.webLink = webLink;
    }
}

