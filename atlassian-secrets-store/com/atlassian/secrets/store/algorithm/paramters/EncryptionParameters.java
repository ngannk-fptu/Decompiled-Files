/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.BooleanUtils
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.secrets.store.algorithm.paramters;

import java.io.File;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

public class EncryptionParameters {
    private final String plainTextPassword;
    private final String algorithm;
    private final String algorithmKey;
    private final String algorithmParametersFilePath;
    private final String keyFilePath;
    private final String outputFilesBasePath;
    private final Boolean saveAlgorithmParametersToSeparateFile;
    private final Boolean saveSealedObjectToSeparateFile;

    public EncryptionParameters(Builder builder) {
        this.plainTextPassword = builder.plainTextPassword;
        this.algorithm = builder.algorithm;
        this.algorithmKey = builder.algorithmKey;
        this.algorithmParametersFilePath = builder.algorithmParametersFilePath;
        this.keyFilePath = builder.keyFilePath;
        this.outputFilesBasePath = builder.outputFilesBasePath;
        this.saveAlgorithmParametersToSeparateFile = builder.saveAlgorithmParametersToSeparateFile;
        this.saveSealedObjectToSeparateFile = builder.saveSealedObjectToSeparateFile;
    }

    public String getPlainTextPassword() {
        return this.plainTextPassword;
    }

    public String getAlgorithm() {
        return this.algorithm;
    }

    public String getAlgorithmKey() {
        return this.algorithmKey;
    }

    public String getAlgorithmParametersFilePath() {
        return this.algorithmParametersFilePath;
    }

    public String getKeyFilePath() {
        return this.keyFilePath;
    }

    public String getOutputFilesBasePath() {
        return this.outputFilesBasePath;
    }

    public boolean isSaveAlgorithmParametersToSeparateFile() {
        return BooleanUtils.toBooleanDefaultIfNull((Boolean)this.saveAlgorithmParametersToSeparateFile, (boolean)true);
    }

    public boolean isSaveSealedObjectToSeparateFile() {
        return BooleanUtils.toBooleanDefaultIfNull((Boolean)this.saveSealedObjectToSeparateFile, (boolean)true);
    }

    public String toString() {
        return "EncryptionParameters{plainTextPassword='<SANITIZED>', algorithm='" + this.algorithm + '\'' + ", algorithmKey='" + this.algorithmKey + '\'' + ", algorithmParametersFilePath='" + this.algorithmParametersFilePath + '\'' + ", keyFilePath='" + this.keyFilePath + '\'' + ", outputFilesBasePath='" + this.outputFilesBasePath + '\'' + ", saveAlgorithmParametersToSeparateFile='" + this.saveAlgorithmParametersToSeparateFile + '\'' + ", saveSealedObjectToSeparateFile='" + this.saveSealedObjectToSeparateFile + '\'' + '}';
    }

    public static class Builder {
        private String plainTextPassword;
        private String algorithm;
        private String algorithmKey;
        private String algorithmParametersFilePath;
        private String keyFilePath;
        private String outputFilesBasePath;
        private Boolean saveAlgorithmParametersToSeparateFile;
        private Boolean saveSealedObjectToSeparateFile;

        public Builder setPlainTextPassword(String plainTextPassword) {
            this.plainTextPassword = plainTextPassword;
            return this;
        }

        public Builder setAlgorithm(String algorithm) {
            this.algorithm = algorithm;
            return this;
        }

        public Builder setAlgorithmKey(String algorithmKey) {
            this.algorithmKey = algorithmKey;
            return this;
        }

        public Builder setAlgorithmParametersFilePath(String algorithmParametersFilePath) {
            this.algorithmParametersFilePath = algorithmParametersFilePath;
            return this;
        }

        public Builder setKeyFilePath(String keyFilePath) {
            this.keyFilePath = keyFilePath;
            return this;
        }

        public Builder setOutputFilesBasePath(String outputFilesBasePath) {
            if (StringUtils.isNotEmpty((CharSequence)outputFilesBasePath) && !outputFilesBasePath.endsWith(File.separator)) {
                throw new IllegalArgumentException("Base path must be ended with " + File.separator);
            }
            this.outputFilesBasePath = outputFilesBasePath;
            return this;
        }

        public Builder setSaveAlgorithmParametersToSeparateFile(Boolean saveAlgorithmParametersToSeparateFile) {
            this.saveAlgorithmParametersToSeparateFile = saveAlgorithmParametersToSeparateFile;
            return this;
        }

        public Builder setSaveSealedObjectToSeparateFile(Boolean saveSealedObjectToSeparateFile) {
            this.saveSealedObjectToSeparateFile = saveSealedObjectToSeparateFile;
            return this;
        }

        public EncryptionParameters build() {
            return new EncryptionParameters(this);
        }
    }
}

