/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate;

import java.io.Serializable;
import org.hibernate.UnresolvableObjectException;

public class ObjectDeletedException
extends UnresolvableObjectException {
    public ObjectDeletedException(String message, Serializable identifier, String entityName) {
        super(message, identifier, entityName);
    }
}

