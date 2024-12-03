/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.lang.Nullable
 */
package org.springframework.vault.core;

import java.util.List;
import org.springframework.lang.Nullable;
import org.springframework.vault.support.Ciphertext;
import org.springframework.vault.support.Hmac;
import org.springframework.vault.support.Plaintext;
import org.springframework.vault.support.RawTransitKey;
import org.springframework.vault.support.Signature;
import org.springframework.vault.support.SignatureValidation;
import org.springframework.vault.support.TransitKeyType;
import org.springframework.vault.support.VaultDecryptionResult;
import org.springframework.vault.support.VaultEncryptionResult;
import org.springframework.vault.support.VaultHmacRequest;
import org.springframework.vault.support.VaultSignRequest;
import org.springframework.vault.support.VaultSignatureVerificationRequest;
import org.springframework.vault.support.VaultTransitContext;
import org.springframework.vault.support.VaultTransitKey;
import org.springframework.vault.support.VaultTransitKeyConfiguration;
import org.springframework.vault.support.VaultTransitKeyCreationRequest;

public interface VaultTransitOperations {
    public void createKey(String var1);

    public void createKey(String var1, VaultTransitKeyCreationRequest var2);

    public List<String> getKeys();

    public void configureKey(String var1, VaultTransitKeyConfiguration var2);

    @Nullable
    public RawTransitKey exportKey(String var1, TransitKeyType var2);

    @Nullable
    public VaultTransitKey getKey(String var1);

    public void deleteKey(String var1);

    public void rotate(String var1);

    public String encrypt(String var1, String var2);

    public Ciphertext encrypt(String var1, Plaintext var2);

    public String encrypt(String var1, byte[] var2, VaultTransitContext var3);

    public List<VaultEncryptionResult> encrypt(String var1, List<Plaintext> var2);

    public String decrypt(String var1, String var2);

    public Plaintext decrypt(String var1, Ciphertext var2);

    public byte[] decrypt(String var1, String var2, VaultTransitContext var3);

    public List<VaultDecryptionResult> decrypt(String var1, List<Ciphertext> var2);

    public String rewrap(String var1, String var2);

    public String rewrap(String var1, String var2, VaultTransitContext var3);

    public Hmac getHmac(String var1, Plaintext var2);

    public Hmac getHmac(String var1, VaultHmacRequest var2);

    public Signature sign(String var1, Plaintext var2);

    public Signature sign(String var1, VaultSignRequest var2);

    public boolean verify(String var1, Plaintext var2, Signature var3);

    public SignatureValidation verify(String var1, VaultSignatureVerificationRequest var2);
}

