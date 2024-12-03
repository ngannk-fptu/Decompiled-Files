/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.user.User
 */
package com.atlassian.confluence.security.delegate;

import com.atlassian.confluence.pages.templates.PageTemplate;
import com.atlassian.confluence.security.delegate.AbstractPermissionsDelegate;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.spaces.Spaced;
import com.atlassian.user.User;

public class PageTemplatePermissionsDelegate
extends AbstractPermissionsDelegate<PageTemplate> {
    @Override
    public boolean canView(User user, PageTemplate target) {
        return this.hasSpaceLevelPermission("VIEWSPACE", user, target);
    }

    @Override
    public boolean canEdit(User user, PageTemplate target) {
        return this.hasSpaceLevelPermission("SETSPACEPERMISSIONS", user, target);
    }

    @Override
    public boolean canSetPermissions(User user, PageTemplate target) {
        return this.hasSpaceLevelPermission("SETSPACEPERMISSIONS", user, target);
    }

    @Override
    public boolean canRemove(User user, PageTemplate target) {
        return this.hasSpaceLevelPermission("SETSPACEPERMISSIONS", user, target);
    }

    @Override
    public boolean canExport(User user, PageTemplate target) {
        return this.hasSpaceLevelPermission("EXPORTSPACE", user, target);
    }

    @Override
    public boolean canAdminister(User user, PageTemplate target) {
        return this.hasSpaceLevelPermission("SETSPACEPERMISSIONS", user, target);
    }

    @Override
    public boolean canCreate(User user, Object container) {
        return this.hasSpaceLevelPermission("SETSPACEPERMISSIONS", user, container);
    }

    @Override
    protected Space getSpaceFrom(Object target) {
        if (target instanceof PageTemplate) {
            return ((PageTemplate)target).getSpace();
        }
        if (target instanceof Space) {
            return (Space)target;
        }
        if (target instanceof Spaced) {
            return ((Spaced)target).getSpace();
        }
        throw new IllegalArgumentException("Unsupported target for page template permission check: " + target.getClass());
    }
}

