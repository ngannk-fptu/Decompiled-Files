/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.activeobjects.external.ActiveObjects
 *  com.atlassian.plugin.ModuleCompleteKey
 *  com.google.common.collect.Lists
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 */
package com.atlassian.confluence.plugins.createcontent.impl;

import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.confluence.plugins.createcontent.AoBackedManager;
import com.atlassian.confluence.plugins.createcontent.activeobjects.PluginBackedBlueprintAo;
import com.atlassian.confluence.plugins.createcontent.impl.HelperAoManager;
import com.atlassian.confluence.plugins.createcontent.impl.PluginBackedBlueprint;
import com.atlassian.plugin.ModuleCompleteKey;
import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public abstract class AbstractAoManager<O extends PluginBackedBlueprint, A extends PluginBackedBlueprintAo>
implements AoBackedManager<O, A> {
    private final Class<A> aoClass;
    protected final ActiveObjects activeObjects;
    protected final HelperAoManager<A> helperAoManager;
    private final HelperAoManager.HelperCallback<A> helperCallback = new HelperAoManager.HelperCallback<A>(){

        @Override
        public void onDelete(A ao) {
            AbstractAoManager.this.internalDeleteAo(ao);
        }

        @Override
        public void onDeleteAll(A[] aos) {
            AbstractAoManager.this.internalDeleteAllAo((PluginBackedBlueprintAo[])aos);
        }
    };

    protected AbstractAoManager(@Nonnull ActiveObjects activeObjects, @Nonnull Class<A> aoClass) {
        this.activeObjects = activeObjects;
        this.aoClass = aoClass;
        this.helperAoManager = new HelperAoManager<A>(activeObjects, aoClass);
    }

    @Override
    @Nullable
    public O getById(@Nonnull UUID id) {
        Object ao = this.getAoById(id);
        return ao != null ? (O)this.build(ao) : null;
    }

    @Override
    @Nullable
    public A getAoById(@Nonnull UUID id) {
        return (A)((PluginBackedBlueprintAo)this.activeObjects.executeInTransaction(() -> this.internalGetAoById(id)));
    }

    @Override
    @Nullable
    public O getCloneByModuleCompleteKey(@Nonnull ModuleCompleteKey moduleCompleteKey) {
        PluginBackedBlueprintAo[] ao = this.getAosByModuleCompleteKey(moduleCompleteKey, true);
        return ao.length > 0 ? (O)this.build(ao[0]) : null;
    }

    @Override
    @Nonnull
    public List<O> getNonClonesByModuleCompleteKey(@Nonnull ModuleCompleteKey moduleCompleteKey) {
        return (List)this.activeObjects.executeInTransaction(() -> {
            PluginBackedBlueprintAo[] aos;
            ArrayList result = Lists.newArrayList();
            for (PluginBackedBlueprintAo ao : aos = this.internalGetAosByModuleCompleteKey(moduleCompleteKey, false)) {
                result.add(this.build(ao));
            }
            return result;
        });
    }

    @Nonnull
    protected A[] getAosByModuleCompleteKey(@Nonnull ModuleCompleteKey moduleCompleteKey, boolean pluginClone) {
        return (PluginBackedBlueprintAo[])this.activeObjects.executeInTransaction(() -> this.internalGetAosByModuleCompleteKey(moduleCompleteKey, pluginClone));
    }

    @Override
    @Nonnull
    public List<O> getAll() {
        ArrayList result = Lists.newArrayList();
        PluginBackedBlueprintAo[] aos = (PluginBackedBlueprintAo[])this.activeObjects.find(this.aoClass);
        if (aos != null && aos.length != 0) {
            for (PluginBackedBlueprintAo ao : aos) {
                result.add(this.build(ao));
            }
        }
        return result;
    }

    @Nonnull
    protected List<O> getAll(String query, Object ... args) {
        ArrayList result = Lists.newArrayList();
        PluginBackedBlueprintAo[] aos = (PluginBackedBlueprintAo[])this.activeObjects.find(this.aoClass, query, args);
        if (aos != null && aos.length != 0) {
            for (PluginBackedBlueprintAo ao : aos) {
                result.add(this.build(ao));
            }
        }
        return result;
    }

    @Override
    @Nonnull
    public O create(@Nonnull O original) {
        return this.build(this.createAo(original));
    }

    @Override
    @Nonnull
    public A createAo(@Nonnull O original) {
        return (A)((PluginBackedBlueprintAo)this.activeObjects.executeInTransaction(() -> this.internalCreateAo(original)));
    }

    @Override
    @Nonnull
    public final O update(@Nonnull O object) {
        this.activeObjects.executeInTransaction(() -> {
            this.internalUpdateAo(object);
            return null;
        });
        return object;
    }

    @Override
    @Nonnull
    public final A updateAo(@Nonnull O object) {
        return (A)((PluginBackedBlueprintAo)this.activeObjects.executeInTransaction(() -> this.internalUpdateAo(object)));
    }

    @Override
    public final boolean delete(@Nonnull UUID id) {
        return this.helperAoManager.delete(id, this.helperCallback);
    }

    @Override
    public final void delete(@Nonnull A object) {
        this.helperAoManager.delete(object, this.helperCallback);
    }

    @Override
    public final int deleteAll() {
        return this.helperAoManager.deleteAll(this.helperCallback);
    }

    @Nonnull
    protected A[] internalGetAosByModuleCompleteKey(@Nonnull ModuleCompleteKey moduleCompleteKey, boolean pluginClone) {
        return (PluginBackedBlueprintAo[])this.activeObjects.find(this.aoClass, "PLUGIN_MODULE_KEY = ? AND PLUGIN_CLONE = ?", new Object[]{moduleCompleteKey.getCompleteKey(), pluginClone});
    }

    @Nullable
    protected A internalGetAoById(@Nonnull UUID id) {
        return this.helperAoManager.internalGetAoById(id);
    }

    @Nonnull
    protected abstract A internalCreateAo(@Nonnull O var1);

    @Nonnull
    protected abstract A internalUpdateAo(@Nonnull O var1);

    protected abstract void internalDeleteAo(@Nonnull A var1);

    protected void internalDeleteAllAo(@Nonnull A[] aos) {
        for (A ao : aos) {
            this.internalDeleteAo(ao);
        }
    }

    @Nonnull
    protected abstract O build(@Nonnull A var1);
}

