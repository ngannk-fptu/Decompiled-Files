/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate;

import java.io.Serializable;
import org.hibernate.UnresolvableObjectException;

public class ObjectNotFoundException
extends UnresolvableObjectException {
    public ObjectNotFoundException(Serializable identifier, String entityName) {
        super(identifier, entityName);
    }
}

