/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.core.persistence.hibernate;

import com.atlassian.confluence.core.persistence.hibernate.VersionedHibernateObjectDao;
import com.atlassian.confluence.internal.persistence.ContentPermissionDaoInternal;
import com.atlassian.confluence.security.ContentPermission;
import com.atlassian.confluence.user.ConfluenceUser;
import java.util.List;

public class ContentPermissionHibernateDao
extends VersionedHibernateObjectDao<ContentPermission>
implements ContentPermissionDaoInternal {
    @Override
    public ContentPermission getById(long id) {
        return (ContentPermission)super.getByClassId(id);
    }

    @Override
    public List<ContentPermission> getGroupPermissions(String groupName) {
        return super.findNamedQueryStringParam("confluence.contentpermission_findGroupPermissions", "groupname", groupName);
    }

    @Override
    public List<ContentPermission> getUserPermissions(ConfluenceUser user) {
        return super.findNamedQueryStringParam("confluence.contentpermission_findUserPermissions", "user", user);
    }

    @Override
    public Class getPersistentClass() {
        return ContentPermission.class;
    }
}

