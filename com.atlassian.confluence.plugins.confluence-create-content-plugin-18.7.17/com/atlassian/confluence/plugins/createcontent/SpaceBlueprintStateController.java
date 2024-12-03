/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.user.ConfluenceUser
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 */
package com.atlassian.confluence.plugins.createcontent;

import com.atlassian.confluence.plugins.createcontent.model.BlueprintState;
import com.atlassian.confluence.user.ConfluenceUser;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface SpaceBlueprintStateController {
    public void enableSpaceBlueprint(UUID var1);

    public void disableSpaceBlueprint(UUID var1);

    public void disableSpaceBlueprints(Set<UUID> var1);

    public Set<UUID> getDisabledSpaceBlueprintIds();

    public Set<String> getDisabledSpaceBlueprintModuleCompleteKeys();

    public Map<UUID, BlueprintState> getAllSpaceBlueprintState(@Nonnull String var1, @Nullable ConfluenceUser var2);
}

