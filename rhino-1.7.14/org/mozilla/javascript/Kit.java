/*
 * Decompiled with CFR 0.152.
 */
package org.mozilla.javascript;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.Map;
import org.mozilla.javascript.ScriptRuntime;

public class Kit {
    public static Class<?> classOrNull(String className) {
        try {
            return Class.forName(className);
        }
        catch (ClassNotFoundException classNotFoundException) {
        }
        catch (SecurityException securityException) {
        }
        catch (LinkageError linkageError) {
        }
        catch (IllegalArgumentException illegalArgumentException) {
            // empty catch block
        }
        return null;
    }

    public static Class<?> classOrNull(ClassLoader loader, String className) {
        try {
            return loader.loadClass(className);
        }
        catch (ClassNotFoundException classNotFoundException) {
        }
        catch (SecurityException securityException) {
        }
        catch (LinkageError linkageError) {
        }
        catch (IllegalArgumentException illegalArgumentException) {
            // empty catch block
        }
        return null;
    }

    static Object newInstanceOrNull(Class<?> cl) {
        try {
            return cl.newInstance();
        }
        catch (SecurityException securityException) {
        }
        catch (LinkageError linkageError) {
        }
        catch (InstantiationException instantiationException) {
        }
        catch (IllegalAccessException illegalAccessException) {
            // empty catch block
        }
        return null;
    }

    static boolean testIfCanLoadRhinoClasses(ClassLoader loader) {
        Class<?> testClass = ScriptRuntime.ContextFactoryClass;
        Class<?> x = Kit.classOrNull(loader, testClass.getName());
        return x == testClass;
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public static int xDigitToInt(int c, int accumulator) {
        if (c <= 57) {
            if (0 > (c -= 48)) return -1;
            return accumulator << 4 | c;
        } else if (c <= 70) {
            if (65 > c) return -1;
            c -= 55;
            return accumulator << 4 | c;
        } else {
            if (c > 102 || 97 > c) return -1;
            c -= 87;
        }
        return accumulator << 4 | c;
    }

    public static Object addListener(Object bag, Object listener) {
        if (listener == null) {
            throw new IllegalArgumentException();
        }
        if (listener instanceof Object[]) {
            throw new IllegalArgumentException();
        }
        if (bag == null) {
            bag = listener;
        } else if (!(bag instanceof Object[])) {
            bag = new Object[]{bag, listener};
        } else {
            Object[] array = bag;
            int L = array.length;
            if (L < 2) {
                throw new IllegalArgumentException();
            }
            Object[] tmp = new Object[L + 1];
            System.arraycopy(array, 0, tmp, 0, L);
            tmp[L] = listener;
            bag = tmp;
        }
        return bag;
    }

    public static Object removeListener(Object bag, Object listener) {
        if (listener == null) {
            throw new IllegalArgumentException();
        }
        if (listener instanceof Object[]) {
            throw new IllegalArgumentException();
        }
        if (bag == listener) {
            bag = null;
        } else if (bag instanceof Object[]) {
            Object[] array = bag;
            int L = array.length;
            if (L < 2) {
                throw new IllegalArgumentException();
            }
            if (L == 2) {
                if (array[1] == listener) {
                    bag = array[0];
                } else if (array[0] == listener) {
                    bag = array[1];
                }
            } else {
                int i = L;
                do {
                    if (array[--i] != listener) continue;
                    Object[] tmp = new Object[L - 1];
                    System.arraycopy(array, 0, tmp, 0, i);
                    System.arraycopy(array, i + 1, tmp, i, L - (i + 1));
                    bag = tmp;
                    break;
                } while (i != 0);
            }
        }
        return bag;
    }

    public static Object getListener(Object bag, int index) {
        if (index == 0) {
            if (bag == null) {
                return null;
            }
            if (!(bag instanceof Object[])) {
                return bag;
            }
            Object[] array = (Object[])bag;
            if (array.length < 2) {
                throw new IllegalArgumentException();
            }
            return array[0];
        }
        if (index == 1) {
            if (!(bag instanceof Object[])) {
                if (bag == null) {
                    throw new IllegalArgumentException();
                }
                return null;
            }
            Object[] array = (Object[])bag;
            return array[1];
        }
        Object[] array = (Object[])bag;
        int L = array.length;
        if (L < 2) {
            throw new IllegalArgumentException();
        }
        if (index == L) {
            return null;
        }
        return array[index];
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    static Object initHash(Map<Object, Object> h, Object key, Object initialValue) {
        Map<Object, Object> map = h;
        synchronized (map) {
            Object current = h.get(key);
            if (current == null) {
                h.put(key, initialValue);
            } else {
                initialValue = current;
            }
        }
        return initialValue;
    }

    public static Object makeHashKeyFromPair(Object key1, Object key2) {
        if (key1 == null) {
            throw new IllegalArgumentException();
        }
        if (key2 == null) {
            throw new IllegalArgumentException();
        }
        return new ComplexKey(key1, key2);
    }

    public static String readReader(Reader reader) throws IOException {
        try (BufferedReader in = new BufferedReader(reader);){
            int bytes_read;
            char[] cbuf = new char[1024];
            StringBuilder sb = new StringBuilder(1024);
            while ((bytes_read = in.read(cbuf, 0, 1024)) != -1) {
                sb.append(cbuf, 0, bytes_read);
            }
            String string = sb.toString();
            return string;
        }
    }

    public static byte[] readStream(InputStream is, int initialBufferCapacity) throws IOException {
        int n;
        if (initialBufferCapacity <= 0) {
            throw new IllegalArgumentException("Bad initialBufferCapacity: " + initialBufferCapacity);
        }
        byte[] buffer = new byte[initialBufferCapacity];
        int cursor = 0;
        while ((n = is.read(buffer, cursor, buffer.length - cursor)) >= 0) {
            if ((cursor += n) != buffer.length) continue;
            int readahead = -1;
            if (cursor == initialBufferCapacity && (readahead = is.read()) < 0) {
                return buffer;
            }
            byte[] tmp = new byte[buffer.length * 2];
            System.arraycopy(buffer, 0, tmp, 0, cursor);
            buffer = tmp;
            if (readahead == -1) continue;
            buffer[cursor++] = (byte)readahead;
            readahead = -1;
        }
        if (cursor != buffer.length) {
            byte[] tmp = new byte[cursor];
            System.arraycopy(buffer, 0, tmp, 0, cursor);
            buffer = tmp;
        }
        return buffer;
    }

    public static RuntimeException codeBug() throws RuntimeException {
        IllegalStateException ex = new IllegalStateException("FAILED ASSERTION");
        ex.printStackTrace(System.err);
        throw ex;
    }

    public static RuntimeException codeBug(String msg) throws RuntimeException {
        msg = "FAILED ASSERTION: " + msg;
        IllegalStateException ex = new IllegalStateException(msg);
        ex.printStackTrace(System.err);
        throw ex;
    }

    private static final class ComplexKey {
        private Object key1;
        private Object key2;
        private int hash;

        ComplexKey(Object key1, Object key2) {
            this.key1 = key1;
            this.key2 = key2;
        }

        public boolean equals(Object anotherObj) {
            if (!(anotherObj instanceof ComplexKey)) {
                return false;
            }
            ComplexKey another = (ComplexKey)anotherObj;
            return this.key1.equals(another.key1) && this.key2.equals(another.key2);
        }

        public int hashCode() {
            if (this.hash == 0) {
                this.hash = this.key1.hashCode() ^ this.key2.hashCode();
            }
            return this.hash;
        }
    }
}

