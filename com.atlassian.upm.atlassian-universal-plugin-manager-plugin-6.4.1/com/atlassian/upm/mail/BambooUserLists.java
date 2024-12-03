/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.bamboo.security.BambooPermissionManager
 *  com.atlassian.bamboo.user.BambooUserManager
 *  com.atlassian.sal.api.user.UserKey
 */
package com.atlassian.upm.mail;

import com.atlassian.bamboo.security.BambooPermissionManager;
import com.atlassian.bamboo.user.BambooUserManager;
import com.atlassian.sal.api.user.UserKey;
import com.atlassian.upm.mail.ProductUserLists;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public class BambooUserLists
implements ProductUserLists {
    private final BambooPermissionManager bambooPermissionManager;
    private final BambooUserManager bambooUserManager;

    public BambooUserLists(BambooPermissionManager bambooPermissionManager, BambooUserManager bambooUserManager) {
        this.bambooPermissionManager = Objects.requireNonNull(bambooPermissionManager, "bambooPermissionManager");
        this.bambooUserManager = Objects.requireNonNull(bambooUserManager, "bambooUserManager");
    }

    @Override
    public Set<UserKey> getSystemAdmins() {
        return this.getAdminsAndSystemAdmins();
    }

    @Override
    public Set<UserKey> getAdminsAndSystemAdmins() {
        Function<String, List> toAdminList = adminGroup -> this.bambooUserManager.getMemberNamesAsList(this.bambooUserManager.getGroup(adminGroup)).stream().map(UserKey::new).collect(Collectors.toList());
        return Collections.unmodifiableSet(this.bambooPermissionManager.getAdminGroups().stream().map(toAdminList).flatMap(Collection::stream).collect(Collectors.toSet()));
    }
}

