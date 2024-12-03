/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.auth.signer.internal;

import java.security.NoSuchAlgorithmException;
import javax.crypto.Mac;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.core.exception.SdkClientException;

@SdkInternalApi
public enum SigningAlgorithm {
    HmacSHA256;

    private final ThreadLocal<Mac> macReference;

    private SigningAlgorithm() {
        String algorithmName = this.toString();
        this.macReference = new MacThreadLocal(algorithmName);
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
                throw SdkClientException.builder().message("Unable to fetch Mac instance for Algorithm " + this.algorithmName + e.getMessage()).cause(e).build();
            }
        }
    }
}

