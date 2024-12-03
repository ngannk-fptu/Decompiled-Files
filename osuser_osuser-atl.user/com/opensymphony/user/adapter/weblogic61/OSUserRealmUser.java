/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  weblogic.security.acl.BasicRealm
 *  weblogic.security.acl.User
 */
package com.opensymphony.user.adapter.weblogic61;

import com.opensymphony.user.User;
import com.opensymphony.user.adapter.weblogic61.OSUserRealm;
import java.util.Enumeration;
import java.util.Iterator;
import weblogic.security.acl.BasicRealm;

public class OSUserRealmUser
extends weblogic.security.acl.User {
    private static final String WEBLOGIC_INFO = "weblogic.info";
    protected OSUserRealm realm;
    protected User osUser;

    public OSUserRealmUser(User osUser, OSUserRealm realm) {
        super(osUser.getName());
        this.osUser = osUser;
        this.realm = realm;
    }

    public BasicRealm getRealm() {
        return this.realm;
    }

    static class UserEnum
    implements Enumeration {
        Iterator itr;
        OSUserRealm realm;

        UserEnum(OSUserRealm realm) {
            this.realm = realm;
            this.itr = realm.getOSUsers().iterator();
        }

        public boolean hasMoreElements() {
            return this.itr.hasNext();
        }

        public Object nextElement() {
            return new OSUserRealmUser((User)this.itr.next(), this.realm);
        }
    }
}

