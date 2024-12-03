/*
 * Decompiled with CFR 0.152.
 */
package io.micrometer.core.ipc.http;

enum HttpStatusClass {
    INFORMATIONAL(100, 200),
    SUCCESS(200, 300),
    REDIRECTION(300, 400),
    CLIENT_ERROR(400, 500),
    SERVER_ERROR(500, 600),
    UNKNOWN(0, 0){

        @Override
        public boolean contains(int code) {
            return code < 100 || code >= 600;
        }
    };

    private final int min;
    private final int max;

    public static HttpStatusClass valueOf(int code) {
        if (INFORMATIONAL.contains(code)) {
            return INFORMATIONAL;
        }
        if (SUCCESS.contains(code)) {
            return SUCCESS;
        }
        if (REDIRECTION.contains(code)) {
            return REDIRECTION;
        }
        if (CLIENT_ERROR.contains(code)) {
            return CLIENT_ERROR;
        }
        if (SERVER_ERROR.contains(code)) {
            return SERVER_ERROR;
        }
        return UNKNOWN;
    }

    private HttpStatusClass(int min, int max) {
        this.min = min;
        this.max = max;
    }

    public boolean contains(int code) {
        return code >= this.min && code < this.max;
    }
}

