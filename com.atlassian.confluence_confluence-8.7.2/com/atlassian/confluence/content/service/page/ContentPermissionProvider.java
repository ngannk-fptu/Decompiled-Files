/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.content.service.page;

import com.atlassian.confluence.security.ContentPermission;
import java.util.Collection;

public interface ContentPermissionProvider {
    public Collection<ContentPermission> getViewPermissions();

    public Collection<ContentPermission> getEditPermissions();
}

