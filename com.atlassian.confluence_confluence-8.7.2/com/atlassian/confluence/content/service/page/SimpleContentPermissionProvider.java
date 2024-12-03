/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.content.service.page;

import com.atlassian.confluence.content.service.page.ContentPermissionProvider;
import com.atlassian.confluence.security.ContentPermission;
import java.util.Collection;

public class SimpleContentPermissionProvider
implements ContentPermissionProvider {
    private Collection<ContentPermission> viewPermissions;
    private Collection<ContentPermission> editPermissions;

    @Override
    public Collection<ContentPermission> getViewPermissions() {
        return this.viewPermissions;
    }

    public void setViewPermissions(Collection<ContentPermission> viewPermissions) {
        this.viewPermissions = viewPermissions;
    }

    @Override
    public Collection<ContentPermission> getEditPermissions() {
        return this.editPermissions;
    }

    public void setEditPermissions(Collection<ContentPermission> editPermissions) {
        this.editPermissions = editPermissions;
    }
}

