/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 *  org.bouncycastle.asn1.ASN1Encodable
 *  org.bouncycastle.asn1.ASN1EncodableVector
 *  org.bouncycastle.asn1.ASN1Enumerated
 *  org.bouncycastle.asn1.ASN1InputStream
 *  org.bouncycastle.asn1.ASN1Integer
 *  org.bouncycastle.asn1.ASN1ObjectIdentifier
 *  org.bouncycastle.asn1.ASN1OctetString
 *  org.bouncycastle.asn1.ASN1OutputStream
 *  org.bouncycastle.asn1.ASN1Primitive
 *  org.bouncycastle.asn1.ASN1Sequence
 *  org.bouncycastle.asn1.ASN1Set
 *  org.bouncycastle.asn1.ASN1String
 *  org.bouncycastle.asn1.ASN1TaggedObject
 *  org.bouncycastle.asn1.DERNull
 *  org.bouncycastle.asn1.DEROctetString
 *  org.bouncycastle.asn1.DERSequence
 *  org.bouncycastle.asn1.DERSet
 *  org.bouncycastle.asn1.DERTaggedObject
 *  org.bouncycastle.asn1.DERUTCTime
 *  org.bouncycastle.asn1.cms.Attribute
 *  org.bouncycastle.asn1.cms.AttributeTable
 *  org.bouncycastle.asn1.cms.ContentInfo
 *  org.bouncycastle.asn1.ocsp.BasicOCSPResponse
 *  org.bouncycastle.asn1.ocsp.OCSPObjectIdentifiers
 *  org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers
 *  org.bouncycastle.asn1.tsp.MessageImprint
 *  org.bouncycastle.asn1.x509.Extension
 *  org.bouncycastle.cert.X509CertificateHolder
 *  org.bouncycastle.cert.jcajce.JcaX509CertificateHolder
 *  org.bouncycastle.cert.ocsp.BasicOCSPResp
 *  org.bouncycastle.cert.ocsp.CertificateID
 *  org.bouncycastle.cert.ocsp.SingleResp
 *  org.bouncycastle.jcajce.provider.asymmetric.x509.CertificateFactory
 *  org.bouncycastle.jce.provider.X509CRLParser
 *  org.bouncycastle.operator.DigestCalculatorProvider
 *  org.bouncycastle.operator.jcajce.JcaDigestCalculatorProviderBuilder
 *  org.bouncycastle.tsp.TimeStampToken
 */
package com.lowagie.text.pdf;

import com.lowagie.text.ExceptionConverter;
import com.lowagie.text.error_messages.MessageLocalization;
import com.lowagie.text.pdf.TSAClient;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.cert.CRL;
import java.security.cert.Certificate;
import java.security.cert.X509CRL;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Enumeration;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Enumerated;
import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1OutputStream;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1Set;
import org.bouncycastle.asn1.ASN1String;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERNull;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERSet;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.DERUTCTime;
import org.bouncycastle.asn1.cms.Attribute;
import org.bouncycastle.asn1.cms.AttributeTable;
import org.bouncycastle.asn1.cms.ContentInfo;
import org.bouncycastle.asn1.ocsp.BasicOCSPResponse;
import org.bouncycastle.asn1.ocsp.OCSPObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.tsp.MessageImprint;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateHolder;
import org.bouncycastle.cert.ocsp.BasicOCSPResp;
import org.bouncycastle.cert.ocsp.CertificateID;
import org.bouncycastle.cert.ocsp.SingleResp;
import org.bouncycastle.jcajce.provider.asymmetric.x509.CertificateFactory;
import org.bouncycastle.jce.provider.X509CRLParser;
import org.bouncycastle.operator.DigestCalculatorProvider;
import org.bouncycastle.operator.jcajce.JcaDigestCalculatorProviderBuilder;
import org.bouncycastle.tsp.TimeStampToken;

public class PdfPKCS7 {
    private byte[] sigAttr;
    private byte[] digestAttr;
    private int version;
    private int signerversion;
    private Set<String> digestalgos;
    private List<Certificate> certs;
    private List<Certificate> signCerts;
    private List<CRL> crls;
    private X509Certificate signCert;
    private byte[] digest;
    private MessageDigest messageDigest;
    private String digestAlgorithm;
    private String digestEncryptionAlgorithm;
    private Signature sig;
    private transient PrivateKey privKey;
    private byte[] RSAdata;
    private boolean verified;
    private boolean verifyResult;
    private byte[] externalDigest;
    private byte[] externalRSAdata;
    private String provider;
    private static final String ID_PKCS7_DATA = "1.2.840.113549.1.7.1";
    private static final String ID_PKCS7_SIGNED_DATA = "1.2.840.113549.1.7.2";
    private static final String ID_RSA = "1.2.840.113549.1.1.1";
    private static final String ID_DSA = "1.2.840.10040.4.1";
    private static final String ID_CONTENT_TYPE = "1.2.840.113549.1.9.3";
    private static final String ID_MESSAGE_DIGEST = "1.2.840.113549.1.9.4";
    private static final String ID_SIGNING_TIME = "1.2.840.113549.1.9.5";
    private static final String ID_ADBE_REVOCATION = "1.2.840.113583.1.1.8";
    private String reason;
    private String location;
    private Calendar signDate;
    private String signName;
    private TimeStampToken timeStampToken;
    private static final Map<String, String> digestNames = new HashMap<String, String>();
    private static final Map<String, String> algorithmNames = new HashMap<String, String>();
    private static final Map<String, String> allowedDigests = new HashMap<String, String>();
    private BasicOCSPResp basicResp;

    public static String getDigest(String oid) {
        return Optional.ofNullable(digestNames.get(oid)).orElse(oid);
    }

    public static String getAlgorithm(String oid) {
        return Optional.ofNullable(algorithmNames.get(oid)).orElse(oid);
    }

    public TimeStampToken getTimeStampToken() {
        return this.timeStampToken;
    }

    @Nullable
    public Calendar getTimeStampDate() {
        if (this.timeStampToken == null) {
            return null;
        }
        GregorianCalendar cal = new GregorianCalendar();
        Date date = this.timeStampToken.getTimeStampInfo().getGenTime();
        cal.setTime(date);
        return cal;
    }

