/*
 * Decompiled with CFR 0.152.
 */
package org.terracotta.modules.ehcache.collections;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamClass;
import java.io.OutputStream;
import net.sf.ehcache.util.FindBugsSuppressWarnings;

public class SerializationHelper {
    private static final char MARKER = '\ufffe';

    public static byte[] serialize(Object obj) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(obj);
            oos.close();
            return baos.toByteArray();
        }
        catch (IOException e) {
            throw new RuntimeException("error serializing " + obj, e);
        }
    }

    @FindBugsSuppressWarnings(value={"DMI_INVOKING_TOSTRING_ON_ARRAY"})
    public static Object deserialize(byte[] bytes) {
        try {
            ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
            ObjectInputStream ois = new ObjectInputStream(bais);
            Object obj = ois.readObject();
            ois.close();
            return obj;
        }
        catch (Exception e) {
            throw new RuntimeException("error deserializing " + bytes, e);
        }
    }

    public static Object deserializeFromString(String key, final ClassLoader loader) throws IOException, ClassNotFoundException {
        if (key.length() >= 1 && key.charAt(0) == '\ufffe') {
            ObjectInputStream ois = new ObjectInputStream(new StringSerializedObjectInputStream(key)){

                @Override
                protected Class<?> resolveClass(ObjectStreamClass desc) throws IOException, ClassNotFoundException {
                    try {
                        return super.resolveClass(desc);
                    }
                    catch (ClassNotFoundException e) {
                        if (loader != null) {
                            return loader.loadClass(desc.getName());
                        }
                        throw e;
                    }
                }
            };
            return ois.readObject();
        }
        return key;
    }

    public static String serializeToString(Object key) throws IOException {
        if (key instanceof String) {
            String stringKey = (String)key;
            if (stringKey.length() >= 1 && stringKey.charAt(0) == '\ufffe') {
                throw new IOException("Illegal string key: " + stringKey);
            }
            return stringKey;
        }
        StringSerializedObjectOutputStream out = new StringSerializedObjectOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(out);
        SerializationHelper.writeStringKey(key, oos);
        oos.close();
        return out.toString();
    }

    private static void writeStringKey(Object key, ObjectOutputStream oos) throws IOException {
        oos.writeObject(key);
    }

    private static class StringSerializedObjectInputStream
    extends InputStream {
        private final String source;
        private final int length;
        private int index;

        StringSerializedObjectInputStream(String source) {
            this.source = source;
            this.length = source.length();
            this.read();
        }

        @Override
        public int read() {
            if (this.index == this.length) {
                return -1;
            }
            return this.source.charAt(this.index++) & 0xFF;
        }
    }

    private static class StringSerializedObjectOutputStream
    extends OutputStream {
        private int count;
        private char[] buf;

        StringSerializedObjectOutputStream() {
            this(16);
        }

        StringSerializedObjectOutputStream(int size) {
            size = Math.max(1, size);
            this.buf = new char[size];
            this.buf[this.count++] = 65534;
        }

        @Override
        public void write(int b) {
            if (this.count + 1 > this.buf.length) {
                char[] newbuf = new char[this.buf.length << 1];
                System.arraycopy(this.buf, 0, newbuf, 0, this.count);
                this.buf = newbuf;
            }
            this.writeChar(b);
        }

        private void writeChar(int b) {
            this.buf[this.count++] = (char)(b & 0xFF);
        }

        @Override
        public void write(byte[] b, int off, int len) {
            if (off < 0 || off > b.length || len < 0 || off + len > b.length || off + len < 0) {
                throw new IndexOutOfBoundsException();
            }
            if (len == 0) {
                return;
            }
            int newcount = this.count + len;
            if (newcount > this.buf.length) {
                char[] newbuf = new char[Math.max(this.buf.length << 1, newcount)];
                System.arraycopy(this.buf, 0, newbuf, 0, this.count);
                this.buf = newbuf;
            }
            for (int i = 0; i < len; ++i) {
                this.writeChar(b[off + i]);
            }
        }

        public String toString() {
            return new String(this.buf, 0, this.count);
        }
    }
}

