/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.embedded.api.CrowdService
 *  com.atlassian.spring.container.LazyComponentReference
 *  com.atlassian.user.User
 *  com.google.common.base.Supplier
 *  org.apache.commons.lang3.StringUtils
 *  org.apache.commons.lang3.builder.CompareToBuilder
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.security;

import com.atlassian.confluence.core.ConfluenceEntityObject;
import com.atlassian.confluence.security.ContentPermissionSet;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.persistence.dao.compatibility.FindUserHelper;
import com.atlassian.crowd.embedded.api.CrowdService;
import com.atlassian.spring.container.LazyComponentReference;
import com.atlassian.user.User;
import com.google.common.base.Supplier;
import java.io.ObjectInputStream;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.CompareToBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ContentPermission
extends ConfluenceEntityObject
implements Comparable<ContentPermission> {
    private static final Logger log = LoggerFactory.getLogger(ContentPermission.class);
    public static final String VIEW_PERMISSION = "View";
    public static final String EDIT_PERMISSION = "Edit";
    public static final String SHARED_PERMISSION = "Share";
    private static final long serialVersionUID = -7934777632484866795L;
    private String type;
    private ConfluenceUser userSubject;
    private String groupName;
    private ContentPermissionSet owningSet;
    private transient Supplier<CrowdService> crowdService = ContentPermission.makeCrowdServiceRef();

    public ContentPermission() {
    }

    public ContentPermission(ContentPermission other) {
        this.type = other.type;
        this.userSubject = other.userSubject;
        this.groupName = other.groupName;
        this.crowdService = other.crowdService;
    }

    @Deprecated
    public static ContentPermission createUserPermission(String type, String userName) {
        if (StringUtils.isEmpty((CharSequence)type)) {
            throw new IllegalArgumentException("Type is required.");
        }
        if (StringUtils.isEmpty((CharSequence)userName)) {
            throw new IllegalArgumentException("Username is required.");
        }
        ContentPermission permission = new ContentPermission(type, null, userName);
        permission.setCreator(AuthenticatedUserThreadLocal.get());
        return permission;
    }

    public static ContentPermission createUserPermission(String type, ConfluenceUser user) {
        if (StringUtils.isEmpty((CharSequence)type)) {
            throw new IllegalArgumentException("Type is required.");
        }
        if (user == null) {
            throw new IllegalArgumentException("user is required.");
        }
        ContentPermission permission = new ContentPermission(type, null, user);
        permission.setCreator(AuthenticatedUserThreadLocal.get());
        return permission;
    }

    public static ContentPermission createGroupPermission(String type, String groupName) {
        if (StringUtils.isEmpty((CharSequence)type)) {
            throw new IllegalArgumentException("Type is required.");
        }
        if (StringUtils.isEmpty((CharSequence)groupName)) {
            throw new IllegalArgumentException("Groupname is required.");
        }
        ContentPermission permission = new ContentPermission(type, groupName, (ConfluenceUser)null);
        permission.setCreator(AuthenticatedUserThreadLocal.get());
        return permission;
    }

    protected ContentPermission(String type, String groupName, String userName) {
        this.type = type;
        this.groupName = groupName;
        this.userSubject = FindUserHelper.getUserByUsername(userName);
        if (StringUtils.isNotEmpty((CharSequence)userName) && this.userSubject == null) {
            throw new IllegalArgumentException("No user could be found for the username " + userName);
        }
    }

    protected ContentPermission(String type, String groupName, ConfluenceUser user) {
        this.type = type;
        this.groupName = groupName;
        this.userSubject = user;
    }

    public boolean isPermitted(User user) {
        ConfluenceUser confluenceUser = FindUserHelper.getUser(user);
        if (confluenceUser == null) {
            return false;
        }
        if (!this.isValid()) {
            return false;
        }
        if (this.isUserPermission()) {
            return this.userSubject.getKey().equals((Object)confluenceUser.getKey());
        }
        return this.getCrowdService().isUserMemberOfGroup(confluenceUser.getName(), this.groupName);
    }

    private boolean isValid() {
        if (StringUtils.isEmpty((CharSequence)this.type)) {
            log.error("Corrupt content permission found with null type.");
            return false;
        }
        if (this.userSubject == null && StringUtils.isEmpty((CharSequence)this.groupName)) {
            log.error("Corrupt content permission found with null username and groupname.");
            return false;
        }
        return true;
    }

    public boolean isGroupPermission() {
        return this.getGroupName() != null;
    }

    public boolean isUserPermission() {
        return this.userSubject != null;
    }

    public String getType() {
        return this.type;
    }

    @Deprecated
    public String getUserName() {
        if (this.userSubject != null) {
            return this.userSubject.getName();
        }
        return null;
    }

    public void setSubject(ConfluenceUser subject) {
        this.userSubject = subject;
    }

    private CrowdService getCrowdService() {
        return (CrowdService)this.crowdService.get();
    }

    private void readObject(ObjectInputStream in) {
        this.crowdService = ContentPermission.makeCrowdServiceRef();
    }

    private static Supplier<CrowdService> makeCrowdServiceRef() {
        return new LazyComponentReference("crowdService");
    }

    public ConfluenceUser getUserSubject() {
        return this.userSubject;
    }

    public String getGroupName() {
        return this.groupName;
    }

    private void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public ContentPermissionSet getOwningSet() {
        return this.owningSet;
    }

    public void setOwningSet(ContentPermissionSet owningSet) {
        this.owningSet = owningSet;
    }

    @Override
    public int compareTo(ContentPermission that) {
        CompareToBuilder builder = new CompareToBuilder();
        builder.append((Object)this.type, (Object)that.type);
        String thisUserName = this.userSubject != null ? this.userSubject.getName() : null;
        String thatUserName = that.userSubject != null ? that.userSubject.getName() : null;
        builder.append((Object)thisUserName, (Object)thatUserName);
        builder.append((Object)this.groupName, (Object)that.groupName);
        return builder.toComparison();
    }

    public String toString() {
        return this.getClass().getName() + "@" + this.hashCode() + "[type=" + this.type + ", user=" + this.userSubject + ", groupName=" + this.groupName + "]";
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        ContentPermission that = (ContentPermission)o;
        if (this.type != null ? !this.type.equals(that.type) : that.type != null) {
            return false;
        }
        if (this.userSubject != null ? !this.userSubject.equals(that.userSubject) : that.userSubject != null) {
            return false;
        }
        return !(this.groupName != null ? !this.groupName.equals(that.groupName) : that.groupName != null);
    }

    public int hashCode() {
        int result = 0;
        result = 31 * result + (this.type != null ? this.type.hashCode() : 0);
        result = 31 * result + (this.userSubject != null ? this.userSubject.hashCode() : 0);
        result = 31 * result + (this.groupName != null ? this.groupName.hashCode() : 0);
        return result;
    }
}

