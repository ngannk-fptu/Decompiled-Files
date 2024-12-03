/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.ws.Response
 *  javax.xml.ws.WebServiceException
 */
package com.sun.xml.ws.client.sei;

import com.sun.xml.ws.client.sei.AsyncMethodHandler;
import com.sun.xml.ws.client.sei.SEIStub;
import java.lang.reflect.Method;
import javax.xml.ws.Response;
import javax.xml.ws.WebServiceException;

final class PollingMethodHandler
extends AsyncMethodHandler {
    PollingMethodHandler(SEIStub owner, Method m) {
        super(owner, m);
    }

    Response<?> invoke(Object proxy, Object[] args) throws WebServiceException {
        return this.doInvoke(proxy, args, null);
    }
}

