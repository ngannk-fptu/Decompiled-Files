/*
 * Decompiled with CFR 0.152.
 */
package com.lowagie.text.pdf;

import com.lowagie.text.ExceptionConverter;
import com.lowagie.text.pdf.PdfName;
import com.lowagie.text.pdf.PdfNumber;
import com.lowagie.text.pdf.PdfPKCS7;
import com.lowagie.text.pdf.PdfSignature;
import com.lowagie.text.pdf.PdfString;
import java.io.ByteArrayOutputStream;
import java.security.PrivateKey;
import java.security.cert.CRL;
import java.security.cert.Certificate;

public abstract class PdfSigGenericPKCS
extends PdfSignature {
    protected String hashAlgorithm;
    protected String provider = null;
    protected PdfPKCS7 pkcs;
    protected String name;
    private byte[] externalDigest;
    private byte[] externalRSAdata;
    private String digestEncryptionAlgorithm;

    public PdfSigGenericPKCS(PdfName filter, PdfName subFilter) {
        super(filter, subFilter);
    }

    public void setSignInfo(PrivateKey privKey, Certificate[] certChain, CRL[] crlList) {
        try {
            this.pkcs = new PdfPKCS7(privKey, certChain, crlList, this.hashAlgorithm, this.provider, PdfName.ADBE_PKCS7_SHA1.equals(this.get(PdfName.SUBFILTER)));
            this.pkcs.setExternalDigest(this.externalDigest, this.externalRSAdata, this.digestEncryptionAlgorithm);
            if (PdfName.ADBE_X509_RSA_SHA1.equals(this.get(PdfName.SUBFILTER))) {
                ByteArrayOutputStream bout = new ByteArrayOutputStream();
                for (Certificate certificate : certChain) {
                    bout.write(certificate.getEncoded());
                }
                bout.close();
                this.setCert(bout.toByteArray());
                this.setContents(this.pkcs.getEncodedPKCS1());
            } else {
                this.setContents(this.pkcs.getEncodedPKCS7());
            }
            this.name = PdfPKCS7.getSubjectFields(this.pkcs.getSigningCertificate()).getField("CN");
            if (this.name != null) {
                this.put(PdfName.NAME, new PdfString(this.name, "UnicodeBig"));
            }
            this.pkcs = new PdfPKCS7(privKey, certChain, crlList, this.hashAlgorithm, this.provider, PdfName.ADBE_PKCS7_SHA1.equals(this.get(PdfName.SUBFILTER)));
            this.pkcs.setExternalDigest(this.externalDigest, this.externalRSAdata, this.digestEncryptionAlgorithm);
        }
        catch (Exception e) {
            throw new ExceptionConverter(e);
        }
    }

    public void setExternalDigest(byte[] digest, byte[] RSAdata, String digestEncryptionAlgorithm) {
        this.externalDigest = digest;
        this.externalRSAdata = RSAdata;
        this.digestEncryptionAlgorithm = digestEncryptionAlgorithm;
    }

    public String getName() {
        return this.name;
    }

    public PdfPKCS7 getSigner() {
        return this.pkcs;
    }

    public byte[] getSignerContents() {
        if (PdfName.ADBE_X509_RSA_SHA1.equals(this.get(PdfName.SUBFILTER))) {
            return this.pkcs.getEncodedPKCS1();
        }
        return this.pkcs.getEncodedPKCS7();
    }

    public static class PPKMS
    extends PdfSigGenericPKCS {
        public PPKMS() {
            super(PdfName.ADOBE_PPKMS, PdfName.ADBE_PKCS7_SHA1);
            this.hashAlgorithm = "SHA1";
        }

        public PPKMS(String provider) {
            this();
            this.provider = provider;
        }
    }

    public static class PPKLite
    extends PdfSigGenericPKCS {
        public PPKLite() {
            super(PdfName.ADOBE_PPKLITE, PdfName.ADBE_X509_RSA_SHA1);
            this.hashAlgorithm = "SHA1";
            this.put(PdfName.R, new PdfNumber(65541));
        }

        public PPKLite(String provider) {
            this();
            this.provider = provider;
        }
    }

    public static class VeriSign
    extends PdfSigGenericPKCS {
        public VeriSign() {
            super(PdfName.VERISIGN_PPKVS, PdfName.ADBE_PKCS7_DETACHED);
            this.hashAlgorithm = "MD5";
            this.put(PdfName.R, new PdfNumber(65537));
        }

        public VeriSign(String provider) {
            this();
            this.provider = provider;
        }
    }
}

