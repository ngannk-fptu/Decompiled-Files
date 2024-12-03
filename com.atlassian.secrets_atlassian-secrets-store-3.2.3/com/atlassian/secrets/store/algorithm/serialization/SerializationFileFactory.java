/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.secrets.store.algorithm.serialization;

import com.atlassian.secrets.store.algorithm.serialization.AlgorithmParametersSerializationFile;
import com.atlassian.secrets.store.algorithm.serialization.SerializationFile;

public class SerializationFileFactory {
    public SerializationFile getSerializationFile(String notEmptyFilePath) {
        return new SerializationFile(notEmptyFilePath);
    }

    public AlgorithmParametersSerializationFile getAlgorithmParametersSerializationFile(String notEmptyFilePath) {
        return new AlgorithmParametersSerializationFile(notEmptyFilePath);
    }
}

