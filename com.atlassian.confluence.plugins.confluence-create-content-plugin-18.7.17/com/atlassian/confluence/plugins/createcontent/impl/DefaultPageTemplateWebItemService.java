/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.pages.templates.PageTemplate
 *  com.atlassian.confluence.pages.templates.PageTemplateManager
 *  com.atlassian.confluence.security.Permission
 *  com.atlassian.confluence.security.PermissionManager
 *  com.atlassian.confluence.spaces.Space
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.plugin.ModuleCompleteKey
 *  com.atlassian.plugin.PluginAccessor
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.user.User
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Sets
 *  org.apache.commons.lang3.StringUtils
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.plugins.createcontent.impl;

import com.atlassian.confluence.pages.templates.PageTemplate;
import com.atlassian.confluence.pages.templates.PageTemplateManager;
import com.atlassian.confluence.plugins.createcontent.extensions.BlueprintModuleDescriptor;
import com.atlassian.confluence.plugins.createcontent.rest.IconUrlProvider;
import com.atlassian.confluence.plugins.createcontent.rest.PageTemplateWebItemService;
import com.atlassian.confluence.plugins.createcontent.rest.entities.CreateDialogWebItemEntity;
import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.plugin.ModuleCompleteKey;
import com.atlassian.plugin.PluginAccessor;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.user.User;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DefaultPageTemplateWebItemService
implements PageTemplateWebItemService {
    private final PermissionManager permissionManager;
    private final PageTemplateManager pageTemplateManager;
    private final PluginAccessor pluginAccessor;
    private final IconUrlProvider iconUrlProvider;

    @Autowired
    public DefaultPageTemplateWebItemService(@ComponentImport PermissionManager permissionManager, @ComponentImport PageTemplateManager pageTemplateManager, @ComponentImport PluginAccessor pluginAccessor, IconUrlProvider iconUrlProvider) {
        this.permissionManager = permissionManager;
        this.pageTemplateManager = pageTemplateManager;
        this.pluginAccessor = pluginAccessor;
        this.iconUrlProvider = iconUrlProvider;
    }

    @Override
    public List<CreateDialogWebItemEntity> getPageTemplateItems(Space space, ConfluenceUser user) {
        ArrayList entities = Lists.newArrayList();
        LinkedList pageTemplates = Lists.newLinkedList();
        if (space != null && this.permissionManager.hasPermission((User)user, Permission.VIEW, (Object)space)) {
            pageTemplates.addAll(space.getPageTemplates());
        }
        if (this.permissionManager.hasPermission((User)user, Permission.VIEW, PermissionManager.TARGET_APPLICATION)) {
            pageTemplates.addAll(this.pageTemplateManager.getGlobalPageTemplates());
        }
        Set<String> blueprintTemplateKeys = this.getBlueprintTemplateKeys();
        for (PageTemplate pageTemplate : pageTemplates) {
            if (this.shouldHideTemplate(blueprintTemplateKeys, pageTemplate)) continue;
            String description = (String)StringUtils.defaultIfBlank((CharSequence)pageTemplate.getDescription(), (CharSequence)"");
            String iconURL = this.iconUrlProvider.getDefaultIconUrl();
            String templateId = String.valueOf(pageTemplate.getId());
            entities.add(new CreateDialogWebItemEntity(pageTemplate.getName(), description, "icon-content-template-large", iconURL, templateId));
        }
        return entities;
    }

    private boolean shouldHideTemplate(Set<String> blueprintTemplateKeys, PageTemplate pageTemplate) {
        boolean isBlueprintTemplateKey;
        if (pageTemplate.getReferencingModuleCompleteKey() != null) {
            return true;
        }
        ModuleCompleteKey templateKey = pageTemplate.getModuleCompleteKey();
        boolean bl = isBlueprintTemplateKey = templateKey != null && blueprintTemplateKeys.contains(templateKey.getCompleteKey());
        if (isBlueprintTemplateKey) {
            return true;
        }
        String pluginKey = pageTemplate.getPluginKey();
        return pluginKey != null && this.pluginAccessor.isSystemPlugin(pluginKey);
    }

    private Set<String> getBlueprintTemplateKeys() {
        List moduleDescriptors = this.pluginAccessor.getEnabledModuleDescriptorsByClass(BlueprintModuleDescriptor.class);
        HashSet blueprintTemplateKeys = Sets.newHashSet();
        for (BlueprintModuleDescriptor moduleDescriptor : moduleDescriptors) {
            for (ModuleCompleteKey templateKey : moduleDescriptor.getContentTemplates()) {
                blueprintTemplateKeys.add(templateKey.getCompleteKey());
            }
            if (moduleDescriptor.getIndexTemplate() == null) continue;
            blueprintTemplateKeys.add(moduleDescriptor.getIndexTemplate().getCompleteKey());
        }
        return blueprintTemplateKeys;
    }
}

