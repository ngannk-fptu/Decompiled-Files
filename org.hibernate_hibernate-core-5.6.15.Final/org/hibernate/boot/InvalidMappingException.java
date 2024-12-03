/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.boot;

import org.hibernate.boot.jaxb.Origin;

public class InvalidMappingException
extends org.hibernate.InvalidMappingException {
    private final Origin origin;

    public InvalidMappingException(Origin origin) {
        super(String.format("Could not parse mapping document: %s (%s)", new Object[]{origin.getName(), origin.getType()}), origin);
        this.origin = origin;
    }

    public InvalidMappingException(Origin origin, Throwable e) {
        super(String.format("Could not parse mapping document: %s (%s)", new Object[]{origin.getName(), origin.getType()}), origin.getType().getLegacyTypeText(), origin.getName(), e);
        this.origin = origin;
    }

    public Origin getOrigin() {
        return this.origin;
    }
}

