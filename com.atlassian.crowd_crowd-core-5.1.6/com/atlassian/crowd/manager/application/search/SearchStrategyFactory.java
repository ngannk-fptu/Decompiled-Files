/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.embedded.api.Directory
 */
package com.atlassian.crowd.manager.application.search;

import com.atlassian.crowd.embedded.api.Directory;
import com.atlassian.crowd.manager.application.canonicality.CanonicalityChecker;
import com.atlassian.crowd.manager.application.filtering.AccessFilter;
import com.atlassian.crowd.manager.application.search.GroupSearchStrategy;
import com.atlassian.crowd.manager.application.search.MembershipSearchStrategy;
import com.atlassian.crowd.manager.application.search.UserSearchStrategy;
import java.util.List;

public interface SearchStrategyFactory {
    public MembershipSearchStrategy createMembershipSearchStrategy(boolean var1, List<Directory> var2, CanonicalityChecker var3, AccessFilter var4);

    public UserSearchStrategy createUserSearchStrategy(boolean var1, List<Directory> var2, AccessFilter var3);

    public GroupSearchStrategy createGroupSearchStrategy(boolean var1, List<Directory> var2, AccessFilter var3);
}

