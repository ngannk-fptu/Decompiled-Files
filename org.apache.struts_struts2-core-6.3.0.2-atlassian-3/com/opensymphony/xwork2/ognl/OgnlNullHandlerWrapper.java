/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  ognl.NullHandler
 */
package com.opensymphony.xwork2.ognl;

import java.util.Map;
import ognl.NullHandler;

public class OgnlNullHandlerWrapper
implements NullHandler {
    private final com.opensymphony.xwork2.conversion.NullHandler wrapped;

    public OgnlNullHandlerWrapper(com.opensymphony.xwork2.conversion.NullHandler target) {
        this.wrapped = target;
    }

    public Object nullMethodResult(Map context, Object target, String methodName, Object[] args) {
        return this.wrapped.nullMethodResult(context, target, methodName, args);
    }

    public Object nullPropertyValue(Map context, Object target, Object property) {
        return this.wrapped.nullPropertyValue(context, target, property);
    }
}

