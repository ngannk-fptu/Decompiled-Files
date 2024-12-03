/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.spaces.Space
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 */
package com.atlassian.confluence.plugins.createcontent.services;

import com.atlassian.confluence.plugins.createcontent.api.exceptions.BlueprintIllegalArgumentException;
import com.atlassian.confluence.plugins.createcontent.impl.ContentBlueprint;
import com.atlassian.confluence.spaces.Space;
import java.util.Collection;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface PromotedBlueprintService {
    @Nonnull
    public Collection<ContentBlueprint> getPromotedBlueprints(@Nonnull Collection<ContentBlueprint> var1, @Nullable Space var2);

    public boolean promoteBlueprint(@Nonnull String var1, @Nonnull String var2) throws BlueprintIllegalArgumentException;

    public boolean demoteBlueprint(@Nonnull String var1, @Nonnull String var2) throws BlueprintIllegalArgumentException;

    public void promoteBlueprints(@Nonnull List<String> var1, @Nonnull Space var2);
}

