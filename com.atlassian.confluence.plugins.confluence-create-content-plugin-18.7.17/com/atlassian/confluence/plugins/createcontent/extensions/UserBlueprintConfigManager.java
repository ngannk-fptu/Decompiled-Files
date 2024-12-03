/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.user.ConfluenceUser
 */
package com.atlassian.confluence.plugins.createcontent.extensions;

import com.atlassian.confluence.user.ConfluenceUser;
import java.util.Set;
import java.util.UUID;

public interface UserBlueprintConfigManager {
    public Set<UUID> getSkipHowToUseKeys(ConfluenceUser var1);

    public void setSkipHowToUse(ConfluenceUser var1, UUID var2, boolean var3);

    public boolean isFirstBlueprintOfTypeForUser(UUID var1, ConfluenceUser var2);

    public void setBlueprintCreatedByUser(UUID var1, ConfluenceUser var2);
}

