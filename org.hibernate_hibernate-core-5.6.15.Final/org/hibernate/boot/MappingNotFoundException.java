/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.boot;

import org.hibernate.boot.MappingException;
import org.hibernate.boot.jaxb.Origin;

public class MappingNotFoundException
extends MappingException {
    public MappingNotFoundException(String message, Origin origin) {
        super(message, origin);
    }

    public MappingNotFoundException(Origin origin) {
        super(String.format("Mapping (%s) not found : %s", new Object[]{origin.getType(), origin.getName()}), origin);
    }

    public MappingNotFoundException(String message, Throwable root, Origin origin) {
        super(message, root, origin);
    }

    public MappingNotFoundException(Throwable root, Origin origin) {
        super(String.format("Mapping (%s) not found : %s", new Object[]{origin.getType(), origin.getName()}), root, origin);
    }
}

