/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.internal.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamClass;
import java.io.OutputStream;
import java.io.Serializable;
import org.hibernate.Hibernate;
import org.hibernate.internal.CoreLogging;
import org.hibernate.internal.CoreMessageLogger;
import org.hibernate.type.SerializationException;

public final class SerializationHelper {
    private static final CoreMessageLogger LOG = CoreLogging.messageLogger(SerializationHelper.class);

    private SerializationHelper() {
    }

    public static Object clone(Serializable object) throws SerializationException {
        LOG.trace("Starting clone through serialization");
        if (object == null) {
            return null;
        }
        return SerializationHelper.deserialize(SerializationHelper.serialize(object), object.getClass().getClassLoader());
    }

    public static void serialize(Serializable obj, OutputStream outputStream) throws SerializationException {
        if (outputStream == null) {
            throw new IllegalArgumentException("The OutputStream must not be null");
        }
        if (LOG.isTraceEnabled()) {
            if (Hibernate.isInitialized(obj)) {
                LOG.tracev("Starting serialization of object [{0}]", obj);
            } else {
                LOG.trace("Starting serialization of [uninitialized proxy]");
            }
        }
        ObjectOutputStream out = null;
        try {
            out = new ObjectOutputStream(outputStream);
            out.writeObject(obj);
        }
        catch (IOException ex) {
            throw new SerializationException("could not serialize", ex);
        }
        finally {
            try {
                if (out != null) {
                    out.close();
                }
            }
            catch (IOException iOException) {}
        }
    }

    public static byte[] serialize(Serializable obj) throws SerializationException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(512);
        SerializationHelper.serialize(obj, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }

    public static <T> T deserialize(InputStream inputStream) throws SerializationException {
        return SerializationHelper.doDeserialize(inputStream, SerializationHelper.defaultClassLoader(), SerializationHelper.hibernateClassLoader(), null);
    }

    public static ClassLoader defaultClassLoader() {
        return Thread.currentThread().getContextClassLoader();
    }

    public static ClassLoader hibernateClassLoader() {
        return SerializationHelper.class.getClassLoader();
    }

    public static Object deserialize(InputStream inputStream, ClassLoader loader) throws SerializationException {
        return SerializationHelper.doDeserialize(inputStream, loader, SerializationHelper.defaultClassLoader(), SerializationHelper.hibernateClassLoader());
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    public static <T> T doDeserialize(InputStream inputStream, ClassLoader loader, ClassLoader fallbackLoader1, ClassLoader fallbackLoader2) throws SerializationException {
        if (inputStream == null) {
            throw new IllegalArgumentException("The InputStream must not be null");
        }
        LOG.trace("Starting deserialization of object");
        try {
            CustomObjectInputStream in = new CustomObjectInputStream(inputStream, loader, fallbackLoader1, fallbackLoader2);
            try {
                Object object = in.readObject();
                return (T)object;
            }
            catch (ClassNotFoundException e) {
                throw new SerializationException("could not deserialize", e);
            }
            catch (IOException e) {
                throw new SerializationException("could not deserialize", e);
            }
            finally {
                try {
                    in.close();
                }
                catch (IOException iOException) {}
            }
        }
        catch (IOException e) {
            throw new SerializationException("could not deserialize", e);
        }
    }

    public static Object deserialize(byte[] objectData) throws SerializationException {
        return SerializationHelper.doDeserialize(SerializationHelper.wrap(objectData), SerializationHelper.defaultClassLoader(), SerializationHelper.hibernateClassLoader(), null);
    }

    private static InputStream wrap(byte[] objectData) {
        if (objectData == null) {
            throw new IllegalArgumentException("The byte[] must not be null");
        }
        return new ByteArrayInputStream(objectData);
    }

    public static Object deserialize(byte[] objectData, ClassLoader loader) throws SerializationException {
        return SerializationHelper.doDeserialize(SerializationHelper.wrap(objectData), loader, SerializationHelper.defaultClassLoader(), SerializationHelper.hibernateClassLoader());
    }

    private static final class CustomObjectInputStream
    extends ObjectInputStream {
        private final ClassLoader loader1;
        private final ClassLoader loader2;
        private final ClassLoader loader3;

        private CustomObjectInputStream(InputStream in, ClassLoader loader1, ClassLoader loader2, ClassLoader loader3) throws IOException {
            super(in);
            this.loader1 = loader1;
            this.loader2 = loader2;
            this.loader3 = loader3;
        }

        protected Class resolveClass(ObjectStreamClass v) throws IOException, ClassNotFoundException {
            String className = v.getName();
            LOG.tracev("Attempting to locate class [{0}]", className);
            try {
                return Class.forName(className, false, this.loader1);
            }
            catch (ClassNotFoundException e) {
                LOG.trace("Unable to locate class using given classloader");
                if (this.different(this.loader1, this.loader2)) {
                    try {
                        return Class.forName(className, false, this.loader2);
                    }
                    catch (ClassNotFoundException e2) {
                        LOG.trace("Unable to locate class using given classloader");
                    }
                }
                if (this.different(this.loader1, this.loader3) && this.different(this.loader2, this.loader3)) {
                    try {
                        return Class.forName(className, false, this.loader3);
                    }
                    catch (ClassNotFoundException e3) {
                        LOG.trace("Unable to locate class using given classloader");
                    }
                }
                return super.resolveClass(v);
            }
        }

        private boolean different(ClassLoader one, ClassLoader other) {
            if (one == null) {
                return other != null;
            }
            return !one.equals(other);
        }
    }
}

