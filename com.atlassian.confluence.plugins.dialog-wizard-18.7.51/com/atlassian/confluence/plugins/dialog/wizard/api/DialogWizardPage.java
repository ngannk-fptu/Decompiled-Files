/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.annotation.XmlAttribute
 */
package com.atlassian.confluence.plugins.dialog.wizard.api;

import javax.xml.bind.annotation.XmlAttribute;

public class DialogWizardPage {
    @XmlAttribute
    private String id;
    @XmlAttribute
    private String templateKey;
    @XmlAttribute
    private String titleKey;
    @XmlAttribute
    private String descriptionHeaderLink;
    @XmlAttribute
    private String descriptionHeaderKey;
    @XmlAttribute
    private String descriptionContentKey;
    @XmlAttribute
    private Boolean last;

    private DialogWizardPage() {
    }

    public DialogWizardPage(String id, String templateKey, String titleKey, String descriptionHeaderLink, String descriptionHeaderKey, String descriptionContentKey, String last) {
        this.id = id;
        this.templateKey = templateKey;
        this.titleKey = titleKey;
        this.descriptionHeaderLink = descriptionHeaderLink;
        this.descriptionHeaderKey = descriptionHeaderKey;
        this.descriptionContentKey = descriptionContentKey;
        this.last = last == null ? null : Boolean.valueOf(last);
    }

    public String getId() {
        return this.id;
    }

    public String getTemplateKey() {
        return this.templateKey;
    }

    public String getTitleKey() {
        return this.titleKey;
    }

    public String getDescriptionHeaderLink() {
        return this.descriptionHeaderLink;
    }

    public String getDescriptionHeaderKey() {
        return this.descriptionHeaderKey;
    }

    public String getDescriptionContentKey() {
        return this.descriptionContentKey;
    }

    public Boolean getLast() {
        return this.last;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setTemplateKey(String templateKey) {
        this.templateKey = templateKey;
    }

    public void setTitleKey(String titleKey) {
        this.titleKey = titleKey;
    }

    public void setDescriptionHeaderLink(String descriptionHeaderLink) {
        this.descriptionHeaderLink = descriptionHeaderLink;
    }

    public void setDescriptionHeaderKey(String descriptionHeaderKey) {
        this.descriptionHeaderKey = descriptionHeaderKey;
    }

    public void setDescriptionContentKey(String descriptionContentKey) {
        this.descriptionContentKey = descriptionContentKey;
    }

    public void setLast(Boolean last) {
        this.last = last;
    }
}

