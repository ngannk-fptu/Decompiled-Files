/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.troubleshooting.stp.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Base64;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class Base64ObjectSerializerUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(Base64ObjectSerializerUtil.class);

    private Base64ObjectSerializerUtil() {
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    public static <T extends Serializable> Optional<T> deserialize(String serialized) {
        try {
            byte[] data = Base64.getDecoder().decode(serialized);
            try (ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(data));){
                Object o = ois.readObject();
                ois.close();
                Optional<Serializable> optional = Optional.of((Serializable)o);
                return optional;
            }
        }
        catch (Exception e) {
            LOGGER.error("Failed to deserialize task monitor", (Throwable)e);
            return Optional.empty();
        }
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    public static <T extends Serializable> String serialize(T object) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();){
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(object);
            oos.close();
            String string = Base64.getEncoder().encodeToString(baos.toByteArray());
            return string;
        }
        catch (Exception e) {
            LOGGER.error("Failed to serialize task monitor", (Throwable)e);
            return "";
        }
    }
}

