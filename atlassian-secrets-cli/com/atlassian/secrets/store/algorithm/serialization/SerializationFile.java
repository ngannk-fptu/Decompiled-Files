/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.secrets.store.algorithm.serialization;

import com.atlassian.secrets.api.SecretStoreException;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SerializationFile {
    private static final Logger log = LoggerFactory.getLogger(SerializationFile.class);
    private final String notEmptyFilePath;

    public SerializationFile(String notEmptyFilePath) {
        this.notEmptyFilePath = notEmptyFilePath;
    }

    public <T> void createFileAndSave(T objectToSave) {
        try (ObjectOutputStream objectOutputStream = new ObjectOutputStream(new FileOutputStream(this.notEmptyFilePath));){
            objectOutputStream.writeObject(objectToSave);
            log.debug("Saved file: {}", (Object)this.notEmptyFilePath);
        }
        catch (IOException e) {
            log.error("Couldn't save or create file needed for encryption/decryption. Tried to save object: {} using file path: {}", objectToSave, this.notEmptyFilePath, e);
            throw new SecretStoreException(e);
        }
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    public <T> T read(Class<T> clazz) {
        try (ObjectInputStream objectIn = new ObjectInputStream(new FileInputStream(this.notEmptyFilePath));){
            Object o = objectIn.readObject();
            if (!o.getClass().getCanonicalName().equals(clazz.getCanonicalName())) {
                throw new ClassCastException("Expected: " + clazz + ", got: " + o.getClass());
            }
            Object object = o;
            return (T)object;
        }
        catch (IOException e) {
            log.error("Couldn't read file needed for encryption/decryption. Tried to read file under: {}", (Object)this.notEmptyFilePath, (Object)e);
            throw new SecretStoreException(e);
        }
        catch (ClassCastException e) {
            log.error("Couldn't cast object found under: {} Make sure you are passing correct file path.", (Object)this.notEmptyFilePath, (Object)e);
            throw new SecretStoreException(e);
        }
        catch (ClassNotFoundException e) {
            log.error("Couldn't find class for object found under: {} Make sure you are passing correct file path.", (Object)this.notEmptyFilePath, (Object)e);
            throw new SecretStoreException(e);
        }
    }
}

