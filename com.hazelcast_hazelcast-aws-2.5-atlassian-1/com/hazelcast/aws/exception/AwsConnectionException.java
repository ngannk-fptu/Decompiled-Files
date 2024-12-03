/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.aws.exception;

public class AwsConnectionException
extends RuntimeException {
    private final int httpReponseCode;
    private final String errorMessage;

    public AwsConnectionException(int httpReponseCode, String errorMessage) {
        super(AwsConnectionException.messageFrom(httpReponseCode, errorMessage));
        this.httpReponseCode = httpReponseCode;
        this.errorMessage = errorMessage;
    }

    private static String messageFrom(int httpReponseCode, String errorMessage) {
        return String.format("Connection to AWS failed (HTTP Response Code: %s, Message: \"%s\")", httpReponseCode, errorMessage);
    }

    public int getHttpReponseCode() {
        return this.httpReponseCode;
    }

    public String getErrorMessage() {
        return this.errorMessage;
    }
}

