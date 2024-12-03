/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tika.fork;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamClass;

class ForkObjectInputStream
extends ObjectInputStream {
    private final ClassLoader loader;

    public ForkObjectInputStream(InputStream input, ClassLoader loader) throws IOException {
        super(input);
        this.loader = loader;
    }

    @Override
    protected Class<?> resolveClass(ObjectStreamClass desc) throws ClassNotFoundException {
        return Class.forName(desc.getName(), false, this.loader);
    }

    public static void sendObject(Object object, DataOutputStream output) throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        ObjectOutputStream serializer = new ObjectOutputStream(buffer);
        serializer.writeObject(object);
        serializer.close();
        byte[] data = buffer.toByteArray();
        output.writeInt(data.length);
        output.write(data);
    }

    public static Object readObject(DataInputStream input, ClassLoader loader) throws IOException, ClassNotFoundException {
        int n = input.readInt();
        byte[] data = new byte[n];
        input.readFully(data);
        ForkObjectInputStream deserializer = new ForkObjectInputStream(new ByteArrayInputStream(data), loader);
        return deserializer.readObject();
    }
}

