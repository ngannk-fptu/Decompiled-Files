/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.crowd.directory.rest.util;

import com.atlassian.crowd.directory.rest.entity.membership.DirectoryObject;
import com.atlassian.crowd.directory.rest.entity.membership.GraphMembershipGroup;
import com.atlassian.crowd.directory.rest.entity.membership.GraphMembershipUser;

public final class MembershipFilterUtil {
    private MembershipFilterUtil() {
    }

    public static boolean isGroup(DirectoryObject directoryObject) {
        return directoryObject instanceof GraphMembershipGroup;
    }

    public static boolean isUser(DirectoryObject directoryObject) {
        return directoryObject instanceof GraphMembershipUser;
    }
}

