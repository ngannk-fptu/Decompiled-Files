/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.ldap.odm.core.impl;

import org.springframework.ldap.odm.core.OdmException;

public class UnmanagedClassException
extends OdmException {
    public UnmanagedClassException(String message, Throwable reason) {
        super(message, reason);
    }

    public UnmanagedClassException(String message) {
        super(message);
    }
}

