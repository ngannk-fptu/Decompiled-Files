/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1Boolean
 *  org.bouncycastle.asn1.ASN1Encodable
 *  org.bouncycastle.asn1.ASN1GeneralizedTime
 *  org.bouncycastle.asn1.ASN1Integer
 *  org.bouncycastle.asn1.ASN1ObjectIdentifier
 *  org.bouncycastle.asn1.ASN1Primitive
 *  org.bouncycastle.asn1.LocaleUtil
 *  org.bouncycastle.asn1.cms.AttributeTable
 *  org.bouncycastle.asn1.ess.ESSCertID
 *  org.bouncycastle.asn1.ess.ESSCertIDv2
 *  org.bouncycastle.asn1.ess.SigningCertificate
 *  org.bouncycastle.asn1.ess.SigningCertificateV2
 *  org.bouncycastle.asn1.oiw.OIWObjectIdentifiers
 *  org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers
 *  org.bouncycastle.asn1.tsp.Accuracy
 *  org.bouncycastle.asn1.tsp.MessageImprint
 *  org.bouncycastle.asn1.tsp.TSTInfo
 *  org.bouncycastle.asn1.x509.AlgorithmIdentifier
 *  org.bouncycastle.asn1.x509.Extensions
 *  org.bouncycastle.asn1.x509.ExtensionsGenerator
 *  org.bouncycastle.asn1.x509.GeneralName
 *  org.bouncycastle.asn1.x509.GeneralNames
 *  org.bouncycastle.asn1.x509.IssuerSerial
 *  org.bouncycastle.util.CollectionStore
 *  org.bouncycastle.util.Store
 */
package org.bouncycastle.tsp;

import java.io.IOException;
import java.io.OutputStream;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.SimpleTimeZone;
import org.bouncycastle.asn1.ASN1Boolean;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1GeneralizedTime;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.LocaleUtil;
import org.bouncycastle.asn1.cms.AttributeTable;
import org.bouncycastle.asn1.ess.ESSCertID;
import org.bouncycastle.asn1.ess.ESSCertIDv2;
import org.bouncycastle.asn1.ess.SigningCertificate;
import org.bouncycastle.asn1.ess.SigningCertificateV2;
import org.bouncycastle.asn1.oiw.OIWObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.tsp.Accuracy;
import org.bouncycastle.asn1.tsp.MessageImprint;
import org.bouncycastle.asn1.tsp.TSTInfo;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.Extensions;
import org.bouncycastle.asn1.x509.ExtensionsGenerator;
import org.bouncycastle.asn1.x509.GeneralName;
import org.bouncycastle.asn1.x509.GeneralNames;
import org.bouncycastle.asn1.x509.IssuerSerial;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cms.CMSAttributeTableGenerationException;
import org.bouncycastle.cms.CMSAttributeTableGenerator;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.cms.CMSProcessableByteArray;
import org.bouncycastle.cms.CMSSignedData;
import org.bouncycastle.cms.CMSSignedDataGenerator;
import org.bouncycastle.cms.SignerInfoGenerator;
import org.bouncycastle.operator.DigestCalculator;
import org.bouncycastle.tsp.TSPException;
import org.bouncycastle.tsp.TSPUtil;
import org.bouncycastle.tsp.TimeStampRequest;
import org.bouncycastle.tsp.TimeStampToken;
import org.bouncycastle.util.CollectionStore;
import org.bouncycastle.util.Store;

public class TimeStampTokenGenerator {
    public static final int R_SECONDS = 0;
    public static final int R_TENTHS_OF_SECONDS = 1;
    public static final int R_HUNDREDTHS_OF_SECONDS = 2;
    public static final int R_MILLISECONDS = 3;
    private int resolution = 0;
    private Locale locale = null;
    private int accuracySeconds = -1;
    private int accuracyMillis = -1;
    private int accuracyMicros = -1;
    boolean ordering = false;
    GeneralName tsa = null;
    private ASN1ObjectIdentifier tsaPolicyOID;
    private List certs = new ArrayList();
    private List crls = new ArrayList();
    private List attrCerts = new ArrayList();
    private Map otherRevoc = new HashMap();
    private SignerInfoGenerator signerInfoGen;

    public TimeStampTokenGenerator(SignerInfoGenerator signerInfoGen, DigestCalculator digestCalculator, ASN1ObjectIdentifier tsaPolicy) throws IllegalArgumentException, TSPException {
        this(signerInfoGen, digestCalculator, tsaPolicy, false);
    }

