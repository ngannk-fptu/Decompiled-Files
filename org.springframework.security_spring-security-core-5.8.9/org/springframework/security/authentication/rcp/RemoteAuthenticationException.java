/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.core.NestedRuntimeException
 */
package org.springframework.security.authentication.rcp;

import org.springframework.core.NestedRuntimeException;

@Deprecated
public class RemoteAuthenticationException
extends NestedRuntimeException {
    private static final long serialVersionUID = 580L;

    public RemoteAuthenticationException(String msg) {
        super(msg);
    }
}

