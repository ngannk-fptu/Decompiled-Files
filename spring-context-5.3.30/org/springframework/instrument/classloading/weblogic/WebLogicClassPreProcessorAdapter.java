/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.lang.Nullable
 */
package org.springframework.instrument.classloading.weblogic;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Hashtable;
import org.springframework.lang.Nullable;

class WebLogicClassPreProcessorAdapter
implements InvocationHandler {
    private final ClassFileTransformer transformer;
    private final ClassLoader loader;

    public WebLogicClassPreProcessorAdapter(ClassFileTransformer transformer, ClassLoader loader) {
        this.transformer = transformer;
        this.loader = loader;
    }

    @Override
    @Nullable
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        String name = method.getName();
        if ("equals".equals(name)) {
            return proxy == args[0];
        }
        if ("hashCode".equals(name)) {
            return this.hashCode();
        }
        if ("toString".equals(name)) {
            return this.toString();
        }
        if ("initialize".equals(name)) {
            this.initialize((Hashtable)args[0]);
            return null;
        }
        if ("preProcess".equals(name)) {
            return this.preProcess((String)args[0], (byte[])args[1]);
        }
        throw new IllegalArgumentException("Unknown method: " + method);
    }

    public void initialize(Hashtable<?, ?> params) {
    }

    public byte[] preProcess(String className, byte[] classBytes) {
        try {
            byte[] result = this.transformer.transform(this.loader, className, null, null, classBytes);
            return result != null ? result : classBytes;
        }
        catch (IllegalClassFormatException ex) {
            throw new IllegalStateException("Cannot transform due to illegal class format", ex);
        }
    }

    public String toString() {
        return this.getClass().getName() + " for transformer: " + this.transformer;
    }
}

