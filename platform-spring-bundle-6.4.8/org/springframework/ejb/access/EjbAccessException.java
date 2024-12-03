/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.ejb.access;

import org.springframework.core.NestedRuntimeException;

public class EjbAccessException
extends NestedRuntimeException {
    public EjbAccessException(String msg) {
        super(msg);
    }

    public EjbAccessException(String msg, Throwable cause) {
        super(msg, cause);
    }
}

