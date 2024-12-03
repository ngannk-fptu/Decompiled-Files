/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.event.events.template.TemplateUpdateEvent
 *  com.atlassian.confluence.pages.templates.PageTemplate
 *  com.atlassian.confluence.pages.templates.PluginTemplateReference
 *  com.atlassian.confluence.plugins.templates.actions.AbstractEditPageTemplateAction
 *  com.atlassian.confluence.security.Permission
 *  com.atlassian.confluence.security.PermissionManager
 *  com.atlassian.confluence.spaces.Space
 *  com.atlassian.event.Event
 *  com.atlassian.plugin.ModuleCompleteKey
 *  javax.annotation.Nullable
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.plugins.createcontent.actions;

import com.atlassian.confluence.event.events.template.TemplateUpdateEvent;
import com.atlassian.confluence.pages.templates.PageTemplate;
import com.atlassian.confluence.pages.templates.PluginTemplateReference;
import com.atlassian.confluence.plugins.createcontent.ContentTemplateRefManager;
import com.atlassian.confluence.plugins.createcontent.api.events.BlueprintTemplateUpdateEvent;
import com.atlassian.confluence.plugins.createcontent.impl.ContentBlueprint;
import com.atlassian.confluence.plugins.createcontent.impl.ContentTemplateRef;
import com.atlassian.confluence.plugins.createcontent.services.BlueprintResolver;
import com.atlassian.confluence.plugins.createcontent.template.PluginPageTemplateHelper;
import com.atlassian.confluence.plugins.templates.actions.AbstractEditPageTemplateAction;
import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.event.Event;
import com.atlassian.plugin.ModuleCompleteKey;
import java.util.UUID;
import javax.annotation.Nullable;
import org.apache.commons.lang3.StringUtils;

