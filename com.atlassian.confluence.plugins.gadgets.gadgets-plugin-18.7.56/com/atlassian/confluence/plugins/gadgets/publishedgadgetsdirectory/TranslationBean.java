/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.annotation.XmlElement
 *  javax.xml.bind.annotation.XmlRootElement
 */
package com.atlassian.confluence.plugins.gadgets.publishedgadgetsdirectory;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
class TranslationBean {
    @XmlElement
    private String header;
    @XmlElement
    private String helpTitle1;
    @XmlElement
    private String helpBody1;
    @XmlElement
    private String helpTitle2;
    @XmlElement
    private String helpBody2;
    @XmlElement
    private String moreInfo;
    @XmlElement
    private String gadgetUrl;
    @XmlElement
    private String noAuthor;
    @XmlElement
    private String noDescription;
    @XmlElement
    private String closeButton;

    TranslationBean() {
    }

    void setHeader(String header) {
        this.header = header;
    }

    void setHelpTitle1(String helpTitle1) {
        this.helpTitle1 = helpTitle1;
    }

    void setHelpBody1(String helpBody1) {
        this.helpBody1 = helpBody1;
    }

    public void setHelpTitle2(String helpTitle2) {
        this.helpTitle2 = helpTitle2;
    }

    public void setHelpBody2(String helpBody2) {
        this.helpBody2 = helpBody2;
    }

    public void setMoreInfo(String moreInfo) {
        this.moreInfo = moreInfo;
    }

    void setGadgetUrl(String gadgetUrl) {
        this.gadgetUrl = gadgetUrl;
    }

    void setNoAuthor(String noAuthor) {
        this.noAuthor = noAuthor;
    }

    void setNoDescription(String noDescription) {
        this.noDescription = noDescription;
    }

    void setCloseButton(String closeButton) {
        this.closeButton = closeButton;
    }
}

