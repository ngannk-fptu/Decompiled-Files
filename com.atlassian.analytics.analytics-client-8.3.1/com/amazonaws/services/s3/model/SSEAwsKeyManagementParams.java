/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.services.s3.model;

import com.amazonaws.services.s3.model.SSEAlgorithm;
import java.io.Serializable;

public class SSEAwsKeyManagementParams
implements Serializable {
    private String awsKmsKeyId;
    private String awsKmsEncryptionContext;

    public SSEAwsKeyManagementParams() {
        this.awsKmsEncryptionContext = null;
        this.awsKmsKeyId = null;
    }

    public SSEAwsKeyManagementParams(String awsKmsKeyId) {
        if (awsKmsKeyId == null || awsKmsKeyId.trim().isEmpty()) {
            throw new IllegalArgumentException("AWS Key Management System Key id cannot be null");
        }
        this.awsKmsKeyId = awsKmsKeyId;
        this.awsKmsEncryptionContext = null;
    }

    public String getAwsKmsKeyId() {
        return this.awsKmsKeyId;
    }

    public SSEAwsKeyManagementParams withAwsKmsKeyId(String awsKmsKeyId) {
        this.setAwsKmsKeyId(awsKmsKeyId);
        return this;
    }

    private void setAwsKmsKeyId(String awsKmsKeyId) {
        this.awsKmsKeyId = awsKmsKeyId;
    }

    public String getEncryption() {
        return SSEAlgorithm.KMS.getAlgorithm();
    }

    public String getAwsKmsEncryptionContext() {
        return this.awsKmsEncryptionContext;
    }

    public SSEAwsKeyManagementParams withAwsKmsEncryptionContext(String awsKmsEncryptionContext) {
        this.setAwsKmsEncryptionContext(awsKmsEncryptionContext);
        return this;
    }

    private void setAwsKmsEncryptionContext(String awsKmsEncryptionContext) {
        this.awsKmsEncryptionContext = awsKmsEncryptionContext;
    }
}

