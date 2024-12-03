/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.remoting;

import org.springframework.remoting.RemoteAccessException;

public class RemoteLookupFailureException
extends RemoteAccessException {
    public RemoteLookupFailureException(String msg) {
        super(msg);
    }

    public RemoteLookupFailureException(String msg, Throwable cause) {
        super(msg, cause);
    }
}

