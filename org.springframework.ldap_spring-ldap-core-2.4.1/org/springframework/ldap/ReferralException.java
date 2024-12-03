/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.ldap;

import org.springframework.ldap.NamingException;

public class ReferralException
extends NamingException {
    public ReferralException(javax.naming.ReferralException cause) {
        super(cause);
    }
}

