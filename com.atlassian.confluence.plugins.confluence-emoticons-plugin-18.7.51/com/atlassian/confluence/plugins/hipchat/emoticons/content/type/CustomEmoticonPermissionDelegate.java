/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.security.PermissionDelegate
 *  com.atlassian.user.User
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.plugins.hipchat.emoticons.content.type;

import com.atlassian.confluence.security.PermissionDelegate;
import com.atlassian.user.User;
import org.springframework.stereotype.Component;

@Component
public class CustomEmoticonPermissionDelegate
implements PermissionDelegate {
    public boolean canView(User user, Object o) {
        return true;
    }

    public boolean canView(User user) {
        return false;
    }

    public boolean canEdit(User user, Object o) {
        return false;
    }

    public boolean canSetPermissions(User user, Object o) {
        return false;
    }

    public boolean canRemove(User user, Object o) {
        return false;
    }

    public boolean canExport(User user, Object o) {
        return true;
    }

    public boolean canAdminister(User user, Object o) {
        return false;
    }

    public boolean canCreate(User user, Object o) {
        return true;
    }

    public boolean canCreateInTarget(User user, Class aClass) {
        return true;
    }
}

