/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 */
package com.atlassian.migration.agent.service.extract;

import com.atlassian.migration.agent.entity.GlobalEntityType;
import java.util.Set;
import javax.annotation.Nonnull;

public interface UserExtractionService {
    public Set<String> getUsersWithPermissionFromSpaces(@Nonnull Set<String> var1);

    public Set<String> getUsersFromGlobalEntities(@Nonnull GlobalEntityType var1);
}

