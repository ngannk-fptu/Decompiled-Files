/*
 * Decompiled with CFR 0.152.
 */
package com.microsoft.sqlserver.jdbc;

import com.microsoft.sqlserver.jdbc.BaseAttestationResponse;
import com.microsoft.sqlserver.jdbc.SQLServerBouncyCastleLoader;
import com.microsoft.sqlserver.jdbc.SQLServerException;
import com.microsoft.sqlserver.jdbc.SQLServerResource;
import java.io.ByteArrayInputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.security.GeneralSecurityException;
import java.security.NoSuchAlgorithmException;
import java.security.Signature;
import java.security.SignatureException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateExpiredException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.MGF1ParameterSpec;
import java.security.spec.PSSParameterSpec;
import java.text.MessageFormat;
import java.util.Collection;

class VSMAttestationResponse
extends BaseAttestationResponse {
    private byte[] healthReportCertificate;
    private byte[] enclaveReportPackage;
    private X509Certificate healthCert;

    VSMAttestationResponse(byte[] b) throws SQLServerException {
        ByteBuffer response;
        ByteBuffer byteBuffer = response = null != b ? ByteBuffer.wrap(b).order(ByteOrder.LITTLE_ENDIAN) : null;
        if (null != response) {
            this.totalSize = response.getInt();
            this.identitySize = response.getInt();
            int healthReportSize = response.getInt();
            int enclaveReportSize = response.getInt();
            this.enclavePK = new byte[this.identitySize];
            this.healthReportCertificate = new byte[healthReportSize];
            this.enclaveReportPackage = new byte[enclaveReportSize];
            response.get(this.enclavePK, 0, this.identitySize);
            response.get(this.healthReportCertificate, 0, healthReportSize);
            response.get(this.enclaveReportPackage, 0, enclaveReportSize);
            this.sessionInfoSize = response.getInt();
            response.get(this.sessionID, 0, 8);
            this.dhpkSize = response.getInt();
            this.dhpkSsize = response.getInt();
            this.dhPublicKey = new byte[this.dhpkSize];
            this.publicKeySig = new byte[this.dhpkSsize];
            response.get(this.dhPublicKey, 0, this.dhpkSize);
            response.get(this.publicKeySig, 0, this.dhpkSsize);
        }
        if (null == response || 0 != response.remaining()) {
            SQLServerException.makeFromDriverError(null, this, SQLServerResource.getResource("R_EnclaveResponseLengthError"), "0", false);
        }
        try {
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            this.healthCert = (X509Certificate)cf.generateCertificate(new ByteArrayInputStream(this.healthReportCertificate));
        }
        catch (CertificateException ce) {
            MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_HealthCertError"));
            Object[] msgArgs = new Object[]{ce.getLocalizedMessage()};
            SQLServerException.makeFromDriverError(null, null, form.format(msgArgs), null, true);
        }
    }

    void validateCert(byte[] b) throws SQLServerException {
        if (null != b) {
            try {
                CertificateFactory cf = CertificateFactory.getInstance("X.509");
                Collection<? extends Certificate> certs = cf.generateCertificates(new ByteArrayInputStream(b));
                for (X509Certificate x509Certificate : certs) {
                    try {
                        x509Certificate.checkValidity();
                        this.healthCert.verify(x509Certificate.getPublicKey());
                        return;
                    }
                    catch (SignatureException | CertificateExpiredException generalSecurityException) {
                    }
                }
            }
            catch (GeneralSecurityException e) {
                SQLServerException.makeFromDriverError(null, this, e.getLocalizedMessage(), "0", false);
            }
        }
        SQLServerException.makeFromDriverError(null, this, SQLServerResource.getResource("R_InvalidHealthCert"), "0", false);
    }

    void validateStatementSignature() throws SQLServerException, GeneralSecurityException {
        ByteBuffer enclaveReportPackageBuffer = ByteBuffer.wrap(this.enclaveReportPackage).order(ByteOrder.LITTLE_ENDIAN);
        enclaveReportPackageBuffer.getInt();
        enclaveReportPackageBuffer.getInt();
        enclaveReportPackageBuffer.getInt();
        int signedStatementSize = enclaveReportPackageBuffer.getInt();
        int signatureSize = enclaveReportPackageBuffer.getInt();
        enclaveReportPackageBuffer.getInt();
        byte[] signedStatement = new byte[signedStatementSize];
        enclaveReportPackageBuffer.get(signedStatement, 0, signedStatementSize);
        byte[] signatureBlob = new byte[signatureSize];
        enclaveReportPackageBuffer.get(signatureBlob, 0, signatureSize);
        if (enclaveReportPackageBuffer.remaining() != 0) {
            SQLServerException.makeFromDriverError(null, this, SQLServerResource.getResource("R_EnclavePackageLengthError"), "0", false);
        }
        Signature sig = null;
        try {
            sig = Signature.getInstance("RSASSA-PSS");
        }
        catch (NoSuchAlgorithmException e) {
            SQLServerBouncyCastleLoader.loadBouncyCastle();
            sig = Signature.getInstance("RSASSA-PSS");
        }
        PSSParameterSpec pss = new PSSParameterSpec("SHA-256", "MGF1", MGF1ParameterSpec.SHA256, 32, 1);
        sig.setParameter(pss);
        sig.initVerify(this.healthCert);
        sig.update(signedStatement);
        if (!sig.verify(signatureBlob)) {
            SQLServerException.makeFromDriverError(null, this, SQLServerResource.getResource("R_InvalidSignedStatement"), "0", false);
        }
    }
}

