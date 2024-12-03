/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.core.bean.EntityObject
 *  com.atlassian.sal.api.user.UserKey
 *  com.atlassian.user.User
 *  com.google.common.collect.ImmutableCollection
 *  com.google.common.collect.ImmutableSet
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.security;

import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.security.ContentPermission;
import com.atlassian.core.bean.EntityObject;
import com.atlassian.sal.api.user.UserKey;
import com.atlassian.user.User;
import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableSet;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ContentPermissionSet
extends EntityObject
implements Iterable<ContentPermission>,
Serializable {
    private static Logger log = LoggerFactory.getLogger(ContentPermissionSet.class);
    private Set<ContentPermission> contentPermissions = new TreeSet<ContentPermission>();
    private ContentEntityObject owningContent;
    private String type;

    public ContentPermissionSet() {
    }

    public ContentPermissionSet(String type, ContentEntityObject owningContent) {
        this.type = type;
        this.owningContent = owningContent;
    }

    public void addContentPermission(ContentPermission contentPermission) {
        if (this.contentPermissions.add(contentPermission)) {
            contentPermission.setOwningSet(this);
        }
    }

    public void removeContentPermission(ContentPermission contentPermission) {
        if (this.contentPermissions.remove(contentPermission)) {
            contentPermission.setOwningSet(null);
        } else {
            log.debug("ContentPermission {} was not removed from set {}", (Object)contentPermission.getId(), (Object)this.getId());
        }
    }

    public boolean isPermitted(User user) {
        for (ContentPermission contentPermission : this.contentPermissions) {
            if (!contentPermission.isPermitted(user)) continue;
            return true;
        }
        return false;
    }

    private void setContentPermissions(Set<ContentPermission> contentPermissions) {
        this.contentPermissions = contentPermissions;
    }

    private Set getContentPermissions() {
        return this.contentPermissions;
    }

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isEmpty() {
        return this.contentPermissions.isEmpty();
    }

    public int size() {
        return this.contentPermissions.size();
    }

    public boolean contains(ContentPermission contentPermission) {
        return this.contentPermissions.contains(contentPermission);
    }

    public boolean containsAll(ContentPermissionSet permissionSet) {
        return this.contentPermissions.containsAll(permissionSet.contentPermissions);
    }

    public ContentEntityObject getOwningContent() {
        return this.owningContent;
    }

    public void setOwningContent(ContentEntityObject owningContent) {
        this.owningContent = owningContent;
    }

    @Override
    public Iterator<ContentPermission> iterator() {
        return Collections.unmodifiableCollection(this.contentPermissions).iterator();
    }

    public List<String> getGroupNames() {
        ArrayList<String> result = new ArrayList<String>();
        for (ContentPermission contentPermission : this.contentPermissions) {
            if (!contentPermission.isGroupPermission()) continue;
            result.add(contentPermission.getGroupName());
        }
        return result;
    }

    @Deprecated
    public List<String> getUserNames() {
        ArrayList<String> result = new ArrayList<String>();
        for (ContentPermission contentPermission : this.contentPermissions) {
            if (!contentPermission.isUserPermission()) continue;
            result.add(contentPermission.getUserName());
        }
        return result;
    }

    public List<UserKey> getUserKeys() {
        ArrayList<UserKey> result = new ArrayList<UserKey>();
        for (ContentPermission contentPermission : this.contentPermissions) {
            if (!contentPermission.isUserPermission()) continue;
            result.add(contentPermission.getUserSubject().getKey());
        }
        return result;
    }

    public Collection<ContentPermission> getAllExcept(Collection<ContentPermission> exclusions) {
        TreeSet<ContentPermission> copy = new TreeSet<ContentPermission>(this.contentPermissions);
        copy.removeAll(new TreeSet<ContentPermission>(exclusions));
        return copy;
    }

    @Deprecated
    public ImmutableCollection<ContentPermission> getContentPermissionsCopy() {
        return ImmutableSet.copyOf(this.contentPermissions);
    }

    public Collection<ContentPermission> contentPermissionsCopy() {
        return this.getContentPermissionsCopy();
    }

    public String toString() {
        return this.getClass().getName() + "@" + this.hashCode() + "[type=" + this.type + ", owningContent=" + this.owningContent + ", contentPermissions=" + this.contentPermissions + "]";
    }
}

