/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.ldap;

import org.springframework.ldap.LinkException;

public class LinkLoopException
extends LinkException {
    public LinkLoopException(javax.naming.LinkLoopException cause) {
        super(cause);
    }
}

