/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  java.security.acl.Group
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 *  weblogic.security.acl.FlatGroup
 *  weblogic.security.acl.FlatGroup$Source
 */
package com.opensymphony.user.adapter.weblogic61;

import com.opensymphony.user.EntityNotFoundException;
import com.opensymphony.user.Group;
import com.opensymphony.user.UserManager;
import com.opensymphony.user.adapter.weblogic61.OSUserRealm;
import java.security.Principal;
import java.util.Enumeration;
import java.util.Iterator;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import weblogic.security.acl.FlatGroup;

public class OSUserRealmGroup
extends FlatGroup
implements java.security.acl.Group {
    private static final Log log = LogFactory.getLog((Class)(class$com$opensymphony$user$adapter$weblogic61$OSUserRealmGroup == null ? (class$com$opensymphony$user$adapter$weblogic61$OSUserRealmGroup = OSUserRealmGroup.class$("com.opensymphony.user.adapter.weblogic61.OSUserRealmGroup")) : class$com$opensymphony$user$adapter$weblogic61$OSUserRealmGroup));
    private Group osGroup;
    private OSUserRealm wlRealm;
    static /* synthetic */ Class class$com$opensymphony$user$adapter$weblogic61$OSUserRealmGroup;
    static /* synthetic */ Class class$com$opensymphony$user$adapter$weblogic61$OSUserRealmUser;

    public OSUserRealmGroup(Group osGroup, OSUserRealm wlRealm) {
        super(osGroup.getName(), (FlatGroup.Source)wlRealm);
        this.osGroup = osGroup;
        this.wlRealm = wlRealm;
    }

    public boolean addMember(Principal user) {
        log.info((Object)("Starting OSUserRealmGroup::addMember(" + user + ")"));
        UserManager um = null;
        try {
            um = this.osGroup.getUserManager();
            if (this.osGroup.addUser(um.getUser(user.getName()))) {
                super.addMemberInternal(user);
                return true;
            }
            return false;
        }
        catch (EntityNotFoundException enfe) {
            return false;
        }
    }

    public boolean removeMember(Principal user) {
        log.info((Object)("Starting OSUserRealmGroup::removeMember(" + user + ")"));
        UserManager um = null;
        try {
            um = this.osGroup.getUserManager();
            if (this.osGroup.removeUser(um.getUser(user.getName()))) {
                super.removeMemberInternal(user);
                return true;
            }
            return false;
        }
        catch (EntityNotFoundException enfe) {
            return false;
        }
    }

    protected Class getUserClass() {
        return class$com$opensymphony$user$adapter$weblogic61$OSUserRealmUser == null ? (class$com$opensymphony$user$adapter$weblogic61$OSUserRealmUser = OSUserRealmGroup.class$("com.opensymphony.user.adapter.weblogic61.OSUserRealmUser")) : class$com$opensymphony$user$adapter$weblogic61$OSUserRealmUser;
    }

    static /* synthetic */ Class class$(String x0) {
        try {
            return Class.forName(x0);
        }
        catch (ClassNotFoundException x1) {
            throw new NoClassDefFoundError(x1.getMessage());
        }
    }

    static class GroupEnum
    implements Enumeration {
        Iterator itr;
        OSUserRealm realm;
        private boolean everyoneRetreived = false;

        GroupEnum(OSUserRealm realm) {
            this.realm = realm;
            this.itr = realm.getOSGroups().iterator();
        }

        public boolean hasMoreElements() {
            return !this.everyoneRetreived || this.itr.hasNext();
        }

        public Object nextElement() {
            if (!this.everyoneRetreived) {
                this.everyoneRetreived = true;
                return this.realm.everyoneGroup;
            }
            return new OSUserRealmGroup((Group)this.itr.next(), this.realm);
        }
    }
}

