/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.spi.application.TypeId
 *  com.atlassian.plugin.web.WebInterfaceManager
 *  com.atlassian.plugin.web.descriptors.AbstractWebFragmentModuleDescriptor
 *  com.atlassian.plugin.web.descriptors.WebFragmentModuleDescriptor
 *  com.atlassian.plugin.web.model.DefaultWebLink
 *  com.atlassian.plugin.web.model.WebLink
 *  org.apache.commons.lang3.StringUtils
 *  org.dom4j.Element
 */
package com.atlassian.plugins.navlink.producer.contentlinks.plugin;

import com.atlassian.applinks.spi.application.TypeId;
import com.atlassian.plugin.web.WebInterfaceManager;
import com.atlassian.plugin.web.descriptors.AbstractWebFragmentModuleDescriptor;
import com.atlassian.plugin.web.descriptors.WebFragmentModuleDescriptor;
import com.atlassian.plugin.web.model.DefaultWebLink;
import com.atlassian.plugin.web.model.WebLink;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.Element;

public class ContentLinkModuleDescriptor
extends AbstractWebFragmentModuleDescriptor<Void> {
    private WebLink link;
    private Set<TypeId> entityTypes;

    public ContentLinkModuleDescriptor(WebInterfaceManager webInterfaceManager) {
        super(webInterfaceManager);
    }

    public Void getModule() {
        return null;
    }

    public void enabled() {
        super.enabled();
        if (this.element.element("link") != null) {
            this.link = new DefaultWebLink(this.element.element("link"), this.webInterfaceManager.getWebFragmentHelper(), this.contextProvider, (WebFragmentModuleDescriptor)this);
        }
        HashSet<TypeId> entities = new HashSet<TypeId>();
        for (Element contextElement : this.element.elements("entityType")) {
            String typeString = contextElement.getTextTrim();
            if (!StringUtils.isNotBlank((CharSequence)typeString)) continue;
            entities.add(new TypeId(typeString));
        }
        this.entityTypes = Collections.unmodifiableSet(entities);
    }

    public WebLink getLink() {
        return this.link;
    }

    public Set<TypeId> getEntityTypes() {
        return this.entityTypes;
    }
}