    public PdfPKCS7(byte[] contentsKey, byte[] certsKey, String provider) {
        try {
            this.provider = provider;
            CertificateFactory certificateFactory = new CertificateFactory();
            Collection certificates = certificateFactory.engineGenerateCertificates((InputStream)new ByteArrayInputStream(certsKey));
            this.certs = new ArrayList<Certificate>(certificates);
            this.signCerts = this.certs;
            this.signCert = (X509Certificate)this.certs.iterator().next();
            this.crls = new ArrayList<CRL>();
            ASN1InputStream in = new ASN1InputStream((InputStream)new ByteArrayInputStream(contentsKey));
            this.digest = ((DEROctetString)in.readObject()).getOctets();
            this.sig = provider == null ? Signature.getInstance("SHA1withRSA") : Signature.getInstance("SHA1withRSA", provider);
            this.sig.initVerify(this.signCert.getPublicKey());
        }
        catch (Exception e) {
            throw new ExceptionConverter(e);
        }
    }

    public BasicOCSPResp getOcsp() {
        return this.basicResp;
    }

    private void findOcsp(ASN1Sequence seq) throws IOException {
        this.basicResp = null;
        while (!(seq.getObjectAt(0) instanceof ASN1ObjectIdentifier) || !((ASN1ObjectIdentifier)seq.getObjectAt(0)).getId().equals(OCSPObjectIdentifiers.id_pkix_ocsp_basic.getId())) {
            boolean ret = true;
            for (int k = 0; k < seq.size(); ++k) {
                if (seq.getObjectAt(k) instanceof ASN1Sequence) {
                    seq = (ASN1Sequence)seq.getObjectAt(0);
                    ret = false;
                    break;
                }
                if (!(seq.getObjectAt(k) instanceof ASN1TaggedObject)) continue;
                ASN1TaggedObject tag = (ASN1TaggedObject)seq.getObjectAt(k);
                if (tag.getObject() instanceof ASN1Sequence) {
                    seq = (ASN1Sequence)tag.getObject();
                    ret = false;
                    break;
                }
                return;
            }
            if (!ret) continue;
            return;
        }
        DEROctetString os = (DEROctetString)seq.getObjectAt(1);
        ASN1InputStream inp = new ASN1InputStream(os.getOctets());
        BasicOCSPResponse resp = BasicOCSPResponse.getInstance((Object)inp.readObject());
        this.basicResp = new BasicOCSPResp(resp);
    }

    public PdfPKCS7(byte[] contentsKey, String provider) {
        try {
            ASN1TaggedObject taggedObject;
            ASN1Set unat;
            AttributeTable attble;
            Attribute ts;
            ASN1Primitive pkcs;
            this.provider = provider;
            ASN1InputStream din = new ASN1InputStream((InputStream)new ByteArrayInputStream(contentsKey));
            try {
                pkcs = din.readObject();
            }
            catch (IOException e) {
                throw new IllegalArgumentException(MessageLocalization.getComposedMessage("can.t.decode.pkcs7signeddata.object"));
            }
            if (!(pkcs instanceof ASN1Sequence)) {
                throw new IllegalArgumentException(MessageLocalization.getComposedMessage("not.a.valid.pkcs.7.object.not.a.sequence"));
            }
            ASN1Sequence signedData = (ASN1Sequence)pkcs;
            ASN1ObjectIdentifier objId = (ASN1ObjectIdentifier)signedData.getObjectAt(0);
            if (!objId.getId().equals(ID_PKCS7_SIGNED_DATA)) {
                throw new IllegalArgumentException(MessageLocalization.getComposedMessage("not.a.valid.pkcs.7.object.not.signed.data"));
            }
            ASN1Sequence content = (ASN1Sequence)((ASN1TaggedObject)signedData.getObjectAt(1)).getObject();
            this.version = ((ASN1Integer)content.getObjectAt(0)).getValue().intValue();
            this.digestalgos = new HashSet<String>();
            Enumeration e = ((ASN1Set)content.getObjectAt(1)).getObjects();
            while (e.hasMoreElements()) {
                ASN1Sequence s = (ASN1Sequence)e.nextElement();
                ASN1ObjectIdentifier o = (ASN1ObjectIdentifier)s.getObjectAt(0);
                this.digestalgos.add(o.getId());
            }
            CertificateFactory certificateFactory = new CertificateFactory();
            Collection certificates = certificateFactory.engineGenerateCertificates((InputStream)new ByteArrayInputStream(contentsKey));
            this.certs = new ArrayList<Certificate>(certificates);
            X509CRLParser cl = new X509CRLParser();
            cl.engineInit((InputStream)new ByteArrayInputStream(contentsKey));
            this.crls = (List)cl.engineReadAll();
            ASN1Sequence rsaData = (ASN1Sequence)content.getObjectAt(2);
            if (rsaData.size() > 1) {
                ASN1OctetString rsaDataContent = (ASN1OctetString)((ASN1TaggedObject)rsaData.getObjectAt(1)).getObject();
                this.RSAdata = rsaDataContent.getOctets();
            }
            int next = 3;
            while (content.getObjectAt(next) instanceof ASN1TaggedObject) {
                ++next;
            }
            ASN1Set signerInfos = (ASN1Set)content.getObjectAt(next);
            if (signerInfos.size() != 1) {
                throw new IllegalArgumentException(MessageLocalization.getComposedMessage("this.pkcs.7.object.has.multiple.signerinfos.only.one.is.supported.at.this.time"));
            }
            ASN1Sequence signerInfo = (ASN1Sequence)signerInfos.getObjectAt(0);
            this.signerversion = ((ASN1Integer)signerInfo.getObjectAt(0)).getValue().intValue();
            ASN1Sequence issuerAndSerialNumber = (ASN1Sequence)signerInfo.getObjectAt(1);
            BigInteger serialNumber = ((ASN1Integer)issuerAndSerialNumber.getObjectAt(1)).getValue();
            for (Certificate cert1 : this.certs) {
                X509Certificate cert = (X509Certificate)cert1;
                if (!serialNumber.equals(cert.getSerialNumber())) continue;
                this.signCert = cert;
                break;
            }
            if (this.signCert == null) {
                throw new IllegalArgumentException(MessageLocalization.getComposedMessage("can.t.find.signing.certificate.with.serial.1", serialNumber.toString(16)));
            }
            this.signCertificateChain();
            this.digestAlgorithm = ((ASN1ObjectIdentifier)((ASN1Sequence)signerInfo.getObjectAt(2)).getObjectAt(0)).getId();
            next = 3;
            if (signerInfo.getObjectAt(next) instanceof ASN1TaggedObject) {
                ASN1TaggedObject tagsig = (ASN1TaggedObject)signerInfo.getObjectAt(next);
                ASN1Set sseq = ASN1Set.getInstance((ASN1TaggedObject)tagsig, (boolean)false);
                this.sigAttr = sseq.getEncoded("DER");
                for (int k = 0; k < sseq.size(); ++k) {
                    ASN1Sequence seq2 = (ASN1Sequence)sseq.getObjectAt(k);
                    if (((ASN1ObjectIdentifier)seq2.getObjectAt(0)).getId().equals(ID_MESSAGE_DIGEST)) {
                        ASN1Set set = (ASN1Set)seq2.getObjectAt(1);
                        this.digestAttr = ((DEROctetString)set.getObjectAt(0)).getOctets();
                        continue;
                    }
                    if (!((ASN1ObjectIdentifier)seq2.getObjectAt(0)).getId().equals(ID_ADBE_REVOCATION)) continue;
                    ASN1Set setout = (ASN1Set)seq2.getObjectAt(1);
                    ASN1Sequence seqout = (ASN1Sequence)setout.getObjectAt(0);
                    for (int j = 0; j < seqout.size(); ++j) {
                        ASN1TaggedObject tg = (ASN1TaggedObject)seqout.getObjectAt(j);
                        if (tg.getTagNo() != 1) continue;
                        ASN1Sequence seqin = (ASN1Sequence)tg.getObject();
                        this.findOcsp(seqin);
                    }
                }
                if (this.digestAttr == null) {
                    throw new IllegalArgumentException(MessageLocalization.getComposedMessage("authenticated.attribute.is.missing.the.digest"));
                }
            }
            int n = ++next;
            this.digestEncryptionAlgorithm = ((ASN1ObjectIdentifier)((ASN1Sequence)signerInfo.getObjectAt(n)).getObjectAt(0)).getId();
            int n2 = ++next;
            this.digest = ((DEROctetString)signerInfo.getObjectAt(n2)).getOctets();
            if (++next < signerInfo.size() && signerInfo.getObjectAt(next) instanceof DERTaggedObject && (ts = (attble = new AttributeTable(unat = ASN1Set.getInstance((ASN1TaggedObject)(taggedObject = (ASN1TaggedObject)signerInfo.getObjectAt(next)), (boolean)false))).get(PKCSObjectIdentifiers.id_aa_signatureTimeStampToken)) != null && ts.getAttrValues().size() > 0) {
                ASN1Set attributeValues = ts.getAttrValues();
                ASN1Sequence tokenSequence = ASN1Sequence.getInstance((Object)attributeValues.getObjectAt(0));
                ContentInfo contentInfo = ContentInfo.getInstance((Object)tokenSequence);
                this.timeStampToken = new TimeStampToken(contentInfo);
            }
            if (this.RSAdata != null || this.digestAttr != null) {
                this.messageDigest = provider == null || provider.startsWith("SunPKCS11") ? MessageDigest.getInstance(PdfPKCS7.getStandardJavaName(this.getHashAlgorithm())) : MessageDigest.getInstance(PdfPKCS7.getStandardJavaName(this.getHashAlgorithm()), provider);
            }
            this.sig = provider == null ? Signature.getInstance(this.getDigestAlgorithm()) : Signature.getInstance(this.getDigestAlgorithm(), provider);
            this.sig.initVerify(this.signCert.getPublicKey());
        }
        catch (Exception e) {
            throw new ExceptionConverter(e);
        }
    }

