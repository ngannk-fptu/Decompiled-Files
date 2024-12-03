/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.MoreObjects
 */
package com.atlassian.crowd.model.permission;

import com.atlassian.crowd.model.group.InternalGroup;
import com.atlassian.crowd.model.user.InternalUser;
import com.google.common.base.MoreObjects;
import java.util.Objects;

public class UserAdministrationGrantToGroup {
    private Long id;
    private InternalUser user;
    private InternalGroup targetGroup;

    public UserAdministrationGrantToGroup() {
    }

    public UserAdministrationGrantToGroup(InternalUser user, InternalGroup targetGroup) {
        this.user = user;
        this.targetGroup = targetGroup;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public InternalUser getUser() {
        return this.user;
    }

    public void setUser(InternalUser user) {
        this.user = user;
    }

    public InternalGroup getTargetGroup() {
        return this.targetGroup;
    }

    public void setTargetGroup(InternalGroup targetGroup) {
        this.targetGroup = targetGroup;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        UserAdministrationGrantToGroup that = (UserAdministrationGrantToGroup)o;
        return Objects.equals(this.id, that.id) && Objects.equals((Object)this.user, (Object)that.user) && Objects.equals((Object)this.targetGroup, (Object)that.targetGroup);
    }

    public int hashCode() {
        return Objects.hash(new Object[]{this.id, this.user, this.targetGroup});
    }

    public String toString() {
        return MoreObjects.toStringHelper((Object)this).add("id", (Object)this.id).add("user", (Object)this.user).add("targetGroup", (Object)this.targetGroup).toString();
    }
}

