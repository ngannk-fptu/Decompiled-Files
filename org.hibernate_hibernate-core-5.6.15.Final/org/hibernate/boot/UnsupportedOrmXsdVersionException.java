/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.boot;

import org.hibernate.boot.MappingException;
import org.hibernate.boot.jaxb.Origin;

public class UnsupportedOrmXsdVersionException
extends MappingException {
    private final String requestedVersion;

    public UnsupportedOrmXsdVersionException(String requestedVersion, Origin origin) {
        super("Encountered unsupported orm.xml xsd version [" + requestedVersion + "]", origin);
        this.requestedVersion = requestedVersion;
    }

    public String getRequestedVersion() {
        return this.requestedVersion;
    }
}

