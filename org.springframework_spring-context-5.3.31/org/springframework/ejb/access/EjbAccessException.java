/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.core.NestedRuntimeException
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

