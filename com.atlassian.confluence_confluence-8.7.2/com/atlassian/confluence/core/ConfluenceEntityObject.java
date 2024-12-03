/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.impl.hibernate.Hibernate
 *  com.atlassian.core.bean.EntityObject
 */
package com.atlassian.confluence.core;

import com.atlassian.confluence.impl.hibernate.Hibernate;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.persistence.dao.compatibility.FindUserHelper;
import com.atlassian.core.bean.EntityObject;
import java.io.Serializable;

public class ConfluenceEntityObject
extends EntityObject
implements Serializable {
    private ConfluenceUser creator;
    private ConfluenceUser lastModifier;

    public String getCreatorName() {
        if (this.creator != null) {
            return this.creator.getName();
        }
        return null;
    }

    public ConfluenceUser getCreator() {
        return this.creator;
    }

    @Deprecated
    public void setCreatorName(String creatorName) {
        this.creator = ConfluenceEntityObject.resolveUser(creatorName);
    }

    private static ConfluenceUser resolveUser(String username) {
        if (username == null) {
            return null;
        }
        ConfluenceUser user = FindUserHelper.getUserByUsername(username);
        if (user == null) {
            throw new IllegalArgumentException("No such user with username [" + username + "]");
        }
        return user;
    }

    public String getLastModifierName() {
        if (this.lastModifier != null) {
            return this.lastModifier.getName();
        }
        return null;
    }

    public ConfluenceUser getLastModifier() {
        return this.lastModifier;
    }

    @Deprecated
    public void setLastModifierName(String lastModifierName) {
        this.lastModifier = ConfluenceEntityObject.resolveUser(lastModifierName);
    }

    public void setCreator(ConfluenceUser creator) {
        this.creator = creator;
    }

    public void setLastModifier(ConfluenceUser lastModifier) {
        this.lastModifier = lastModifier;
    }

    public boolean isPersistent() {
        return this.getId() != 0L;
    }

    public static Class getRealClass(Object ceo) {
        return Hibernate.getClass((Object)ceo);
    }
}

