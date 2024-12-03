/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.activeobjects.external.ActiveObjects
 *  com.atlassian.confluence.spaces.Space
 *  com.atlassian.plugin.ModuleCompleteKey
 *  com.atlassian.plugin.ModuleDescriptor
 *  com.atlassian.plugin.PluginAccessor
 *  com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 *  net.java.ao.Query
 *  net.java.ao.RawEntity
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.plugins.createcontent.impl;

import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.confluence.plugins.createcontent.BlueprintConstants;
import com.atlassian.confluence.plugins.createcontent.ContentBlueprintManager;
import com.atlassian.confluence.plugins.createcontent.activeobjects.ContentBlueprintAo;
import com.atlassian.confluence.plugins.createcontent.activeobjects.ContentTemplateRefAo;
import com.atlassian.confluence.plugins.createcontent.api.exceptions.ResourceErrorType;
import com.atlassian.confluence.plugins.createcontent.concurrent.LazyInsertExecutor;
import com.atlassian.confluence.plugins.createcontent.concurrent.LazyInserter;
import com.atlassian.confluence.plugins.createcontent.exceptions.BlueprintPluginNotFoundException;
import com.atlassian.confluence.plugins.createcontent.exceptions.ModuleNotBlueprintException;
import com.atlassian.confluence.plugins.createcontent.extensions.BlueprintModuleDescriptor;
import com.atlassian.confluence.plugins.createcontent.extensions.ContentTemplateModuleDescriptor;
import com.atlassian.confluence.plugins.createcontent.impl.AbstractAoManager;
import com.atlassian.confluence.plugins.createcontent.impl.ActiveObjectsUtils;
import com.atlassian.confluence.plugins.createcontent.impl.ContentBlueprint;
import com.atlassian.confluence.plugins.createcontent.impl.ContentTemplateRef;
import com.atlassian.confluence.plugins.createcontent.impl.DefaultContentTemplateRefManager;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.plugin.ModuleCompleteKey;
import com.atlassian.plugin.ModuleDescriptor;
import com.atlassian.plugin.PluginAccessor;
import com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.java.ao.Query;
import net.java.ao.RawEntity;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component(value="contentBlueprintManager")
@ExportAsService(value={ContentBlueprintManager.class})
public class DefaultContentBlueprintManager
extends AbstractAoManager<ContentBlueprint, ContentBlueprintAo>
implements ContentBlueprintManager {
    private static final Logger log = LoggerFactory.getLogger(DefaultContentBlueprintManager.class);
    private final PluginAccessor pluginAccessor;
    private final DefaultContentTemplateRefManager contentTemplateRefManager;
    private final LazyInsertExecutor lazyInsertExecutor;

    @Autowired
    public DefaultContentBlueprintManager(@ComponentImport @Nonnull ActiveObjects activeObjects, @ComponentImport @Nonnull PluginAccessor pluginAccessor, @Nonnull DefaultContentTemplateRefManager contentTemplateRefManager, @Nonnull LazyInsertExecutor lazyInsertExecutor) {
        super(activeObjects, ContentBlueprintAo.class);
        this.pluginAccessor = pluginAccessor;
        this.contentTemplateRefManager = contentTemplateRefManager;
        this.lazyInsertExecutor = lazyInsertExecutor;
    }

    @Nullable
    private ContentBlueprint getOrCreateClonedBlueprint(@Nonnull ModuleCompleteKey moduleCompleteKey) {
        try {
            BlueprintModuleDescriptor pluginBlueprint = this.getBlueprintDescriptor(moduleCompleteKey.getCompleteKey());
            return this.getOrCreateClonedBlueprint(moduleCompleteKey, pluginBlueprint);
        }
        catch (ModuleNotBlueprintException e) {
            return null;
        }
    }

    @Override
    @Nullable
    public ContentBlueprint getPluginBlueprint(@Nonnull ModuleCompleteKey moduleCompleteKey) {
        if (moduleCompleteKey.getCompleteKey().equals("com.atlassian.confluence.plugins.confluence-create-content-plugin:create-blank-page")) {
            return BlueprintConstants.BLANK_PAGE_BLUEPRINT;
        }
        if (moduleCompleteKey.getCompleteKey().equals("com.atlassian.confluence.plugins.confluence-create-content-plugin:create-blog-post")) {
            return BlueprintConstants.BLOG_POST_BLUEPRINT;
        }
        try {
            BlueprintModuleDescriptor pluginBlueprint = this.getEnabledBlueprintDescriptor(moduleCompleteKey.getCompleteKey());
            return this.getOrCreateClonedBlueprint(moduleCompleteKey, pluginBlueprint);
        }
        catch (ModuleNotBlueprintException e) {
            return null;
        }
    }

    private ContentBlueprint getOrCreateClonedBlueprint(final @Nonnull ModuleCompleteKey moduleCompleteKey, final @Nullable BlueprintModuleDescriptor pluginBlueprint) {
        if (pluginBlueprint == null) {
            return null;
        }
        return this.lazyInsertExecutor.lazyInsertAndRead(new LazyInserter<ContentBlueprint>(){

            @Override
            public ContentBlueprint read() {
                ContentBlueprintAo blueprintAo = DefaultContentBlueprintManager.this.getContentBlueprint(moduleCompleteKey, null, true);
                return blueprintAo != null ? DefaultContentBlueprintManager.this.build(blueprintAo) : null;
            }

            @Override
            public ContentBlueprint insert() {
                return DefaultContentBlueprintManager.this.storePluginBlueprintClone(pluginBlueprint);
            }
        }, "cc:getOrCreateClonedBlueprint:" + moduleCompleteKey.getCompleteKey());
    }

    @Override
    @Nonnull
    public List<ContentBlueprint> getAll(Space space) {
        String spaceKey = space == null ? null : space.getKey();
        return this.getAllBySpaceKey(spaceKey);
    }

    @Override
    @Nonnull
    public List<ContentBlueprint> getAllBySpaceKey(String spaceKey) {
        if (spaceKey == null) {
            return super.getAll("SPACE_KEY IS NULL", new Object[0]);
        }
        return super.getAll("SPACE_KEY = ?", spaceKey);
    }

    @Override
    @Nullable
    public ContentBlueprint getPluginBackedContentBlueprint(ModuleCompleteKey moduleCompleteKey, String spaceKey) {
        ContentBlueprintAo blueprintAo = null;
        if (spaceKey != null) {
            blueprintAo = this.getContentBlueprint(moduleCompleteKey, spaceKey, false);
        }
        if (blueprintAo == null) {
            blueprintAo = this.getContentBlueprint(moduleCompleteKey, null, false);
        }
        if (blueprintAo != null) {
            return this.build(blueprintAo);
        }
        return this.getPluginBlueprint(moduleCompleteKey);
    }

    @Override
    @Nonnull
    protected ContentBlueprintAo internalCreateAo(@Nonnull ContentBlueprint original) {
        ContentBlueprintAo ao = ActiveObjectsUtils.createWithUuid(this.activeObjects, ContentBlueprintAo.class);
        ContentTemplateRef indexPageTemplateRef = original.getIndexPageTemplateRef();
        ContentTemplateRefAo childAo = this.contentTemplateRefManager.internalCreateAo(indexPageTemplateRef);
        childAo.setContentBlueprintIndexParent(ao);
        childAo.save();
        for (ContentTemplateRef moduleCompleteKey : original.getContentTemplateRefs()) {
            ContentTemplateRefAo contentTemplateRefAo = this.contentTemplateRefManager.internalCreateAo(moduleCompleteKey);
            contentTemplateRefAo.setContentBlueprintParent(ao);
            contentTemplateRefAo.save();
        }
        this.copyPropertiesIntoAo(ao, original, true);
        ao.save();
        return ao;
    }

    @Override
    @Nonnull
    protected ContentBlueprintAo internalUpdateAo(@Nonnull ContentBlueprint object) {
        ContentBlueprintAo ao = (ContentBlueprintAo)this.internalGetAoById(object.getId());
        if (ao == null) {
            String error = String.format("Blueprint with UUID %s not found", object.getId());
            throw new IllegalStateException(error);
        }
        ContentTemplateRef indexPageTemplateRef = object.getIndexPageTemplateRef();
        ContentTemplateRefAo childAo = this.contentTemplateRefManager.internalUpdateAo(indexPageTemplateRef);
        childAo.setContentBlueprintIndexParent(ao);
        childAo.save();
        for (ContentTemplateRef contentTemplateRef : object.getContentTemplateRefs()) {
            ContentTemplateRefAo contentTemplateRefAo = this.contentTemplateRefManager.internalUpdateAo(contentTemplateRef);
            contentTemplateRefAo.setContentBlueprintParent(ao);
            contentTemplateRefAo.save();
        }
        this.copyPropertiesIntoAo(ao, object, false);
        ao.save();
        return ao;
    }

    private void copyPropertiesIntoAo(@Nonnull ContentBlueprintAo ao, @Nonnull ContentBlueprint original, boolean isCreate) {
        if (isCreate) {
            ao.setPluginModuleKey(original.getModuleCompleteKey());
        }
        ao.setPluginClone(original.isPluginClone());
        ao.setI18nNameKey(original.getI18nNameKey());
        ao.setSpaceKey(original.getSpaceKey());
        ao.setCreateResult(original.getCreateResult());
        ao.setHowToUseTemplate(original.getHowToUseTemplate());
        ao.setIndexKey(original.getIndexKey());
        ao.setIndexTitleI18nKey(original.getIndexTitleI18nKey());
    }

    @Override
    @Nonnull
    public ContentBlueprint getOrCreateCustomBlueprint(final @Nonnull ModuleCompleteKey moduleCompleteKey, @Nullable Space space) {
        final String spaceKey = this.getSpaceKey(space);
        Object spaceSuffix = spaceKey != null ? ":" + spaceKey : "";
        return this.lazyInsertExecutor.lazyInsertAndRead(new LazyInserter<ContentBlueprint>(){

            @Override
            public ContentBlueprint read() {
                ContentBlueprintAo ao = DefaultContentBlueprintManager.this.getContentBlueprint(moduleCompleteKey, spaceKey, false);
                return ao != null ? DefaultContentBlueprintManager.this.build(ao) : null;
            }

            @Override
            public ContentBlueprint insert() {
                return (ContentBlueprint)DefaultContentBlueprintManager.this.activeObjects.executeInTransaction(() -> {
                    ContentBlueprint pluginBlueprintClone = DefaultContentBlueprintManager.this.getOrCreateClonedBlueprint(moduleCompleteKey);
                    if (pluginBlueprintClone == null) {
                        throw new BlueprintPluginNotFoundException("Tried to create a clone of a disabled / uninstalled / non existing blueprint with module " + moduleCompleteKey, ResourceErrorType.NOT_FOUND_BLUEPRINT, (Object)moduleCompleteKey.getCompleteKey());
                    }
                    pluginBlueprintClone.setId(null);
                    pluginBlueprintClone.setPluginClone(false);
                    pluginBlueprintClone.setSpaceKey(spaceKey);
                    pluginBlueprintClone.getIndexPageTemplateRef().setPluginClone(false);
                    List<ContentTemplateRef> contentTemplateRefs = pluginBlueprintClone.getContentTemplateRefs();
                    for (ContentTemplateRef contentTemplateRef : contentTemplateRefs) {
                        contentTemplateRef.setPluginClone(false);
                    }
                    return DefaultContentBlueprintManager.this.create(pluginBlueprintClone);
                });
            }
        }, "cc:getOrCreateCustomBlueprint:" + moduleCompleteKey.getCompleteKey() + (String)spaceSuffix);
    }

    @Nullable
    private ContentBlueprintAo getContentBlueprint(@Nonnull ModuleCompleteKey moduleCompleteKey, @Nullable String spaceKey, boolean isPluginClone) {
        return (ContentBlueprintAo)this.activeObjects.executeInTransaction(() -> {
            String completeKey = moduleCompleteKey.getCompleteKey();
            Query query = Query.select();
            if (spaceKey == null) {
                query.setWhereClause("PLUGIN_MODULE_KEY = ? AND SPACE_KEY IS NULL AND PLUGIN_CLONE = ?");
                query.setWhereParams(new Object[]{completeKey, isPluginClone});
            } else {
                query.setWhereClause("PLUGIN_MODULE_KEY = ? AND SPACE_KEY = ? AND PLUGIN_CLONE = ?");
                query.setWhereParams(new Object[]{completeKey, spaceKey, isPluginClone});
            }
            query.order("ID");
            List<ContentBlueprintAo> aos = Arrays.asList((ContentBlueprintAo[])this.activeObjects.find(ContentBlueprintAo.class, query));
            if (aos.isEmpty()) {
                return null;
            }
            if (aos.size() > 1) {
                log.warn("Should only be one Blueprint with space {} and module key {}", (Object)spaceKey, (Object)completeKey);
            }
            return aos.get(0);
        });
    }

    @Nullable
    private String getSpaceKey(@Nullable Space space) {
        if (space != null) {
            String spaceKey = space.getKey().trim();
            return spaceKey.isEmpty() ? null : spaceKey;
        }
        return null;
    }

    @Override
    protected void internalDeleteAo(@Nonnull ContentBlueprintAo ao) {
        ContentTemplateRefAo[] contentTemplates;
        ContentTemplateRefAo indexTemplateRef = ao.getIndexTemplateRef();
        if (indexTemplateRef != null) {
            this.contentTemplateRefManager.internalDeleteAo(indexTemplateRef);
            this.activeObjects.delete(new RawEntity[]{indexTemplateRef});
        }
        for (ContentTemplateRefAo contentTemplate : contentTemplates = ao.getContentTemplates()) {
            this.contentTemplateRefManager.internalDeleteAo(contentTemplate);
        }
        this.activeObjects.delete((RawEntity[])contentTemplates);
    }

    @Nullable
    private BlueprintModuleDescriptor getEnabledBlueprintDescriptor(@Nonnull String blueprintKey) {
        return this.castIfModuleIsBlueprint(blueprintKey, this.pluginAccessor.getEnabledPluginModule(blueprintKey));
    }

    @Nullable
    private BlueprintModuleDescriptor getBlueprintDescriptor(@Nonnull String blueprintKey) {
        return this.castIfModuleIsBlueprint(blueprintKey, this.pluginAccessor.getPluginModule(blueprintKey));
    }

    private BlueprintModuleDescriptor castIfModuleIsBlueprint(@Nonnull String blueprintKey, @Nullable ModuleDescriptor<?> pluginModule) {
        if (pluginModule == null) {
            return null;
        }
        if (pluginModule instanceof BlueprintModuleDescriptor) {
            return (BlueprintModuleDescriptor)pluginModule;
        }
        throw new ModuleNotBlueprintException(blueprintKey, pluginModule, ResourceErrorType.INVALID_MODULE, (Object)blueprintKey);
    }

    @Nonnull
    private ContentBlueprint storePluginBlueprintClone(@Nonnull BlueprintModuleDescriptor pluginBlueprint) {
        ContentBlueprintAo ao = (ContentBlueprintAo)this.activeObjects.executeInTransaction(() -> {
            ContentBlueprintAo newBp = ActiveObjectsUtils.createWithUuid(this.activeObjects, ContentBlueprintAo.class);
            String blueprintKey = pluginBlueprint.getCompleteKey();
            newBp.setPluginModuleKey(blueprintKey);
            newBp.setPluginClone(true);
            String i18nNameKey = this.getI18nNameKey(pluginBlueprint);
            newBp.setI18nNameKey(i18nNameKey);
            newBp.setCreateResult(pluginBlueprint.getCreateResult());
            newBp.setHowToUseTemplate(pluginBlueprint.getHowToUseTemplate());
            newBp.setIndexKey(pluginBlueprint.getIndexKey());
            newBp.setIndexTitleI18nKey(pluginBlueprint.getIndexTitleI18nKey());
            newBp.setIndexDisabled(pluginBlueprint.isIndexDisabled());
            newBp.save();
            ModuleCompleteKey indexTemplateKey = pluginBlueprint.getIndexTemplate();
            if (indexTemplateKey != null) {
                ContentTemplateRefAo indexContentTemplateRef = this.createContentTemplateRefAo(indexTemplateKey);
                indexContentTemplateRef.setContentBlueprintIndexParent(newBp);
                indexContentTemplateRef.save();
            }
            for (ModuleCompleteKey moduleCompleteKey : pluginBlueprint.getContentTemplates()) {
                if (moduleCompleteKey == null) continue;
                ContentTemplateRefAo ao1 = this.createContentTemplateRefAo(moduleCompleteKey);
                ao1.setContentBlueprintParent(newBp);
                ao1.save();
            }
            return newBp;
        });
        return this.build(ao);
    }

    String getI18nNameKey(@Nonnull BlueprintModuleDescriptor pluginBlueprint) {
        String i18nNameKey = pluginBlueprint.getI18nNameKey();
        if (StringUtils.isBlank((CharSequence)i18nNameKey)) {
            log.warn("i18n-name-key must be specified for: " + pluginBlueprint.getCompleteKey());
            i18nNameKey = pluginBlueprint.getName();
            if (StringUtils.isBlank((CharSequence)i18nNameKey)) {
                i18nNameKey = pluginBlueprint.getKey();
            }
        }
        return i18nNameKey;
    }

    @Nonnull
    ContentTemplateRefAo createContentTemplateRefAo(@Nonnull ModuleCompleteKey indexTemplateKey) {
        String moduleCompleteKey = indexTemplateKey.getCompleteKey();
        ContentTemplateModuleDescriptor contentTemplateDescriptor = (ContentTemplateModuleDescriptor)this.pluginAccessor.getEnabledPluginModule(moduleCompleteKey);
        if (contentTemplateDescriptor == null) {
            throw new BlueprintPluginNotFoundException("The content-template module descriptor with key '" + moduleCompleteKey + "' was not found", ResourceErrorType.NOT_FOUND_CONTENT_TEMPLATE, (Object)moduleCompleteKey);
        }
        String i18nNameKey = this.getI18nNameKey(contentTemplateDescriptor);
        return (ContentTemplateRefAo)this.contentTemplateRefManager.createAo(new ContentTemplateRef(UUID.randomUUID(), 0L, moduleCompleteKey, i18nNameKey, true, null));
    }

    private String getI18nNameKey(@Nonnull ContentTemplateModuleDescriptor contentTemplateDescriptor) {
        String i18nNameKey = contentTemplateDescriptor.getI18nNameKey();
        if (StringUtils.isBlank((CharSequence)i18nNameKey)) {
            log.warn("i18n-name-key must be specified for: " + contentTemplateDescriptor.getCompleteKey());
            i18nNameKey = contentTemplateDescriptor.getKey();
        }
        return i18nNameKey;
    }

    @Override
    @Nonnull
    protected ContentBlueprint build(@Nonnull ContentBlueprintAo ao) {
        BlueprintModuleDescriptor blueprintDescriptor;
        ContentBlueprint contentBlueprint = new ContentBlueprint();
        contentBlueprint.setId(UUID.fromString(ao.getUuid()));
        String pluginModuleKey = ao.getPluginModuleKey();
        contentBlueprint.setModuleCompleteKey(pluginModuleKey);
        contentBlueprint.setI18nNameKey(ao.getI18nNameKey());
        contentBlueprint.setPluginClone(ao.isPluginClone());
        contentBlueprint.setSpaceKey(ao.getSpaceKey());
        contentBlueprint.setIndexKey(ao.getIndexKey());
        contentBlueprint.setIndexPageTemplateRef(this.convertAo(ao.getIndexTemplateRef(), contentBlueprint));
        contentBlueprint.setCreateResult(ao.getCreateResult());
        contentBlueprint.setHowToUseTemplate(ao.getHowToUseTemplate());
        contentBlueprint.setIndexTitleI18nKey(ao.getIndexTitleI18nKey());
        contentBlueprint.setIndexDisabled(ao.isIndexDisabled());
        contentBlueprint.setContentTemplateRefs(this.convertAos(ao.getContentTemplates(), contentBlueprint));
        if (StringUtils.isNotBlank((CharSequence)pluginModuleKey) && (blueprintDescriptor = this.getEnabledBlueprintDescriptor(pluginModuleKey)) != null) {
            contentBlueprint.setDialogWizard(blueprintDescriptor.getDialogWizard());
        }
        return contentBlueprint;
    }

    @Nonnull
    private List<ContentTemplateRef> convertAos(@Nonnull ContentTemplateRefAo[] aos, @Nonnull ContentBlueprint parent) {
        return Arrays.stream(aos).filter(item -> item.getID() != 0).map(item -> this.convertAo((ContentTemplateRefAo)item, parent)).collect(Collectors.toList());
    }

    @Nullable
    private ContentTemplateRef convertAo(@Nullable ContentTemplateRefAo ao, @Nonnull ContentBlueprint parent) {
        if (ao == null) {
            return null;
        }
        return new ContentTemplateRef(UUID.fromString(ao.getUuid()), ao.getTemplateId(), ao.getPluginModuleKey(), ao.getI18nNameKey(), ao.isPluginClone(), parent);
    }
}

