/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.secrets.store.algorithm.serialization;

import com.atlassian.secrets.api.SecretStoreException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.security.AlgorithmParameters;
import java.security.NoSuchAlgorithmException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AlgorithmParametersSerializationFile {
    private static final Logger log = LoggerFactory.getLogger(AlgorithmParametersSerializationFile.class);
    private final String notEmptyFilePath;

    public AlgorithmParametersSerializationFile(String notEmptyFilePath) {
        this.notEmptyFilePath = notEmptyFilePath;
    }

    public void createFileAndSave(AlgorithmParameters objectToSave) {
        try (FileOutputStream fileOutputStream = new FileOutputStream(this.notEmptyFilePath);){
            fileOutputStream.write(objectToSave.getEncoded());
        }
        catch (IOException e) {
            log.error("Couldn't save or create file needed for encryption/decryption. Tried to save object: {} using file path: {}", objectToSave, this.notEmptyFilePath, e);
            throw new SecretStoreException(e);
        }
    }

    public AlgorithmParameters read(String algorithm) {
        try {
            AlgorithmParameters parameters = AlgorithmParameters.getInstance(algorithm);
            parameters.init(Files.readAllBytes(new File(this.notEmptyFilePath).toPath()));
            return parameters;
        }
        catch (IOException e) {
            log.error("Couldn't read file needed for encryption/decryption. Tried to read file under: {}", (Object)this.notEmptyFilePath, (Object)e);
            throw new SecretStoreException(e);
        }
        catch (NoSuchAlgorithmException e) {
            log.error("Wrong algorithm key passed: {}", (Object)algorithm);
            throw new SecretStoreException(e);
        }
    }
}

