/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.secrets.store.algorithm;

import com.atlassian.secrets.store.algorithm.AlgorithmSecretStore;
import com.atlassian.secrets.store.algorithm.paramters.DecryptionParameters;
import com.atlassian.secrets.store.algorithm.paramters.EncryptionParameters;
import com.atlassian.secrets.store.algorithm.serialization.SerializationFileFactory;
import java.time.Clock;
import java.util.Objects;
import java.util.function.Function;
import javax.crypto.SealedObject;

public class AesOnlyAlgorithmSecretStore
extends AlgorithmSecretStore {
    public AesOnlyAlgorithmSecretStore() {
    }

    AesOnlyAlgorithmSecretStore(SerializationFileFactory factory, Clock clock, Function<String, String> getSystemEnv) {
        super(factory, clock, getSystemEnv);
    }

    @Override
    protected DecryptionParameters encrypt(EncryptionParameters parameters) {
        if (this.isAlgorithmRestricted(parameters.getAlgorithm())) {
            throw new IllegalArgumentException(String.format("Cannot encrypt with algorithm %s as it's not allowed", parameters.getAlgorithm()));
        }
        return super.encrypt(parameters);
    }

    @Override
    protected SealedObject getEncryptedPassword(DecryptionParameters dataToDecrypt) {
        SealedObject encryptedPassword = super.getEncryptedPassword(dataToDecrypt);
        if (this.isAlgorithmRestricted(encryptedPassword.getAlgorithm())) {
            throw new IllegalArgumentException(String.format("Cannot decrypt algorithm %s as it's not allowed", encryptedPassword.getAlgorithm()));
        }
        return encryptedPassword;
    }

    private boolean isAlgorithmRestricted(String algorithm) {
        String[] algorithmSplitIntoTransforms = algorithm.split("/");
        String algorithmToCompare = algorithmSplitIntoTransforms.length > 0 ? algorithmSplitIntoTransforms[0] : algorithm;
        return !Objects.equals(algorithmToCompare, "AES");
    }
}

