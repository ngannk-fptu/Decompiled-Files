/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.kubernetes;

class RestClientException
extends RuntimeException {
    private int httpErrorCode;

    RestClientException(String message, int httpErrorCode) {
        super(String.format("%s. HTTP Error Code: %s", message, httpErrorCode));
        this.httpErrorCode = httpErrorCode;
    }

    RestClientException(String message, Throwable cause) {
        super(message, cause);
    }

    int getHttpErrorCode() {
        return this.httpErrorCode;
    }
}

