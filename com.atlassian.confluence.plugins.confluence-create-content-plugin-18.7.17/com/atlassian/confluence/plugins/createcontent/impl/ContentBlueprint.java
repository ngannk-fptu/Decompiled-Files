/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.content.template.ContentBlueprintId
 *  com.atlassian.confluence.plugins.dialog.wizard.api.DialogWizard
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 *  javax.xml.bind.annotation.XmlElement
 *  javax.xml.bind.annotation.XmlRootElement
 *  javax.xml.bind.annotation.XmlTransient
 */
package com.atlassian.confluence.plugins.createcontent.impl;

import com.atlassian.confluence.api.model.content.template.ContentBlueprintId;
import com.atlassian.confluence.plugins.createcontent.impl.ContentTemplateRef;
import com.atlassian.confluence.plugins.createcontent.impl.PluginBackedBlueprint;
import com.atlassian.confluence.plugins.dialog.wizard.api.DialogWizard;
import java.util.List;
import java.util.UUID;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

@XmlRootElement
public class ContentBlueprint
extends PluginBackedBlueprint {
    private ContentTemplateRef indexPageTemplateRef;
    private String indexKey;
    private String createResult;
    private String howToUseTemplate;
    private String indexTitleI18nKey;
    private DialogWizard dialogWizard;
    private List<ContentTemplateRef> contentTemplates;
    private String spaceKey;
    private boolean indexDisabled;

    public ContentBlueprint() {
    }

    public ContentBlueprint(UUID id) {
        this.setId(id);
    }

    @Nonnull
    @XmlTransient
    public ContentTemplateRef getFirstContentTemplateRef() {
        return this.contentTemplates.get(0);
    }

    @Nonnull
    @XmlElement
    public List<ContentTemplateRef> getContentTemplateRefs() {
        return this.contentTemplates;
    }

    @Nonnull
    @XmlElement
    public ContentTemplateRef getIndexPageTemplateRef() {
        return this.indexPageTemplateRef;
    }

    @Nonnull
    @XmlElement
    public String getIndexKey() {
        return this.indexKey;
    }

    @Nullable
    @XmlElement
    public String getCreateResult() {
        return this.createResult;
    }

    @Nullable
    @XmlElement
    public String getIndexTitleI18nKey() {
        return this.indexTitleI18nKey;
    }

    @Nullable
    @XmlElement
    public String getHowToUseTemplate() {
        return this.howToUseTemplate;
    }

    @Nullable
    @XmlElement
    public DialogWizard getDialogWizard() {
        return this.dialogWizard;
    }

    @XmlElement
    public boolean isIndexDisabled() {
        return this.indexDisabled;
    }

    public void setContentTemplateRefs(@Nonnull List<ContentTemplateRef> contentTemplates) {
        this.contentTemplates = contentTemplates;
    }

    public void setCreateResult(@Nullable String createResult) {
        this.createResult = createResult;
    }

    public void setDialogWizard(@Nullable DialogWizard dialogWizard) {
        this.dialogWizard = dialogWizard;
    }

    public void setHowToUseTemplate(@Nullable String howToUseTemplate) {
        this.howToUseTemplate = howToUseTemplate;
    }

    public void setIndexKey(@Nonnull String indexKey) {
        this.indexKey = indexKey;
    }

    public void setIndexPageTemplateRef(@Nonnull ContentTemplateRef indexPageTemplateRef) {
        this.indexPageTemplateRef = indexPageTemplateRef;
    }

    public void setIndexTitleI18nKey(@Nonnull String indexTitleI18nKey) {
        this.indexTitleI18nKey = indexTitleI18nKey;
    }

    public void setIndexDisabled(@Nonnull boolean indexDisabled) {
        this.indexDisabled = indexDisabled;
    }

    @Nullable
    public String getSpaceKey() {
        return this.spaceKey;
    }

    public void setSpaceKey(@Nullable String spaceKey) {
        this.spaceKey = spaceKey != null && spaceKey.isEmpty() ? null : spaceKey;
    }

    @XmlTransient
    public ContentBlueprintId getContentBlueprintId() {
        return ContentBlueprintId.fromKeyAndSpaceString((String)this.getModuleCompleteKey(), (String)this.spaceKey);
    }

    public String toString() {
        return "KEY:" + this.getModuleCompleteKey() + "/SPACE:" + this.getSpaceKey() + "/CLONE:" + this.isPluginClone() + "/ID:" + this.getId();
    }
}

