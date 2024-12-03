/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  weblogic.security.acl.BasicRealm
 *  weblogic.security.acl.User
 */
package com.opensymphony.user.adapter.weblogic;

import com.opensymphony.user.User;
import weblogic.security.acl.BasicRealm;

public class WeblogicUserAdapter
extends weblogic.security.acl.User {
    private static final String WEBLOGIC_INFO = "weblogic.info";
    protected BasicRealm realm;
    protected User osUser;

    public WeblogicUserAdapter(User osUser, BasicRealm realm) {
        super(osUser.getName());
        this.osUser = osUser;
        this.realm = realm;
    }

    public void setInfo(String info) {
        this.osUser.getPropertySet().setString(WEBLOGIC_INFO, info);
    }

    public String getInfo() {
        return this.osUser.getPropertySet().getString(WEBLOGIC_INFO);
    }

    public BasicRealm getRealm() {
        return this.realm;
    }
}

