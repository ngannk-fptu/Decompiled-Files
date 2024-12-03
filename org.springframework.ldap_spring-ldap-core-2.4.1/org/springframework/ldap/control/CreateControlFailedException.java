/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.ldap.control;

import org.springframework.ldap.NamingException;

public class CreateControlFailedException
extends NamingException {
    public CreateControlFailedException(String msg) {
        super(msg);
    }

    public CreateControlFailedException(String msg, Throwable cause) {
        super(msg, cause);
    }
}

