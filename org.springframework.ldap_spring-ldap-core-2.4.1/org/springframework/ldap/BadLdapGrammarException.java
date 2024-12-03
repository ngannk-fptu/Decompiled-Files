/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.ldap;

import org.springframework.ldap.NamingException;

public class BadLdapGrammarException
extends NamingException {
    private static final long serialVersionUID = 961612585331409470L;

    public BadLdapGrammarException(String message) {
        super(message);
    }

    public BadLdapGrammarException(String message, Throwable cause) {
        super(message, cause);
    }
}

