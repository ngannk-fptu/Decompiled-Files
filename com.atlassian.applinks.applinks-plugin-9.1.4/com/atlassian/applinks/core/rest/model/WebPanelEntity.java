/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.annotation.XmlRootElement
 */
package com.atlassian.applinks.core.rest.model;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="webPanel")
public class WebPanelEntity {
    private String html;

    public WebPanelEntity() {
    }

    public WebPanelEntity(String html) {
        this.html = html;
    }

    public String getHtml() {
        return this.html;
    }
}

