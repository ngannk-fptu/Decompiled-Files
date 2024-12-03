/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.opensymphony.user.provider.ldap.LDAPCredentialsProvider
 */
package com.atlassian.confluence.user;

import com.opensymphony.user.provider.ldap.LDAPCredentialsProvider;

public class ConfluenceLDAPCredentialsProvider
extends LDAPCredentialsProvider {
    public boolean handles(String name) {
        return true;
    }
}

