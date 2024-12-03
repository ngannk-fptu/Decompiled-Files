/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.lang;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import org.apache.commons.lang.SerializationException;

public class SerializationUtils {
    public static Object clone(Serializable object) {
        return SerializationUtils.deserialize(SerializationUtils.serialize(object));
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    public static void serialize(Serializable obj, OutputStream outputStream) {
        if (outputStream == null) {
            throw new IllegalArgumentException("The OutputStream must not be null");
        }
        ObjectOutputStream out = null;
        try {
            try {
                out = new ObjectOutputStream(outputStream);
                out.writeObject(obj);
            }
            catch (IOException ex) {
                throw new SerializationException(ex);
            }
            Object var5_3 = null;
        }
        catch (Throwable throwable) {
            Object var5_4 = null;
            try {
                if (out == null) throw throwable;
                out.close();
                throw throwable;
            }
            catch (IOException ex) {
                // empty catch block
            }
            throw throwable;
        }
        try {}
        catch (IOException ex) {}
        if (out == null) return;
        out.close();
        return;
    }

    public static byte[] serialize(Serializable obj) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream(512);
        SerializationUtils.serialize(obj, baos);
        return baos.toByteArray();
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    public static Object deserialize(InputStream inputStream) {
        Object object;
        if (inputStream == null) {
            throw new IllegalArgumentException("The InputStream must not be null");
        }
        ObjectInputStream in = null;
        try {
            try {
                in = new ObjectInputStream(inputStream);
                object = in.readObject();
                Object var4_5 = null;
            }
            catch (ClassNotFoundException ex) {
                throw new SerializationException(ex);
            }
            catch (IOException ex) {
                throw new SerializationException(ex);
            }
        }
        catch (Throwable throwable) {
            Object var4_6 = null;
            try {
                if (in == null) throw throwable;
                in.close();
                throw throwable;
            }
            catch (IOException ex) {
                throw throwable;
            }
        }
        try {}
        catch (IOException ex) {
            // empty catch block
            return object;
        }
        if (in == null) return object;
        in.close();
        return object;
    }

    public static Object deserialize(byte[] objectData) {
        if (objectData == null) {
            throw new IllegalArgumentException("The byte[] must not be null");
        }
        ByteArrayInputStream bais = new ByteArrayInputStream(objectData);
        return SerializationUtils.deserialize(bais);
    }
}

