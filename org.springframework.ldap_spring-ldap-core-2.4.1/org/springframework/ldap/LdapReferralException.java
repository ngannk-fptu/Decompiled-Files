/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.ldap;

import org.springframework.ldap.ReferralException;

public class LdapReferralException
extends ReferralException {
    public LdapReferralException(javax.naming.ldap.LdapReferralException cause) {
        super(cause);
    }
}

