/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.ldap;

import org.springframework.ldap.NamingException;

public class ServiceUnavailableException
extends NamingException {
    public ServiceUnavailableException(javax.naming.ServiceUnavailableException cause) {
        super(cause);
    }
}

