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
import org.springframework.util.Assert;
import org.springframework.util.Base64Utils;
import org.springframework.vault.VaultException;
import org.springframework.vault.support.KeystoreUtil;

public class Certificate {
    private final String serialNumber;
    private final String certificate;
    private final String issuingCaCertificate;

    Certificate(@JsonProperty(value="serial_number") String serialNumber, @JsonProperty(value="certificate") String certificate, @JsonProperty(value="issuing_ca") String issuingCaCertificate) {
        this.serialNumber = serialNumber;
        this.certificate = certificate;
        this.issuingCaCertificate = issuingCaCertificate;
    }

    public static Certificate of(String serialNumber, String certificate, String issuingCaCertificate) {
        Assert.hasText(serialNumber, "Serial number must not be empty");
        Assert.hasText(certificate, "Certificate must not be empty");
        Assert.hasText(issuingCaCertificate, "Issuing CA certificate must not be empty");
        return new Certificate(serialNumber, certificate, issuingCaCertificate);
    }

    public String getSerialNumber() {
        return this.serialNumber;
    }

    public String getCertificate() {
        return this.certificate;
    }

    public String getIssuingCaCertificate() {
        return this.issuingCaCertificate;
    }

    public X509Certificate getX509Certificate() {
        try {
            byte[] bytes = Base64Utils.decodeFromString(this.getCertificate());
            return KeystoreUtil.getCertificate(bytes);
        }
        catch (CertificateException e) {
            throw new VaultException("Cannot create Certificate from certificate", e);
        }
    }

    public X509Certificate getX509IssuerCertificate() {
        try {
            byte[] bytes = Base64Utils.decodeFromString(this.getIssuingCaCertificate());
            return KeystoreUtil.getCertificate(bytes);
        }
        catch (CertificateException e) {
            throw new VaultException("Cannot create Certificate from issuing CA certificate", e);
        }
    }

    public KeyStore createTrustStore() {
        try {
            return KeystoreUtil.createKeyStore(this.getX509Certificate(), this.getX509IssuerCertificate());
        }
        catch (IOException | GeneralSecurityException e) {
            throw new VaultException("Cannot create KeyStore", e);
        }
    }
}

