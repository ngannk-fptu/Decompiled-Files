/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.security.auth.trustedapps;

import com.atlassian.security.auth.trustedapps.Application;
import com.atlassian.security.auth.trustedapps.ApplicationCertificate;
import com.atlassian.security.auth.trustedapps.ApplicationRetriever;
import com.atlassian.security.auth.trustedapps.EncryptedCertificate;
import com.atlassian.security.auth.trustedapps.InvalidCertificateException;
import com.atlassian.security.auth.trustedapps.UnableToVerifySignatureException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;

@Deprecated
public interface EncryptionProvider {
    public static final String SIGNATURE_BASE_SEPARATOR = "\n";

    public Application getApplicationCertificate(String var1) throws ApplicationRetriever.RetrievalException;

    public KeyPair generateNewKeyPair() throws NoSuchAlgorithmException, NoSuchProviderException;

    public String generateUID();

    public ApplicationCertificate decodeEncryptedCertificate(EncryptedCertificate var1, PublicKey var2, String var3) throws InvalidCertificateException;

    @Deprecated
    public EncryptedCertificate createEncryptedCertificate(String var1, PrivateKey var2, String var3);

    public EncryptedCertificate createEncryptedCertificate(String var1, PrivateKey var2, String var3, String var4);

    public PrivateKey toPrivateKey(byte[] var1) throws NoSuchAlgorithmException, InvalidKeySpecException, NoSuchProviderException;

    public PublicKey toPublicKey(byte[] var1) throws NoSuchAlgorithmException, InvalidKeySpecException, NoSuchProviderException;

    public String generateSignature(PrivateKey var1, byte[] var2) throws NoSuchAlgorithmException, InvalidKeyException, SignatureException, UnsupportedEncodingException;

    public boolean verifySignature(PublicKey var1, byte[] var2, String var3) throws UnableToVerifySignatureException;
}

