/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.annotation.XmlElement
 *  javax.xml.bind.annotation.XmlRootElement
 */
package com.atlassian.applinks.core.rest.model;

import com.atlassian.applinks.core.rest.model.WebPanelEntity;
import java.util.List;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="webPanels")
public class WebPanelEntityList {
    @XmlElement(name="webPanel")
    private List<WebPanelEntity> panels;

    public WebPanelEntityList() {
    }

    public WebPanelEntityList(List<WebPanelEntity> panels) {
        this.panels = panels;
    }

    public List<WebPanelEntity> getWebPanels() {
        return this.panels;
    }
}

