/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.services.s3.model;

import com.amazonaws.services.s3.model.SSEAlgorithm;
import com.amazonaws.util.Base64;
import java.io.Serializable;
import javax.crypto.SecretKey;

public class SSECustomerKey
implements Serializable {
    private final String base64EncodedKey;
    private String base64EncodedMd5;
    private String algorithm;

    public SSECustomerKey(String base64EncodedKey) {
        if (base64EncodedKey == null || base64EncodedKey.length() == 0) {
            throw new IllegalArgumentException("Encryption key must be specified");
        }
        this.algorithm = SSEAlgorithm.AES256.getAlgorithm();
        this.base64EncodedKey = base64EncodedKey;
    }

    public SSECustomerKey(byte[] rawKeyMaterial) {
        if (rawKeyMaterial == null || rawKeyMaterial.length == 0) {
            throw new IllegalArgumentException("Encryption key must be specified");
        }
        this.algorithm = SSEAlgorithm.AES256.getAlgorithm();
        this.base64EncodedKey = Base64.encodeAsString(rawKeyMaterial);
    }

    public SSECustomerKey(SecretKey key) {
        if (key == null) {
            throw new IllegalArgumentException("Encryption key must be specified");
        }
        this.algorithm = SSEAlgorithm.AES256.getAlgorithm();
        this.base64EncodedKey = Base64.encodeAsString(key.getEncoded());
    }

    private SSECustomerKey() {
        this.base64EncodedKey = null;
    }

    public String getKey() {
        return this.base64EncodedKey;
    }

    public String getAlgorithm() {
        return this.algorithm;
    }

    public void setAlgorithm(String algorithm) {
        this.algorithm = algorithm;
    }

    public SSECustomerKey withAlgorithm(String algorithm) {
        this.setAlgorithm(algorithm);
        return this;
    }

    public String getMd5() {
        return this.base64EncodedMd5;
    }

    public void setMd5(String md5Digest) {
        this.base64EncodedMd5 = md5Digest;
    }

    public SSECustomerKey withMd5(String md5Digest) {
        this.setMd5(md5Digest);
        return this;
    }

    public static SSECustomerKey generateSSECustomerKeyForPresignUrl(String algorithm) {
        if (algorithm == null) {
            throw new IllegalArgumentException();
        }
        return new SSECustomerKey().withAlgorithm(algorithm);
    }
}

