/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.ldap;

import org.springframework.ldap.NamingException;

public class ConfigurationException
extends NamingException {
    public ConfigurationException(javax.naming.ConfigurationException cause) {
        super(cause);
    }
}

