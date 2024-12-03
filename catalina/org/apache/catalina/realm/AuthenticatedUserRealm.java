/*
 * Decompiled with CFR 0.152.
 */
package org.apache.catalina.realm;

import java.security.Principal;
import org.apache.catalina.realm.GenericPrincipal;
import org.apache.catalina.realm.RealmBase;

public class AuthenticatedUserRealm
extends RealmBase {
    @Override
    protected String getPassword(String username) {
        return null;
    }

    @Override
    protected Principal getPrincipal(String username) {
        return new GenericPrincipal(username, null, null);
    }
}

