/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.ws.WebServiceException
 */
package com.sun.xml.ws.client.sei;

import com.sun.xml.ws.client.sei.SEIStub;
import java.lang.reflect.Method;
import javax.xml.ws.WebServiceException;

public abstract class MethodHandler {
    protected final SEIStub owner;
    protected Method method;

    protected MethodHandler(SEIStub owner, Method m) {
        this.owner = owner;
        this.method = m;
    }

    abstract Object invoke(Object var1, Object[] var2) throws WebServiceException, Throwable;
}

