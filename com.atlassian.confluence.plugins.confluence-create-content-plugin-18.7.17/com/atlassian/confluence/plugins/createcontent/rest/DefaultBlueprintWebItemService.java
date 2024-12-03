/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.pages.BlogPost
 *  com.atlassian.confluence.pages.Page
 *  com.atlassian.confluence.plugin.descriptor.web.DefaultWebInterfaceContext
 *  com.atlassian.confluence.plugin.descriptor.web.WebInterfaceContext
 *  com.atlassian.confluence.plugins.dialog.wizard.api.DialogManager
 *  com.atlassian.confluence.plugins.dialog.wizard.api.DialogWizard
 *  com.atlassian.confluence.plugins.dialog.wizard.api.DialogWizardEntity
 *  com.atlassian.confluence.security.PermissionManager
 *  com.atlassian.confluence.spaces.Space
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.confluence.util.i18n.DocumentationBean
 *  com.atlassian.confluence.util.i18n.I18NBean
 *  com.atlassian.plugin.ModuleCompleteKey
 *  com.atlassian.plugin.ModuleDescriptor
 *  com.atlassian.plugin.PluginAccessor
 *  com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.plugin.web.WebInterfaceManager
 *  com.atlassian.plugin.web.descriptors.WebItemModuleDescriptor
 *  com.atlassian.user.User
 *  com.google.common.collect.Lists
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.beans.factory.annotation.Qualifier
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.plugins.createcontent.rest;

