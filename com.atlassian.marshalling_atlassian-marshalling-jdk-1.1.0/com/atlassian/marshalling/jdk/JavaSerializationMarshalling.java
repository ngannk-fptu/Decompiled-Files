/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.PublicApi
 *  com.atlassian.marshalling.api.Marshaller
 *  com.atlassian.marshalling.api.MarshallingException
 *  com.atlassian.marshalling.api.MarshallingPair
 *  com.atlassian.marshalling.api.Unmarshaller
 */
package com.atlassian.marshalling.jdk;

import com.atlassian.annotations.PublicApi;
import com.atlassian.marshalling.api.Marshaller;
import com.atlassian.marshalling.api.MarshallingException;
import com.atlassian.marshalling.api.MarshallingPair;
import com.atlassian.marshalling.api.Unmarshaller;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamClass;
import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;

@PublicApi
public class JavaSerializationMarshalling<T extends Serializable>
implements Marshaller<T>,
Unmarshaller<T> {
    private final Class<T> clazz;
    private final Optional<ClassLoader> loader;

    public JavaSerializationMarshalling(Class<T> clazz) {
        this.clazz = Objects.requireNonNull(clazz);
        this.loader = Optional.empty();
    }

    public JavaSerializationMarshalling(Class<T> clazz, ClassLoader loader) {
        this.clazz = Objects.requireNonNull(clazz);
        this.loader = Optional.of(loader);
    }

    public byte[] marshallToBytes(T obj) throws MarshallingException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (ObjectOutputStream oos = new ObjectOutputStream(baos);){
            oos.writeObject(obj);
        }
        catch (IOException ioe) {
            throw new MarshallingException("Unable to marshall", (Throwable)ioe);
        }
        return baos.toByteArray();
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    public T unmarshallFrom(byte[] raw) throws MarshallingException {
        ByteArrayInputStream bais = new ByteArrayInputStream(raw);
        try (ObjectInputStream ois = this.createObjectInputStream(bais);){
            Serializable serializable = (Serializable)this.clazz.cast(ois.readObject());
            return (T)serializable;
        }
        catch (IOException | ClassCastException | ClassNotFoundException ex) {
            throw new MarshallingException("Unable to unmarshall", (Throwable)ex);
        }
    }

    private ObjectInputStream createObjectInputStream(InputStream istr) throws IOException {
        return this.loader.isPresent() ? new ObjectInputStreamWithLoader(istr, this.loader.get()) : new ObjectInputStream(istr);
    }

    public static <T extends Serializable> MarshallingPair<T> pair(Class<T> clazz) {
        JavaSerializationMarshalling<T> jsm = new JavaSerializationMarshalling<T>(clazz);
        return new MarshallingPair(jsm, jsm);
    }

    public static <T extends Serializable> MarshallingPair<T> pair(Class<T> clazz, ClassLoader loader) {
        JavaSerializationMarshalling<T> jsm = new JavaSerializationMarshalling<T>(clazz, loader);
        return new MarshallingPair(jsm, jsm);
    }

    private static class ObjectInputStreamWithLoader
    extends ObjectInputStream {
        private final ClassLoader loader;

        public ObjectInputStreamWithLoader(InputStream in, ClassLoader loader) throws IOException {
            super(in);
            this.loader = Objects.requireNonNull(loader);
        }

        @Override
        protected Class<?> resolveClass(ObjectStreamClass desc) throws IOException, ClassNotFoundException {
            return Class.forName(desc.getName(), false, this.loader);
        }
    }
}

