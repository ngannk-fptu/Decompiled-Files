/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.core.NestedRuntimeException
 */
package org.springframework.scheduling;

import org.springframework.core.NestedRuntimeException;

public class SchedulingException
extends NestedRuntimeException {
    public SchedulingException(String msg) {
        super(msg);
    }

    public SchedulingException(String msg, Throwable cause) {
        super(msg, cause);
    }
}

