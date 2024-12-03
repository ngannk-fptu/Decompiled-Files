/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.activeobjects.external.ActiveObjects
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 *  net.java.ao.DBParam
 *  net.java.ao.RawEntity
 */
package com.atlassian.confluence.plugins.createcontent.impl;

import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.confluence.plugins.createcontent.activeobjects.PluginBackedBlueprintAo;
import java.util.UUID;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.java.ao.DBParam;
import net.java.ao.RawEntity;

public class HelperAoManager<A extends PluginBackedBlueprintAo> {
    private final ActiveObjects activeObjects;
    private final Class<A> aoClass;

    public HelperAoManager(@Nonnull ActiveObjects activeObjects, @Nonnull Class<A> aoClass) {
        this.activeObjects = activeObjects;
        this.aoClass = aoClass;
    }

    @Nonnull
    public A createWithUuid() {
        PluginBackedBlueprintAo newBp = (PluginBackedBlueprintAo)this.activeObjects.create(this.aoClass, new DBParam[0]);
        newBp.setUuid(UUID.randomUUID().toString());
        return (A)newBp;
    }

    public boolean delete(@Nonnull UUID id, @Nonnull HelperCallback<A> callback) {
        return (Boolean)this.activeObjects.executeInTransaction(() -> {
            A ao = this.internalGetAoById(id);
            if (ao != null) {
                callback.onDelete(ao);
                this.activeObjects.delete(new RawEntity[]{ao});
            }
            return ao != null;
        });
    }

    public void delete(@Nonnull A object, @Nonnull HelperCallback<A> callback) {
        this.activeObjects.executeInTransaction(() -> {
            callback.onDelete(object);
            this.activeObjects.delete(new RawEntity[]{object});
            return null;
        });
    }

    public int deleteAll(@Nonnull HelperCallback<A> callback) {
        return (Integer)this.activeObjects.executeInTransaction(() -> {
            PluginBackedBlueprintAo[] results = (PluginBackedBlueprintAo[])this.activeObjects.find(this.aoClass);
            callback.onDeleteAll(results);
            this.activeObjects.delete((RawEntity[])results);
            return results.length;
        });
    }

    @Nullable
    public A internalGetAoById(@Nonnull UUID id) {
        PluginBackedBlueprintAo[] aos = (PluginBackedBlueprintAo[])this.activeObjects.find(this.aoClass, "UUID = ?", new Object[]{id.toString()});
        if (aos != null && aos.length > 1) {
            throw new RuntimeException("More than one object with the same UUID! (" + id.toString() + ")");
        }
        return (A)(aos != null && aos.length > 0 ? aos[0] : null);
    }

    static interface HelperCallback<A extends PluginBackedBlueprintAo> {
        public void onDelete(A var1);

        public void onDeleteAll(A[] var1);
    }
}

