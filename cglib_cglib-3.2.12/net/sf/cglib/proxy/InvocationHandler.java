/*
 * Decompiled with CFR 0.152.
 */
package net.sf.cglib.proxy;

import java.lang.reflect.Method;
import net.sf.cglib.proxy.Callback;

public interface InvocationHandler
extends Callback {
    public Object invoke(Object var1, Method var2, Object[] var3) throws Throwable;
}

