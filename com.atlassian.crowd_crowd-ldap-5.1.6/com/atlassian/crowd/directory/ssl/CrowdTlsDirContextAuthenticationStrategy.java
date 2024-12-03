/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.ldap.core.support.AbstractTlsDirContextAuthenticationStrategy
 */
package com.atlassian.crowd.directory.ssl;

import javax.naming.NamingException;
import javax.naming.ldap.LdapContext;
import org.springframework.ldap.core.support.AbstractTlsDirContextAuthenticationStrategy;

public class CrowdTlsDirContextAuthenticationStrategy
extends AbstractTlsDirContextAuthenticationStrategy {
    static final String SIMPLE_AUTHENTICATION = "simple";

    public void applyAuthentication(LdapContext ctx, String userDn, String password) throws NamingException {
        ctx.addToEnvironment("java.naming.security.authentication", SIMPLE_AUTHENTICATION);
        ctx.addToEnvironment("java.naming.security.principal", userDn);
        ctx.addToEnvironment("java.naming.security.credentials", password);
        ctx.lookup("");
    }
}

