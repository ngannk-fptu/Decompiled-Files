/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jvnet.hk2.annotations.Service
 */
package org.glassfish.ha.store.spi;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectStreamClass;
import java.io.StreamCorruptedException;
import java.lang.reflect.Array;
import org.jvnet.hk2.annotations.Service;

@Service
public class ObjectInputStreamWithLoader
extends ObjectInputStream {
    protected ClassLoader loader;

    public ObjectInputStreamWithLoader(InputStream in, ClassLoader loader) throws IOException, StreamCorruptedException {
        super(in);
        if (loader == null) {
            throw new IllegalArgumentException("Illegal null argument to ObjectInputStreamWithLoader");
        }
        this.loader = loader;
    }

    private Class primitiveType(char type) {
        switch (type) {
            case 'B': {
                return Byte.TYPE;
            }
            case 'C': {
                return Character.TYPE;
            }
            case 'D': {
                return Double.TYPE;
            }
            case 'F': {
                return Float.TYPE;
            }
            case 'I': {
                return Integer.TYPE;
            }
            case 'J': {
                return Long.TYPE;
            }
            case 'S': {
                return Short.TYPE;
            }
            case 'Z': {
                return Boolean.TYPE;
            }
        }
        return null;
    }

    protected Class resolveClass(ObjectStreamClass classDesc) throws IOException, ClassNotFoundException {
        try {
            String cname = classDesc.getName();
            if (cname.startsWith("[")) {
                Class<?> component;
                int dcount = 1;
                while (cname.charAt(dcount) == '[') {
                    ++dcount;
                }
                if (cname.charAt(dcount) == 'L') {
                    component = this.loader.loadClass(cname.substring(dcount + 1, cname.length() - 1));
                } else {
                    if (cname.length() != dcount + 1) {
                        throw new ClassNotFoundException(cname);
                    }
                    component = this.primitiveType(cname.charAt(dcount));
                }
                int[] dim = new int[dcount];
                for (int i = 0; i < dcount; ++i) {
                    dim[i] = 0;
                }
                return Array.newInstance(component, dim).getClass();
            }
            return this.loader.loadClass(cname);
        }
        catch (ClassNotFoundException e) {
            return super.resolveClass(classDesc);
        }
    }
}

