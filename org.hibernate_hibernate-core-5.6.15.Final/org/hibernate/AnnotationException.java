/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate;

import org.hibernate.MappingException;

public class AnnotationException
extends MappingException {
    public AnnotationException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public AnnotationException(String msg) {
        super(msg);
    }
}

