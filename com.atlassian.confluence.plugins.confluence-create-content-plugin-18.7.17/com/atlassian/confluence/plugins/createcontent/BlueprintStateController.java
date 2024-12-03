/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.spaces.Space
 *  com.atlassian.confluence.user.ConfluenceUser
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 */
package com.atlassian.confluence.plugins.createcontent;

import com.atlassian.confluence.plugins.createcontent.model.BlueprintState;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.user.ConfluenceUser;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface BlueprintStateController {
    public void enableBlueprint(UUID var1, Space var2);

    public void disableBlueprint(UUID var1, Space var2);

    public void disableBlueprints(Set<UUID> var1, Space var2);

    public Set<UUID> getDisabledBlueprintIds(Space var1);

    public Set<String> getDisabledBlueprintModuleCompleteKeys(Space var1);

    public Map<UUID, BlueprintState> getAllContentBlueprintState(@Nonnull String var1, @Nullable ConfluenceUser var2, @Nullable Space var3);
}