    public PdfPKCS7(PrivateKey privKey, Certificate[] certChain, CRL[] crlList, String hashAlgorithm, String provider, boolean hasRSAdata) throws InvalidKeyException, NoSuchProviderException, NoSuchAlgorithmException {
        this.privKey = privKey;
        this.provider = provider;
        this.digestAlgorithm = allowedDigests.get(hashAlgorithm.toUpperCase());
        if (this.digestAlgorithm == null) {
            throw new NoSuchAlgorithmException(MessageLocalization.getComposedMessage("unknown.hash.algorithm.1", hashAlgorithm));
        }
        this.signerversion = 1;
        this.version = 1;
        this.certs = new ArrayList<Certificate>();
        this.crls = new ArrayList<CRL>();
        this.digestalgos = new HashSet<String>();
        this.digestalgos.add(this.digestAlgorithm);
        this.signCert = (X509Certificate)certChain[0];
        this.certs.addAll(Arrays.asList(certChain));
        if (crlList != null) {
            this.crls.addAll(Arrays.asList(crlList));
        }
        if (privKey != null) {
            this.digestEncryptionAlgorithm = privKey.getAlgorithm();
            if (this.digestEncryptionAlgorithm.equals("RSA")) {
                this.digestEncryptionAlgorithm = ID_RSA;
            } else if (this.digestEncryptionAlgorithm.equals("DSA")) {
                this.digestEncryptionAlgorithm = ID_DSA;
            } else {
                throw new NoSuchAlgorithmException(MessageLocalization.getComposedMessage("unknown.key.algorithm.1", this.digestEncryptionAlgorithm));
            }
        }
        if (hasRSAdata) {
            this.RSAdata = new byte[0];
            this.messageDigest = provider == null || provider.startsWith("SunPKCS11") ? MessageDigest.getInstance(PdfPKCS7.getStandardJavaName(this.getHashAlgorithm())) : MessageDigest.getInstance(PdfPKCS7.getStandardJavaName(this.getHashAlgorithm()), provider);
        }
        if (privKey != null) {
            this.sig = provider == null ? Signature.getInstance(this.getDigestAlgorithm()) : Signature.getInstance(this.getDigestAlgorithm(), provider);
            this.sig.initSign(privKey);
        }
    }

    public void update(byte[] buf, int off, int len) throws SignatureException {
        if (this.RSAdata != null || this.digestAttr != null) {
            this.messageDigest.update(buf, off, len);
        } else {
            this.sig.update(buf, off, len);
        }
    }

    public boolean verify() throws SignatureException {
        if (this.verified) {
            return this.verifyResult;
        }
        if (this.sigAttr != null) {
            this.sig.update(this.sigAttr);
            if (this.RSAdata != null) {
                byte[] msd = this.messageDigest.digest();
                this.messageDigest.update(msd);
            }
            this.verifyResult = Arrays.equals(this.messageDigest.digest(), this.digestAttr) && this.sig.verify(this.digest);
        } else {
            if (this.RSAdata != null) {
                this.sig.update(this.messageDigest.digest());
            }
            this.verifyResult = this.sig.verify(this.digest);
        }
        this.verified = true;
        return this.verifyResult;
    }

