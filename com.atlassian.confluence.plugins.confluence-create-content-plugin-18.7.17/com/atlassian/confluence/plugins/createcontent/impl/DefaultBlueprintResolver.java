/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.ModuleCompleteKey
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.plugin.web.WebInterfaceManager
 *  com.atlassian.plugin.web.descriptors.WebItemModuleDescriptor
 *  com.google.common.collect.Lists
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.plugins.createcontent.impl;

import com.atlassian.confluence.plugins.createcontent.BlueprintConstants;
import com.atlassian.confluence.plugins.createcontent.ContentBlueprintManager;
import com.atlassian.confluence.plugins.createcontent.impl.ContentBlueprint;
import com.atlassian.confluence.plugins.createcontent.impl.ContentTemplateRef;
import com.atlassian.confluence.plugins.createcontent.model.BlueprintIdBundle;
import com.atlassian.confluence.plugins.createcontent.services.BlueprintResolver;
import com.atlassian.plugin.ModuleCompleteKey;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.plugin.web.WebInterfaceManager;
import com.atlassian.plugin.web.descriptors.WebItemModuleDescriptor;
import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DefaultBlueprintResolver
implements BlueprintResolver {
    private static final Logger log = LoggerFactory.getLogger(DefaultBlueprintResolver.class);
    private final WebInterfaceManager webInterfaceManager;
    private final ContentBlueprintManager contentBlueprintManager;

    @Autowired
    public DefaultBlueprintResolver(@ComponentImport WebInterfaceManager webInterfaceManager, ContentBlueprintManager contentBlueprintManager) {
        this.webInterfaceManager = webInterfaceManager;
        this.contentBlueprintManager = contentBlueprintManager;
    }

    @Override
    public WebItemModuleDescriptor getWebItemMatchingBlueprint(UUID blueprintId) {
        String moduleCompleteKey = ((ContentBlueprint)this.contentBlueprintManager.getById(blueprintId)).getModuleCompleteKey();
        return this.getWebItemMatchingBlueprint(moduleCompleteKey);
    }

    @Override
    public WebItemModuleDescriptor getWebItemMatchingBlueprint(String blueprintModuleKey) {
        List items = this.webInterfaceManager.getItems("system.create.dialog/content");
        for (WebItemModuleDescriptor webItem : items) {
            String pluginKey;
            String blueprintKey;
            String moduleKey = (String)webItem.getParams().get(BlueprintConstants.BLUEPRINT_PARAM_KEY);
            if (StringUtils.isBlank((CharSequence)moduleKey) || !blueprintModuleKey.equals(blueprintKey = this.getModuleCompleteKeyFromRelative(pluginKey = webItem.getPluginKey(), moduleKey).getCompleteKey())) continue;
            return webItem;
        }
        return null;
    }

    @Override
    public ContentTemplateRef resolveTemplateRef(ContentTemplateRef templateRef) {
        ContentBlueprint globalBlueprint;
        if (templateRef.getTemplateId() > 0L) {
            return templateRef;
        }
        ContentBlueprint parentBlueprint = templateRef.getParent();
        if (StringUtils.isNotBlank((CharSequence)parentBlueprint.getSpaceKey())) {
            ModuleCompleteKey moduleCompleteKey = new ModuleCompleteKey(parentBlueprint.getModuleCompleteKey());
            globalBlueprint = this.contentBlueprintManager.getPluginBackedContentBlueprint(moduleCompleteKey, null);
        } else {
            globalBlueprint = parentBlueprint;
        }
        ArrayList contentTemplateRefs = Lists.newArrayList();
        if (globalBlueprint.getContentTemplateRefs() != null) {
            contentTemplateRefs.addAll(globalBlueprint.getContentTemplateRefs());
        }
        if (globalBlueprint.getIndexPageTemplateRef() != null) {
            contentTemplateRefs.add(globalBlueprint.getIndexPageTemplateRef());
        }
        return this.findTemplateWithKey(templateRef.getModuleCompleteKey(), contentTemplateRefs);
    }

    @Override
    @Nonnull
    public ContentBlueprint resolveContentBlueprint(@Nonnull String blueprintId, @Nullable String spaceKey) throws IllegalArgumentException {
        ContentBlueprint blueprint = (ContentBlueprint)this.contentBlueprintManager.getById(UUID.fromString(blueprintId));
        return this.getContentBlueprint(blueprintId, blueprint.getModuleCompleteKey(), spaceKey);
    }

    @Override
    @Nonnull
    public ContentBlueprint getContentBlueprint(String contentBlueprintId, String blueprintModuleCompleteKey, String spaceKey) throws IllegalArgumentException {
        UUID blueprintUUID = null;
        if (StringUtils.isNotBlank((CharSequence)contentBlueprintId)) {
            blueprintUUID = UUID.fromString(contentBlueprintId);
        }
        ModuleCompleteKey moduleCompleteKey = null;
        if (StringUtils.isNotBlank((CharSequence)blueprintModuleCompleteKey)) {
            moduleCompleteKey = new ModuleCompleteKey(blueprintModuleCompleteKey);
        }
        if (blueprintUUID == null && moduleCompleteKey == null) {
            throw new IllegalArgumentException("Not enough data to get Blueprint: " + contentBlueprintId + ", " + blueprintModuleCompleteKey);
        }
        BlueprintIdBundle idBundle = new BlueprintIdBundle(blueprintUUID, moduleCompleteKey, spaceKey);
        ContentBlueprint blueprint = this.get(idBundle);
        if (blueprint == null) {
            throw new IllegalArgumentException("Unknown Blueprint: " + idBundle);
        }
        return blueprint;
    }

    private ContentBlueprint get(BlueprintIdBundle idBundle) {
        UUID incomingId = idBundle.getBlueprintId();
        ModuleCompleteKey moduleKey = idBundle.getBlueprintModuleKey();
        if (moduleKey == null) {
            return (ContentBlueprint)this.contentBlueprintManager.getById(incomingId);
        }
        ContentBlueprint bp = this.contentBlueprintManager.getPluginBackedContentBlueprint(moduleKey, idBundle.getSpaceKey());
        if (bp == null) {
            log.warn("No ContentBlueprint found for id-bundle: " + idBundle);
            return null;
        }
        UUID foundId = bp.getId();
        if (incomingId != null && !foundId.equals(incomingId)) {
            log.warn("ContentBlueprint id mismatch for id-bundle: " + idBundle + ". Found blueprint with id: " + foundId);
        }
        return bp;
    }

    private ContentTemplateRef findTemplateWithKey(String moduleCompleteKey, List<ContentTemplateRef> contentTemplateRefs) {
        for (ContentTemplateRef contentTemplateRef : contentTemplateRefs) {
            if (!contentTemplateRef.getModuleCompleteKey().equals(moduleCompleteKey)) continue;
            return contentTemplateRef;
        }
        throw new IllegalStateException("No content template ref found with module key: " + moduleCompleteKey);
    }

    private ModuleCompleteKey getModuleCompleteKeyFromRelative(String pluginKey, String moduleKey) {
        try {
            return new ModuleCompleteKey(moduleKey);
        }
        catch (Exception e) {
            return new ModuleCompleteKey(pluginKey, moduleKey);
        }
    }
}

