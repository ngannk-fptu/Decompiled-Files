/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 */
package com.atlassian.confluence.plugins.createcontent;

import com.atlassian.confluence.plugins.createcontent.AoBackedManager;
import com.atlassian.confluence.plugins.createcontent.activeobjects.SpaceBlueprintAo;
import com.atlassian.confluence.plugins.createcontent.impl.SpaceBlueprint;
import java.util.UUID;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface SpaceBlueprintManager
extends AoBackedManager<SpaceBlueprint, SpaceBlueprintAo> {
    public SpaceBlueprint create(@Nonnull SpaceBlueprint var1, @Nullable UUID var2);
}

