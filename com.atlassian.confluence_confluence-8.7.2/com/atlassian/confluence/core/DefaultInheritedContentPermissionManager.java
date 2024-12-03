/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.core;

import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.core.InheritedContentPermissionManager;
import com.atlassian.confluence.pages.Page;
import com.atlassian.confluence.security.ContentPermissionSet;
import com.atlassian.confluence.security.persistence.dao.ContentPermissionSetDao;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DefaultInheritedContentPermissionManager
implements InheritedContentPermissionManager {
    private ContentPermissionSetDao contentPermissionSetDao;

    @Override
    public List<ContentPermissionSet> getInheritedContentPermissionSets(ContentEntityObject contentEntityObject) {
        if (this.cannotHaveInheritedPermissions(contentEntityObject)) {
            return Collections.emptyList();
        }
        Page page = (Page)contentEntityObject;
        return this.getInheritedContentPermissionSets(page, "View");
    }

    @Override
    public List<ContentPermissionSet> getInheritedContentPermissionSetsIncludeEdit(ContentEntityObject contentEntityObject) {
        if (this.cannotHaveInheritedPermissions(contentEntityObject)) {
            return Collections.emptyList();
        }
        Page page = (Page)contentEntityObject;
        ArrayList<ContentPermissionSet> permissionSets = new ArrayList<ContentPermissionSet>();
        permissionSets.addAll(this.getInheritedContentPermissionSets(page, "View"));
        permissionSets.addAll(this.getInheritedContentPermissionSets(page, "Edit"));
        return Collections.unmodifiableList(permissionSets);
    }

    private List<ContentPermissionSet> getInheritedContentPermissionSets(Page page, String permissionType) {
        List inheritedContentPermissionSets = this.contentPermissionSetDao.getInheritedContentPermissionSets(page, permissionType);
        return Collections.unmodifiableList(inheritedContentPermissionSets);
    }

    protected boolean cannotHaveInheritedPermissions(ContentEntityObject contentEntityObject) {
        if (contentEntityObject instanceof Page) {
            Page page = (Page)contentEntityObject;
            return page.isRootLevel();
        }
        return true;
    }

    public void setContentPermissionSetDao(ContentPermissionSetDao contentPermissionSetDao) {
        this.contentPermissionSetDao = contentPermissionSetDao;
    }
}

