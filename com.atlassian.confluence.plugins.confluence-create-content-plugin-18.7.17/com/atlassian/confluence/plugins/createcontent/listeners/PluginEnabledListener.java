/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.activeobjects.external.ActiveObjects
 *  com.atlassian.event.api.AsynchronousPreferred
 *  com.atlassian.event.api.EventListener
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.plugin.ModuleCompleteKey
 *  com.atlassian.plugin.ModuleDescriptor
 *  com.atlassian.plugin.Plugin
 *  com.atlassian.plugin.PluginAccessor
 *  com.atlassian.plugin.descriptors.AbstractModuleDescriptor
 *  com.atlassian.plugin.event.events.PluginEnabledEvent
 *  com.atlassian.plugin.event.events.PluginFrameworkStartedEvent
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.sal.api.transaction.TransactionTemplate
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 *  javax.annotation.PostConstruct
 *  javax.annotation.PreDestroy
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.plugins.createcontent.listeners;

import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.confluence.plugins.createcontent.AoBackedManager;
import com.atlassian.confluence.plugins.createcontent.ContentBlueprintManager;
import com.atlassian.confluence.plugins.createcontent.ContentTemplateRefManager;
import com.atlassian.confluence.plugins.createcontent.SpaceBlueprintManager;
import com.atlassian.confluence.plugins.createcontent.api.exceptions.ResourceErrorType;
import com.atlassian.confluence.plugins.createcontent.exceptions.BlueprintPluginNotFoundException;
import com.atlassian.confluence.plugins.createcontent.extensions.BlueprintModuleDescriptor;
import com.atlassian.confluence.plugins.createcontent.extensions.ContentTemplateModuleDescriptor;
import com.atlassian.confluence.plugins.createcontent.extensions.SpaceBlueprintModuleDescriptor;
import com.atlassian.confluence.plugins.createcontent.impl.ContentBlueprint;
import com.atlassian.confluence.plugins.createcontent.impl.ContentTemplateRef;
import com.atlassian.confluence.plugins.createcontent.impl.PluginBackedBlueprint;
import com.atlassian.confluence.plugins.createcontent.impl.SpaceBlueprint;
import com.atlassian.event.api.AsynchronousPreferred;
import com.atlassian.event.api.EventListener;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.plugin.ModuleCompleteKey;
import com.atlassian.plugin.ModuleDescriptor;
import com.atlassian.plugin.Plugin;
import com.atlassian.plugin.PluginAccessor;
import com.atlassian.plugin.descriptors.AbstractModuleDescriptor;
import com.atlassian.plugin.event.events.PluginEnabledEvent;
import com.atlassian.plugin.event.events.PluginFrameworkStartedEvent;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.sal.api.transaction.TransactionTemplate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PluginEnabledListener {
    private static final Logger log = LoggerFactory.getLogger(PluginEnabledListener.class);
    private static final String KEY_CC_PLUGIN = "com.atlassian.confluence.plugins.confluence-create-content-plugin";
    private final ContentBlueprintManager contentBlueprintManager;
    private final SpaceBlueprintManager spaceBlueprintManager;
    private final ContentTemplateRefManager contentTemplateRefManager;
    private final PluginAccessor pluginAccessor;
    private final EventPublisher eventPublisher;
    private final TransactionTemplate transactionTemplate;
    private final ActiveObjects activeObjects;
    final AtomicBoolean initialPluginsScanned = new AtomicBoolean(false);

    @Autowired
    public PluginEnabledListener(ContentBlueprintManager contentBlueprintManager, SpaceBlueprintManager spaceBlueprintManager, ContentTemplateRefManager contentTemplateRefManager, @ComponentImport PluginAccessor pluginAccessor, @ComponentImport EventPublisher eventPublisher, @ComponentImport TransactionTemplate transactionTemplate, @ComponentImport ActiveObjects activeObjects) {
        this.contentBlueprintManager = contentBlueprintManager;
        this.spaceBlueprintManager = spaceBlueprintManager;
        this.contentTemplateRefManager = contentTemplateRefManager;
        this.pluginAccessor = pluginAccessor;
        this.eventPublisher = eventPublisher;
        this.transactionTemplate = transactionTemplate;
        this.activeObjects = activeObjects;
    }

    @PostConstruct
    public void postConstruct() {
        this.eventPublisher.register((Object)this);
    }

    @PreDestroy
    public void preDestroy() {
        this.eventPublisher.unregister((Object)this);
    }

    @EventListener
    public void onPluginEnabledEvent(PluginEnabledEvent event) {
        if (!this.initialPluginsScanned.get() || KEY_CC_PLUGIN.equals(event.getPlugin().getKey())) {
            return;
        }
        this.eventPublisher.publish((Object)new AsyncPluginEnabledEvent(event.getPlugin()));
    }

    @EventListener
    public void onAsyncPluginEnabledEvent(AsyncPluginEnabledEvent event) {
        this.activeObjects.flushAll();
        this.updatePluginModuleMetadata(event.getPlugin());
    }

    @EventListener
    public void onPluginFrameworkStartedEvent(PluginFrameworkStartedEvent event) {
        this.initialPluginsScanned.getAndSet(true);
        log.debug("Plugins have finished loading. Flushing all Active Objects tables before scanning for blueprints.");
        this.activeObjects.flushAll();
        Collection enabledPlugins = this.pluginAccessor.getEnabledPlugins();
        enabledPlugins.removeIf(plugin -> KEY_CC_PLUGIN.equals(plugin.getKey()));
        enabledPlugins.forEach(this::updatePluginModuleMetadata);
    }

    void updatePluginModuleMetadata(Plugin plugin) {
        this.transactionTemplate.execute(() -> {
            Collection moduleDescriptors = plugin.getModuleDescriptors();
            for (ModuleDescriptor module : moduleDescriptors) {
                if (module instanceof BlueprintModuleDescriptor) {
                    this.parse((BlueprintModuleDescriptor)module);
                    continue;
                }
                if (!(module instanceof SpaceBlueprintModuleDescriptor)) continue;
                this.parse((SpaceBlueprintModuleDescriptor)module);
            }
            return null;
        });
    }

    private void parse(BlueprintModuleDescriptor module) {
        ContentBlueprint storedPluginClone = (ContentBlueprint)this.contentBlueprintManager.getCloneByModuleCompleteKey(module.getBlueprintKey());
        if (storedPluginClone == null) {
            return;
        }
        ModuleCompleteKey indexTemplateKey = module.getIndexTemplate();
        ContentTemplateRef indexTemplateRef = storedPluginClone.getIndexPageTemplateRef();
        ContentTemplateRef newIndexTemplateRef = this.deleteCreateUpdate(indexTemplateKey, indexTemplateRef);
        storedPluginClone.setIndexPageTemplateRef(newIndexTemplateRef);
        List<ModuleCompleteKey> contentTemplateKeys = module.getContentTemplates();
        List<ContentTemplateRef> dbContentTemplates = storedPluginClone.getContentTemplateRefs();
        this.deleteCreateUpdate(contentTemplateKeys, dbContentTemplates);
        this.update(module, storedPluginClone);
    }

    private void parse(@Nonnull SpaceBlueprintModuleDescriptor module) {
        if (module.getCompleteKey().equals("com.atlassian.confluence.plugins.confluence-create-content-plugin:create-blank-space-blueprint")) {
            return;
        }
        ModuleCompleteKey moduleCompleteKey = new ModuleCompleteKey(module.getCompleteKey());
        SpaceBlueprint spaceBlueprint = (SpaceBlueprint)this.spaceBlueprintManager.getCloneByModuleCompleteKey(moduleCompleteKey);
        if (spaceBlueprint == null) {
            return;
        }
        SpaceBlueprintModuleDescriptor.ContentTemplateRefNode contentTemplateRefNode = module.getContentTemplateRefNode();
        UUID homePageId = spaceBlueprint.getHomePageId();
        if (homePageId != null) {
            ContentTemplateRef homePage = (ContentTemplateRef)this.contentTemplateRefManager.getById(homePageId);
            if (homePage == null) {
                throw new RuntimeException("ContentTemplate with UUID " + homePageId.toString() + " not found!");
            }
            if (contentTemplateRefNode != null) {
                this.parseHomepageChildren(contentTemplateRefNode, homePage);
                this.deleteCreateUpdate(contentTemplateRefNode.ref, homePage);
            } else {
                spaceBlueprint.setHomePageId(null);
            }
        } else if (contentTemplateRefNode != null) {
            ContentTemplateRef homePageRef = this.deleteCreateUpdate(contentTemplateRefNode.ref, null);
            spaceBlueprint.setHomePageId(homePageRef.getId());
        }
        this.update(module, spaceBlueprint);
        if (homePageId != null && spaceBlueprint.getHomePageId() == null) {
            this.deleteIfExists(homePageId);
        }
    }

    private void parseHomepageChildren(@Nonnull SpaceBlueprintModuleDescriptor.ContentTemplateRefNode descNode, @Nonnull ContentTemplateRef homePage) {
        List<SpaceBlueprintModuleDescriptor.ContentTemplateRefNode> children = descNode.children;
        if (children != null && !children.isEmpty()) {
            List<ContentTemplateRef> dbContentTemplates = homePage.getChildren();
            ArrayList<ContentTemplateRef> toRemove = new ArrayList<ContentTemplateRef>(dbContentTemplates);
            for (SpaceBlueprintModuleDescriptor.ContentTemplateRefNode child : children) {
                ContentTemplateRef dbObj = this.findByKey(dbContentTemplates, child.ref);
                boolean needsAdding = false;
                if (dbObj != null) {
                    toRemove.remove(dbObj);
                } else {
                    needsAdding = true;
                }
                if ((dbObj = this.deleteCreateUpdate(child.ref, dbObj)) == null) continue;
                if (needsAdding) {
                    homePage.addChildTemplateRef(dbObj);
                }
                this.parseHomepageChildren(child, dbObj);
            }
            this.delete(toRemove);
            dbContentTemplates.removeAll(toRemove);
        }
    }

    private ContentTemplateRef findByKey(List<ContentTemplateRef> dbList, ModuleCompleteKey key) {
        String strKey = key.getCompleteKey();
        for (ContentTemplateRef contentTemplateRef : dbList) {
            ContentTemplateRef result;
            if (strKey.equals(contentTemplateRef.getModuleCompleteKey())) {
                return contentTemplateRef;
            }
            List<ContentTemplateRef> children = contentTemplateRef.getChildren();
            if (children.isEmpty() || (result = this.findByKey(children, key)) == null) continue;
            return result;
        }
        return null;
    }

    private ContentTemplateRef create(ContentTemplateModuleDescriptor desc) {
        ContentTemplateRef dbObj = new ContentTemplateRef(null, 0L, desc.getCompleteKey(), this.i18n(desc), true, null);
        return this.contentTemplateRefManager.create(dbObj);
    }

    private void update(@Nonnull BlueprintModuleDescriptor desc, @Nonnull ContentBlueprint dbObj) {
        dbObj.setCreateResult(desc.getCreateResult());
        dbObj.setHowToUseTemplate(desc.getHowToUseTemplate());
        dbObj.setIndexKey(desc.getIndexKey());
        dbObj.setIndexTitleI18nKey(desc.getIndexTitleI18nKey());
        String i18nNameKey = this.i18n(desc, false);
        if (i18nNameKey == null) {
            i18nNameKey = desc.getName();
        }
        dbObj.setI18nNameKey(i18nNameKey);
        this.updateNonClones(this.contentBlueprintManager, dbObj, i18nNameKey);
        this.contentBlueprintManager.update(dbObj);
    }

    private void update(@Nonnull ContentTemplateModuleDescriptor desc, @Nonnull ContentTemplateRef dbObj) {
        String i18nNameKey = this.i18n(desc);
        dbObj.setI18nNameKey(i18nNameKey);
        this.updateNonClones(this.contentTemplateRefManager, dbObj, i18nNameKey);
        this.contentTemplateRefManager.update(dbObj);
    }

    private void update(@Nonnull SpaceBlueprintModuleDescriptor desc, @Nonnull SpaceBlueprint dbObj) {
        String i18nNameKey = this.i18n(desc);
        dbObj.setI18nNameKey(i18nNameKey);
        dbObj.setPromotedBps(desc.getPromotedBlueprintKeys());
        dbObj.setCategory(desc.getCategory());
        this.updateNonClones(this.spaceBlueprintManager, dbObj, i18nNameKey);
        this.spaceBlueprintManager.update(dbObj);
    }

    private <O extends PluginBackedBlueprint, M extends AoBackedManager<O, ?>> void updateNonClones(M manager, O dbObj, String i18nNameKey) {
        List<O> nonClones = manager.getNonClonesByModuleCompleteKey(new ModuleCompleteKey(dbObj.getModuleCompleteKey()));
        for (PluginBackedBlueprint nonClone : nonClones) {
            nonClone.setI18nNameKey(i18nNameKey);
            manager.update((PluginBackedBlueprint)nonClone);
        }
    }

    private void delete(@Nonnull List<ContentTemplateRef> dbObjs) {
        for (ContentTemplateRef dbObj : dbObjs) {
            this.contentTemplateRefManager.delete(dbObj.getId());
        }
    }

    @Nullable
    private ContentTemplateRef deleteCreateUpdate(@Nullable ModuleCompleteKey descKey, @Nullable ContentTemplateRef dbObj) {
        if (descKey == null) {
            this.deleteIfExists(dbObj);
            dbObj = null;
        } else {
            ContentTemplateModuleDescriptor desc;
            String completeKey = descKey.getCompleteKey();
            if (dbObj != null && !completeKey.equals(dbObj.getModuleCompleteKey())) {
                this.deleteIfExists(dbObj);
                dbObj = null;
            }
            if ((desc = (ContentTemplateModuleDescriptor)this.pluginAccessor.getPluginModule(completeKey)) == null) {
                throw new BlueprintPluginNotFoundException("Module with key " + completeKey + " not found!", ResourceErrorType.NOT_FOUND_CONTENT_TEMPLATE_REF, (Object)completeKey);
            }
            if (dbObj == null) {
                dbObj = this.create(desc);
            } else {
                this.update(desc, dbObj);
            }
        }
        return dbObj;
    }

    private void deleteCreateUpdate(@Nonnull List<ModuleCompleteKey> descKeys, @Nonnull List<ContentTemplateRef> dbObjs) {
        ArrayList<ContentTemplateRef> toRemove = new ArrayList<ContentTemplateRef>(dbObjs);
        for (ModuleCompleteKey descKey : descKeys) {
            ContentTemplateRef dbObj = this.findByKey(dbObjs, descKey);
            ContentTemplateModuleDescriptor desc = (ContentTemplateModuleDescriptor)this.pluginAccessor.getPluginModule(descKey.getCompleteKey());
            if (dbObj == null) {
                ContentTemplateRef newDbObj = this.create(desc);
                dbObjs.add(newDbObj);
                continue;
            }
            this.update(desc, dbObj);
            toRemove.remove(dbObj);
        }
        this.delete(toRemove);
        dbObjs.removeAll(toRemove);
    }

    private void deleteIfExists(@Nullable ContentTemplateRef dbObj) {
        if (dbObj == null) {
            return;
        }
        this.deleteIfExists(dbObj.getId());
    }

    private void deleteIfExists(@Nonnull UUID id) {
        this.contentTemplateRefManager.delete(id);
    }

    @Nonnull
    private String i18n(@Nonnull AbstractModuleDescriptor desc) {
        return this.i18n(desc, true);
    }

    @Nullable
    private String i18n(@Nonnull AbstractModuleDescriptor desc, boolean failIfNotFound) {
        String i18nNameKey = desc.getI18nNameKey();
        if (i18nNameKey == null) {
            String message = "i18n-name-key must be specified for: " + desc.getCompleteKey();
            log.warn(message);
            if (failIfNotFound) {
                throw new IllegalArgumentException(message);
            }
        }
        return i18nNameKey;
    }

    @AsynchronousPreferred
    static class AsyncPluginEnabledEvent {
        private final Plugin plugin;

        public AsyncPluginEnabledEvent(Plugin plugin) {
            this.plugin = plugin;
        }

        public Plugin getPlugin() {
            return this.plugin;
        }
    }
}