    public TimeStampTokenGenerator(final SignerInfoGenerator signerInfoGen, DigestCalculator digestCalculator, ASN1ObjectIdentifier tsaPolicy, boolean isIssuerSerialIncluded) throws IllegalArgumentException, TSPException {
        this.signerInfoGen = signerInfoGen;
        this.tsaPolicyOID = tsaPolicy;
        if (!signerInfoGen.hasAssociatedCertificate()) {
            throw new IllegalArgumentException("SignerInfoGenerator must have an associated certificate");
        }
        X509CertificateHolder assocCert = signerInfoGen.getAssociatedCertificate();
        TSPUtil.validateCertificate(assocCert);
        try {
            OutputStream dOut = digestCalculator.getOutputStream();
            dOut.write(assocCert.getEncoded());
            dOut.close();
            if (digestCalculator.getAlgorithmIdentifier().getAlgorithm().equals((ASN1Primitive)OIWObjectIdentifiers.idSHA1)) {
                final ESSCertID essCertid = new ESSCertID(digestCalculator.getDigest(), isIssuerSerialIncluded ? new IssuerSerial(new GeneralNames(new GeneralName(assocCert.getIssuer())), assocCert.getSerialNumber()) : null);
                this.signerInfoGen = new SignerInfoGenerator(signerInfoGen, new CMSAttributeTableGenerator(){

                    @Override
                    public AttributeTable getAttributes(Map parameters) throws CMSAttributeTableGenerationException {
                        AttributeTable table = signerInfoGen.getSignedAttributeTableGenerator().getAttributes(parameters);
                        if (table.get(PKCSObjectIdentifiers.id_aa_signingCertificate) == null) {
                            return table.add(PKCSObjectIdentifiers.id_aa_signingCertificate, (ASN1Encodable)new SigningCertificate(essCertid));
                        }
                        return table;
                    }
                }, signerInfoGen.getUnsignedAttributeTableGenerator());
            } else {
                AlgorithmIdentifier digAlgID = new AlgorithmIdentifier(digestCalculator.getAlgorithmIdentifier().getAlgorithm());
                final ESSCertIDv2 essCertid = new ESSCertIDv2(digAlgID, digestCalculator.getDigest(), isIssuerSerialIncluded ? new IssuerSerial(new GeneralNames(new GeneralName(assocCert.getIssuer())), new ASN1Integer(assocCert.getSerialNumber())) : null);
                this.signerInfoGen = new SignerInfoGenerator(signerInfoGen, new CMSAttributeTableGenerator(){

                    @Override
                    public AttributeTable getAttributes(Map parameters) throws CMSAttributeTableGenerationException {
                        AttributeTable table = signerInfoGen.getSignedAttributeTableGenerator().getAttributes(parameters);
                        if (table.get(PKCSObjectIdentifiers.id_aa_signingCertificateV2) == null) {
                            return table.add(PKCSObjectIdentifiers.id_aa_signingCertificateV2, (ASN1Encodable)new SigningCertificateV2(essCertid));
                        }
                        return table;
                    }
                }, signerInfoGen.getUnsignedAttributeTableGenerator());
            }
        }
        catch (IOException e) {
            throw new TSPException("Exception processing certificate.", e);
        }
    }

    public void addCertificates(Store certStore) {
        this.certs.addAll(certStore.getMatches(null));
    }

    public void addCRLs(Store crlStore) {
        this.crls.addAll(crlStore.getMatches(null));
    }

    public void addAttributeCertificates(Store attrStore) {
        this.attrCerts.addAll(attrStore.getMatches(null));
    }

    public void addOtherRevocationInfo(ASN1ObjectIdentifier otherRevocationInfoFormat, Store otherRevocationInfos) {
        this.otherRevoc.put(otherRevocationInfoFormat, otherRevocationInfos.getMatches(null));
    }

    public void setResolution(int resolution) {
        this.resolution = resolution;
    }

    public void setLocale(Locale locale) {
        this.locale = locale;
    }

    public void setAccuracySeconds(int accuracySeconds) {
        this.accuracySeconds = accuracySeconds;
    }

    public void setAccuracyMillis(int accuracyMillis) {
        this.accuracyMillis = accuracyMillis;
    }

    public void setAccuracyMicros(int accuracyMicros) {
        this.accuracyMicros = accuracyMicros;
    }

    public void setOrdering(boolean ordering) {
        this.ordering = ordering;
    }

    public void setTSA(GeneralName tsa) {
        this.tsa = tsa;
    }

    public TimeStampToken generate(TimeStampRequest request, BigInteger serialNumber, Date genTime) throws TSPException {
        return this.generate(request, serialNumber, genTime, null);
    }

