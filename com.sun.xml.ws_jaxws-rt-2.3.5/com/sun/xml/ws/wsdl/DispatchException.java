/*
 * Decompiled with CFR 0.152.
 */
package com.sun.xml.ws.wsdl;

import com.sun.xml.ws.api.message.Message;

public final class DispatchException
extends Exception {
    public final Message fault;

    public DispatchException(Message fault) {
        this.fault = fault;
    }
}

