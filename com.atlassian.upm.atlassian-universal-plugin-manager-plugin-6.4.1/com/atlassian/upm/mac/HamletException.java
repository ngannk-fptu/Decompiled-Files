/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.upm.mac;

public class HamletException
extends Exception {
    public HamletException(String message) {
        super(message);
    }

    public HamletException(Exception cause) {
        super(cause);
    }

    public static class SenRequiredException
    extends HamletException {
        public SenRequiredException() {
            super("can't send Hamlet request without a SEN");
        }
    }

    public static class HttpException
    extends HamletException {
        public HttpException(int statusCode) {
            super("error " + statusCode);
        }
    }

    public static class InvalidCredentialsException
    extends HamletException {
        public InvalidCredentialsException() {
            super("invalid credentials");
        }
    }
}

