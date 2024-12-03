/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.web.ContextProvider
 *  com.atlassian.plugin.web.WebFragmentHelper
 *  com.atlassian.plugin.web.descriptors.WebFragmentModuleDescriptor
 *  com.atlassian.plugin.web.model.WebIcon
 *  com.atlassian.plugin.web.model.WebLink
 *  org.dom4j.Element
 */
package com.atlassian.plugin.web.model;

import com.atlassian.plugin.web.ContextProvider;
import com.atlassian.plugin.web.WebFragmentHelper;
import com.atlassian.plugin.web.descriptors.WebFragmentModuleDescriptor;
import com.atlassian.plugin.web.model.DefaultWebLink;
import com.atlassian.plugin.web.model.WebIcon;
import com.atlassian.plugin.web.model.WebLink;
import org.dom4j.Element;

public class DefaultWebIcon
implements WebIcon {
    private WebLink url;
    private int width;
    private int height;

    public DefaultWebIcon(Element iconEl, WebFragmentHelper webFragmentHelper, ContextProvider contextProvider, WebFragmentModuleDescriptor descriptor) {
        this.url = new DefaultWebLink(iconEl.element("link"), webFragmentHelper, contextProvider, descriptor);
        this.width = Integer.parseInt(iconEl.attributeValue("width"));
        this.height = Integer.parseInt(iconEl.attributeValue("height"));
    }

    public WebLink getUrl() {
        return this.url;
    }

    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return this.height;
    }
}

