/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.ws.AsyncHandler
 *  javax.xml.ws.WebServiceException
 */
package com.sun.xml.ws.client.sei;

import com.sun.xml.ws.client.sei.AsyncMethodHandler;
import com.sun.xml.ws.client.sei.SEIStub;
import java.lang.reflect.Method;
import java.util.concurrent.Future;
import javax.xml.ws.AsyncHandler;
import javax.xml.ws.WebServiceException;

final class CallbackMethodHandler
extends AsyncMethodHandler {
    private final int handlerPos;

    CallbackMethodHandler(SEIStub owner, Method m, int handlerPos) {
        super(owner, m);
        this.handlerPos = handlerPos;
    }

    @Override
    Future<?> invoke(Object proxy, Object[] args) throws WebServiceException {
        AsyncHandler handler = (AsyncHandler)args[this.handlerPos];
        return this.doInvoke(proxy, args, handler);
    }
}