    public boolean verifyTimestampImprint() throws NoSuchAlgorithmException {
        if (this.timeStampToken == null) {
            return false;
        }
        MessageImprint imprint = this.timeStampToken.getTimeStampInfo().toASN1Structure().getMessageImprint();
        byte[] md = MessageDigest.getInstance("SHA-1").digest(this.digest);
        byte[] imphashed = imprint.getHashedMessage();
        return Arrays.equals(md, imphashed);
    }

    public Certificate[] getCertificates() {
        return this.certs.toArray(new Certificate[0]);
    }

    public Certificate[] getSignCertificateChain() {
        return this.signCerts.toArray(new X509Certificate[0]);
    }

    private void signCertificateChain() {
        ArrayList<Certificate> cc = new ArrayList<Certificate>();
        cc.add(this.signCert);
        ArrayList<Certificate> oc = new ArrayList<Certificate>(this.certs);
        for (int k = 0; k < oc.size(); ++k) {
            if (!this.signCert.getSerialNumber().equals(((X509Certificate)oc.get(k)).getSerialNumber())) continue;
            oc.remove(k);
            --k;
        }
        boolean found = true;
        block3: while (found) {
            X509Certificate v = (X509Certificate)cc.get(cc.size() - 1);
            found = false;
            for (int k = 0; k < oc.size(); ++k) {
                try {
                    if (this.provider == null) {
                        v.verify(((Certificate)oc.get(k)).getPublicKey());
                    } else {
                        v.verify(((Certificate)oc.get(k)).getPublicKey(), this.provider);
                    }
                    found = true;
                    cc.add((Certificate)oc.get(k));
                    oc.remove(k);
                    continue block3;
                }
                catch (Exception exception) {
                    continue;
                }
            }
        }
        this.signCerts = cc;
    }

    public Collection getCRLs() {
        return this.crls;
    }

    public X509Certificate getSigningCertificate() {
        return this.signCert;
    }

    public int getVersion() {
        return this.version;
    }

    public int getSigningInfoVersion() {
        return this.signerversion;
    }

    public String getDigestAlgorithm() {
        String dea = PdfPKCS7.getAlgorithm(this.digestEncryptionAlgorithm);
        if (dea == null) {
            dea = this.digestEncryptionAlgorithm;
        }
        return this.getHashAlgorithm() + "with" + dea;
    }

    public String getHashAlgorithm() {
        return PdfPKCS7.getDigest(this.digestAlgorithm);
    }

