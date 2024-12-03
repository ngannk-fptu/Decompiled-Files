/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.util.i18n.I18NBean
 *  com.atlassian.plugin.web.descriptors.WebItemModuleDescriptor
 *  com.atlassian.plugin.web.model.WebIcon
 *  com.atlassian.plugin.web.model.WebLabel
 *  javax.xml.bind.annotation.XmlElement
 *  javax.xml.bind.annotation.XmlRootElement
 */
package com.atlassian.confluence.plugins.highlight.rest;

import com.atlassian.confluence.util.i18n.I18NBean;
import com.atlassian.plugin.web.descriptors.WebItemModuleDescriptor;
import com.atlassian.plugin.web.model.WebIcon;
import com.atlassian.plugin.web.model.WebLabel;
import java.util.HashMap;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class WebItemModuleDescriptorEntity {
    @XmlElement
    String iconUrl;
    @XmlElement
    String label;
    @XmlElement
    String tooltip;
    @XmlElement
    String styleClass;
    @XmlElement
    String key;

    public WebItemModuleDescriptorEntity() {
    }

    public WebItemModuleDescriptorEntity(WebItemModuleDescriptor webItemModuleDescriptor, I18NBean i18nBean) {
        WebIcon icon = webItemModuleDescriptor.getIcon();
        this.iconUrl = icon != null ? icon.getUrl().getRenderedUrl(new HashMap()) : null;
        WebLabel webLabel = webItemModuleDescriptor.getWebLabel();
        this.label = webLabel != null ? i18nBean.getText(webLabel.getKey()) : null;
        WebLabel tooltip = webItemModuleDescriptor.getTooltip();
        this.tooltip = tooltip != null ? i18nBean.getText(tooltip.getKey()) : null;
        this.styleClass = webItemModuleDescriptor.getStyleClass();
        this.key = webItemModuleDescriptor.getCompleteKey();
    }

    public String getIconUrl() {
        return this.iconUrl;
    }

    public String getLabel() {
        return this.label;
    }

    public String getTooltip() {
        return this.tooltip;
    }

    public String getKey() {
        return this.key;
    }

    public String getStyleClass() {
        return this.styleClass;
    }
}

