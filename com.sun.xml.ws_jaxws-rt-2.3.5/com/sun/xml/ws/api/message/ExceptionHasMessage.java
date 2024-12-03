/*
 * Decompiled with CFR 0.152.
 */
package com.sun.xml.ws.api.message;

import com.sun.xml.ws.api.message.Message;
import com.sun.xml.ws.util.exception.JAXWSExceptionBase;

public abstract class ExceptionHasMessage
extends JAXWSExceptionBase {
    public ExceptionHasMessage(String key, Object ... args) {
        super(key, args);
    }

    public abstract Message getFaultMessage();
}

