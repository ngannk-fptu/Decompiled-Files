/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.plugins.dialog.wizard.api.DialogWizardEntity
 *  javax.xml.bind.annotation.XmlElement
 *  javax.xml.bind.annotation.XmlRootElement
 */
package com.atlassian.confluence.plugins.createcontent.rest.entities;

import com.atlassian.confluence.plugins.dialog.wizard.api.DialogWizardEntity;
import java.util.UUID;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class CreateDialogWebItemEntity {
    @XmlElement
    private String name;
    @XmlElement
    private String description;
    @XmlElement
    private String styleClass;
    @XmlElement
    private String iconURL;
    @XmlElement
    private String itemModuleCompleteKey;
    @XmlElement
    private String blueprintModuleCompleteKey;
    @XmlElement
    private UUID contentBlueprintId;
    @XmlElement
    private String templateId;
    @XmlElement
    private String createResult;
    @XmlElement
    private String howToUseTemplate;
    @XmlElement
    private String directLink;
    @XmlElement
    private boolean skipHowToUse;
    @XmlElement
    private DialogWizardEntity wizard;
    @XmlElement
    private boolean isNew;
    @XmlElement
    private boolean isPromoted;

    private CreateDialogWebItemEntity() {
    }

    public CreateDialogWebItemEntity(String name, String description, String styleClass, String iconURL, String templateId) {
        this.name = name;
        this.description = description;
        this.styleClass = styleClass;
        this.iconURL = iconURL;
        this.templateId = templateId;
    }

    public CreateDialogWebItemEntity(String name, String description, String styleClass, String iconURL, String itemModuleCompleteKey, UUID contentBlueprintId, String createResult, String howToUseTemplate, boolean skipHowToUse, DialogWizardEntity wizard) {
        this(name, description, styleClass, iconURL, itemModuleCompleteKey, contentBlueprintId, createResult, howToUseTemplate, null, skipHowToUse, wizard);
    }

    public CreateDialogWebItemEntity(String name, String description, String styleClass, String iconURL, String itemModuleCompleteKey, UUID contentBlueprintId, String createResult, String howToUseTemplate, String directLink, boolean skipHowToUse, DialogWizardEntity wizard) {
        this.name = name;
        this.description = description;
        this.styleClass = styleClass;
        this.iconURL = iconURL;
        this.itemModuleCompleteKey = itemModuleCompleteKey;
        this.contentBlueprintId = contentBlueprintId;
        this.createResult = createResult;
        this.howToUseTemplate = howToUseTemplate;
        this.directLink = directLink;
        this.wizard = wizard;
        this.skipHowToUse = skipHowToUse;
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

    public String getIconURL() {
        return this.iconURL;
    }

    public void setIconURL(String iconURL) {
        this.iconURL = iconURL;
    }

    public String getItemModuleCompleteKey() {
        return this.itemModuleCompleteKey;
    }

    public void setItemModuleCompleteKey(String itemModuleCompleteKey) {
        this.itemModuleCompleteKey = itemModuleCompleteKey;
    }

    public String getBlueprintModuleCompleteKey() {
        return this.blueprintModuleCompleteKey;
    }

    public void setBlueprintModuleCompleteKey(String blueprintModuleCompleteKey) {
        this.blueprintModuleCompleteKey = blueprintModuleCompleteKey;
    }

    public UUID getContentBlueprintId() {
        return this.contentBlueprintId;
    }

    public void setContentBlueprintId(UUID contentBlueprintId) {
        this.contentBlueprintId = contentBlueprintId;
    }

    public String getTemplateId() {
        return this.templateId;
    }

    public void setTemplateId(String templateId) {
        this.templateId = templateId;
    }

    public String getCreateResult() {
        return this.createResult;
    }

    public void setCreateResult(String createResult) {
        this.createResult = createResult;
    }

    public String getStyleClass() {
        return this.styleClass;
    }

    public void setStyleClass(String styleClass) {
        this.styleClass = styleClass;
    }

    public String getHowToUseTemplate() {
        return this.howToUseTemplate;
    }

    public void setHowToUseTemplate(String howToUseTemplate) {
        this.howToUseTemplate = howToUseTemplate;
    }

    public String getDirectLink() {
        return this.directLink;
    }

    public void setDirectLink(String directLink) {
        this.directLink = directLink;
    }

    public boolean isSkipHowToUse() {
        return this.skipHowToUse;
    }

    public void setSkipHowToUse(boolean skipHowToUse) {
        this.skipHowToUse = skipHowToUse;
    }

    public DialogWizardEntity getWizard() {
        return this.wizard;
    }

    public void setWizard(DialogWizardEntity wizard) {
        this.wizard = wizard;
    }

    public boolean isNew() {
        return this.isNew;
    }

    public void setNew(boolean aNew) {
        this.isNew = aNew;
    }

    public boolean isPromoted() {
        return this.isPromoted;
    }

    public void setPromoted(boolean promoted) {
        this.isPromoted = promoted;
    }

    public String toString() {
        return this.name + ", " + this.itemModuleCompleteKey;
    }
}

