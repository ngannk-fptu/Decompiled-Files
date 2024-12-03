/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1Primitive
 *  org.bouncycastle.asn1.cms.Attribute
 *  org.bouncycastle.asn1.cms.AttributeTable
 *  org.bouncycastle.asn1.cms.ContentInfo
 *  org.bouncycastle.asn1.cms.IssuerAndSerialNumber
 *  org.bouncycastle.asn1.ess.ESSCertID
 *  org.bouncycastle.asn1.ess.ESSCertIDv2
 *  org.bouncycastle.asn1.ess.SigningCertificate
 *  org.bouncycastle.asn1.ess.SigningCertificateV2
 *  org.bouncycastle.asn1.oiw.OIWObjectIdentifiers
 *  org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers
 *  org.bouncycastle.asn1.tsp.TSTInfo
 *  org.bouncycastle.asn1.x500.X500Name
 *  org.bouncycastle.asn1.x509.AlgorithmIdentifier
 *  org.bouncycastle.asn1.x509.GeneralName
 *  org.bouncycastle.asn1.x509.IssuerSerial
 *  org.bouncycastle.util.Arrays
 *  org.bouncycastle.util.Store
 */
package org.bouncycastle.tsp;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Collection;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.cms.Attribute;
import org.bouncycastle.asn1.cms.AttributeTable;
import org.bouncycastle.asn1.cms.ContentInfo;
import org.bouncycastle.asn1.cms.IssuerAndSerialNumber;
import org.bouncycastle.asn1.ess.ESSCertID;
import org.bouncycastle.asn1.ess.ESSCertIDv2;
import org.bouncycastle.asn1.ess.SigningCertificate;
import org.bouncycastle.asn1.ess.SigningCertificateV2;
import org.bouncycastle.asn1.oiw.OIWObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.tsp.TSTInfo;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.GeneralName;
import org.bouncycastle.asn1.x509.IssuerSerial;
import org.bouncycastle.cert.X509AttributeCertificateHolder;
import org.bouncycastle.cert.X509CRLHolder;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.cms.CMSSignedData;
import org.bouncycastle.cms.CMSTypedData;
import org.bouncycastle.cms.SignerId;
import org.bouncycastle.cms.SignerInformation;
import org.bouncycastle.cms.SignerInformationVerifier;
import org.bouncycastle.operator.DigestCalculator;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.tsp.TSPException;
import org.bouncycastle.tsp.TSPUtil;
import org.bouncycastle.tsp.TSPValidationException;
import org.bouncycastle.tsp.TimeStampTokenInfo;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Store;

public class TimeStampToken {
    CMSSignedData tsToken;
    SignerInformation tsaSignerInfo;
    TimeStampTokenInfo tstInfo;
    CertID certID;

    public TimeStampToken(ContentInfo contentInfo) throws TSPException, IOException {
        this(TimeStampToken.getSignedData(contentInfo));
    }

    private static CMSSignedData getSignedData(ContentInfo contentInfo) throws TSPException {
        try {
            return new CMSSignedData(contentInfo);
        }
        catch (CMSException e) {
            throw new TSPException("TSP parsing error: " + e.getMessage(), e.getCause());
        }
    }

    public TimeStampToken(CMSSignedData signedData) throws TSPException, IOException {
        this.tsToken = signedData;
        if (!this.tsToken.getSignedContentTypeOID().equals(PKCSObjectIdentifiers.id_ct_TSTInfo.getId())) {
            throw new TSPValidationException("ContentInfo object not for a time stamp.");
        }
        Collection<SignerInformation> signers = this.tsToken.getSignerInfos().getSigners();
        if (signers.size() != 1) {
            throw new IllegalArgumentException("Time-stamp token signed by " + signers.size() + " signers, but it must contain just the TSA signature.");
        }
        this.tsaSignerInfo = signers.iterator().next();
        try {
            CMSTypedData content = this.tsToken.getSignedContent();
            ByteArrayOutputStream bOut = new ByteArrayOutputStream();
            content.write(bOut);
            this.tstInfo = new TimeStampTokenInfo(TSTInfo.getInstance((Object)ASN1Primitive.fromByteArray((byte[])bOut.toByteArray())));
            Attribute attr = this.tsaSignerInfo.getSignedAttributes().get(PKCSObjectIdentifiers.id_aa_signingCertificate);
            if (attr != null) {
                SigningCertificate signCert = SigningCertificate.getInstance((Object)attr.getAttrValues().getObjectAt(0));
                this.certID = new CertID(ESSCertID.getInstance((Object)signCert.getCerts()[0]));
            } else {
                attr = this.tsaSignerInfo.getSignedAttributes().get(PKCSObjectIdentifiers.id_aa_signingCertificateV2);
                if (attr == null) {
                    throw new TSPValidationException("no signing certificate attribute found, time stamp invalid.");
                }
                SigningCertificateV2 signCertV2 = SigningCertificateV2.getInstance((Object)attr.getAttrValues().getObjectAt(0));
                this.certID = new CertID(ESSCertIDv2.getInstance((Object)signCertV2.getCerts()[0]));
            }
        }
        catch (CMSException e) {
            throw new TSPException(e.getMessage(), e.getUnderlyingException());
        }
    }

