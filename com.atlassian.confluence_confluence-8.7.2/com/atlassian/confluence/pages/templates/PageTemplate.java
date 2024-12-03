/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.content.template.ContentTemplateId
 *  com.atlassian.confluence.impl.hibernate.Hibernate
 *  com.atlassian.plugin.ModuleCompleteKey
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.pages.templates;

import com.atlassian.confluence.api.model.content.template.ContentTemplateId;
import com.atlassian.confluence.core.AbstractLabelableEntityObject;
import com.atlassian.confluence.core.BodyType;
import com.atlassian.confluence.impl.hibernate.Hibernate;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.plugin.ModuleCompleteKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PageTemplate
extends AbstractLabelableEntityObject {
    public static final String LABEL_SEPARATOR = " ";
    private String name;
    private String description;
    private String referencingPluginKey;
    private String referencingModuleKey;
    private String pluginKey;
    private String moduleKey;
    private String content = "";
    private Space space;
    private BodyType bodyType;
    private static final Logger log = LoggerFactory.getLogger(PageTemplate.class);

    public PageTemplate() {
    }

    public PageTemplate(PageTemplate pageTemplate) {
        this.name = pageTemplate.name;
        this.description = pageTemplate.description;
        this.content = pageTemplate.content;
        this.space = pageTemplate.space;
        this.bodyType = pageTemplate.bodyType;
        this.pluginKey = pageTemplate.pluginKey;
        this.moduleKey = pageTemplate.moduleKey;
        this.referencingPluginKey = pageTemplate.referencingPluginKey;
        this.referencingModuleKey = pageTemplate.referencingModuleKey;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getContent() {
        return this.content;
    }

    public void setContent(String content) {
        if (content == null) {
            content = "";
        }
        this.content = content;
    }

    public Space getSpace() {
        return this.space;
    }

    public void setSpace(Space space) {
        this.space = space;
    }

    public void setModuleCompleteKey(ModuleCompleteKey moduleCompleteKey) {
        if (moduleCompleteKey != null) {
            this.pluginKey = moduleCompleteKey.getPluginKey();
            this.moduleKey = moduleCompleteKey.getModuleKey();
        }
    }

    public void setReferencingModuleCompleteKey(ModuleCompleteKey referencingModuleCompleteKey) {
        if (referencingModuleCompleteKey != null) {
            this.referencingPluginKey = referencingModuleCompleteKey.getPluginKey();
            this.referencingModuleKey = referencingModuleCompleteKey.getModuleKey();
        }
    }

    public ModuleCompleteKey getModuleCompleteKey() {
        if (this.pluginKey == null || this.moduleKey == null) {
            return null;
        }
        return new ModuleCompleteKey(this.pluginKey, this.moduleKey);
    }

    public ModuleCompleteKey getReferencingModuleCompleteKey() {
        if (this.referencingPluginKey == null || this.referencingModuleKey == null) {
            return null;
        }
        return new ModuleCompleteKey(this.referencingPluginKey, this.referencingModuleKey);
    }

    public String getModuleKey() {
        return this.moduleKey;
    }

    public String getPluginKey() {
        return this.pluginKey;
    }

    private void setModuleKey(String moduleKey) {
        this.moduleKey = moduleKey;
    }

    private void setPluginKey(String pluginKey) {
        this.pluginKey = pluginKey;
    }

    public PageTemplate getOriginalVersionPageTemplate() {
        return this.isLatestVersion() ? null : this.getLatestVersion();
    }

    @Override
    public PageTemplate getLatestVersion() {
        return (PageTemplate)super.getLatestVersion();
    }

    public void setOriginalVersionPageTemplate(PageTemplate originalVersionPageTemplate) {
        super.setOriginalVersion(originalVersionPageTemplate);
    }

    public boolean isGlobalPageTemplate() {
        return this.getSpace() == null;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || Hibernate.getClass((Object)this) != Hibernate.getClass((Object)o)) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        PageTemplate that = (PageTemplate)o;
        return !(this.getContent() != null ? !this.getContent().equals(that.getContent()) : that.getContent() != null);
    }

    public int hashCode() {
        int result = super.hashCode();
        result = 29 * result + (this.getContent() != null ? this.getContent().hashCode() : 0);
        return result;
    }

    @Override
    public void convertToHistoricalVersion() {
        super.convertToHistoricalVersion();
        this.setSpace(null);
    }

    @Override
    public String getTitle() {
        return this.getName();
    }

    public BodyType getBodyType() {
        return this.bodyType != null ? this.bodyType : BodyType.WIKI;
    }

    public void setBodyType(BodyType bodyType) {
        this.bodyType = bodyType;
    }

    public String getReferencingPluginKey() {
        return this.referencingPluginKey;
    }

    public void setReferencingPluginKey(String referencingPluginKey) {
        this.referencingPluginKey = referencingPluginKey;
    }

    public String getReferencingModuleKey() {
        return this.referencingModuleKey;
    }

    public void setReferencingModuleKey(String referencingModuleKey) {
        this.referencingModuleKey = referencingModuleKey;
    }

    public ContentTemplateId getContentTemplateId() {
        return ContentTemplateId.fromLong((long)this.getId());
    }
}

