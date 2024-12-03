/*
 * Decompiled with CFR 0.152.
 */
package com.thoughtworks.xstream;

import com.thoughtworks.xstream.XStream;

public class InitializationException
extends XStream.InitializationException {
    public InitializationException(String message, Throwable cause) {
        super(message, cause);
    }

    public InitializationException(String message) {
        super(message);
    }
}