    public TimeStampToken generate(TimeStampRequest request, BigInteger serialNumber, Date genTime, Extensions additionalExtensions) throws TSPException {
        AlgorithmIdentifier algID = request.getMessageImprintAlgID();
        MessageImprint messageImprint = new MessageImprint(algID, request.getMessageImprintDigest());
        Accuracy accuracy = null;
        if (this.accuracySeconds > 0 || this.accuracyMillis > 0 || this.accuracyMicros > 0) {
            ASN1Integer seconds = null;
            if (this.accuracySeconds > 0) {
                seconds = new ASN1Integer((long)this.accuracySeconds);
            }
            ASN1Integer millis = null;
            if (this.accuracyMillis > 0) {
                millis = new ASN1Integer((long)this.accuracyMillis);
            }
            ASN1Integer micros = null;
            if (this.accuracyMicros > 0) {
                micros = new ASN1Integer((long)this.accuracyMicros);
            }
            accuracy = new Accuracy(seconds, millis, micros);
        }
        ASN1Boolean derOrdering = null;
        if (this.ordering) {
            derOrdering = ASN1Boolean.getInstance((boolean)this.ordering);
        }
        ASN1Integer nonce = null;
        if (request.getNonce() != null) {
            nonce = new ASN1Integer(request.getNonce());
        }
        ASN1ObjectIdentifier tsaPolicy = this.tsaPolicyOID;
        if (request.getReqPolicy() != null) {
            tsaPolicy = request.getReqPolicy();
        }
        Extensions respExtensions = request.getExtensions();
        if (additionalExtensions != null) {
            Enumeration en;
            ExtensionsGenerator extGen = new ExtensionsGenerator();
            if (respExtensions != null) {
                en = respExtensions.oids();
                while (en.hasMoreElements()) {
                    extGen.addExtension(respExtensions.getExtension(ASN1ObjectIdentifier.getInstance(en.nextElement())));
                }
            }
            en = additionalExtensions.oids();
            while (en.hasMoreElements()) {
                extGen.addExtension(additionalExtensions.getExtension(ASN1ObjectIdentifier.getInstance(en.nextElement())));
            }
            respExtensions = extGen.generate();
        }
        ASN1GeneralizedTime timeStampTime = this.resolution == 0 ? (this.locale == null ? new ASN1GeneralizedTime(genTime) : new ASN1GeneralizedTime(genTime, this.locale)) : this.createGeneralizedTime(genTime);
        TSTInfo tstInfo = new TSTInfo(tsaPolicy, messageImprint, new ASN1Integer(serialNumber), timeStampTime, accuracy, derOrdering, nonce, this.tsa, respExtensions);
        try {
            CMSSignedDataGenerator signedDataGenerator = new CMSSignedDataGenerator();
            if (request.getCertReq()) {
                signedDataGenerator.addCertificates((Store)new CollectionStore((Collection)this.certs));
                signedDataGenerator.addAttributeCertificates((Store)new CollectionStore((Collection)this.attrCerts));
            }
            signedDataGenerator.addCRLs((Store)new CollectionStore((Collection)this.crls));
            if (!this.otherRevoc.isEmpty()) {
                for (ASN1ObjectIdentifier format : this.otherRevoc.keySet()) {
                    signedDataGenerator.addOtherRevocationInfo(format, (Store)new CollectionStore((Collection)this.otherRevoc.get(format)));
                }
            }
            signedDataGenerator.addSignerInfoGenerator(this.signerInfoGen);
            byte[] derEncodedTSTInfo = tstInfo.getEncoded("DER");
            CMSSignedData signedData = signedDataGenerator.generate(new CMSProcessableByteArray(PKCSObjectIdentifiers.id_ct_TSTInfo, derEncodedTSTInfo), true);
            return new TimeStampToken(signedData);
        }
        catch (CMSException cmsEx) {
            throw new TSPException("Error generating time-stamp token", cmsEx);
        }
        catch (IOException e) {
            throw new TSPException("Exception encoding info", e);
        }
    }

    private ASN1GeneralizedTime createGeneralizedTime(Date time) throws TSPException {
        String format = "yyyyMMddHHmmss.SSS";
        SimpleDateFormat dateF = this.locale == null ? new SimpleDateFormat(format, LocaleUtil.EN_Locale) : new SimpleDateFormat(format, this.locale);
        dateF.setTimeZone(new SimpleTimeZone(0, "Z"));
        StringBuilder sBuild = new StringBuilder(dateF.format(time));
        int dotIndex = sBuild.indexOf(".");
        if (dotIndex < 0) {
            sBuild.append("Z");
            return new ASN1GeneralizedTime(sBuild.toString());
        }
        switch (this.resolution) {
            case 1: {
                if (sBuild.length() <= dotIndex + 2) break;
                sBuild.delete(dotIndex + 2, sBuild.length());
                break;
            }
            case 2: {
                if (sBuild.length() <= dotIndex + 3) break;
                sBuild.delete(dotIndex + 3, sBuild.length());
                break;
            }
            case 3: {
                break;
            }
            default: {
                throw new TSPException("unknown time-stamp resolution: " + this.resolution);
            }
        }
        while (sBuild.charAt(sBuild.length() - 1) == '0') {
            sBuild.deleteCharAt(sBuild.length() - 1);
        }
        if (sBuild.length() - 1 == dotIndex) {
            sBuild.deleteCharAt(sBuild.length() - 1);
        }
        sBuild.append("Z");
        return new ASN1GeneralizedTime(sBuild.toString());
    }
}