    public TimeStampTokenInfo getTimeStampInfo() {
        return this.tstInfo;
    }

    public SignerId getSID() {
        return this.tsaSignerInfo.getSID();
    }

    public AttributeTable getSignedAttributes() {
        return this.tsaSignerInfo.getSignedAttributes();
    }

    public AttributeTable getUnsignedAttributes() {
        return this.tsaSignerInfo.getUnsignedAttributes();
    }

    public Store<X509CertificateHolder> getCertificates() {
        return this.tsToken.getCertificates();
    }

    public Store<X509CRLHolder> getCRLs() {
        return this.tsToken.getCRLs();
    }

    public Store<X509AttributeCertificateHolder> getAttributeCertificates() {
        return this.tsToken.getAttributeCertificates();
    }

    public void validate(SignerInformationVerifier sigVerifier) throws TSPException, TSPValidationException {
        if (!sigVerifier.hasAssociatedCertificate()) {
            throw new IllegalArgumentException("verifier provider needs an associated certificate");
        }
        try {
            X509CertificateHolder certHolder = sigVerifier.getAssociatedCertificate();
            DigestCalculator calc = sigVerifier.getDigestCalculator(this.certID.getHashAlgorithm());
            OutputStream cOut = calc.getOutputStream();
            cOut.write(certHolder.getEncoded());
            cOut.close();
            if (!Arrays.constantTimeAreEqual((byte[])this.certID.getCertHash(), (byte[])calc.getDigest())) {
                throw new TSPValidationException("certificate hash does not match certID hash.");
            }
            if (this.certID.getIssuerSerial() != null) {
                IssuerAndSerialNumber issuerSerial = new IssuerAndSerialNumber(certHolder.toASN1Structure());
                if (!this.certID.getIssuerSerial().getSerial().equals((ASN1Primitive)issuerSerial.getSerialNumber())) {
                    throw new TSPValidationException("certificate serial number does not match certID for signature.");
                }
                GeneralName[] names = this.certID.getIssuerSerial().getIssuer().getNames();
                boolean found = false;
                for (int i = 0; i != names.length; ++i) {
                    if (names[i].getTagNo() != 4 || !X500Name.getInstance((Object)names[i].getName()).equals((Object)X500Name.getInstance((Object)issuerSerial.getName()))) continue;
                    found = true;
                    break;
                }
                if (!found) {
                    throw new TSPValidationException("certificate name does not match certID for signature. ");
                }
            }
            TSPUtil.validateCertificate(certHolder);
            if (!certHolder.isValidOn(this.tstInfo.getGenTime())) {
                throw new TSPValidationException("certificate not valid when time stamp created.");
            }
            if (!this.tsaSignerInfo.verify(sigVerifier)) {
                throw new TSPValidationException("signature not created by certificate.");
            }
        }
        catch (CMSException e) {
            if (e.getUnderlyingException() != null) {
                throw new TSPException(e.getMessage(), e.getUnderlyingException());
            }
            throw new TSPException("CMS exception: " + e, e);
        }
        catch (IOException e) {
            throw new TSPException("problem processing certificate: " + e, e);
        }
        catch (OperatorCreationException e) {
            throw new TSPException("unable to create digest: " + e.getMessage(), e);
        }
    }

    public boolean isSignatureValid(SignerInformationVerifier sigVerifier) throws TSPException {
        try {
            return this.tsaSignerInfo.verify(sigVerifier);
        }
        catch (CMSException e) {
            if (e.getUnderlyingException() != null) {
                throw new TSPException(e.getMessage(), e.getUnderlyingException());
            }
            throw new TSPException("CMS exception: " + e, e);
        }
    }

    public CMSSignedData toCMSSignedData() {
        return this.tsToken;
    }

    public byte[] getEncoded() throws IOException {
        return this.tsToken.getEncoded("DL");
    }

    public byte[] getEncoded(String encoding) throws IOException {
        return this.tsToken.getEncoded(encoding);
    }

    private static class CertID {
        private ESSCertID certID;
        private ESSCertIDv2 certIDv2;

        CertID(ESSCertID certID) {
            this.certID = certID;
            this.certIDv2 = null;
        }

        CertID(ESSCertIDv2 certID) {
            this.certIDv2 = certID;
            this.certID = null;
        }

        public AlgorithmIdentifier getHashAlgorithm() {
            if (this.certID != null) {
                return new AlgorithmIdentifier(OIWObjectIdentifiers.idSHA1);
            }
            return this.certIDv2.getHashAlgorithm();
        }

        public byte[] getCertHash() {
            if (this.certID != null) {
                return this.certID.getCertHash();
            }
            return this.certIDv2.getCertHash();
        }

        public IssuerSerial getIssuerSerial() {
            if (this.certID != null) {
                return this.certID.getIssuerSerial();
            }
            return this.certIDv2.getIssuerSerial();
        }
    }
}

