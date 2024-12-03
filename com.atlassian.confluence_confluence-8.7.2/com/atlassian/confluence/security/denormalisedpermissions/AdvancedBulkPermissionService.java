/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.ExperimentalApi
 *  javax.annotation.Nullable
 *  org.springframework.transaction.annotation.Transactional
 */
package com.atlassian.confluence.security.denormalisedpermissions;

import com.atlassian.annotations.ExperimentalApi;
import com.atlassian.confluence.user.ConfluenceUser;
import java.util.Set;
import javax.annotation.Nullable;
import org.springframework.transaction.annotation.Transactional;

@ExperimentalApi
@Transactional(readOnly=true)
public interface AdvancedBulkPermissionService {
    public boolean isApiUpAndRunning();

    public boolean isSpaceApiUpAndRunning();

    public boolean isContentApiUpAndRunning();

    public Set<Long> getAllUserSids(@Nullable ConfluenceUser var1);

    public boolean isUserSuperAdmin(Set<Long> var1);

    public void flushPermissionsQueue();

    @Deprecated(since="8.0", forRemoval=true)
    public String getDatabaseDialect();
}

