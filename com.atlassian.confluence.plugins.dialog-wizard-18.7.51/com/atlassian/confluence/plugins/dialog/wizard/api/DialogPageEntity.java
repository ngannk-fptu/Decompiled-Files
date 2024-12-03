/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.util.i18n.I18NBean
 *  javax.xml.bind.annotation.XmlAttribute
 */
package com.atlassian.confluence.plugins.dialog.wizard.api;

import com.atlassian.confluence.plugins.dialog.wizard.api.DialogWizardPage;
import com.atlassian.confluence.util.i18n.I18NBean;
import javax.xml.bind.annotation.XmlAttribute;

public class DialogPageEntity {
    @XmlAttribute
    private String id;
    @XmlAttribute(name="templateKey")
    private String templateKey;
    @XmlAttribute(name="title")
    private String title;
    @XmlAttribute(name="descriptionHeaderLink")
    private String descriptionHeaderLink;
    @XmlAttribute(name="descriptionHeader")
    private String descriptionHeader;
    @XmlAttribute(name="descriptionContent")
    private String descriptionContent;
    @XmlAttribute
    private Boolean last;

    public DialogPageEntity() {
    }

    public DialogPageEntity(I18NBean i18NBean, DialogWizardPage page) {
        this.id = page.getId();
        this.templateKey = page.getTemplateKey();
        this.title = i18NBean.getText(page.getTitleKey());
        this.descriptionHeaderLink = page.getDescriptionHeaderLink();
        this.descriptionHeader = i18NBean.getText(page.getDescriptionHeaderKey());
        this.descriptionContent = i18NBean.getText(page.getDescriptionContentKey());
        this.last = page.getLast();
    }

    String getDescriptionContent() {
        return this.descriptionContent;
    }

    String getDescriptionHeader() {
        return this.descriptionHeader;
    }

    String getDescriptionHeaderLink() {
        return this.descriptionHeaderLink;
    }

    String getId() {
        return this.id;
    }

    Boolean getLast() {
        return this.last;
    }

    String getTemplateKey() {
        return this.templateKey;
    }

    String getTitle() {
        return this.title;
    }
}