    public static KeyStore loadCacertsKeyStore() {
        return PdfPKCS7.loadCacertsKeyStore(null);
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    public static KeyStore loadCacertsKeyStore(String provider) {
        File file = new File(System.getProperty("java.home"), "lib");
        file = new File(file, "security");
        file = new File(file, "cacerts");
        try (FileInputStream fin = new FileInputStream(file);){
            KeyStore k = provider == null ? KeyStore.getInstance("JKS") : KeyStore.getInstance("JKS", provider);
            k.load(fin, null);
            KeyStore keyStore = k;
            return keyStore;
        }
        catch (Exception e) {
            throw new ExceptionConverter(e);
        }
    }

    public static String verifyCertificate(X509Certificate cert, Collection crls, Calendar calendar) {
        if (calendar == null) {
            calendar = new GregorianCalendar();
        }
        if (cert.hasUnsupportedCriticalExtension()) {
            return "Has unsupported critical extension";
        }
        try {
            cert.checkValidity(calendar.getTime());
        }
        catch (Exception e) {
            return e.getMessage();
        }
        if (crls != null) {
            for (Object crl : crls) {
                if (!((CRL)crl).isRevoked(cert)) continue;
                return "Certificate revoked";
            }
        }
        return null;
    }

    /*
     * Loose catch block
     */
    public static Object[] verifyCertificates(Certificate[] certs, KeyStore keystore, Collection crls, Calendar calendar) {
        if (calendar == null) {
            calendar = new GregorianCalendar();
        }
        for (int k = 0; k < certs.length; ++k) {
            int j;
            X509Certificate cert = (X509Certificate)certs[k];
            String err = PdfPKCS7.verifyCertificate(cert, crls, calendar);
            if (err != null) {
                return new Object[]{cert, err};
            }
            try {
                Enumeration<String> aliases = keystore.aliases();
                while (aliases.hasMoreElements()) {
                    String alias22222222222 = aliases.nextElement();
                    if (!keystore.isCertificateEntry(alias22222222222)) continue;
                    X509Certificate certStoreX509 = (X509Certificate)keystore.getCertificate(alias22222222222);
                    if (PdfPKCS7.verifyCertificate(certStoreX509, crls, calendar) != null) continue;
                    {
                        catch (Exception alias22222222222) {}
                    }
                    try {
                        cert.verify(certStoreX509.getPublicKey());
                        return null;
                    }
                    catch (Exception exception) {
                    }
                }
            }
            catch (Exception aliases) {
                // empty catch block
            }
            for (j = 0; j < certs.length; ++j) {
                if (j == k) continue;
                X509Certificate certNext = (X509Certificate)certs[j];
                try {
                    cert.verify(certNext.getPublicKey());
                    break;
                }
                catch (Exception exception) {
                    // empty catch block
                }
            }
            if (j != certs.length) continue;
            return new Object[]{cert, "Cannot be verified against the KeyStore or the certificate chain"};
        }
        return new Object[]{null, "Invalid state. Possible circular certificate chain"};
    }

    public static String getOCSPURL(X509Certificate certificate) {
        try {
            ASN1Primitive obj = PdfPKCS7.getExtensionValue(certificate, Extension.authorityInfoAccess.getId());
            if (obj == null) {
                return null;
            }
            ASN1Sequence AccessDescriptions = (ASN1Sequence)obj;
            for (int i = 0; i < AccessDescriptions.size(); ++i) {
                ASN1Sequence AccessDescription = (ASN1Sequence)AccessDescriptions.getObjectAt(i);
                if (AccessDescription.size() != 2 || !(AccessDescription.getObjectAt(0) instanceof ASN1ObjectIdentifier) || !((ASN1ObjectIdentifier)AccessDescription.getObjectAt(0)).getId().equals("1.3.6.1.5.5.7.48.1")) continue;
                return PdfPKCS7.getStringFromGeneralName((ASN1Primitive)AccessDescription.getObjectAt(1));
            }
        }
        catch (Exception exception) {
            // empty catch block
        }
        return null;
    }

    public boolean isRevocationValid() {
        if (this.basicResp == null) {
            return false;
        }
        if (this.signCerts.size() < 2) {
            return false;
        }
        try {
            X509Certificate[] cs = (X509Certificate[])this.getSignCertificateChain();
            SingleResp sr = this.basicResp.getResponses()[0];
            CertificateID cid = sr.getCertID();
            X509Certificate sigcer = this.getSigningCertificate();
            X509Certificate isscer = cs[1];
            DigestCalculatorProvider digCalcProv = new JcaDigestCalculatorProviderBuilder().setProvider(this.provider).build();
            CertificateID id = new CertificateID(digCalcProv.get(CertificateID.HASH_SHA1), (X509CertificateHolder)new JcaX509CertificateHolder(isscer), sigcer.getSerialNumber());
            return id.equals((Object)cid);
        }
        catch (Exception exception) {
            return false;
        }
    }

    @Nullable
    private static ASN1Primitive getExtensionValue(X509Certificate cert, String oid) throws IOException {
        byte[] bytes = cert.getExtensionValue(oid);
        if (bytes == null) {
            return null;
        }
        ASN1InputStream aIn = new ASN1InputStream((InputStream)new ByteArrayInputStream(bytes));
        ASN1OctetString octs = (ASN1OctetString)aIn.readObject();
        aIn = new ASN1InputStream((InputStream)new ByteArrayInputStream(octs.getOctets()));
        return aIn.readObject();
    }

    @Nonnull
    private static String getStringFromGeneralName(ASN1Primitive names) {
        ASN1TaggedObject taggedObject = (ASN1TaggedObject)names;
        return new String(ASN1OctetString.getInstance((ASN1TaggedObject)taggedObject, (boolean)false).getOctets(), StandardCharsets.ISO_8859_1);
    }

    private static ASN1Primitive getIssuer(byte[] enc) {
        try {
            ASN1InputStream in = new ASN1InputStream((InputStream)new ByteArrayInputStream(enc));
            ASN1Sequence seq = (ASN1Sequence)in.readObject();
            return (ASN1Primitive)seq.getObjectAt(seq.getObjectAt(0) instanceof ASN1TaggedObject ? 3 : 2);
        }
        catch (IOException e) {
            throw new ExceptionConverter(e);
        }
    }

    private static ASN1Primitive getSubject(byte[] enc) {
        try {
            ASN1InputStream in = new ASN1InputStream((InputStream)new ByteArrayInputStream(enc));
            ASN1Sequence seq = (ASN1Sequence)in.readObject();
            return (ASN1Primitive)seq.getObjectAt(seq.getObjectAt(0) instanceof ASN1TaggedObject ? 5 : 4);
        }
        catch (IOException e) {
            throw new ExceptionConverter(e);
        }
    }

    public static X509Name getIssuerFields(X509Certificate cert) {
        try {
            return new X509Name((ASN1Sequence)PdfPKCS7.getIssuer(cert.getTBSCertificate()));
        }
        catch (Exception e) {
            throw new ExceptionConverter(e);
        }
    }

    public static X509Name getSubjectFields(X509Certificate cert) {
        try {
            return new X509Name((ASN1Sequence)PdfPKCS7.getSubject(cert.getTBSCertificate()));
        }
        catch (Exception e) {
            throw new ExceptionConverter(e);
        }
    }

    public byte[] getEncodedPKCS1() {
        try {
            this.digest = this.externalDigest != null ? this.externalDigest : this.sig.sign();
            ByteArrayOutputStream bOut = new ByteArrayOutputStream();
            ASN1OutputStream dout = new ASN1OutputStream((OutputStream)bOut);
            dout.writeObject((ASN1Primitive)new DEROctetString(this.digest));
            dout.close();
            return bOut.toByteArray();
        }
        catch (Exception e) {
            throw new ExceptionConverter(e);
        }
    }

    public void setExternalDigest(byte[] digest, byte[] RSAdata, String digestEncryptionAlgorithm) {
        this.externalDigest = digest;
        this.externalRSAdata = RSAdata;
        if (digestEncryptionAlgorithm != null) {
            if (digestEncryptionAlgorithm.equals("RSA")) {
                this.digestEncryptionAlgorithm = ID_RSA;
            } else if (digestEncryptionAlgorithm.equals("DSA")) {
                this.digestEncryptionAlgorithm = ID_DSA;
            } else {
                throw new ExceptionConverter(new NoSuchAlgorithmException(MessageLocalization.getComposedMessage("unknown.key.algorithm.1", digestEncryptionAlgorithm)));
            }
        }
    }

    public byte[] getEncodedPKCS7() {
        return this.getEncodedPKCS7(null, null, null, null);
    }

    public byte[] getEncodedPKCS7(byte[] secondDigest, Calendar signingTime) {
        return this.getEncodedPKCS7(secondDigest, signingTime, null, null);
    }

    public byte[] getEncodedPKCS7(byte[] secondDigest, Calendar signingTime, TSAClient tsaClient, byte[] ocsp) {
        try {
            ASN1EncodableVector unauthAttributes;
            byte[] tsImprint;
            byte[] tsToken;
            if (this.externalDigest != null) {
                this.digest = this.externalDigest;
                if (this.RSAdata != null) {
                    this.RSAdata = this.externalRSAdata;
                }
            } else if (this.externalRSAdata != null && this.RSAdata != null) {
                this.RSAdata = this.externalRSAdata;
                this.sig.update(this.RSAdata);
                this.digest = this.sig.sign();
            } else {
                if (this.RSAdata != null) {
                    this.RSAdata = this.messageDigest.digest();
                    this.sig.update(this.RSAdata);
                }
                this.digest = this.sig.sign();
            }
            ASN1EncodableVector digestAlgorithms = new ASN1EncodableVector();
            for (String digestalgo : this.digestalgos) {
                ASN1EncodableVector algos = new ASN1EncodableVector();
                algos.add((ASN1Encodable)new ASN1ObjectIdentifier(digestalgo));
                algos.add((ASN1Encodable)DERNull.INSTANCE);
                digestAlgorithms.add((ASN1Encodable)new DERSequence(algos));
            }
            ASN1EncodableVector v = new ASN1EncodableVector();
            v.add((ASN1Encodable)new ASN1ObjectIdentifier(ID_PKCS7_DATA));
            if (this.RSAdata != null) {
                v.add((ASN1Encodable)new DERTaggedObject(0, (ASN1Encodable)new DEROctetString(this.RSAdata)));
            }
            DERSequence contentinfo = new DERSequence(v);
            v = new ASN1EncodableVector();
            for (Object e : this.certs) {
                ASN1InputStream tempstream = new ASN1InputStream((InputStream)new ByteArrayInputStream(((X509Certificate)e).getEncoded()));
                v.add((ASN1Encodable)tempstream.readObject());
            }
            DERSet dercertificates = new DERSet(v);
            ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
            aSN1EncodableVector.add((ASN1Encodable)new ASN1Integer((long)this.signerversion));
            v = new ASN1EncodableVector();
            v.add((ASN1Encodable)PdfPKCS7.getIssuer(this.signCert.getTBSCertificate()));
            v.add((ASN1Encodable)new ASN1Integer(this.signCert.getSerialNumber()));
            aSN1EncodableVector.add((ASN1Encodable)new DERSequence(v));
            v = new ASN1EncodableVector();
            v.add((ASN1Encodable)new ASN1ObjectIdentifier(this.digestAlgorithm));
            v.add((ASN1Encodable)DERNull.INSTANCE);
            aSN1EncodableVector.add((ASN1Encodable)new DERSequence(v));
            if (secondDigest != null && signingTime != null) {
                aSN1EncodableVector.add((ASN1Encodable)new DERTaggedObject(false, 0, (ASN1Encodable)this.getAuthenticatedAttributeSet(secondDigest, signingTime, ocsp)));
            }
            v = new ASN1EncodableVector();
            v.add((ASN1Encodable)new ASN1ObjectIdentifier(this.digestEncryptionAlgorithm));
            v.add((ASN1Encodable)DERNull.INSTANCE);
            aSN1EncodableVector.add((ASN1Encodable)new DERSequence(v));
            aSN1EncodableVector.add((ASN1Encodable)new DEROctetString(this.digest));
            if (tsaClient != null && (tsToken = tsaClient.getTimeStampToken(this, tsImprint = MessageDigest.getInstance("SHA-1").digest(this.digest))) != null && (unauthAttributes = this.buildUnauthenticatedAttributes(tsToken)) != null) {
                aSN1EncodableVector.add((ASN1Encodable)new DERTaggedObject(false, 1, (ASN1Encodable)new DERSet(unauthAttributes)));
            }
            ASN1EncodableVector body = new ASN1EncodableVector();
            body.add((ASN1Encodable)new ASN1Integer((long)this.version));
            body.add((ASN1Encodable)new DERSet(digestAlgorithms));
            body.add((ASN1Encodable)contentinfo);
            body.add((ASN1Encodable)new DERTaggedObject(false, 0, (ASN1Encodable)dercertificates));
            body.add((ASN1Encodable)new DERSet((ASN1Encodable)new DERSequence(aSN1EncodableVector)));
            ASN1EncodableVector whole = new ASN1EncodableVector();
            whole.add((ASN1Encodable)new ASN1ObjectIdentifier(ID_PKCS7_SIGNED_DATA));
            whole.add((ASN1Encodable)new DERTaggedObject(0, (ASN1Encodable)new DERSequence(body)));
            ByteArrayOutputStream bOut = new ByteArrayOutputStream();
            ASN1OutputStream dout = new ASN1OutputStream((OutputStream)bOut);
            dout.writeObject((ASN1Primitive)new DERSequence(whole));
            dout.close();
            return bOut.toByteArray();
        }
        catch (Exception e) {
            throw new ExceptionConverter(e);
        }
    }

    private ASN1EncodableVector buildUnauthenticatedAttributes(byte[] timeStampToken) throws IOException {
        if (timeStampToken == null) {
            return null;
        }
        String ID_TIME_STAMP_TOKEN = "1.2.840.113549.1.9.16.2.14";
        ASN1InputStream tempstream = new ASN1InputStream((InputStream)new ByteArrayInputStream(timeStampToken));
        ASN1EncodableVector unauthAttributes = new ASN1EncodableVector();
        ASN1EncodableVector v = new ASN1EncodableVector();
        v.add((ASN1Encodable)new ASN1ObjectIdentifier(ID_TIME_STAMP_TOKEN));
        ASN1Sequence seq = (ASN1Sequence)tempstream.readObject();
        v.add((ASN1Encodable)new DERSet((ASN1Encodable)seq));
        unauthAttributes.add((ASN1Encodable)new DERSequence(v));
        return unauthAttributes;
    }

    public byte[] getAuthenticatedAttributeBytes(byte[] secondDigest, Calendar signingTime, byte[] ocsp) {
        try {
            return this.getAuthenticatedAttributeSet(secondDigest, signingTime, ocsp).getEncoded("DER");
        }
        catch (Exception e) {
            throw new ExceptionConverter(e);
        }
    }

    private DERSet getAuthenticatedAttributeSet(byte[] secondDigest, Calendar signingTime, byte[] ocsp) {
        try {
            ASN1EncodableVector attribute = new ASN1EncodableVector();
            ASN1EncodableVector v = new ASN1EncodableVector();
            v.add((ASN1Encodable)new ASN1ObjectIdentifier(ID_CONTENT_TYPE));
            v.add((ASN1Encodable)new DERSet((ASN1Encodable)new ASN1ObjectIdentifier(ID_PKCS7_DATA)));
            attribute.add((ASN1Encodable)new DERSequence(v));
            v = new ASN1EncodableVector();
            v.add((ASN1Encodable)new ASN1ObjectIdentifier(ID_SIGNING_TIME));
            v.add((ASN1Encodable)new DERSet((ASN1Encodable)new DERUTCTime(signingTime.getTime())));
            attribute.add((ASN1Encodable)new DERSequence(v));
            v = new ASN1EncodableVector();
            v.add((ASN1Encodable)new ASN1ObjectIdentifier(ID_MESSAGE_DIGEST));
            v.add((ASN1Encodable)new DERSet((ASN1Encodable)new DEROctetString(secondDigest)));
            attribute.add((ASN1Encodable)new DERSequence(v));
            if (ocsp != null) {
                v = new ASN1EncodableVector();
                v.add((ASN1Encodable)new ASN1ObjectIdentifier(ID_ADBE_REVOCATION));
                DEROctetString doctet = new DEROctetString(ocsp);
                ASN1EncodableVector vo1 = new ASN1EncodableVector();
                ASN1EncodableVector v2 = new ASN1EncodableVector();
                v2.add((ASN1Encodable)OCSPObjectIdentifiers.id_pkix_ocsp_basic);
                v2.add((ASN1Encodable)doctet);
                ASN1Enumerated den = new ASN1Enumerated(0);
                ASN1EncodableVector v3 = new ASN1EncodableVector();
                v3.add((ASN1Encodable)den);
                v3.add((ASN1Encodable)new DERTaggedObject(true, 0, (ASN1Encodable)new DERSequence(v2)));
                vo1.add((ASN1Encodable)new DERSequence(v3));
                v.add((ASN1Encodable)new DERSet((ASN1Encodable)new DERSequence((ASN1Encodable)new DERTaggedObject(true, 1, (ASN1Encodable)new DERSequence(vo1)))));
                attribute.add((ASN1Encodable)new DERSequence(v));
            } else if (!this.crls.isEmpty()) {
                v = new ASN1EncodableVector();
                v.add((ASN1Encodable)new ASN1ObjectIdentifier(ID_ADBE_REVOCATION));
                ASN1EncodableVector v2 = new ASN1EncodableVector();
                for (CRL crl : this.crls) {
                    ASN1InputStream t = new ASN1InputStream((InputStream)new ByteArrayInputStream(((X509CRL)crl).getEncoded()));
                    v2.add((ASN1Encodable)t.readObject());
                }
                v.add((ASN1Encodable)new DERSet((ASN1Encodable)new DERSequence((ASN1Encodable)new DERTaggedObject(true, 0, (ASN1Encodable)new DERSequence(v2)))));
                attribute.add((ASN1Encodable)new DERSequence(v));
            }
            return new DERSet(attribute);
        }
        catch (Exception e) {
            throw new ExceptionConverter(e);
        }
    }

    public String getReason() {
        return this.reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getLocation() {
        return this.location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Calendar getSignDate() {
        return this.signDate;
    }

    public void setSignDate(Calendar signDate) {
        this.signDate = signDate;
    }

    public String getSignName() {
        return this.signName;
    }

    public void setSignName(String signName) {
        this.signName = signName;
    }

    private static String getStandardJavaName(String algName) {
        if ("SHA1".equals(algName)) {
            return "SHA-1";
        }
        if ("SHA224".equals(algName)) {
            return "SHA-224";
        }
        if ("SHA256".equals(algName)) {
            return "SHA-256";
        }
        if ("SHA384".equals(algName)) {
            return "SHA-384";
        }
        if ("SHA512".equals(algName)) {
            return "SHA-512";
        }
        return algName;
    }

    static {
        digestNames.put("1.2.840.113549.2.5", "MD5");
        digestNames.put("1.2.840.113549.2.2", "MD2");
        digestNames.put("1.3.14.3.2.26", "SHA1");
        digestNames.put("2.16.840.1.101.3.4.2.4", "SHA224");
        digestNames.put("2.16.840.1.101.3.4.2.1", "SHA256");
        digestNames.put("2.16.840.1.101.3.4.2.2", "SHA384");
        digestNames.put("2.16.840.1.101.3.4.2.3", "SHA512");
        digestNames.put("1.3.36.3.2.2", "RIPEMD128");
        digestNames.put("1.3.36.3.2.1", "RIPEMD160");
        digestNames.put("1.3.36.3.2.3", "RIPEMD256");
        digestNames.put("1.2.840.113549.1.1.4", "MD5");
        digestNames.put("1.2.840.113549.1.1.2", "MD2");
        digestNames.put("1.2.840.113549.1.1.5", "SHA1");
        digestNames.put("1.2.840.113549.1.1.14", "SHA224");
        digestNames.put("1.2.840.113549.1.1.11", "SHA256");
        digestNames.put("1.2.840.113549.1.1.12", "SHA384");
        digestNames.put("1.2.840.113549.1.1.13", "SHA512");
        digestNames.put("1.2.840.10040.4.3", "SHA1");
        digestNames.put("2.16.840.1.101.3.4.3.1", "SHA224");
        digestNames.put("2.16.840.1.101.3.4.3.2", "SHA256");
        digestNames.put("2.16.840.1.101.3.4.3.3", "SHA384");
        digestNames.put("2.16.840.1.101.3.4.3.4", "SHA512");
        digestNames.put("1.3.36.3.3.1.3", "RIPEMD128");
        digestNames.put("1.3.36.3.3.1.2", "RIPEMD160");
        digestNames.put("1.3.36.3.3.1.4", "RIPEMD256");
        algorithmNames.put(ID_RSA, "RSA");
        algorithmNames.put(ID_DSA, "DSA");
        algorithmNames.put("1.2.840.113549.1.1.2", "RSA");
        algorithmNames.put("1.2.840.113549.1.1.4", "RSA");
        algorithmNames.put("1.2.840.113549.1.1.5", "RSA");
        algorithmNames.put("1.2.840.113549.1.1.14", "RSA");
        algorithmNames.put("1.2.840.113549.1.1.11", "RSA");
        algorithmNames.put("1.2.840.113549.1.1.12", "RSA");
        algorithmNames.put("1.2.840.113549.1.1.13", "RSA");
        algorithmNames.put("1.2.840.10040.4.3", "DSA");
        algorithmNames.put("2.16.840.1.101.3.4.3.1", "DSA");
        algorithmNames.put("2.16.840.1.101.3.4.3.2", "DSA");
        algorithmNames.put("1.3.36.3.3.1.3", "RSA");
        algorithmNames.put("1.3.36.3.3.1.2", "RSA");
        algorithmNames.put("1.3.36.3.3.1.4", "RSA");
        allowedDigests.put("MD5", "1.2.840.113549.2.5");
        allowedDigests.put("MD2", "1.2.840.113549.2.2");
        allowedDigests.put("SHA1", "1.3.14.3.2.26");
        allowedDigests.put("SHA224", "2.16.840.1.101.3.4.2.4");
        allowedDigests.put("SHA256", "2.16.840.1.101.3.4.2.1");
        allowedDigests.put("SHA384", "2.16.840.1.101.3.4.2.2");
        allowedDigests.put("SHA512", "2.16.840.1.101.3.4.2.3");
        allowedDigests.put("MD-5", "1.2.840.113549.2.5");
        allowedDigests.put("MD-2", "1.2.840.113549.2.2");
        allowedDigests.put("SHA-1", "1.3.14.3.2.26");
        allowedDigests.put("SHA-224", "2.16.840.1.101.3.4.2.4");
        allowedDigests.put("SHA-256", "2.16.840.1.101.3.4.2.1");
        allowedDigests.put("SHA-384", "2.16.840.1.101.3.4.2.2");
        allowedDigests.put("SHA-512", "2.16.840.1.101.3.4.2.3");
        allowedDigests.put("RIPEMD128", "1.3.36.3.2.2");
        allowedDigests.put("RIPEMD-128", "1.3.36.3.2.2");
        allowedDigests.put("RIPEMD160", "1.3.36.3.2.1");
        allowedDigests.put("RIPEMD-160", "1.3.36.3.2.1");
        allowedDigests.put("RIPEMD256", "1.3.36.3.2.3");
        allowedDigests.put("RIPEMD-256", "1.3.36.3.2.3");
    }

    public static class X509NameTokenizer {
        private final String oid;
        private int index;
        private final StringBuffer buf = new StringBuffer();

        public X509NameTokenizer(String oid) {
            this.oid = oid;
            this.index = -1;
        }

        public boolean hasMoreTokens() {
            return this.index != this.oid.length();
        }

        public String nextToken() {
            int end;
            if (this.index == this.oid.length()) {
                return null;
            }
            boolean quoted = false;
            boolean escaped = false;
            this.buf.setLength(0);
            for (end = this.index + 1; end != this.oid.length(); ++end) {
                char c = this.oid.charAt(end);
                if (c == '\"') {
                    if (!escaped) {
                        quoted = !quoted;
                    } else {
                        this.buf.append(c);
                    }
                    escaped = false;
                    continue;
                }
                if (escaped || quoted) {
                    this.buf.append(c);
                    escaped = false;
                    continue;
                }
                if (c == '\\') {
                    escaped = true;
                    continue;
                }
                if (c == ',') break;
                this.buf.append(c);
            }
            this.index = end;
            return this.buf.toString().trim();
        }
    }

    public static class X509Name {
        public static final ASN1ObjectIdentifier C = new ASN1ObjectIdentifier("2.5.4.6");
        public static final ASN1ObjectIdentifier O = new ASN1ObjectIdentifier("2.5.4.10");
        public static final ASN1ObjectIdentifier OU = new ASN1ObjectIdentifier("2.5.4.11");
        public static final ASN1ObjectIdentifier T = new ASN1ObjectIdentifier("2.5.4.12");
        public static final ASN1ObjectIdentifier CN = new ASN1ObjectIdentifier("2.5.4.3");
        public static final ASN1ObjectIdentifier SN = new ASN1ObjectIdentifier("2.5.4.5");
        public static final ASN1ObjectIdentifier L = new ASN1ObjectIdentifier("2.5.4.7");
        public static final ASN1ObjectIdentifier ST = new ASN1ObjectIdentifier("2.5.4.8");
        public static final ASN1ObjectIdentifier SURNAME = new ASN1ObjectIdentifier("2.5.4.4");
        public static final ASN1ObjectIdentifier GIVENNAME = new ASN1ObjectIdentifier("2.5.4.42");
        public static final ASN1ObjectIdentifier INITIALS = new ASN1ObjectIdentifier("2.5.4.43");
        public static final ASN1ObjectIdentifier GENERATION = new ASN1ObjectIdentifier("2.5.4.44");
        public static final ASN1ObjectIdentifier UNIQUE_IDENTIFIER = new ASN1ObjectIdentifier("2.5.4.45");
        public static final ASN1ObjectIdentifier EmailAddress;
        public static final ASN1ObjectIdentifier E;
        public static final ASN1ObjectIdentifier DC;
        public static final ASN1ObjectIdentifier UID;
        @Deprecated
        public static HashMap DefaultSymbols;
        public static Map<ASN1Encodable, String> defaultSymbols;
        @Deprecated
        public HashMap values = new HashMap();
        public Map<String, List<String>> valuesMap = new HashMap<String, List<String>>();

        public X509Name(ASN1Sequence seq) {
            Enumeration e = seq.getObjects();
            while (e.hasMoreElements()) {
                ASN1Set set = (ASN1Set)e.nextElement();
                for (int i = 0; i < set.size(); ++i) {
                    ASN1Sequence s = (ASN1Sequence)set.getObjectAt(i);
                    ASN1Encodable encodable = s.getObjectAt(0);
                    String id = defaultSymbols.get(encodable);
                    if (id == null) continue;
                    List vs = this.valuesMap.computeIfAbsent(id, k -> new ArrayList());
                    vs.add(((ASN1String)s.getObjectAt(1)).getString());
                }
            }
        }

        public X509Name(String dirName) {
            X509NameTokenizer nTok = new X509NameTokenizer(dirName);
            while (nTok.hasMoreTokens()) {
                String token = nTok.nextToken();
                int index = token.indexOf(61);
                if (index == -1) {
                    throw new IllegalArgumentException(MessageLocalization.getComposedMessage("badly.formated.directory.string"));
                }
                String id = token.substring(0, index).toUpperCase();
                String value = token.substring(index + 1);
                List vs = this.valuesMap.computeIfAbsent(id, k -> new ArrayList());
                vs.add(value);
            }
        }

        @Nullable
        public String getField(String name) {
            List<String> vs = this.valuesMap.get(name);
            return vs == null ? null : vs.get(0);
        }

        @Deprecated
        public ArrayList getFieldArray(String name) {
            return (ArrayList)this.valuesMap.get(name);
        }

        public List<String> getFieldsByName(String name) {
            return this.valuesMap.get(name);
        }

        @Deprecated
        public HashMap getFields() {
            return (HashMap)this.valuesMap;
        }

        public Map<String, List<String>> getAllFields() {
            return this.valuesMap;
        }

        public String toString() {
            return this.valuesMap.toString();
        }

        static {
            E = EmailAddress = new ASN1ObjectIdentifier("1.2.840.113549.1.9.1");
            DC = new ASN1ObjectIdentifier("0.9.2342.19200300.100.1.25");
            UID = new ASN1ObjectIdentifier("0.9.2342.19200300.100.1.1");
            DefaultSymbols = new HashMap();
            defaultSymbols = new HashMap<ASN1Encodable, String>();
            defaultSymbols.put((ASN1Encodable)C, "C");
            defaultSymbols.put((ASN1Encodable)O, "O");
            defaultSymbols.put((ASN1Encodable)T, "T");
            defaultSymbols.put((ASN1Encodable)OU, "OU");
            defaultSymbols.put((ASN1Encodable)CN, "CN");
            defaultSymbols.put((ASN1Encodable)L, "L");
            defaultSymbols.put((ASN1Encodable)ST, "ST");
            defaultSymbols.put((ASN1Encodable)SN, "SN");
            defaultSymbols.put((ASN1Encodable)EmailAddress, "E");
            defaultSymbols.put((ASN1Encodable)DC, "DC");
            defaultSymbols.put((ASN1Encodable)UID, "UID");
            defaultSymbols.put((ASN1Encodable)SURNAME, "SURNAME");
            defaultSymbols.put((ASN1Encodable)GIVENNAME, "GIVENNAME");
            defaultSymbols.put((ASN1Encodable)INITIALS, "INITIALS");
            defaultSymbols.put((ASN1Encodable)GENERATION, "GENERATION");
            DefaultSymbols.putAll(defaultSymbols);
        }
    }
}