public class EditTemplateAction
extends AbstractEditPageTemplateAction {
    private PluginPageTemplateHelper pluginPageTemplateHelper;
    private ContentTemplateRef templateRef;
    private ContentTemplateRefManager contentTemplateRefManager;
    private BlueprintResolver blueprintResolver;
    private String pluginKey;
    private String moduleKey;
    @Nullable
    private String referencingPluginKey;
    @Nullable
    private String referencingModuleKey;
    private int pageTemplateVersion;

    public boolean isPermitted() {
        Space space = this.getSpace();
        Object target = space != null ? space : PermissionManager.TARGET_APPLICATION;
        return this.permissionManager.hasPermission(this.getRemoteUser(), Permission.ADMINISTER, target);
    }

    public String doDefault() throws Exception {
        this.pageTemplate = this.getPageTemplate();
        if (this.pageTemplate == null) {
            return "notfound";
        }
        PageTemplate usedTemplate = this.pageTemplate;
        if (this.templateRef != null) {
            ContentTemplateRef usedTemplateRef = this.blueprintResolver.resolveTemplateRef(this.templateRef);
            usedTemplate = this.pluginPageTemplateHelper.getPageTemplate(usedTemplateRef);
        }
        this.title = usedTemplate.getName();
        this.description = usedTemplate.getDescription();
        this.wysiwygContent = this.formatConverter.convertToEditorFormat(usedTemplate.getContent(), this.getRenderContext());
        this.pageTemplateVersion = this.pageTemplate.getVersion();
        return super.doDefault();
    }

    public String execute() throws Exception {
        PageTemplate originalVersion;
        if (StringUtils.isNotBlank((CharSequence)this.back)) {
            return "input";
        }
        if (StringUtils.isNotBlank((CharSequence)this.preview)) {
            return "preview";
        }
        this.pageTemplate = this.getPageTemplate();
        if (this.pageTemplate == null) {
            return "notfound";
        }
        if (this.pageTemplate.getVersion() != this.pageTemplateVersion) {
            this.addActionError(this.getText("create.content.plugin.template.updated.since.edit"));
            return "error";
        }
        Space space = this.getSpace();
        if (space != null) {
            if (this.pageTemplate.getSpace() == null && !this.pageTemplate.isNew()) {
                this.pageTemplate = new PageTemplate(this.pageTemplate);
            }
            space.addPageTemplate(this.pageTemplate);
        }
        if (this.pageTemplate.isNew()) {
            this.pageTemplateManager.savePageTemplate(this.pageTemplate, null);
        }
        try {
            originalVersion = (PageTemplate)this.pageTemplate.clone();
        }
        catch (CloneNotSupportedException e) {
            throw new AssertionError((Object)"Should not happen");
        }
        this.pageTemplate.setName(this.title);
        this.pageTemplate.setDescription(this.description);
        this.pageTemplate.setContent(this.formatConverter.convertToStorageFormat(this.wysiwygContent, this.getRenderContext()));
        if (this.templateRef != null) {
            ModuleCompleteKey templateModuleKey = null;
            String templateModuleKeyStr = this.templateRef.getModuleCompleteKey();
            if (StringUtils.isNotBlank((CharSequence)templateModuleKeyStr)) {
                templateModuleKey = new ModuleCompleteKey(templateModuleKeyStr);
            }
            this.pageTemplate.setModuleCompleteKey(templateModuleKey);
            ContentBlueprint blueprint = this.templateRef.getParent();
            ModuleCompleteKey blueprintModuleKey = new ModuleCompleteKey(blueprint.getModuleCompleteKey());
            this.pageTemplate.setReferencingModuleCompleteKey(blueprintModuleKey);
            this.pageTemplateManager.savePageTemplate(this.pageTemplate, originalVersion);
            String pluginKey = templateModuleKey.getPluginKey();
            String moduleKey = templateModuleKey.getModuleKey();
            BlueprintTemplateUpdateEvent templateUpdatedEvent = new BlueprintTemplateUpdateEvent((Object)this, pluginKey, moduleKey, space);
            this.eventManager.publishEvent((Event)templateUpdatedEvent);
        }
        TemplateUpdateEvent templateEvent = new TemplateUpdateEvent((Object)this, null, this.pageTemplate);
        this.eventManager.publishEvent((Event)templateEvent);
        return super.execute();
    }

    public PageTemplate getPageTemplate() {
        if (this.entityId > 0L) {
            return this.pageTemplateManager.getPageTemplate(this.entityId);
        }
        if (StringUtils.isNotBlank((CharSequence)this.pluginKey) && StringUtils.isNotBlank((CharSequence)this.moduleKey)) {
            ModuleCompleteKey referencingModuleCompleteKey = null;
            if (StringUtils.isNotBlank((CharSequence)this.referencingPluginKey) && StringUtils.isNotBlank((CharSequence)this.referencingModuleKey)) {
                referencingModuleCompleteKey = new ModuleCompleteKey(this.referencingPluginKey, this.referencingModuleKey);
            }
            PluginTemplateReference pluginTemplateReference = PluginTemplateReference.spaceTemplateReference((ModuleCompleteKey)new ModuleCompleteKey(this.pluginKey, this.moduleKey), referencingModuleCompleteKey, (Space)this.getSpace());
            return this.pluginPageTemplateHelper.getPageTemplate(pluginTemplateReference);
        }
        if (this.templateRef == null) {
            throw new IllegalStateException("Action should be passed enough data to locate a PageTemplate.");
        }
        return this.pluginPageTemplateHelper.getPageTemplate(this.templateRef);
    }

    public String getPluginKey() {
        return this.pluginKey;
    }

    public void setPluginKey(String pluginKey) {
        this.pluginKey = pluginKey;
    }

    public String getModuleKey() {
        return this.moduleKey;
    }

    public void setModuleKey(String moduleKey) {
        this.moduleKey = moduleKey;
    }

    @Nullable
    public String getReferencingPluginKey() {
        return this.referencingPluginKey;
    }

    public void setReferencingPluginKey(@Nullable String referencingPluginKey) {
        this.referencingPluginKey = referencingPluginKey;
    }

    @Nullable
    public String getReferencingModuleKey() {
        return this.referencingModuleKey;
    }

    public void setReferencingModuleKey(@Nullable String referencingModuleKey) {
        this.referencingModuleKey = referencingModuleKey;
    }

    public int getPageTemplateVersion() {
        return this.pageTemplateVersion;
    }

    public void setPageTemplateVersion(int pageTemplateVersion) {
        this.pageTemplateVersion = pageTemplateVersion;
    }

    public void setSpaceKey(String key) {
        this.setKey(key);
    }

    public String getContentTemplateRefId() {
        return this.templateRef != null ? this.templateRef.getId().toString() : null;
    }

    public void setContentTemplateRefId(String templateRefId) {
        if (StringUtils.isNotBlank((CharSequence)templateRefId)) {
            this.templateRef = (ContentTemplateRef)this.contentTemplateRefManager.getById(UUID.fromString(templateRefId));
        }
    }

    public void setPluginPageTemplateHelper(PluginPageTemplateHelper pluginPageTemplateHelper) {
        this.pluginPageTemplateHelper = pluginPageTemplateHelper;
    }

    public void setContentTemplateRefManager(ContentTemplateRefManager contentTemplateRefManager) {
        this.contentTemplateRefManager = contentTemplateRefManager;
    }

    public void setBlueprintResolver(BlueprintResolver blueprintResolver) {
        this.blueprintResolver = blueprintResolver;
    }
}

