/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.core.MethodParameter
 *  org.springframework.lang.Nullable
 */
package org.springframework.web.server;

import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.lang.Nullable;
import org.springframework.web.server.ResponseStatusException;

public class ServerWebInputException
extends ResponseStatusException {
    @Nullable
    private final MethodParameter parameter;

    public ServerWebInputException(String reason) {
        this(reason, null, null);
    }

    public ServerWebInputException(String reason, @Nullable MethodParameter parameter) {
        this(reason, parameter, null);
    }

    public ServerWebInputException(String reason, @Nullable MethodParameter parameter, @Nullable Throwable cause) {
        super(HttpStatus.BAD_REQUEST, reason, cause);
        this.parameter = parameter;
    }

    @Nullable
    public MethodParameter getMethodParameter() {
        return this.parameter;
    }
}

