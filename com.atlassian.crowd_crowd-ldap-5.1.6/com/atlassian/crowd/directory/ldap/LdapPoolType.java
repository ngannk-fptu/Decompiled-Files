/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.crowd.directory.ldap;

public enum LdapPoolType {
    JNDI,
    COMMONS_POOL2;


    public static LdapPoolType fromString(String name) {
        for (LdapPoolType type : LdapPoolType.values()) {
            if (!type.name().equalsIgnoreCase(name)) continue;
            return type;
        }
        return JNDI;
    }
}

