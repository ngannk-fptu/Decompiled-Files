/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.plugins.hipchat.emoticons.exception;

public class EmoticonException
extends Exception {
    public EmoticonException() {
    }

    public EmoticonException(String message) {
        super(message);
    }

    public EmoticonException(Exception exception) {
        super(exception);
    }

    public EmoticonException(String message, Exception exception) {
        super(message, exception);
    }
}

