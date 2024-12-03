/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.ModuleCompleteKey
 *  com.google.common.base.MoreObjects
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.atlassian.confluence.pages.templates;

import com.atlassian.confluence.spaces.Space;
import com.atlassian.plugin.ModuleCompleteKey;
import com.google.common.base.MoreObjects;
import java.util.Objects;
import org.checkerframework.checker.nullness.qual.Nullable;

public class PluginTemplateReference {
    private final ModuleCompleteKey moduleCompleteKey;
    private final @Nullable ModuleCompleteKey referencingModuleCompleteKey;
    private final @Nullable Space space;

    public static PluginTemplateReference spaceTemplateReference(ModuleCompleteKey moduleCompleteKey, ModuleCompleteKey referencingModuleCompleteKey, Space space) {
        return new PluginTemplateReference(moduleCompleteKey, referencingModuleCompleteKey, space);
    }

    public static PluginTemplateReference globalTemplateReference(ModuleCompleteKey moduleCompleteKey, ModuleCompleteKey referencingModuleCompleteKey) {
        return new PluginTemplateReference(moduleCompleteKey, referencingModuleCompleteKey, null);
    }

    public static PluginTemplateReference systemTemplateReference(ModuleCompleteKey moduleCompleteKey) {
        return new PluginTemplateReference(moduleCompleteKey, null, null);
    }

    private PluginTemplateReference(ModuleCompleteKey moduleCompleteKey, ModuleCompleteKey referencingModuleCompleteKey, Space space) {
        this.moduleCompleteKey = moduleCompleteKey;
        this.referencingModuleCompleteKey = referencingModuleCompleteKey;
        this.space = space;
    }

    public ModuleCompleteKey getModuleCompleteKey() {
        return this.moduleCompleteKey;
    }

    public ModuleCompleteKey getReferencingModuleCompleteKey() {
        return this.referencingModuleCompleteKey;
    }

    public Space getSpace() {
        return this.space;
    }

    public int hashCode() {
        return Objects.hash(this.moduleCompleteKey, this.referencingModuleCompleteKey, this.space);
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof PluginTemplateReference)) {
            return false;
        }
        PluginTemplateReference that = (PluginTemplateReference)obj;
        return Objects.equals(this.moduleCompleteKey, that.moduleCompleteKey) && Objects.equals(this.referencingModuleCompleteKey, that.referencingModuleCompleteKey) && Objects.equals(this.space, that.space);
    }

    public String toString() {
        return MoreObjects.toStringHelper((Object)this).add("Module Complete Key", (Object)this.moduleCompleteKey).add("Referencing Module Complete Key", (Object)this.referencingModuleCompleteKey).add("Space", (Object)this.space).toString();
    }
}

