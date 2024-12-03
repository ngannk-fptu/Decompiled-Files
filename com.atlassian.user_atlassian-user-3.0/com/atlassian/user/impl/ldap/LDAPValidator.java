/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.user.impl.ldap;

import com.atlassian.user.Entity;
import com.atlassian.user.impl.ldap.LDAPEntity;

public class LDAPValidator {
    public static boolean validateLDAPEntity(Entity entity) {
        return entity instanceof LDAPEntity;
    }
}

