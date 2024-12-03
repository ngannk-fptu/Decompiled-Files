/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.vault;

import org.springframework.core.NestedRuntimeException;

public class VaultException
extends NestedRuntimeException {
    public VaultException(String msg) {
        super(msg);
    }

    public VaultException(String msg, Throwable cause) {
        super(msg, cause);
    }
}

