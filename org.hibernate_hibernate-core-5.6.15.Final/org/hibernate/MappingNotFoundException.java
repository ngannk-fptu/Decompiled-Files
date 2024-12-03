/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate;

import org.hibernate.MappingException;

@Deprecated
public class MappingNotFoundException
extends MappingException {
    private final String path;
    private final String type;

    public MappingNotFoundException(String customMessage, String type, String path, Throwable cause) {
        super(customMessage, cause);
        this.type = type;
        this.path = path;
    }

    public MappingNotFoundException(String customMessage, String type, String path) {
        super(customMessage);
        this.type = type;
        this.path = path;
    }

    public MappingNotFoundException(String type, String path) {
        this(type + ": " + path + " not found", type, path);
    }

    public MappingNotFoundException(String type, String path, Throwable cause) {
        this(type + ": " + path + " not found", type, path, cause);
    }

    public String getType() {
        return this.type;
    }

    public String getPath() {
        return this.path;
    }
}

