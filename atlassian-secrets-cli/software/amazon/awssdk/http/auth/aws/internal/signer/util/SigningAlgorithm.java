/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.http.auth.aws.internal.signer.util;

import java.security.NoSuchAlgorithmException;
import javax.crypto.Mac;
import software.amazon.awssdk.annotations.SdkInternalApi;

@SdkInternalApi
public enum SigningAlgorithm {
    HMAC_SHA256("HmacSHA256");

    private final String algorithmName;
    private final ThreadLocal<Mac> macReference;

    private SigningAlgorithm(String algorithmName) {
        this.algorithmName = algorithmName;
        this.macReference = new MacThreadLocal(algorithmName);
    }

    public String getAlgorithmName() {
        return this.algorithmName;
    }

    public Mac getMac() {
        return this.macReference.get();
    }

    private static class MacThreadLocal
    extends ThreadLocal<Mac> {
        private final String algorithmName;

        MacThreadLocal(String algorithmName) {
            this.algorithmName = algorithmName;
        }

        @Override
        protected Mac initialValue() {
            try {
                return Mac.getInstance(this.algorithmName);
            }
            catch (NoSuchAlgorithmException e) {
                throw new RuntimeException("Unable to fetch Mac instance for Algorithm " + this.algorithmName + ": " + e.getMessage());
            }
        }
    }
}

