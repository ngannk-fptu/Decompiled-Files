/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.core.util;

public class InvalidDurationException
extends Exception {
    public InvalidDurationException() {
    }

    public InvalidDurationException(String msg) {
        super(msg);
    }

    public InvalidDurationException(Exception e) {
        super(e);
    }

    public InvalidDurationException(String msg, Exception e) {
        super(msg, e);
    }
}

