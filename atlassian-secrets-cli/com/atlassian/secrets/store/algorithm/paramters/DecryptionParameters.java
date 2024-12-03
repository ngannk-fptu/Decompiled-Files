/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.secrets.store.algorithm.paramters;

public class DecryptionParameters {
    private final String sealedObjectFilePath;
    private final String keyFilePath;
    private final String serializedSealedObject;

    public DecryptionParameters(String sealedObjectFilePath, String keyFilePath, String serializedSealedObject) {
        this.sealedObjectFilePath = sealedObjectFilePath;
        this.keyFilePath = keyFilePath;
        this.serializedSealedObject = serializedSealedObject;
    }

    public String getSealedObjectFilePath() {
        return this.sealedObjectFilePath;
    }

    public String getKeyFilePath() {
        return this.keyFilePath;
    }

    public String getSerializedSealedObject() {
        return this.serializedSealedObject;
    }

    public String toString() {
        return "DecryptionParameters{sealedObjectFilePath='" + this.sealedObjectFilePath + '\'' + ", keyFilePath='" + this.keyFilePath + '\'' + ", serializedSealedObject='" + this.serializedSealedObject + '\'' + '}';
    }

    public static class Builder {
        private String sealedObjectFilePath;
        private String keyFilePath;
        private String serializedSealedObject;

        public Builder setSealedObjectFilePath(String sealedObjectFilePath) {
            this.sealedObjectFilePath = sealedObjectFilePath;
            return this;
        }

        public Builder setKeyFilePath(String keyFilePath) {
            this.keyFilePath = keyFilePath;
            return this;
        }

        public Builder serializedSealedObject(String serializedSealedObject) {
            this.serializedSealedObject = serializedSealedObject;
            return this;
        }

        public DecryptionParameters build() {
            return new DecryptionParameters(this.sealedObjectFilePath, this.keyFilePath, this.serializedSealedObject);
        }
    }
}

