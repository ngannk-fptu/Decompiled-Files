/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  java.security.acl.Group
 */
package com.opensymphony.user.adapter.weblogic;

import com.opensymphony.user.EntityNotFoundException;
import com.opensymphony.user.Group;
import com.opensymphony.user.UserManager;
import com.opensymphony.user.adapter.weblogic.CollectionEnum;
import java.security.Principal;
import java.util.Enumeration;

public class AclGroupAdapter
implements java.security.acl.Group {
    private Group osGroup;
    private UserManager userManager = UserManager.getInstance();

    public AclGroupAdapter(Group osGroup) {
        this.osGroup = osGroup;
    }

    public boolean isMember(Principal member) {
        return this.osGroup.containsUser(member.getName());
    }

    public String getName() {
        return this.osGroup.getName();
    }

    public boolean addMember(Principal user) {
        try {
            return this.osGroup.addUser(this.userManager.getUser(user.getName()));
        }
        catch (EntityNotFoundException enfe) {
            return false;
        }
    }

    public Enumeration members() {
        return new CollectionEnum(this.osGroup.getUsers());
    }

    public boolean removeMember(Principal user) {
        try {
            return this.osGroup.removeUser(this.userManager.getUser(user.getName()));
        }
        catch (EntityNotFoundException enfe) {
            return false;
        }
    }
}

