/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.instrument.classloading.websphere;

import java.lang.instrument.ClassFileTransformer;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.security.CodeSource;
import org.springframework.util.FileCopyUtils;

class WebSphereClassPreDefinePlugin
implements InvocationHandler {
    private final ClassFileTransformer transformer;

    public WebSphereClassPreDefinePlugin(ClassFileTransformer transformer) {
        this.transformer = transformer;
        ClassLoader classLoader = transformer.getClass().getClassLoader();
        try {
            String dummyClass = Dummy.class.getName().replace('.', '/');
            byte[] bytes = FileCopyUtils.copyToByteArray(classLoader.getResourceAsStream(dummyClass + ".class"));
            transformer.transform(classLoader, dummyClass, null, null, bytes);
        }
        catch (Throwable ex) {
            throw new IllegalArgumentException("Cannot load transformer", ex);
        }
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        switch (method.getName()) {
            case "equals": {
                return proxy == args[0];
            }
            case "hashCode": {
                return this.hashCode();
            }
            case "toString": {
                return this.toString();
            }
            case "transformClass": {
                return this.transform((String)args[0], (byte[])args[1], (CodeSource)args[2], (ClassLoader)args[3]);
            }
        }
        throw new IllegalArgumentException("Unknown method: " + method);
    }

    protected byte[] transform(String className, byte[] classfileBuffer, CodeSource codeSource, ClassLoader classLoader) throws Exception {
        byte[] result = this.transformer.transform(classLoader, className.replace('.', '/'), null, null, classfileBuffer);
        return result != null ? result : classfileBuffer;
    }

    public String toString() {
        return this.getClass().getName() + " for transformer: " + this.transformer;
    }

    private static class Dummy {
        private Dummy() {
        }
    }
}

