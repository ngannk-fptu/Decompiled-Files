/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.ldap.odm.core.impl;

import org.springframework.ldap.odm.core.OdmException;

public class MetaDataException
extends OdmException {
    public MetaDataException(String message) {
        super(message);
    }

    public MetaDataException(String message, Throwable reason) {
        super(message, reason);
    }
}

