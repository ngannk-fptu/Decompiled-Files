/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.web.descriptors.WebItemModuleDescriptor
 *  javax.servlet.http.HttpServletRequest
 *  javax.xml.bind.annotation.XmlElement
 *  javax.xml.bind.annotation.XmlRootElement
 */
package com.atlassian.confluence.plugins.ia.model;

import com.atlassian.plugin.web.descriptors.WebItemModuleDescriptor;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class SettingsTabBean {
    @XmlElement
    private final String id;
    @XmlElement
    private final String displayableUrl;
    @XmlElement
    private final String displayableLabel;

    public SettingsTabBean(WebItemModuleDescriptor item, HttpServletRequest req, Map<String, Object> map) {
        this.id = item.getLink().getId();
        this.displayableUrl = item.getLink().getDisplayableUrl(req, map);
        this.displayableLabel = item.getWebLabel().getDisplayableLabel(req, map);
    }

    public String getId() {
        return this.id;
    }

    public String getDisplayableUrl() {
        return this.displayableUrl;
    }

    public String getDisplayableLabel() {
        return this.displayableLabel;
    }
}

