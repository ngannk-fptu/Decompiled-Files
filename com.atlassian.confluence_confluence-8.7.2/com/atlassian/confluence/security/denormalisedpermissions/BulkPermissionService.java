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
import com.atlassian.confluence.security.denormalisedpermissions.impl.content.domain.SimpleContent;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.spaces.SpacesQuery;
import com.atlassian.confluence.user.ConfluenceUser;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nullable;
import org.springframework.transaction.annotation.Transactional;

@ExperimentalApi
@Transactional(readOnly=true)
public interface BulkPermissionService {
    public Map<String, Boolean> getAllSpaceKeysWithPermissionStatuses(@Nullable ConfluenceUser var1, String var2);

    public List<Space> getPermittedSpaces(SpacesQuery var1, int var2, int var3);

    public Set<Long> getPermittedSpaceIds(@Nullable ConfluenceUser var1, Set<Long> var2, String var3);

    public Map<Long, List<SimpleContent>> getVisibleChildPages(@Nullable ConfluenceUser var1, Set<Long> var2, boolean var3);

    public List<SimpleContent> getVisibleTopLevelPages(@Nullable ConfluenceUser var1, long var2);

    public List<SimpleContent> getAllVisiblePagesInSpace(@Nullable ConfluenceUser var1, long var2);

    public Set<Long> getVisiblePageIds(@Nullable ConfluenceUser var1, Set<Long> var2, boolean var3);
}

