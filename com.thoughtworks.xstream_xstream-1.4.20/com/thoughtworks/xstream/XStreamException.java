/*
 * Decompiled with CFR 0.152.
 */
package com.thoughtworks.xstream;

import com.thoughtworks.xstream.core.BaseException;

public class XStreamException
extends BaseException {
    protected XStreamException() {
        this("", null);
    }

    public XStreamException(String message) {
        this(message, null);
    }

    public XStreamException(Throwable cause) {
        this("", cause);
    }

    public XStreamException(String message, Throwable cause) {
        super(message, cause);
    }
}