import com.atlassian.confluence.pages.BlogPost;
import com.atlassian.confluence.pages.Page;
import com.atlassian.confluence.plugin.descriptor.web.DefaultWebInterfaceContext;
import com.atlassian.confluence.plugin.descriptor.web.WebInterfaceContext;
import com.atlassian.confluence.plugins.createcontent.BlueprintConstants;
import com.atlassian.confluence.plugins.createcontent.BlueprintStateController;
import com.atlassian.confluence.plugins.createcontent.ContentBlueprintManager;
import com.atlassian.confluence.plugins.createcontent.SpaceBlueprintManager;
import com.atlassian.confluence.plugins.createcontent.SpaceBlueprintStateController;
import com.atlassian.confluence.plugins.createcontent.api.contextproviders.BlueprintContextKeys;
import com.atlassian.confluence.plugins.createcontent.extensions.BlueprintModuleDescriptor;
import com.atlassian.confluence.plugins.createcontent.extensions.UserBlueprintConfigManager;
import com.atlassian.confluence.plugins.createcontent.impl.ContentBlueprint;
import com.atlassian.confluence.plugins.createcontent.impl.PluginBackedBlueprint;
import com.atlassian.confluence.plugins.createcontent.impl.SpaceBlueprint;
import com.atlassian.confluence.plugins.createcontent.model.BlueprintState;
import com.atlassian.confluence.plugins.createcontent.rest.BlueprintWebItemService;
import com.atlassian.confluence.plugins.createcontent.rest.IconUrlProvider;
import com.atlassian.confluence.plugins.createcontent.rest.PageTemplateWebItemService;
import com.atlassian.confluence.plugins.createcontent.rest.entities.CreateDialogWebItemEntity;
import com.atlassian.confluence.plugins.createcontent.services.BlueprintDiscoveryService;
import com.atlassian.confluence.plugins.createcontent.services.BlueprintSorter;
import com.atlassian.confluence.plugins.dialog.wizard.api.DialogManager;
import com.atlassian.confluence.plugins.dialog.wizard.api.DialogWizard;
import com.atlassian.confluence.plugins.dialog.wizard.api.DialogWizardEntity;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.util.i18n.DocumentationBean;
import com.atlassian.confluence.util.i18n.I18NBean;
import com.atlassian.plugin.ModuleCompleteKey;
import com.atlassian.plugin.ModuleDescriptor;
import com.atlassian.plugin.PluginAccessor;
import com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.plugin.web.WebInterfaceManager;
import com.atlassian.plugin.web.descriptors.WebItemModuleDescriptor;
import com.atlassian.user.User;
import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
@ExportAsService(value={BlueprintWebItemService.class})
public class DefaultBlueprintWebItemService
implements BlueprintWebItemService {
    private static final Logger log = LoggerFactory.getLogger(DefaultBlueprintWebItemService.class);
    private final BlueprintStateController contentBlueprintStateController;
    private final SpaceBlueprintStateController spaceBlueprintStateController;
    private final WebInterfaceManager webInterfaceManager;
    private final PermissionManager permissionManager;
    private final UserBlueprintConfigManager userBlueprintConfigManager;
    private final ContentBlueprintManager contentBlueprintManager;
    private final IconUrlProvider iconUrlProvider;
    private final PluginAccessor pluginAccessor;
    private final PageTemplateWebItemService pageTemplateWebItemService;
    private final SpaceBlueprintManager spaceBlueprintManager;
    private final BlueprintDiscoveryService blueprintDiscoverer;
    private final BlueprintSorter blueprintSorter;
    private final DialogManager dialogManager;

    @Autowired
    public DefaultBlueprintWebItemService(BlueprintStateController contentBlueprintStateController, SpaceBlueprintStateController spaceBlueprintStateController, @ComponentImport WebInterfaceManager webInterfaceManager, @ComponentImport PermissionManager permissionManager, UserBlueprintConfigManager userBlueprintConfigManager, ContentBlueprintManager contentBlueprintManager, IconUrlProvider iconUrlProvider, @ComponentImport PluginAccessor pluginAccessor, PageTemplateWebItemService pageTemplateWebItemService, @Qualifier(value="spaceBlueprintManager") SpaceBlueprintManager spaceBlueprintManager, BlueprintDiscoveryService blueprintDiscoverer, BlueprintSorter blueprintSorter, @ComponentImport DialogManager dialogManager) {
        this.contentBlueprintStateController = contentBlueprintStateController;
        this.spaceBlueprintStateController = spaceBlueprintStateController;
        this.webInterfaceManager = webInterfaceManager;
        this.permissionManager = permissionManager;
        this.userBlueprintConfigManager = userBlueprintConfigManager;
        this.contentBlueprintManager = contentBlueprintManager;
        this.iconUrlProvider = iconUrlProvider;
        this.pluginAccessor = pluginAccessor;
        this.pageTemplateWebItemService = pageTemplateWebItemService;
        this.spaceBlueprintManager = spaceBlueprintManager;
        this.blueprintDiscoverer = blueprintDiscoverer;
        this.blueprintSorter = blueprintSorter;
        this.dialogManager = dialogManager;
    }

    @Override
    public List<CreateDialogWebItemEntity> getCreateContentWebItems(Space space, I18NBean i18NBean, DocumentationBean documentationBean, ConfluenceUser user) {
        List<CreateDialogWebItemEntity> pluginItems = this.getPluginItems(i18NBean, space, user);
        pluginItems.addAll(this.pageTemplateWebItemService.getPageTemplateItems(space, user));
        return this.filterAndSortItems(space, user, pluginItems);
    }

    private List<CreateDialogWebItemEntity> filterAndSortItems(@Nonnull Space space, @Nullable ConfluenceUser user, @Nonnull List<CreateDialogWebItemEntity> pluginItems) {
        boolean canCreatePages = this.canCreatePages(space, user);
        boolean canCreateBlogPosts = this.canCreateBlogPosts(space, user);
        if (!canCreatePages && !canCreateBlogPosts) {
            return Lists.newArrayList();
        }
        CreateDialogWebItemEntity blankPageItem = pluginItems.get(0);
        CreateDialogWebItemEntity blogPostItem = pluginItems.get(1);
        blankPageItem.setContentBlueprintId(BlueprintConstants.BLANK_PAGE_BLUEPRINT.getId());
        blogPostItem.setContentBlueprintId(BlueprintConstants.BLOG_POST_BLUEPRINT.getId());
        if (!canCreatePages) {
            return Lists.newArrayList((Object[])new CreateDialogWebItemEntity[]{blogPostItem});
        }
        if (!canCreateBlogPosts) {
            pluginItems.remove(blogPostItem);
        }
        return this.blueprintSorter.sortContentBlueprintItems(pluginItems, space, user);
    }

    @Override
    public List<CreateDialogWebItemEntity> getCreateSpaceWebItems(I18NBean i18NBean, DocumentationBean documentationBean, ConfluenceUser user) {
        List<CreateDialogWebItemEntity> pluginItems = this.getPluginItems(i18NBean, null, user);
        return this.blueprintSorter.sortSpaceBlueprintItems(pluginItems, user);
    }

    @Override
    @Deprecated
    public BlueprintModuleDescriptor getBlueprintDescriptorForWebItem(ModuleDescriptor webItemDescriptor) {
        String blueprintKey = this.getBlueprintModuleKey(webItemDescriptor);
        if (StringUtils.isBlank((CharSequence)blueprintKey)) {
            return null;
        }
        ModuleCompleteKey blueprintCompleteModuleKey = new ModuleCompleteKey(webItemDescriptor.getPluginKey(), blueprintKey);
        ModuleDescriptor blueprintModuleDescriptor = this.pluginAccessor.getEnabledPluginModule(blueprintCompleteModuleKey.getCompleteKey());
        if (!(blueprintModuleDescriptor instanceof BlueprintModuleDescriptor)) {
            log.debug("{} is not a <blueprint>.", (Object)blueprintCompleteModuleKey);
            return null;
        }
        return (BlueprintModuleDescriptor)blueprintModuleDescriptor;
    }

    @Override
    public List<CreateDialogWebItemEntity> getCreatePersonalSpaceWebItems(I18NBean i18NBean, DocumentationBean documentationBean, ConfluenceUser remoteUser) {
        WebItemModuleDescriptor webItem = (WebItemModuleDescriptor)this.pluginAccessor.getPluginModule("com.atlassian.confluence.plugins.confluence-create-content-plugin:create-personal-space-item");
        SpaceBlueprint pluginBackedBlueprint = this.getSpaceBlueprintForWebItem((ModuleDescriptor)webItem);
        String name = DefaultBlueprintWebItemService.getDisplayableWebItemName(i18NBean, webItem);
        ArrayList webItemEntities = Lists.newArrayList();
        webItemEntities.add(this.createWebItemEntity(i18NBean, webItem, name, Collections.emptySet(), false, pluginBackedBlueprint));
        return webItemEntities;
    }

    private boolean canCreateBlogPosts(Space space, ConfluenceUser remoteUser) {
        return this.permissionManager.hasCreatePermission((User)remoteUser, (Object)space, BlogPost.class);
    }

    private boolean canCreatePages(Space space, ConfluenceUser remoteUser) {
        return this.permissionManager.hasCreatePermission((User)remoteUser, (Object)space, Page.class);
    }

    @Override
    public List<WebItemModuleDescriptor> getCreateDialogWebItemModuleDescriptors(WebInterfaceContext context) {
        return this.webInterfaceManager.getDisplayableItems("system.create.dialog/content", context.toMap());
    }

    private List<CreateDialogWebItemEntity> getPluginItems(I18NBean i18NBean, Space space, ConfluenceUser user) {
        boolean isPageBp = space != null;
        String section = isPageBp ? "system.create.dialog/content" : "system.create.space.dialog/content";
        Map<UUID, BlueprintState> blueprintStateMap = isPageBp ? this.contentBlueprintStateController.getAllContentBlueprintState(section, user, space) : this.spaceBlueprintStateController.getAllSpaceBlueprintState(section, user);
        Set<UUID> skipHowToUseKeys = user != null ? this.userBlueprintConfigManager.getSkipHowToUseKeys(user) : Collections.emptySet();
        DefaultWebInterfaceContext webInterfaceContext = new DefaultWebInterfaceContext();
        webInterfaceContext.setSpace(space);
        webInterfaceContext.setCurrentUser(user);
        List displayableItems = this.webInterfaceManager.getDisplayableItems(section, webInterfaceContext.toMap());
        ArrayList webItems = Lists.newArrayList();
        for (WebItemModuleDescriptor webItem : displayableItems) {
            BlueprintState blueprintState;
            PluginBackedBlueprint blueprint;
            DialogWizard dialogWizard;
            String name = DefaultBlueprintWebItemService.getDisplayableWebItemName(i18NBean, webItem);
            if (StringUtils.isBlank((CharSequence)name)) {
                log.warn(webItem + " not added as it is missing a displayable name.");
                continue;
            }
            String dialogWizardKey = (String)webItem.getParams().get("dialogWizardKey");
            if (StringUtils.isNotBlank((CharSequence)dialogWizardKey) && (dialogWizard = this.dialogManager.getDialogWizardByKey(dialogWizardKey)) != null) {
                webItems.add(this.createWebItemEntity(name, i18NBean, webItem, dialogWizard));
                continue;
            }
            PluginBackedBlueprint pluginBackedBlueprint = blueprint = isPageBp ? this.getContentBlueprintForWebItem((ModuleDescriptor)webItem, space) : this.getSpaceBlueprintForWebItem((ModuleDescriptor)webItem);
            if (blueprint != null && (blueprintState = blueprintStateMap.get(blueprint.getId())) != null && !BlueprintState.FULLY_ENABLED.equals(blueprintState)) continue;
            webItems.add(this.createWebItemEntity(i18NBean, webItem, name, skipHowToUseKeys, isPageBp, blueprint));
        }
        if (AuthenticatedUserThreadLocal.get() != null) {
            return this.blueprintDiscoverer.discoverRecentlyInstalled(webItems);
        }
        return webItems;
    }

    private CreateDialogWebItemEntity createWebItemEntity(String name, I18NBean i18NBean, WebItemModuleDescriptor webItem, DialogWizard dialogWizard) {
        String description = i18NBean.getText(webItem.getDescriptionKey());
        String iconURL = this.iconUrlProvider.getIconURL(webItem);
        DialogWizardEntity dialogWizardEntity = new DialogWizardEntity(i18NBean, dialogWizard);
        return new CreateDialogWebItemEntity(name, description, webItem.getStyleClass(), iconURL, webItem.getCompleteKey(), null, null, null, false, dialogWizardEntity);
    }

    private CreateDialogWebItemEntity createWebItemEntity(I18NBean i18NBean, WebItemModuleDescriptor webItem, String entityName, Set<UUID> skipHowToUseMap, boolean contentBp, PluginBackedBlueprint pluginBackedBlueprint) {
        String description = i18NBean.getText(webItem.getDescriptionKey());
        String iconURL = this.iconUrlProvider.getIconURL(webItem);
        UUID contentBlueprintUuid = null;
        String createResult = null;
        String howToUseTemplate = null;
        boolean skipHowToUse = false;
        DialogWizardEntity dialogWizardEntity = null;
        String moduleCompleteKey = null;
        String directLink = (String)webItem.getParams().get("directLink");
        DialogWizard dialogWizard = null;
        if (contentBp) {
            ContentBlueprint contentBlueprint = (ContentBlueprint)pluginBackedBlueprint;
            if (contentBlueprint != null) {
                contentBlueprintUuid = contentBlueprint.getId();
                createResult = contentBlueprint.getCreateResult();
                howToUseTemplate = contentBlueprint.getHowToUseTemplate();
                skipHowToUse = skipHowToUseMap.contains(contentBlueprint.getId());
                dialogWizard = contentBlueprint.getDialogWizard();
                moduleCompleteKey = contentBlueprint.getModuleCompleteKey();
            }
        } else {
            SpaceBlueprint spaceBlueprint = (SpaceBlueprint)pluginBackedBlueprint;
            if (spaceBlueprint != null) {
                contentBlueprintUuid = spaceBlueprint.getId();
                createResult = "space";
                skipHowToUse = skipHowToUseMap.contains(spaceBlueprint.getId());
                dialogWizard = spaceBlueprint.getDialogWizard();
                moduleCompleteKey = spaceBlueprint.getModuleCompleteKey();
            }
        }
        if (dialogWizard != null) {
            dialogWizardEntity = new DialogWizardEntity(i18NBean, dialogWizard);
        }
        CreateDialogWebItemEntity entity = new CreateDialogWebItemEntity(entityName, description, webItem.getStyleClass(), iconURL, webItem.getCompleteKey(), contentBlueprintUuid, createResult, howToUseTemplate, directLink, skipHowToUse, dialogWizardEntity);
        entity.setBlueprintModuleCompleteKey(moduleCompleteKey);
        return entity;
    }

    private static String getDisplayableWebItemName(I18NBean i18NBean, WebItemModuleDescriptor webItem) {
        if (StringUtils.isNotBlank((CharSequence)webItem.getI18nNameKey())) {
            return i18NBean.getText(webItem.getI18nNameKey());
        }
        if (StringUtils.isNotBlank((CharSequence)webItem.getName())) {
            return webItem.getName();
        }
        return null;
    }

    @Nullable
    private ContentBlueprint getContentBlueprintForWebItem(ModuleDescriptor webItemModuleDescriptor, Space space) {
        String blueprintKey = this.getBlueprintModuleKey(webItemModuleDescriptor);
        if (StringUtils.isBlank((CharSequence)blueprintKey)) {
            return null;
        }
        ModuleCompleteKey completeKey = new ModuleCompleteKey(webItemModuleDescriptor.getPluginKey(), blueprintKey);
        return this.contentBlueprintManager.getPluginBackedContentBlueprint(completeKey, space.getKey());
    }

    @Nullable
    private SpaceBlueprint getSpaceBlueprintForWebItem(ModuleDescriptor webItemModuleDescriptor) {
        String blueprintKey = this.getBlueprintModuleKey(webItemModuleDescriptor);
        if (StringUtils.isBlank((CharSequence)blueprintKey)) {
            return null;
        }
        ModuleCompleteKey completeKey = new ModuleCompleteKey(webItemModuleDescriptor.getPluginKey(), blueprintKey);
        return (SpaceBlueprint)this.spaceBlueprintManager.getCloneByModuleCompleteKey(completeKey);
    }

    private String getBlueprintModuleKey(ModuleDescriptor webItemModuleDescriptor) {
        Map moduleDescriptorParams = webItemModuleDescriptor.getParams();
        String blueprintKey = (String)moduleDescriptorParams.get(BlueprintContextKeys.BLUEPRINT_MODULE_KEY.key());
        if (StringUtils.isBlank((CharSequence)blueprintKey)) {
            log.debug("No param with key '{}' specified in module descriptor with key '{}'", (Object)BlueprintContextKeys.BLUEPRINT_MODULE_KEY.key(), (Object)webItemModuleDescriptor.getCompleteKey());
        }
        return blueprintKey;
    }
}

