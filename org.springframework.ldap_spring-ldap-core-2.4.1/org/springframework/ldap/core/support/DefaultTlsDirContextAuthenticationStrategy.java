/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.ldap.core.support;

import javax.naming.NamingException;
import javax.naming.ldap.LdapContext;
import org.springframework.ldap.core.support.AbstractTlsDirContextAuthenticationStrategy;

public class DefaultTlsDirContextAuthenticationStrategy
extends AbstractTlsDirContextAuthenticationStrategy {
    private static final String SIMPLE_AUTHENTICATION = "simple";

    @Override
    protected void applyAuthentication(LdapContext ctx, String userDn, String password) throws NamingException {
        ctx.addToEnvironment("java.naming.security.authentication", SIMPLE_AUTHENTICATION);
        ctx.addToEnvironment("java.naming.security.principal", userDn);
        ctx.addToEnvironment("java.naming.security.credentials", password);
        ctx.lookup("");
    }
}

