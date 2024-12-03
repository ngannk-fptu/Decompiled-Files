/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.ObjectUtils
 *  org.apache.commons.lang3.SerializationUtils
 *  org.bouncycastle.jce.provider.BouncyCastleProvider
 */
package com.atlassian.secrets.store.algorithm;

import com.atlassian.secrets.api.SecretStore;
import com.atlassian.secrets.api.SecretStoreException;
import com.atlassian.secrets.store.algorithm.KeyWithPath;
import com.atlassian.secrets.store.algorithm.paramters.DecryptionParameters;
import com.atlassian.secrets.store.algorithm.paramters.EncryptionParameters;
import com.atlassian.secrets.store.algorithm.serialization.EnvironmentVarBasedConfiguration;
import com.atlassian.secrets.store.algorithm.serialization.SerializationFileFactory;
import com.atlassian.secrets.store.algorithm.serialization.UniqueFilePathGenerator;
import com.google.gson.Gson;
import java.io.Serializable;
import java.security.AlgorithmParameterGenerator;
import java.security.AlgorithmParameters;
import java.security.Key;
import java.security.Provider;
import java.security.SecureRandom;
import java.security.Security;
import java.time.Clock;
import java.util.Base64;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.UnaryOperator;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SealedObject;
import javax.crypto.spec.SecretKeySpec;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.SerializationUtils;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AlgorithmSecretStore
implements SecretStore {
    private static final Logger log = LoggerFactory.getLogger(AlgorithmSecretStore.class);
    private final Provider provider = new BouncyCastleProvider();
    private final Gson gson = new Gson();
    private final SerializationFileFactory factory;
    private Clock clock = Clock.systemUTC();
    private Function<String, String> getSystemEnv = System::getenv;

    public AlgorithmSecretStore() {
        log.debug("Initiate AlgorithmCipher");
        Security.addProvider(this.provider);
        this.factory = new SerializationFileFactory();
    }

    AlgorithmSecretStore(SerializationFileFactory factory, Clock clock, Function<String, String> getSystemEnv) {
        this.factory = factory;
        this.clock = clock;
        this.getSystemEnv = getSystemEnv;
    }

    @Override
    public String store(String encryptionParamsInJson) {
        log.debug("Encrypting data...");
        String encrypted = this.gson.toJson(this.encrypt(this.gson.fromJson(encryptionParamsInJson, EncryptionParameters.class)));
        log.debug("Encryption done.");
        return encrypted;
    }

    @Override
    public String get(String decryptionParamsInJson) {
        log.debug("Decrypting data...");
        String decrypted = this.decrypt(this.gson.fromJson(decryptionParamsInJson, DecryptionParameters.class)).getPlainTextPassword();
        log.debug("Decryption done.");
        return decrypted;
    }

    private EncryptionParameters decrypt(DecryptionParameters dataToDecrypt) {
        try {
            String plainTextPassword = (String)this.getEncryptedPassword(dataToDecrypt).getObject(this.tryFromParamsThenEnvThenThrow(dataToDecrypt.getKeyFilePath(), SecretKeySpec.class));
            return new EncryptionParameters.Builder().setPlainTextPassword(plainTextPassword).build();
        }
        catch (Exception e) {
            log.error("Runtime Exception thrown when decrypting: {}", (Object)dataToDecrypt, (Object)e);
            throw new SecretStoreException(e);
        }
    }

    protected SealedObject getEncryptedPassword(DecryptionParameters dataToDecrypt) {
        return Optional.ofNullable(dataToDecrypt.getSerializedSealedObject()).map(this::base64ToObject).orElseGet(() -> this.tryFromParamsThenEnvThenThrow(dataToDecrypt.getSealedObjectFilePath(), SealedObject.class));
    }

    protected DecryptionParameters encrypt(EncryptionParameters parameters) {
        try {
            String base64encoded;
            String pathToSealedObject;
            UnaryOperator buildPath = fileName -> this.buildFilePath(parameters.getOutputFilesBasePath(), (String)fileName);
            AlgorithmParameters algorithmParameters = this.getAlgorithmParameters(parameters, buildPath);
            KeyWithPath keyWithPath = this.getKeyOrGenerateNewAndGet(parameters, buildPath);
            SecretKeySpec key = keyWithPath.getSecretKeySpec();
            Cipher cipher = Cipher.getInstance(parameters.getAlgorithm(), this.provider);
            cipher.init(1, (Key)key, algorithmParameters);
            SealedObject encryptedPass = new SealedObject((Serializable)((Object)parameters.getPlainTextPassword()), cipher);
            if (parameters.isSaveSealedObjectToSeparateFile()) {
                pathToSealedObject = (String)buildPath.apply(this.generateFileName(encryptedPass.getClass().getName()));
                this.factory.getSerializationFile(pathToSealedObject).createFileAndSave(encryptedPass);
                base64encoded = null;
            } else {
                base64encoded = this.objectToBase64(encryptedPass);
                pathToSealedObject = null;
            }
            return new DecryptionParameters.Builder().setSealedObjectFilePath(pathToSealedObject).serializedSealedObject(base64encoded).setKeyFilePath(keyWithPath.getPath()).build();
        }
        catch (Exception e) {
            log.error("Exception thrown when encrypting: {}", (Object)parameters, (Object)e);
            throw new SecretStoreException(e);
        }
    }

    private AlgorithmParameters getAlgorithmParameters(EncryptionParameters parameters, UnaryOperator<String> buildPath) {
        AlgorithmParameters algorithmParameters;
        String algParamsPath = (String)ObjectUtils.firstNonNull((Object[])new String[]{parameters.getAlgorithmParametersFilePath(), this.getFromEnv(AlgorithmParameters.class.getName())});
        if (algParamsPath == null) {
            algorithmParameters = this.generateAlgorithmParameters(parameters.getAlgorithmKey());
            if (parameters.isSaveAlgorithmParametersToSeparateFile()) {
                algParamsPath = (String)buildPath.apply(this.generateFileName(AlgorithmParameters.class.getName()));
                this.factory.getAlgorithmParametersSerializationFile(algParamsPath).createFileAndSave(algorithmParameters);
                log.debug("Name of generated file with algorithm params used for encryption: {}", (Object)algParamsPath);
            } else {
                log.debug("Generation of file for algorithm params has been skipped");
            }
        } else {
            algorithmParameters = this.factory.getAlgorithmParametersSerializationFile(algParamsPath).read(parameters.getAlgorithmKey());
        }
        return algorithmParameters;
    }

    private KeyWithPath getKeyOrGenerateNewAndGet(EncryptionParameters parameters, UnaryOperator<String> buildPath) {
        SecretKeySpec key;
        String keyPath = (String)ObjectUtils.firstNonNull((Object[])new String[]{parameters.getKeyFilePath(), this.getFromEnv(SecretKeySpec.class.getName())});
        if (keyPath == null) {
            key = this.generateSecretKey(parameters.getAlgorithmKey());
            keyPath = (String)buildPath.apply(this.generateFileName(SecretKeySpec.class.getName()));
            this.factory.getSerializationFile(keyPath).createFileAndSave(key);
        } else {
            key = this.factory.getSerializationFile(keyPath).read(SecretKeySpec.class);
        }
        return new KeyWithPath(key, keyPath);
    }

    private SecretKeySpec generateSecretKey(String algorithmKey) {
        try {
            KeyGenerator keyGen = KeyGenerator.getInstance(algorithmKey, this.provider);
            keyGen.init(this.getKeySize(algorithmKey));
            return (SecretKeySpec)keyGen.generateKey();
        }
        catch (Exception e) {
            log.error("Exception thrown when generating key for algorithm: {}", (Object)algorithmKey, (Object)e);
            throw new SecretStoreException(e);
        }
    }

    private AlgorithmParameters generateAlgorithmParameters(String algorithmKey) {
        try {
            AlgorithmParameterGenerator algorithmParameterGenerator = AlgorithmParameterGenerator.getInstance(algorithmKey, this.provider);
            algorithmParameterGenerator.init(new SecureRandom().nextInt(), new SecureRandom());
            return algorithmParameterGenerator.generateParameters();
        }
        catch (Exception e) {
            log.error("Exception thrown when generating algorithm parameters for algorithm: {}", (Object)algorithmKey, (Object)e);
            throw new SecretStoreException(e);
        }
    }

    private int getKeySize(String algorithmKey) {
        switch (algorithmKey) {
            case "AES": {
                return 128;
            }
            case "DES": {
                return 56;
            }
            case "DESede": {
                return 168;
            }
        }
        return 24;
    }

    private String getFromEnv(String objectClassName) {
        EnvironmentVarBasedConfiguration environmentVarBasedConfiguration = new EnvironmentVarBasedConfiguration(objectClassName, this.getSystemEnv);
        return environmentVarBasedConfiguration.getFromEnv();
    }

    private String generateFileName(String objectClassName) {
        return new UniqueFilePathGenerator(objectClassName, this.clock).generateName();
    }

    private String buildFilePath(String basePath, String relativePath) {
        return Optional.of(relativePath).map(p -> Optional.ofNullable(basePath).orElse("") + p).orElse(null);
    }

    private SealedObject base64ToObject(String base64) {
        byte[] decoded = Base64.getDecoder().decode(base64);
        return (SealedObject)SerializationUtils.deserialize((byte[])decoded);
    }

    private String objectToBase64(Serializable obj) {
        byte[] serializedBytes = SerializationUtils.serialize((Serializable)obj);
        return Base64.getEncoder().encodeToString(serializedBytes);
    }

    private <T> T tryFromParamsThenEnvThenThrow(String filePathFromParams, Class<T> clz) {
        String keyFilePath = Optional.ofNullable(filePathFromParams).orElseGet(() -> Optional.ofNullable(this.getFromEnv(clz.getName())).orElseThrow(() -> new IllegalArgumentException("Missing file path for: " + clz.getName())));
        return this.factory.getSerializationFile(keyFilePath).read(clz);
    }
}

