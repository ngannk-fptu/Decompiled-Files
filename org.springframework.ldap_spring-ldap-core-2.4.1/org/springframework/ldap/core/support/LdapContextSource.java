/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.ldap.core.support;

import java.util.Hashtable;
import javax.naming.NamingException;
import javax.naming.directory.DirContext;
import javax.naming.ldap.InitialLdapContext;
import org.springframework.ldap.core.support.AbstractContextSource;

public class LdapContextSource
extends AbstractContextSource {
    @Override
    protected DirContext getDirContextInstance(Hashtable<String, Object> environment) throws NamingException {
        return new InitialLdapContext(environment, null);
    }
}

