/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.annotation.XmlElement
 *  javax.xml.bind.annotation.XmlRootElement
 */
package com.atlassian.confluence.plugins.gadgets.confluencepagegadget;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@Deprecated
@XmlRootElement
class PageGadgetBean {
    @XmlElement
    private String body;
    @XmlElement
    private String resources;
    @XmlElement
    private String html;
    @XmlElement
    private boolean userCanEditPage;
    @XmlElement
    private String contentType;
    @XmlElement
    private String pageTitle;

    PageGadgetBean() {
    }

    PageGadgetBean(String body, String resources, String html, boolean userCanEditPage, String contentType, String pageTitle) {
        this.body = body;
        this.resources = resources;
        this.html = html;
        this.userCanEditPage = userCanEditPage;
        this.contentType = contentType;
        this.pageTitle = pageTitle;
    }

    public String getBody() {
        return this.body;
    }

    public String getHtml() {
        return this.html;
    }

    public String getResources() {
        return this.resources;
    }

    public boolean isUserCanEditPage() {
        return this.userCanEditPage;
    }

    public String getContentType() {
        return this.contentType;
    }

    public String getPageTitle() {
        return this.pageTitle;
    }
}

