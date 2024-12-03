/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.secrets.tomcat.cipher;

import com.atlassian.secrets.api.SecretStoreException;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class SerializationService {
    public <T> void save(String destPath, T object) {
        try (ObjectOutputStream objectOutputStream = new ObjectOutputStream(new FileOutputStream(destPath));){
            objectOutputStream.writeObject(object);
        }
        catch (IOException e) {
            throw new SecretStoreException(e);
        }
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    public <T> T load(String scrPath, Class<T> clazz) {
        try (ObjectInputStream objectIn = new ObjectInputStream(new FileInputStream(scrPath));){
            Object passwordDataBean = objectIn.readObject();
            if (!passwordDataBean.getClass().getCanonicalName().equals(clazz.getCanonicalName())) {
                throw new ClassCastException("Expected: " + clazz + ", got: " + passwordDataBean.getClass());
            }
            Object object = passwordDataBean;
            return (T)object;
        }
        catch (IOException | ClassCastException | ClassNotFoundException e) {
            throw new SecretStoreException(e);
        }
    }
}

