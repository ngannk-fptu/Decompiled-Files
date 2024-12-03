/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.ldap;

import org.springframework.ldap.NamingException;

public class CommunicationException
extends NamingException {
    public CommunicationException(javax.naming.CommunicationException cause) {
        super(cause);
    }
}

