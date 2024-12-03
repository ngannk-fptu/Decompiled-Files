/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.ldap;

import org.springframework.ldap.NamingException;

public class LinkException
extends NamingException {
    public LinkException(javax.naming.LinkException cause) {
        super(cause);
    }
}

