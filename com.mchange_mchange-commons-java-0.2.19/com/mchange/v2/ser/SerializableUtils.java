/*
 * Decompiled with CFR 0.152.
 */
package com.mchange.v2.ser;

import com.mchange.v1.io.InputStreamUtils;
import com.mchange.v1.io.OutputStreamUtils;
import com.mchange.v2.log.MLevel;
import com.mchange.v2.log.MLog;
import com.mchange.v2.log.MLogger;
import com.mchange.v2.ser.IndirectPolicy;
import com.mchange.v2.ser.IndirectlySerialized;
import com.mchange.v2.ser.Indirector;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.NotSerializableException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public final class SerializableUtils {
    static final MLogger logger = MLog.getLogger(SerializableUtils.class);

    private SerializableUtils() {
    }

    public static byte[] toByteArray(Object object) throws NotSerializableException {
        return SerializableUtils.serializeToByteArray(object);
    }

    public static byte[] toByteArray(Object object, Indirector indirector, IndirectPolicy indirectPolicy) throws NotSerializableException {
        try {
            if (indirectPolicy == IndirectPolicy.DEFINITELY_INDIRECT) {
                if (indirector == null) {
                    throw new IllegalArgumentException("null indirector is not consistent with " + indirectPolicy);
                }
                IndirectlySerialized indirectlySerialized = indirector.indirectForm(object);
                return SerializableUtils.toByteArray(indirectlySerialized);
            }
            if (indirectPolicy == IndirectPolicy.INDIRECT_ON_EXCEPTION) {
                if (indirector == null) {
                    throw new IllegalArgumentException("null indirector is not consistent with " + indirectPolicy);
                }
                try {
                    return SerializableUtils.toByteArray(object);
                }
                catch (NotSerializableException notSerializableException) {
                    return SerializableUtils.toByteArray(object, indirector, IndirectPolicy.DEFINITELY_INDIRECT);
                }
            }
            if (indirectPolicy == IndirectPolicy.DEFINITELY_DIRECT) {
                return SerializableUtils.toByteArray(object);
            }
            throw new InternalError("unknown indirecting policy: " + indirectPolicy);
        }
        catch (NotSerializableException notSerializableException) {
            throw notSerializableException;
        }
        catch (Exception exception) {
            if (logger.isLoggable(MLevel.WARNING)) {
                logger.log(MLevel.WARNING, "An Exception occurred while serializing an Object to a byte[] with an Indirector.", exception);
            }
            throw new NotSerializableException(exception.toString());
        }
    }

    public static byte[] serializeToByteArray(Object object) throws NotSerializableException {
        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
            objectOutputStream.writeObject(object);
            return byteArrayOutputStream.toByteArray();
        }
        catch (NotSerializableException notSerializableException) {
            notSerializableException.fillInStackTrace();
            throw notSerializableException;
        }
        catch (IOException iOException) {
            if (logger.isLoggable(MLevel.SEVERE)) {
                logger.log(MLevel.SEVERE, "An IOException occurred while writing into a ByteArrayOutputStream?!?", iOException);
            }
            throw new Error("IOException writing to a byte array!");
        }
    }

    public static Object fromByteArray(byte[] byArray) throws IOException, ClassNotFoundException {
        Object object = SerializableUtils.deserializeFromByteArray(byArray);
        if (object instanceof IndirectlySerialized) {
            return ((IndirectlySerialized)object).getObject();
        }
        return object;
    }

    public static Object fromByteArray(byte[] byArray, boolean bl) throws IOException, ClassNotFoundException {
        if (bl) {
            return SerializableUtils.deserializeFromByteArray(byArray);
        }
        return SerializableUtils.fromByteArray(byArray);
    }

    public static Object deserializeFromByteArray(byte[] byArray) throws IOException, ClassNotFoundException {
        ObjectInputStream objectInputStream = new ObjectInputStream(new ByteArrayInputStream(byArray));
        return objectInputStream.readObject();
    }

    public static Object testSerializeDeserialize(Object object) throws IOException, ClassNotFoundException {
        return SerializableUtils.deepCopy(object);
    }

    public static Object deepCopy(Object object) throws IOException, ClassNotFoundException {
        byte[] byArray = SerializableUtils.serializeToByteArray(object);
        return SerializableUtils.deserializeFromByteArray(byArray);
    }

    public static final Object unmarshallObjectFromFile(File file) throws IOException, ClassNotFoundException {
        Object object;
        ObjectInputStream objectInputStream = null;
        try {
            objectInputStream = new ObjectInputStream(new BufferedInputStream(new FileInputStream(file)));
            object = objectInputStream.readObject();
        }
        catch (Throwable throwable) {
            InputStreamUtils.attemptClose(objectInputStream);
            throw throwable;
        }
        InputStreamUtils.attemptClose(objectInputStream);
        return object;
    }

    public static final void marshallObjectToFile(Object object, File file) throws IOException {
        ObjectOutputStream objectOutputStream = null;
        try {
            objectOutputStream = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(file)));
            objectOutputStream.writeObject(object);
        }
        catch (Throwable throwable) {
            OutputStreamUtils.attemptClose(objectOutputStream);
            throw throwable;
        }
        OutputStreamUtils.attemptClose(objectOutputStream);
    }
}

