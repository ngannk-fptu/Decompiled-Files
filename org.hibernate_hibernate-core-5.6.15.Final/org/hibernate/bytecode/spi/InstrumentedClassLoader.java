/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.bytecode.spi;

import java.io.InputStream;
import org.hibernate.bytecode.spi.ByteCodeHelper;
import org.hibernate.bytecode.spi.ClassTransformer;

public class InstrumentedClassLoader
extends ClassLoader {
    private final ClassTransformer classTransformer;

    public InstrumentedClassLoader(ClassLoader parent, ClassTransformer classTransformer) {
        super(parent);
        this.classTransformer = classTransformer;
    }

    public Class loadClass(String name) throws ClassNotFoundException {
        if (name.startsWith("java.") || this.classTransformer == null) {
            return this.getParent().loadClass(name);
        }
        Class<?> c = this.findLoadedClass(name);
        if (c != null) {
            return c;
        }
        InputStream is = this.getResourceAsStream(name.replace('.', '/') + ".class");
        if (is == null) {
            throw new ClassNotFoundException(name + " not found");
        }
        try {
            byte[] originalBytecode = ByteCodeHelper.readByteCode(is);
            byte[] transformedBytecode = this.classTransformer.transform(this.getParent(), name, null, null, originalBytecode);
            if (transformedBytecode == null) {
                return this.getParent().loadClass(name);
            }
            return this.defineClass(name, transformedBytecode, 0, transformedBytecode.length);
        }
        catch (Throwable t) {
            throw new ClassNotFoundException(name + " not found", t);
        }
    }
}

