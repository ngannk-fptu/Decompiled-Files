/*
 * Decompiled with CFR 0.152.
 */
package com.mchange.lang;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.NotSerializableException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public final class ObjectUtils {
    public static final Object DUMMY_OBJECT = new Object();

    private ObjectUtils() {
    }

    public static byte[] objectToByteArray(Object object) throws NotSerializableException {
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
            iOException.printStackTrace();
            throw new Error("IOException writing to a byte array!");
        }
    }

    public static Object objectFromByteArray(byte[] byArray) throws IOException, ClassNotFoundException {
        ObjectInputStream objectInputStream = new ObjectInputStream(new ByteArrayInputStream(byArray));
        return objectInputStream.readObject();
    }
}

