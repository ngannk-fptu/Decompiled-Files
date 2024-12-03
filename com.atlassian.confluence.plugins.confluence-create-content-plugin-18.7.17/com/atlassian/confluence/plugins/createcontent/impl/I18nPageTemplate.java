/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.core.Versioned
 *  com.atlassian.confluence.pages.templates.PageTemplate
 */
package com.atlassian.confluence.plugins.createcontent.impl;

import com.atlassian.confluence.core.Versioned;
import com.atlassian.confluence.pages.templates.PageTemplate;

public class I18nPageTemplate
extends PageTemplate {
    private String i18nNameKey;

    public I18nPageTemplate(String i18nNameKey, PageTemplate module) {
        super(module);
        this.i18nNameKey = i18nNameKey;
        this.setLabellings(module.getLabellings());
        this.setVersion(module.getVersion());
        PageTemplate latestVersion = module.getLatestVersion();
        this.setOriginalVersion((Versioned)(latestVersion == module ? null : latestVersion));
        this.setCreator(module.getCreator());
        this.setLastModifier(module.getLastModifier());
        this.setId(module.getId());
        this.setCreationDate(module.getCreationDate());
        this.setLastModificationDate(module.getLastModificationDate());
    }

    public String getI18nNameKey() {
        return this.i18nNameKey;
    }

    public void setI18nNameKey(String i18nNameKey) {
        this.i18nNameKey = i18nNameKey;
    }
}

