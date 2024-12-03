/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.voorhees;

public enum ErrorCode {
    PARSE_ERROR(-32700),
    INVALID_REQUEST(-32600),
    METHOD_NOT_FOUND(-32601),
    INVALID_METHOD_PARAMETERS(-32602),
    INTERNAL_RPC_ERROR(-32603);

    private final int code;

    private ErrorCode(int code) {
        this.code = code;
    }

    public int intValue() {
        return this.code;
    }
}

