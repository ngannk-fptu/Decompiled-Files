/*
 * Decompiled with CFR 0.152.
 */
package com.sun.jersey.server.impl;

import com.sun.jersey.server.impl.ThreadLocalInvoker;
import java.lang.reflect.Method;
import javax.naming.InitialContext;

public class ThreadLocalNamedInvoker<T>
extends ThreadLocalInvoker<T> {
    private String name;

    public ThreadLocalNamedInvoker(String name) {
        this.name = name;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (this.get() == null) {
            InitialContext ctx = new InitialContext();
            Object t = ctx.lookup(this.name);
            this.set(t);
        }
        return super.invoke(proxy, method, args);
    }
}

