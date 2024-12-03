/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.Internal
 */
package com.atlassian.vcache.marshallers;

import com.atlassian.annotations.Internal;
import com.atlassian.vcache.Marshaller;
import com.atlassian.vcache.MarshallerException;
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

@Deprecated
@Internal
class JavaSerializationMarshaller<T extends Serializable>
implements Marshaller<T> {
    private final Class<T> clazz;
    private final Optional<ClassLoader> loader;

    JavaSerializationMarshaller(Class<T> clazz) {
        this.clazz = Objects.requireNonNull(clazz);
        this.loader = Optional.empty();
    }

    JavaSerializationMarshaller(Class<T> clazz, ClassLoader loader) {
        this.clazz = Objects.requireNonNull(clazz);
        this.loader = Optional.of(loader);
    }

    @Override
    public byte[] marshall(T obj) throws MarshallerException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (ObjectOutputStream oos = new ObjectOutputStream(baos);){
            oos.writeObject(obj);
        }
        catch (IOException ioe) {
            throw new MarshallerException("Unable to marshall", ioe);
        }
        return baos.toByteArray();
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    @Override
    public T unmarshall(byte[] raw) throws MarshallerException {
        ByteArrayInputStream bais = new ByteArrayInputStream(raw);
        try (ObjectInputStream ois = this.createObjectInputStream(bais);){
            Serializable serializable = (Serializable)this.clazz.cast(ois.readObject());
            return (T)serializable;
        }
        catch (IOException | ClassCastException | ClassNotFoundException ex) {
            throw new MarshallerException("Unable to unmarshall", ex);
        }
    }

    private ObjectInputStream createObjectInputStream(InputStream istr) throws IOException {
        return this.loader.isPresent() ? new ObjectInputStreamWithLoader(istr, this.loader.get()) : new ObjectInputStream(istr);
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

