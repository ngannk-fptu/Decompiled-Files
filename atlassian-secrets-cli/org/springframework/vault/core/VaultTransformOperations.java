/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.vault.core;

import java.util.List;
import org.springframework.vault.support.TransformCiphertext;
import org.springframework.vault.support.TransformPlaintext;
import org.springframework.vault.support.VaultTransformContext;
import org.springframework.vault.support.VaultTransformDecodeResult;
import org.springframework.vault.support.VaultTransformEncodeResult;

public interface VaultTransformOperations {
    public String encode(String var1, String var2);

    public TransformCiphertext encode(String var1, TransformPlaintext var2);

    default public TransformCiphertext encode(String roleName, byte[] plaintext, VaultTransformContext transformRequest) {
        return this.encode(roleName, TransformPlaintext.of(plaintext).with(transformRequest));
    }

    public List<VaultTransformEncodeResult> encode(String var1, List<TransformPlaintext> var2);

    default public String decode(String roleName, String ciphertext) {
        return this.decode(roleName, TransformCiphertext.of(ciphertext)).asString();
    }

    public TransformPlaintext decode(String var1, TransformCiphertext var2);

    public String decode(String var1, String var2, VaultTransformContext var3);

    public List<VaultTransformDecodeResult> decode(String var1, List<TransformCiphertext> var2);
}

