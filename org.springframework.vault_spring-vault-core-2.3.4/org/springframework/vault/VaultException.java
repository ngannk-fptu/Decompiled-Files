/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.core.NestedRuntimeException
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

