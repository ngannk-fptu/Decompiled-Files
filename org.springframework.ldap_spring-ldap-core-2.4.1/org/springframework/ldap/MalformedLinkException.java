/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.ldap;

import org.springframework.ldap.LinkException;

public class MalformedLinkException
extends LinkException {
    public MalformedLinkException(javax.naming.MalformedLinkException cause) {
        super(cause);
    }
}

