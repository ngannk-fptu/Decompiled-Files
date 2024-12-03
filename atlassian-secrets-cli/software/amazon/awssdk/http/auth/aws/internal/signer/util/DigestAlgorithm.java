/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.http.auth.aws.internal.signer.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import software.amazon.awssdk.annotations.SdkInternalApi;

@SdkInternalApi
public enum DigestAlgorithm {
    SHA1("SHA-1"),
    MD5("MD5"),
    SHA256("SHA-256");

    private final String algorithmName;
    private final DigestThreadLocal digestReference;

    private DigestAlgorithm(String algorithmName) {
        this.algorithmName = algorithmName;
        this.digestReference = new DigestThreadLocal(algorithmName);
    }

    public String getAlgorithmName() {
        return this.algorithmName;
    }

    public MessageDigest getDigest() {
        MessageDigest digest = (MessageDigest)this.digestReference.get();
        digest.reset();
        return digest;
    }

    private static class DigestThreadLocal
    extends ThreadLocal<MessageDigest> {
        private final String algorithmName;

        DigestThreadLocal(String algorithmName) {
            this.algorithmName = algorithmName;
        }

        @Override
        protected MessageDigest initialValue() {
            try {
                return MessageDigest.getInstance(this.algorithmName);
            }
            catch (NoSuchAlgorithmException e) {
                throw new RuntimeException("Unable to fetch message digest instance for Algorithm " + this.algorithmName + ": " + e.getMessage(), e);
            }
        }
    }
}

