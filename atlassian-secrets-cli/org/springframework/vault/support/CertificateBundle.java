/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.vault.support;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.security.spec.KeySpec;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.springframework.util.Assert;
import org.springframework.util.Base64Utils;
import org.springframework.vault.VaultException;
import org.springframework.vault.support.Certificate;
import org.springframework.vault.support.KeystoreUtil;

public class CertificateBundle
extends Certificate {
    private final String privateKey;
    private final List<String> caChain;

    CertificateBundle(@JsonProperty(value="serial_number") String serialNumber, @JsonProperty(value="certificate") String certificate, @JsonProperty(value="issuing_ca") String issuingCaCertificate, @JsonProperty(value="ca_chain") List<String> caChain, @JsonProperty(value="private_key") String privateKey) {
        super(serialNumber, certificate, issuingCaCertificate);
        this.privateKey = privateKey;
        this.caChain = caChain;
    }

    public static CertificateBundle of(String serialNumber, String certificate, String issuingCaCertificate, String privateKey) {
        Assert.hasText(serialNumber, "Serial number must not be empty");
        Assert.hasText(certificate, "Certificate must not be empty");
        Assert.hasText(issuingCaCertificate, "Issuing CA certificate must not be empty");
        Assert.hasText(privateKey, "Private key must not be empty");
        return new CertificateBundle(serialNumber, certificate, issuingCaCertificate, Collections.singletonList(issuingCaCertificate), privateKey);
    }

    public String getPrivateKey() {
        return this.privateKey;
    }

    public KeySpec getPrivateKeySpec() {
        try {
            byte[] bytes = Base64Utils.decodeFromString(this.getPrivateKey());
            return KeystoreUtil.getRSAPrivateKeySpec(bytes);
        }
        catch (IOException e) {
            throw new VaultException("Cannot create KeySpec from private key", e);
        }
    }

    public KeyStore createKeyStore(String keyAlias) {
        return this.createKeyStore(keyAlias, false);
    }

    public KeyStore createKeyStore(String keyAlias, boolean includeCaChain) {
        Assert.hasText(keyAlias, "Key alias must not be empty");
        try {
            ArrayList<X509Certificate> certificates = new ArrayList<X509Certificate>();
            certificates.add(this.getX509Certificate());
            if (includeCaChain) {
                certificates.addAll(this.getX509IssuerCertificates());
            } else {
                certificates.add(this.getX509IssuerCertificate());
            }
            return KeystoreUtil.createKeyStore(keyAlias, this.getPrivateKeySpec(), certificates.toArray(new X509Certificate[0]));
        }
        catch (IOException | GeneralSecurityException e) {
            throw new VaultException("Cannot create KeyStore", e);
        }
    }

    public List<X509Certificate> getX509IssuerCertificates() {
        ArrayList<X509Certificate> certificates = new ArrayList<X509Certificate>();
        for (String data : this.caChain) {
            try {
                byte[] bytes = Base64Utils.decodeFromString(data);
                certificates.add(KeystoreUtil.getCertificate(bytes));
            }
            catch (CertificateException e) {
                throw new VaultException("Cannot create Certificate from issuing CA certificate", e);
            }
        }
        return certificates;
    }
}

