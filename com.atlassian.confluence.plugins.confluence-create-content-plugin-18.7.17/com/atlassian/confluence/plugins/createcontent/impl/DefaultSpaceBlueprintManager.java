/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.activeobjects.external.ActiveObjects
 *  com.atlassian.confluence.plugins.dialog.wizard.api.DialogWizard
 *  com.atlassian.plugin.ModuleCompleteKey
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.google.common.base.Joiner
 *  com.google.common.collect.Collections2
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.Lists
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 *  net.java.ao.RawEntity
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.plugins.createcontent.impl;

import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.confluence.plugins.createcontent.PluginSpaceBlueprintAccessor;
import com.atlassian.confluence.plugins.createcontent.SpaceBlueprintManager;
import com.atlassian.confluence.plugins.createcontent.activeobjects.ContentTemplateRefAo;
import com.atlassian.confluence.plugins.createcontent.activeobjects.SpaceBlueprintAo;
import com.atlassian.confluence.plugins.createcontent.impl.AbstractAoManager;
import com.atlassian.confluence.plugins.createcontent.impl.DefaultContentTemplateRefManager;
import com.atlassian.confluence.plugins.createcontent.impl.SpaceBlueprint;
import com.atlassian.confluence.plugins.dialog.wizard.api.DialogWizard;
import com.atlassian.plugin.ModuleCompleteKey;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.google.common.base.Joiner;
import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.java.ao.RawEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component(value="spaceBlueprintManager")
public class DefaultSpaceBlueprintManager
extends AbstractAoManager<SpaceBlueprint, SpaceBlueprintAo>
implements SpaceBlueprintManager {
    private final ConcurrentHashMap<ModuleCompleteKey, ModuleCompleteKey> getOrCreateLocks = new ConcurrentHashMap();
    private final PluginSpaceBlueprintAccessor delegate;
    private final DefaultContentTemplateRefManager contentTemplateRefManager;

    @Autowired
    public DefaultSpaceBlueprintManager(PluginSpaceBlueprintAccessor delegate, @ComponentImport ActiveObjects activeObjects, DefaultContentTemplateRefManager contentTemplateRefManager) {
        super(activeObjects, SpaceBlueprintAo.class);
        this.delegate = delegate;
        this.contentTemplateRefManager = contentTemplateRefManager;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Nonnull
    public SpaceBlueprintAo[] getAosByModuleCompleteKey(@Nonnull ModuleCompleteKey moduleCompleteKey, boolean pluginClone) {
        ModuleCompleteKey lock = this.getOrCreateLocks.putIfAbsent(moduleCompleteKey, moduleCompleteKey);
        if (lock == null) {
            lock = moduleCompleteKey;
        }
        ModuleCompleteKey moduleCompleteKey2 = lock;
        synchronized (moduleCompleteKey2) {
            return (SpaceBlueprintAo[])this.activeObjects.executeInTransaction(() -> {
                SpaceBlueprintAo[] aoBlueprints = (SpaceBlueprintAo[])this.internalGetAosByModuleCompleteKey(moduleCompleteKey, pluginClone);
                if (aoBlueprints.length == 0) {
                    SpaceBlueprint pluginBlueprint = this.delegate.getByModuleCompleteKey(moduleCompleteKey);
                    aoBlueprints = new SpaceBlueprintAo[]{this.internalCreateAo(pluginBlueprint)};
                }
                return aoBlueprints;
            });
        }
    }

    @Override
    @Nonnull
    public List<SpaceBlueprint> getAll() {
        List<SpaceBlueprint> pluginBlueprints;
        List<SpaceBlueprint> aoBlueprints = this.getAoSpaceBlueprints();
        if (this.addPluginBlueprintsToAo(aoBlueprints, pluginBlueprints = this.delegate.getAll())) {
            aoBlueprints = this.getAoSpaceBlueprints();
        }
        return aoBlueprints;
    }

    @Override
    protected void internalDeleteAo(@Nonnull SpaceBlueprintAo ao) {
        ContentTemplateRefAo homePage = ao.getHomePage();
        ao.setHomePage(null);
        ao.setCategory(null);
        ao.save();
        if (homePage != null) {
            this.contentTemplateRefManager.internalDeleteAo(homePage);
            this.activeObjects.delete(new RawEntity[]{homePage});
        }
    }

    private boolean addPluginBlueprintsToAo(List<SpaceBlueprint> aoBlueprints, List<SpaceBlueprint> pluginBlueprints) {
        boolean pluginBlueprintsAdded = false;
        for (SpaceBlueprint pluginBlueprint : pluginBlueprints) {
            if (this.findPluginBlueprint(pluginBlueprint.getModuleCompleteKey(), aoBlueprints) != null) continue;
            this.createAo(pluginBlueprint);
            pluginBlueprintsAdded = true;
        }
        return pluginBlueprintsAdded;
    }

    private SpaceBlueprint findPluginBlueprint(String moduleKey, List<SpaceBlueprint> aoBlueprints) {
        for (SpaceBlueprint aoBlueprint : aoBlueprints) {
            if (!aoBlueprint.isPluginClone() || !moduleKey.equals(aoBlueprint.getModuleCompleteKey())) continue;
            return aoBlueprint;
        }
        return null;
    }

    private List<SpaceBlueprint> getAoSpaceBlueprints() {
        return (List)this.activeObjects.executeInTransaction(() -> {
            List<SpaceBlueprintAo> aos = Arrays.asList((SpaceBlueprintAo[])this.activeObjects.find(SpaceBlueprintAo.class));
            return ImmutableList.copyOf((Collection)Collections2.transform(aos, this::build));
        });
    }

    @Override
    @Nonnull
    protected SpaceBlueprintAo internalCreateAo(@Nonnull SpaceBlueprint original) {
        SpaceBlueprintAo ao = (SpaceBlueprintAo)this.helperAoManager.createWithUuid();
        UUID homePageId = original.getHomePageId();
        if (homePageId != null) {
            ao.setHomePage((ContentTemplateRefAo)this.contentTemplateRefManager.internalGetAoById(homePageId));
        }
        this.copyPropertiesIntoAo(ao, original, true);
        ao.save();
        return ao;
    }

    @Override
    @Nonnull
    protected SpaceBlueprintAo internalUpdateAo(@Nonnull SpaceBlueprint object) {
        SpaceBlueprintAo ao = (SpaceBlueprintAo)this.internalGetAoById(object.getId());
        if (ao == null) {
            String error = String.format("Space Blueprint with UUID %s not found", object.getId());
            throw new IllegalStateException(error);
        }
        UUID homePageId = object.getHomePageId();
        ao.setHomePage(homePageId == null ? null : (ContentTemplateRefAo)this.contentTemplateRefManager.getAoById(homePageId));
        this.copyPropertiesIntoAo(ao, object, false);
        ao.save();
        return ao;
    }

    private void copyPropertiesIntoAo(@Nonnull SpaceBlueprintAo ao, @Nonnull SpaceBlueprint original, boolean isCreate) {
        if (isCreate) {
            ao.setPluginModuleKey(original.getModuleCompleteKey());
        }
        ao.setI18nNameKey(original.getI18nNameKey());
        ao.setPluginClone(original.isPluginClone());
        ao.setCategory(original.getCategory());
        List<ModuleCompleteKey> promotedBps = original.getPromotedBps();
        if (promotedBps != null && !promotedBps.isEmpty()) {
            ao.setPromotedBps(Joiner.on((char)',').join(promotedBps));
        } else {
            ao.setPromotedBps(null);
        }
    }

    @Override
    @Nonnull
    protected SpaceBlueprint build(@Nonnull SpaceBlueprintAo ao) {
        ContentTemplateRefAo homePage = ao.getHomePage();
        UUID homePageId = homePage != null ? UUID.fromString(homePage.getUuid()) : null;
        String pluginModuleKey = ao.getPluginModuleKey();
        String category = ao.getCategory();
        ArrayList promotedBps = Lists.newArrayList();
        String promotedBpsStr = ao.getPromotedBps();
        if (promotedBpsStr != null && !promotedBpsStr.isEmpty()) {
            String[] arr;
            for (String promotedBp : arr = promotedBpsStr.split(",")) {
                promotedBps.add(new ModuleCompleteKey(promotedBp));
            }
        }
        DialogWizard dialogWizard = this.delegate.getDialogByModuleCompleteKey(new ModuleCompleteKey(pluginModuleKey));
        SpaceBlueprint result = new SpaceBlueprint(UUID.fromString(ao.getUuid()), pluginModuleKey, ao.getI18nNameKey(), ao.isPluginClone(), promotedBps, dialogWizard, category);
        result.setHomePageId(homePageId);
        return result;
    }

    @Override
    public SpaceBlueprint create(@Nonnull SpaceBlueprint original, @Nullable UUID homePageId) {
        if (homePageId != null) {
            original.setHomePageId(homePageId);
        }
        return this.create(original);
    }
}

