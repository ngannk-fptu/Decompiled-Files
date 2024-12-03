/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.ldap.odm.core.impl;

import org.springframework.ldap.odm.core.OdmException;

public class InvalidEntryException
extends OdmException {
    public InvalidEntryException(String message) {
        super(message);
    }

    public InvalidEntryException(String message, Throwable reason) {
        super(message, reason);
    }
}

