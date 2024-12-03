/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.languages.LocaleManager
 *  com.atlassian.confluence.plugins.dialog.wizard.api.DialogWizard
 *  com.atlassian.confluence.util.i18n.I18NBean
 *  com.atlassian.confluence.util.i18n.I18NBeanFactory
 *  com.atlassian.plugin.ModuleCompleteKey
 *  com.atlassian.plugin.PluginAccessor
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.sal.api.message.I18nResolver
 *  com.google.common.base.Preconditions
 *  com.google.common.collect.Lists
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.plugins.createcontent.impl;

import com.atlassian.confluence.languages.LocaleManager;
import com.atlassian.confluence.plugins.createcontent.ContentTemplateRefManager;
import com.atlassian.confluence.plugins.createcontent.PluginSpaceBlueprintAccessor;
import com.atlassian.confluence.plugins.createcontent.activeobjects.ContentTemplateRefAo;
import com.atlassian.confluence.plugins.createcontent.extensions.ContentTemplateModuleDescriptor;
import com.atlassian.confluence.plugins.createcontent.extensions.SpaceBlueprintModuleDescriptor;
import com.atlassian.confluence.plugins.createcontent.impl.ContentTemplateRef;
import com.atlassian.confluence.plugins.createcontent.impl.SpaceBlueprint;
import com.atlassian.confluence.plugins.dialog.wizard.api.DialogWizard;
import com.atlassian.confluence.util.i18n.I18NBean;
import com.atlassian.confluence.util.i18n.I18NBeanFactory;
import com.atlassian.plugin.ModuleCompleteKey;
import com.atlassian.plugin.PluginAccessor;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.sal.api.message.I18nResolver;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PluginSpaceBlueprintManager
implements PluginSpaceBlueprintAccessor {
    private final PluginAccessor pluginAccessor;
    private final ContentTemplateRefManager contentTemplateRefManager;
    private final I18nResolver i18nResolver;
    private final I18NBeanFactory i18NBeanFactory;
    private final LocaleManager localeManager;

    @Autowired
    public PluginSpaceBlueprintManager(@ComponentImport PluginAccessor pluginAccessor, ContentTemplateRefManager contentTemplateRefManager, @ComponentImport I18nResolver i18nResolver, @ComponentImport I18NBeanFactory i18NBeanFactory, @ComponentImport LocaleManager localeManager) {
        this.pluginAccessor = pluginAccessor;
        this.contentTemplateRefManager = contentTemplateRefManager;
        this.i18nResolver = i18nResolver;
        this.i18NBeanFactory = i18NBeanFactory;
        this.localeManager = localeManager;
    }

    @Override
    @Nonnull
    public SpaceBlueprint getByModuleCompleteKey(@Nonnull ModuleCompleteKey moduleCompleteKey) {
        SpaceBlueprintModuleDescriptor descriptor = this.getDescriptor(moduleCompleteKey);
        Preconditions.checkNotNull((Object)((Object)descriptor), (Object)("Space Blueprint module descriptor not found [key='" + moduleCompleteKey + "']"));
        return this.getSpaceBlueprint(descriptor);
    }

    @Override
    @Nullable
    public DialogWizard getDialogByModuleCompleteKey(@Nonnull ModuleCompleteKey moduleCompleteKey) {
        SpaceBlueprintModuleDescriptor desc = this.getDescriptor(moduleCompleteKey);
        if (desc != null) {
            return desc.getDialogWizard();
        }
        return null;
    }

    @Override
    @Nonnull
    public List<SpaceBlueprint> getAll() {
        List descriptors = this.pluginAccessor.getEnabledModuleDescriptorsByClass(SpaceBlueprintModuleDescriptor.class);
        ArrayList blueprints = Lists.newArrayList();
        for (SpaceBlueprintModuleDescriptor descriptor : descriptors) {
            blueprints.add(this.getSpaceBlueprint(descriptor));
        }
        return blueprints;
    }

    @Nullable
    private SpaceBlueprintModuleDescriptor getDescriptor(@Nonnull ModuleCompleteKey moduleCompleteKey) {
        String key = moduleCompleteKey.getCompleteKey();
        return (SpaceBlueprintModuleDescriptor)this.pluginAccessor.getEnabledPluginModule(key);
    }

    private SpaceBlueprint getSpaceBlueprint(SpaceBlueprintModuleDescriptor moduleDescriptor) {
        UUID homePageId = null;
        SpaceBlueprintModuleDescriptor.ContentTemplateRefNode homePageRefNode = moduleDescriptor.getContentTemplateRefNode();
        if (homePageRefNode != null) {
            homePageId = this.storeContentTemplateRef(homePageRefNode);
        }
        DialogWizard dialogWizard = moduleDescriptor.getDialogWizard();
        Preconditions.checkNotNull((Object)dialogWizard, (Object)("Space Blueprint dialog-wizard not defined [key='" + moduleDescriptor.getCompleteKey() + "']"));
        String i18nNameKey = moduleDescriptor.getI18nNameKey();
        Preconditions.checkNotNull((Object)i18nNameKey, (Object)("Space Blueprint i18n-name-key not defined [key='" + moduleDescriptor.getCompleteKey() + "']"));
        List<ModuleCompleteKey> promotedBlueprintKeys = moduleDescriptor.getPromotedBlueprintKeys();
        SpaceBlueprint spaceBlueprint = new SpaceBlueprint(null, moduleDescriptor.getCompleteKey(), this.i18nResolver.getText(i18nNameKey), true, promotedBlueprintKeys, dialogWizard, moduleDescriptor.getCategory());
        spaceBlueprint.setHomePageId(homePageId);
        return spaceBlueprint;
    }

    @Nonnull
    private UUID storeContentTemplateRef(@Nonnull SpaceBlueprintModuleDescriptor.ContentTemplateRefNode refNode) {
        ContentTemplateRefAo ao = this.storeContentTemplateRefAo(refNode);
        return UUID.fromString(ao.getUuid());
    }

    @Nonnull
    private ContentTemplateRefAo storeContentTemplateRefAo(@Nonnull SpaceBlueprintModuleDescriptor.ContentTemplateRefNode refNode) {
        String moduleCompleteKey = refNode.ref.getCompleteKey();
        ContentTemplateModuleDescriptor contentTemplateDescriptor = (ContentTemplateModuleDescriptor)this.pluginAccessor.getEnabledPluginModule(moduleCompleteKey);
        String i18nNameKey = contentTemplateDescriptor.getI18nNameKey();
        I18NBean i18nBean = this.i18NBeanFactory.getI18NBean(this.localeManager.getSiteDefaultLocale());
        String name = i18nBean.getText(i18nNameKey);
        ContentTemplateRef contentTemplateRef = new ContentTemplateRef(null, 0L, moduleCompleteKey, name, true, null);
        ContentTemplateRefAo contentTemplateRefAo = (ContentTemplateRefAo)this.contentTemplateRefManager.createAo(contentTemplateRef);
        if (refNode.children != null) {
            for (SpaceBlueprintModuleDescriptor.ContentTemplateRefNode child : refNode.children) {
                ContentTemplateRefAo childAo = this.storeContentTemplateRefAo(child);
                childAo.setParent(contentTemplateRefAo);
                childAo.save();
            }
        }
        return contentTemplateRefAo;
    }
}

