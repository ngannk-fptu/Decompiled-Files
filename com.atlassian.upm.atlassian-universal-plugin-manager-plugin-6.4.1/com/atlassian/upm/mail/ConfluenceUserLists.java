/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.user.UserAccessor
 *  com.atlassian.sal.api.user.UserKey
 *  com.atlassian.sal.api.user.UserManager
 *  com.atlassian.user.Group
 *  com.atlassian.user.search.page.Pager
 */
package com.atlassian.upm.mail;

import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.sal.api.user.UserKey;
import com.atlassian.sal.api.user.UserManager;
import com.atlassian.upm.mail.ProductUserLists;
import com.atlassian.user.Group;
import com.atlassian.user.search.page.Pager;
import java.util.Collections;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class ConfluenceUserLists
implements ProductUserLists {
    private final UserAccessor userAccessor;
    private final UserManager userManager;

    public ConfluenceUserLists(UserAccessor userAccessor, UserManager userManager) {
        this.userAccessor = Objects.requireNonNull(userAccessor, "userAccessor");
        this.userManager = Objects.requireNonNull(userManager, "userManager");
    }

    @Override
    public Set<UserKey> getSystemAdmins() {
        Predicate<UserKey> isSystemAdmin = arg_0 -> ((UserManager)this.userManager).isSystemAdmin(arg_0);
        return Collections.unmodifiableSet(this.getConfluenceAdministrators().stream().filter(isSystemAdmin).collect(Collectors.toSet()));
    }

    @Override
    public Set<UserKey> getAdminsAndSystemAdmins() {
        return Collections.unmodifiableSet(this.getConfluenceAdministrators());
    }

    private Set<UserKey> getConfluenceAdministrators() {
        Group adminGroup = this.userAccessor.getGroup("confluence-administrators");
        if (adminGroup == null) {
            return Collections.emptySet();
        }
        Pager usernamesPager = this.userAccessor.getMemberNames(adminGroup);
        if (usernamesPager == null) {
            return Collections.emptySet();
        }
        return StreamSupport.stream(usernamesPager.spliterator(), false).map(UserKey::new).collect(Collectors.toSet());
    }
}

