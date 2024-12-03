/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.Plugin
 *  com.atlassian.plugin.PluginParseException
 *  com.atlassian.plugin.web.WebInterfaceManager
 *  com.atlassian.plugin.web.descriptors.WebItemModuleDescriptor
 *  com.atlassian.plugin.web.model.WebIcon
 *  com.atlassian.plugin.web.model.WebLink
 *  org.apache.commons.lang3.StringUtils
 *  org.dom4j.Element
 */
package com.atlassian.plugin.web.descriptors;

import com.atlassian.plugin.Plugin;
import com.atlassian.plugin.PluginParseException;
import com.atlassian.plugin.web.WebInterfaceManager;
import com.atlassian.plugin.web.descriptors.AbstractWebFragmentModuleDescriptor;
import com.atlassian.plugin.web.descriptors.WebItemModuleDescriptor;
import com.atlassian.plugin.web.model.DefaultWebIcon;
import com.atlassian.plugin.web.model.DefaultWebLink;
import com.atlassian.plugin.web.model.WebIcon;
import com.atlassian.plugin.web.model.WebLink;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.Element;

public class DefaultWebItemModuleDescriptor
extends AbstractWebFragmentModuleDescriptor<Void>
implements WebItemModuleDescriptor {
    private String section;
    private WebIcon icon;
    private DefaultWebLink link;
    private String styleClass;
    private String entryPoint;

    public DefaultWebItemModuleDescriptor(WebInterfaceManager webInterfaceManager) {
        super(webInterfaceManager);
    }

    public DefaultWebItemModuleDescriptor() {
    }

    @Override
    public void init(Plugin plugin, Element element) throws PluginParseException {
        super.init(plugin, element);
        this.section = element.attributeValue("section");
        this.styleClass = element.element("styleClass") != null ? element.element("styleClass").getTextTrim() : "";
        this.entryPoint = element.element("entry-point") != null ? element.element("entry-point").getTextTrim() : "";
    }

    public String getSection() {
        return this.section;
    }

    public WebLink getLink() {
        return this.link;
    }

    public WebIcon getIcon() {
        return this.icon;
    }

    public String getStyleClass() {
        return this.styleClass;
    }

    public String getEntryPoint() {
        if (StringUtils.isNotBlank((CharSequence)this.entryPoint)) {
            if (this.entryPoint.matches("^.+:.+$")) {
                return this.entryPoint;
            }
            return this.plugin.getKey() + ":" + this.entryPoint;
        }
        return null;
    }

    @Override
    public void enabled() {
        super.enabled();
        if (this.element.element("icon") != null) {
            this.icon = new DefaultWebIcon(this.element.element("icon"), this.webInterfaceManager.getWebFragmentHelper(), this.contextProvider, this);
        }
        if (this.element.element("link") != null) {
            this.link = new DefaultWebLink(this.element.element("link"), this.webInterfaceManager.getWebFragmentHelper(), this.contextProvider, this);
        }
    }

    public Void getModule() {
        return null;
    }
}

