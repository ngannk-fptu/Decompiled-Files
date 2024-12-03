/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.spaces.Space
 *  com.atlassian.plugin.ModuleCompleteKey
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 */
package com.atlassian.confluence.plugins.createcontent;

import com.atlassian.confluence.plugins.createcontent.AoBackedManager;
import com.atlassian.confluence.plugins.createcontent.activeobjects.ContentBlueprintAo;
import com.atlassian.confluence.plugins.createcontent.impl.ContentBlueprint;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.plugin.ModuleCompleteKey;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface ContentBlueprintManager
extends AoBackedManager<ContentBlueprint, ContentBlueprintAo> {
    @Nullable
    public ContentBlueprint getPluginBlueprint(ModuleCompleteKey var1);

    @Nonnull
    public List<ContentBlueprint> getAll(Space var1);

    @Nonnull
    public List<ContentBlueprint> getAllBySpaceKey(String var1);

    @Nullable
    public ContentBlueprint getPluginBackedContentBlueprint(ModuleCompleteKey var1, String var2);

    public ContentBlueprint getOrCreateCustomBlueprint(ModuleCompleteKey var1, Space var2);
}

