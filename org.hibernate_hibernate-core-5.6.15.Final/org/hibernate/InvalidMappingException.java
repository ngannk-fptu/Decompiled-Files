/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate;

import org.hibernate.MappingException;
import org.hibernate.boot.jaxb.Origin;

public class InvalidMappingException
extends MappingException {
    private final String path;
    private final String type;

    public InvalidMappingException(String customMessage, String type, String path, Throwable cause) {
        super(customMessage, cause);
        this.type = type;
        this.path = path;
    }

    public InvalidMappingException(String customMessage, String type, String path) {
        super(customMessage);
        this.type = type;
        this.path = path;
    }

    public InvalidMappingException(String customMessage, Origin origin) {
        this(customMessage, origin.getType().getLegacyTypeText(), origin.getName());
    }

    public String getType() {
        return this.type;
    }

    public String getPath() {
        return this.path;
    }
}

