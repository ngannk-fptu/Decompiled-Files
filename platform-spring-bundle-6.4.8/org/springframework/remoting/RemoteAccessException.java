/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.remoting;

import org.springframework.core.NestedRuntimeException;

public class RemoteAccessException
extends NestedRuntimeException {
    private static final long serialVersionUID = -4906825139312227864L;

    public RemoteAccessException(String msg) {
        super(msg);
    }

    public RemoteAccessException(String msg, Throwable cause) {
        super(msg, cause);
    }
}

