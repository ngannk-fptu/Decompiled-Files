/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.mail.Address
 *  javax.mail.MessagingException
 *  javax.mail.Part
 *  javax.mail.internet.InternetAddress
 *  javax.mail.internet.MimeMessage
 *  javax.mail.internet.MimeMultipart
 *  org.bouncycastle.asn1.ASN1IA5String
 *  org.bouncycastle.asn1.ASN1InputStream
 *  org.bouncycastle.asn1.ASN1OctetString
 *  org.bouncycastle.asn1.ASN1Primitive
 *  org.bouncycastle.asn1.ASN1Sequence
 *  org.bouncycastle.asn1.ASN1String
 *  org.bouncycastle.asn1.ASN1TaggedObject
 *  org.bouncycastle.asn1.DEROctetString
 *  org.bouncycastle.asn1.cms.Attribute
 *  org.bouncycastle.asn1.cms.AttributeTable
 *  org.bouncycastle.asn1.cms.CMSAttributes
 *  org.bouncycastle.asn1.cms.Time
 *  org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers
 *  org.bouncycastle.asn1.x500.AttributeTypeAndValue
 *  org.bouncycastle.asn1.x500.RDN
 *  org.bouncycastle.asn1.x509.AuthorityKeyIdentifier
 *  org.bouncycastle.asn1.x509.ExtendedKeyUsage
 *  org.bouncycastle.asn1.x509.Extension
 *  org.bouncycastle.asn1.x509.KeyPurposeId
 *  org.bouncycastle.asn1.x509.TBSCertificate
 *  org.bouncycastle.cert.jcajce.JcaCertStoreBuilder
 *  org.bouncycastle.cms.SignerInformation
 *  org.bouncycastle.cms.SignerInformationStore
 *  org.bouncycastle.cms.jcajce.JcaSimpleSignerInfoVerifierBuilder
 *  org.bouncycastle.cms.jcajce.JcaX509CertSelectorConverter
 *  org.bouncycastle.pkix.jcajce.CertPathReviewerException
 *  org.bouncycastle.pkix.jcajce.PKIXCertPathReviewer
 *  org.bouncycastle.pkix.util.ErrorBundle
 *  org.bouncycastle.pkix.util.filter.TrustedInput
 *  org.bouncycastle.pkix.util.filter.UntrustedInput
 *  org.bouncycastle.util.Integers
 */
package org.bouncycastle.mail.smime.validator;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.PublicKey;
import java.security.cert.CertPath;
import java.security.cert.CertStore;
import java.security.cert.CertStoreException;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateExpiredException;
import java.security.cert.CertificateFactory;
import java.security.cert.CertificateNotYetValidException;
import java.security.cert.PKIXParameters;
import java.security.cert.TrustAnchor;
import java.security.cert.X509CertSelector;
import java.security.cert.X509Certificate;
import java.security.interfaces.DSAPublicKey;
import java.security.interfaces.RSAPublicKey;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.mail.Address;
import javax.mail.MessagingException;
import javax.mail.Part;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import org.bouncycastle.asn1.ASN1IA5String;
import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1String;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.cms.Attribute;
import org.bouncycastle.asn1.cms.AttributeTable;
import org.bouncycastle.asn1.cms.CMSAttributes;
import org.bouncycastle.asn1.cms.Time;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.x500.AttributeTypeAndValue;
import org.bouncycastle.asn1.x500.RDN;
import org.bouncycastle.asn1.x509.AuthorityKeyIdentifier;
import org.bouncycastle.asn1.x509.ExtendedKeyUsage;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.KeyPurposeId;
import org.bouncycastle.asn1.x509.TBSCertificate;
import org.bouncycastle.cert.jcajce.JcaCertStoreBuilder;
import org.bouncycastle.cms.SignerInformation;
import org.bouncycastle.cms.SignerInformationStore;
import org.bouncycastle.cms.jcajce.JcaSimpleSignerInfoVerifierBuilder;
import org.bouncycastle.cms.jcajce.JcaX509CertSelectorConverter;
import org.bouncycastle.mail.smime.SMIMESigned;
import org.bouncycastle.mail.smime.validator.SignedMailValidatorException;
import org.bouncycastle.pkix.jcajce.CertPathReviewerException;
import org.bouncycastle.pkix.jcajce.PKIXCertPathReviewer;
import org.bouncycastle.pkix.util.ErrorBundle;
import org.bouncycastle.pkix.util.filter.TrustedInput;
import org.bouncycastle.pkix.util.filter.UntrustedInput;
import org.bouncycastle.util.Integers;

public class SignedMailValidator {
    private static final String RESOURCE_NAME = "org.bouncycastle.mail.smime.validator.SignedMailValidatorMessages";
    private static final Class DEFAULT_CERT_PATH_REVIEWER = PKIXCertPathReviewer.class;
    private static final String EXT_KEY_USAGE = Extension.extendedKeyUsage.getId();
    private static final String SUBJECT_ALTERNATIVE_NAME = Extension.subjectAlternativeName.getId();
    private static final int shortKeyLength = 512;
    private static final long THIRTY_YEARS_IN_MILLI_SEC = 946728000000L;
    private static final JcaX509CertSelectorConverter selectorConverter = new JcaX509CertSelectorConverter();
    private CertStore certs;
    private SignerInformationStore signers;
    private Map results;
    private String[] fromAddresses;
    private Class certPathReviewerClass;

    public SignedMailValidator(MimeMessage message, PKIXParameters param) throws SignedMailValidatorException {
        this(message, param, DEFAULT_CERT_PATH_REVIEWER);
    }

    public SignedMailValidator(MimeMessage message, PKIXParameters param, Class certPathReviewerClass) throws SignedMailValidatorException {
        this.certPathReviewerClass = certPathReviewerClass;
        boolean isSubclass = DEFAULT_CERT_PATH_REVIEWER.isAssignableFrom(certPathReviewerClass);
        if (!isSubclass) {
            throw new IllegalArgumentException("certPathReviewerClass is not a subclass of " + DEFAULT_CERT_PATH_REVIEWER.getName());
        }
        try {
            SMIMESigned s;
            if (message.isMimeType("multipart/signed")) {
                MimeMultipart mimemp = (MimeMultipart)message.getContent();
                s = new SMIMESigned(mimemp);
            } else if (message.isMimeType("application/pkcs7-mime") || message.isMimeType("application/x-pkcs7-mime")) {
                s = new SMIMESigned((Part)message);
            } else {
                ErrorBundle msg = new ErrorBundle(RESOURCE_NAME, "SignedMailValidator.noSignedMessage");
                throw new SignedMailValidatorException(msg);
            }
            this.certs = new JcaCertStoreBuilder().addCertificates(s.getCertificates()).addCRLs(s.getCRLs()).setProvider("BC").build();
            this.signers = s.getSignerInfos();
            Address[] froms = message.getFrom();
            InternetAddress sender = null;
            try {
                if (message.getHeader("Sender") != null) {
                    sender = new InternetAddress(message.getHeader("Sender")[0]);
                }
            }
            catch (MessagingException messagingException) {
                // empty catch block
            }
            int fromsLength = froms != null ? froms.length : 0;
            this.fromAddresses = new String[fromsLength + (sender != null ? 1 : 0)];
            for (int i = 0; i < fromsLength; ++i) {
                InternetAddress inetAddr = (InternetAddress)froms[i];
                this.fromAddresses[i] = inetAddr.getAddress();
            }
            if (sender != null) {
                this.fromAddresses[fromsLength] = sender.getAddress();
            }
            this.results = new HashMap();
        }
        catch (Exception e) {
            if (e instanceof SignedMailValidatorException) {
                throw (SignedMailValidatorException)((Object)e);
            }
            ErrorBundle msg = new ErrorBundle(RESOURCE_NAME, "SignedMailValidator.exceptionReadingMessage", new Object[]{e.getMessage(), e, e.getClass().getName()});
            throw new SignedMailValidatorException(msg, e);
        }
        this.validateSignatures(param);
    }

    protected void validateSignatures(PKIXParameters pkixParam) {
        PKIXParameters usedParameters = (PKIXParameters)pkixParam.clone();
        usedParameters.addCertStore(this.certs);
        Collection c = this.signers.getSigners();
        Iterator it = c.iterator();
        while (it.hasNext()) {
            ErrorBundle msg;
            ArrayList<ErrorBundle> errors = new ArrayList<ErrorBundle>();
            ArrayList<ErrorBundle> notifications = new ArrayList<ErrorBundle>();
            SignerInformation signer = (SignerInformation)it.next();
            X509Certificate cert = null;
            try {
                List certCollection = SignedMailValidator.findCerts(usedParameters.getCertStores(), selectorConverter.getCertSelector(signer.getSID()));
                Iterator certIt = certCollection.iterator();
                if (certIt.hasNext()) {
                    cert = (X509Certificate)certIt.next();
                }
            }
            catch (CertStoreException cse) {
                msg = new ErrorBundle(RESOURCE_NAME, "SignedMailValidator.exceptionRetrievingSignerCert", new Object[]{cse.getMessage(), cse, cse.getClass().getName()});
                errors.add(msg);
            }
            if (cert != null) {
                ErrorBundle msg2;
                Date signTime;
                ErrorBundle msg3;
                Attribute attr;
                boolean validSignature = false;
                try {
                    validSignature = signer.verify(new JcaSimpleSignerInfoVerifierBuilder().setProvider("BC").build(cert.getPublicKey()));
                    if (!validSignature) {
                        msg = new ErrorBundle(RESOURCE_NAME, "SignedMailValidator.signatureNotVerified");
                        errors.add(msg);
                    }
                }
                catch (Exception e) {
                    ErrorBundle msg4 = new ErrorBundle(RESOURCE_NAME, "SignedMailValidator.exceptionVerifyingSignature", new Object[]{e.getMessage(), e, e.getClass().getName()});
                    errors.add(msg4);
                }
                this.checkSignerCert(cert, errors, notifications);
                AttributeTable atab = signer.getSignedAttributes();
                if (atab != null && (attr = atab.get(PKCSObjectIdentifiers.id_aa_receiptRequest)) != null) {
                    msg3 = new ErrorBundle(RESOURCE_NAME, "SignedMailValidator.signedReceiptRequest");
                    notifications.add(msg3);
                }
                if ((signTime = SignedMailValidator.getSignatureTime(signer)) == null) {
                    msg3 = new ErrorBundle(RESOURCE_NAME, "SignedMailValidator.noSigningTime");
                    notifications.add(msg3);
                    signTime = pkixParam.getDate();
                    if (signTime == null) {
                        signTime = new Date();
                    }
                } else {
                    try {
                        cert.checkValidity(signTime);
                    }
                    catch (CertificateExpiredException e) {
                        msg2 = new ErrorBundle(RESOURCE_NAME, "SignedMailValidator.certExpired", new Object[]{new TrustedInput((Object)signTime), new TrustedInput((Object)cert.getNotAfter())});
                        errors.add(msg2);
                    }
                    catch (CertificateNotYetValidException e) {
                        msg2 = new ErrorBundle(RESOURCE_NAME, "SignedMailValidator.certNotYetValid", new Object[]{new TrustedInput((Object)signTime), new TrustedInput((Object)cert.getNotBefore())});
                        errors.add(msg2);
                    }
                }
                usedParameters.setDate(signTime);
                try {
                    PKIXCertPathReviewer review;
                    ArrayList<CertStore> userCertStores = new ArrayList<CertStore>();
                    userCertStores.add(this.certs);
                    Object[] cpres = SignedMailValidator.createCertPath(cert, usedParameters.getTrustAnchors(), pkixParam.getCertStores(), userCertStores);
                    CertPath certPath = (CertPath)cpres[0];
                    List userProvidedList = (List)cpres[1];
                    try {
                        review = (PKIXCertPathReviewer)this.certPathReviewerClass.newInstance();
                    }
                    catch (IllegalAccessException e) {
                        throw new IllegalArgumentException("Cannot instantiate object of type " + this.certPathReviewerClass.getName() + ": " + e.getMessage());
                    }
                    catch (InstantiationException e) {
                        throw new IllegalArgumentException("Cannot instantiate object of type " + this.certPathReviewerClass.getName() + ": " + e.getMessage());
                    }
                    review.init(certPath, usedParameters);
                    if (!review.isValidCertPath()) {
                        ErrorBundle msg5 = new ErrorBundle(RESOURCE_NAME, "SignedMailValidator.certPathInvalid");
                        errors.add(msg5);
                    }
                    this.results.put(signer, new ValidationResult(review, validSignature, errors, notifications, userProvidedList));
                }
                catch (GeneralSecurityException gse) {
                    msg2 = new ErrorBundle(RESOURCE_NAME, "SignedMailValidator.exceptionCreateCertPath", new Object[]{gse.getMessage(), gse, gse.getClass().getName()});
                    errors.add(msg2);
                    this.results.put(signer, new ValidationResult(null, validSignature, errors, notifications, null));
                }
                catch (CertPathReviewerException cpre) {
                    errors.add(cpre.getErrorMessage());
                    this.results.put(signer, new ValidationResult(null, validSignature, errors, notifications, null));
                }
                continue;
            }
            ErrorBundle msg6 = new ErrorBundle(RESOURCE_NAME, "SignedMailValidator.noSignerCert");
            errors.add(msg6);
            this.results.put(signer, new ValidationResult(null, false, errors, notifications, null));
        }
    }

    public static Set getEmailAddresses(X509Certificate cert) throws IOException, CertificateEncodingException {
        int j;
        HashSet<String> addresses = new HashSet<String>();
        TBSCertificate tbsCertificate = SignedMailValidator.getTBSCert(cert);
        RDN[] rdns = tbsCertificate.getSubject().getRDNs(PKCSObjectIdentifiers.pkcs_9_at_emailAddress);
        for (int i = 0; i < rdns.length; ++i) {
            AttributeTypeAndValue[] atVs = rdns[i].getTypesAndValues();
            for (j = 0; j != atVs.length; ++j) {
                if (!atVs[j].getType().equals((ASN1Primitive)PKCSObjectIdentifiers.pkcs_9_at_emailAddress)) continue;
                String email = ((ASN1String)atVs[j].getValue()).getString().toLowerCase();
                addresses.add(email);
            }
        }
        byte[] ext = cert.getExtensionValue(SUBJECT_ALTERNATIVE_NAME);
        if (ext != null) {
            ASN1Sequence altNames = ASN1Sequence.getInstance((Object)SignedMailValidator.getObject(ext));
            for (j = 0; j < altNames.size(); ++j) {
                ASN1TaggedObject o = (ASN1TaggedObject)altNames.getObjectAt(j);
                if (o.getTagNo() != 1) continue;
                String email = ASN1IA5String.getInstance((ASN1TaggedObject)o, (boolean)false).getString().toLowerCase();
                addresses.add(email);
            }
        }
        return addresses;
    }

    private static ASN1Primitive getObject(byte[] ext) throws IOException {
        ASN1InputStream aIn = new ASN1InputStream(ext);
        ASN1OctetString octs = ASN1OctetString.getInstance((Object)aIn.readObject());
        return ASN1Primitive.fromByteArray((byte[])octs.getOctets());
    }

    protected void checkSignerCert(X509Certificate cert, List errors, List notifications) {
        ErrorBundle msg;
        boolean[] keyUsage;
        long validityPeriod;
        PublicKey key = cert.getPublicKey();
        int keyLength = -1;
        if (key instanceof RSAPublicKey) {
            keyLength = ((RSAPublicKey)key).getModulus().bitLength();
        } else if (key instanceof DSAPublicKey) {
            keyLength = ((DSAPublicKey)key).getParams().getP().bitLength();
        }
        if (keyLength != -1 && keyLength <= 512) {
            ErrorBundle msg2 = new ErrorBundle(RESOURCE_NAME, "SignedMailValidator.shortSigningKey", new Object[]{Integers.valueOf((int)keyLength)});
            notifications.add(msg2);
        }
        if ((validityPeriod = cert.getNotAfter().getTime() - cert.getNotBefore().getTime()) > 946728000000L) {
            ErrorBundle msg3 = new ErrorBundle(RESOURCE_NAME, "SignedMailValidator.longValidity", new Object[]{new TrustedInput((Object)cert.getNotBefore()), new TrustedInput((Object)cert.getNotAfter())});
            notifications.add(msg3);
        }
        if ((keyUsage = cert.getKeyUsage()) != null && !keyUsage[0] && !keyUsage[1]) {
            ErrorBundle msg4 = new ErrorBundle(RESOURCE_NAME, "SignedMailValidator.signingNotPermitted");
            errors.add(msg4);
        }
        try {
            ExtendedKeyUsage extKeyUsage;
            byte[] ext = cert.getExtensionValue(EXT_KEY_USAGE);
            if (ext != null && !(extKeyUsage = ExtendedKeyUsage.getInstance((Object)SignedMailValidator.getObject(ext))).hasKeyPurposeId(KeyPurposeId.anyExtendedKeyUsage) && !extKeyUsage.hasKeyPurposeId(KeyPurposeId.id_kp_emailProtection)) {
                ErrorBundle msg5 = new ErrorBundle(RESOURCE_NAME, "SignedMailValidator.extKeyUsageNotPermitted");
                errors.add(msg5);
            }
        }
        catch (Exception e) {
            msg = new ErrorBundle(RESOURCE_NAME, "SignedMailValidator.extKeyUsageError", new Object[]{e.getMessage(), e, e.getClass().getName()});
            errors.add(msg);
        }
        try {
            Set certEmails = SignedMailValidator.getEmailAddresses(cert);
            if (certEmails.isEmpty()) {
                msg = new ErrorBundle(RESOURCE_NAME, "SignedMailValidator.noEmailInCert");
                errors.add(msg);
            } else {
                boolean equalsFrom = false;
                for (int i = 0; i < this.fromAddresses.length; ++i) {
                    if (!certEmails.contains(this.fromAddresses[i].toLowerCase())) continue;
                    equalsFrom = true;
                    break;
                }
                if (!equalsFrom) {
                    ErrorBundle msg6 = new ErrorBundle(RESOURCE_NAME, "SignedMailValidator.emailFromCertMismatch", new Object[]{new UntrustedInput((Object)SignedMailValidator.addressesToString(this.fromAddresses)), new UntrustedInput((Object)certEmails)});
                    errors.add(msg6);
                }
            }
        }
        catch (Exception e) {
            msg = new ErrorBundle(RESOURCE_NAME, "SignedMailValidator.certGetEmailError", new Object[]{e.getMessage(), e, e.getClass().getName()});
            errors.add(msg);
        }
    }

    static String addressesToString(Object[] a) {
        if (a == null) {
            return "null";
        }
        StringBuffer b = new StringBuffer();
        b.append('[');
        for (int i = 0; i != a.length; ++i) {
            if (i > 0) {
                b.append(", ");
            }
            b.append(String.valueOf(a[i]));
        }
        return b.append(']').toString();
    }

    public static Date getSignatureTime(SignerInformation signer) {
        Attribute attr;
        AttributeTable atab = signer.getSignedAttributes();
        Date result = null;
        if (atab != null && (attr = atab.get(CMSAttributes.signingTime)) != null) {
            Time t = Time.getInstance((Object)attr.getAttrValues().getObjectAt(0).toASN1Primitive());
            result = t.getDate();
        }
        return result;
    }

    private static List findCerts(List certStores, X509CertSelector selector) throws CertStoreException {
        ArrayList<? extends Certificate> result = new ArrayList<Certificate>();
        for (CertStore store : certStores) {
            Collection<? extends Certificate> coll = store.getCertificates(selector);
            result.addAll(coll);
        }
        return result;
    }

    private static X509Certificate findNextCert(List certStores, X509CertSelector selector, Set certSet) throws CertStoreException {
        Iterator certIt = SignedMailValidator.findCerts(certStores, selector).iterator();
        boolean certFound = false;
        X509Certificate nextCert = null;
        while (certIt.hasNext()) {
            nextCert = (X509Certificate)certIt.next();
            if (certSet.contains(nextCert)) continue;
            certFound = true;
            break;
        }
        return certFound ? nextCert : null;
    }

    public static CertPath createCertPath(X509Certificate signerCert, Set trustanchors, List certStores) throws GeneralSecurityException {
        Object[] results = SignedMailValidator.createCertPath(signerCert, trustanchors, certStores, null);
        return (CertPath)results[0];
    }

    public static Object[] createCertPath(X509Certificate signerCert, Set trustanchors, List systemCertStores, List userCertStores) throws GeneralSecurityException {
        LinkedHashSet<X509Certificate> certSet = new LinkedHashSet<X509Certificate>();
        ArrayList<Boolean> userProvidedList = new ArrayList<Boolean>();
        X509Certificate cert = signerCert;
        certSet.add(cert);
        userProvidedList.add(new Boolean(true));
        boolean trustAnchorFound = false;
        X509Certificate taCert = null;
        while (cert != null && !trustAnchorFound) {
            for (TrustAnchor anchor : trustanchors) {
                X509Certificate anchorCert = anchor.getTrustedCert();
                if (anchorCert != null) {
                    if (!anchorCert.getSubjectX500Principal().equals(cert.getIssuerX500Principal())) continue;
                    try {
                        cert.verify(anchorCert.getPublicKey(), "BC");
                        trustAnchorFound = true;
                        taCert = anchorCert;
                        break;
                    }
                    catch (Exception exception) {
                        continue;
                    }
                }
                if (!anchor.getCAName().equals(cert.getIssuerX500Principal().getName())) continue;
                try {
                    cert.verify(anchor.getCAPublicKey(), "BC");
                    trustAnchorFound = true;
                    break;
                }
                catch (Exception exception) {
                }
            }
            if (trustAnchorFound) continue;
            X509CertSelector select = new X509CertSelector();
            try {
                select.setSubject(cert.getIssuerX500Principal().getEncoded());
            }
            catch (IOException e) {
                throw new IllegalStateException(e.toString());
            }
            byte[] authKeyIdentBytes = cert.getExtensionValue(Extension.authorityKeyIdentifier.getId());
            if (authKeyIdentBytes != null) {
                try {
                    AuthorityKeyIdentifier kid = AuthorityKeyIdentifier.getInstance((Object)SignedMailValidator.getObject(authKeyIdentBytes));
                    if (kid.getKeyIdentifier() != null) {
                        select.setSubjectKeyIdentifier(new DEROctetString(kid.getKeyIdentifier()).getEncoded("DER"));
                    }
                }
                catch (IOException kid) {
                    // empty catch block
                }
            }
            boolean userProvided = false;
            cert = SignedMailValidator.findNextCert(systemCertStores, select, certSet);
            if (cert == null && userCertStores != null) {
                userProvided = true;
                cert = SignedMailValidator.findNextCert(userCertStores, select, certSet);
            }
            if (cert == null) continue;
            certSet.add(cert);
            userProvidedList.add(new Boolean(userProvided));
        }
        if (trustAnchorFound) {
            if (taCert != null && taCert.getSubjectX500Principal().equals(taCert.getIssuerX500Principal())) {
                certSet.add(taCert);
                userProvidedList.add(new Boolean(false));
            } else {
                X509CertSelector select = new X509CertSelector();
                try {
                    select.setSubject(cert.getIssuerX500Principal().getEncoded());
                    select.setIssuer(cert.getIssuerX500Principal().getEncoded());
                }
                catch (IOException e) {
                    throw new IllegalStateException(e.toString());
                }
                boolean userProvided = false;
                taCert = SignedMailValidator.findNextCert(systemCertStores, select, certSet);
                if (taCert == null && userCertStores != null) {
                    userProvided = true;
                    taCert = SignedMailValidator.findNextCert(userCertStores, select, certSet);
                }
                if (taCert != null) {
                    try {
                        cert.verify(taCert.getPublicKey(), "BC");
                        certSet.add(taCert);
                        userProvidedList.add(new Boolean(userProvided));
                    }
                    catch (GeneralSecurityException generalSecurityException) {
                        // empty catch block
                    }
                }
            }
        }
        CertPath certPath = CertificateFactory.getInstance("X.509", "BC").generateCertPath(new ArrayList(certSet));
        return new Object[]{certPath, userProvidedList};
    }

    public CertStore getCertsAndCRLs() {
        return this.certs;
    }

    public SignerInformationStore getSignerInformationStore() {
        return this.signers;
    }

    public ValidationResult getValidationResult(SignerInformation signer) throws SignedMailValidatorException {
        if (this.signers.getSigners(signer.getSID()).isEmpty()) {
            ErrorBundle msg = new ErrorBundle(RESOURCE_NAME, "SignedMailValidator.wrongSigner");
            throw new SignedMailValidatorException(msg);
        }
        return (ValidationResult)this.results.get(signer);
    }

    private static TBSCertificate getTBSCert(X509Certificate cert) throws CertificateEncodingException {
        return TBSCertificate.getInstance((Object)cert.getTBSCertificate());
    }

    public static class ValidationResult {
        private PKIXCertPathReviewer review;
        private List errors;
        private List notifications;
        private List userProvidedCerts;
        private boolean signVerified;

        ValidationResult(PKIXCertPathReviewer review, boolean verified, List errors, List notifications, List userProvidedCerts) {
            this.review = review;
            this.errors = errors;
            this.notifications = notifications;
            this.signVerified = verified;
            this.userProvidedCerts = userProvidedCerts;
        }

        public List getErrors() {
            return this.errors;
        }

        public List getNotifications() {
            return this.notifications;
        }

        public PKIXCertPathReviewer getCertPathReview() {
            return this.review;
        }

        public CertPath getCertPath() {
            return this.review != null ? this.review.getCertPath() : null;
        }

        public List getUserProvidedCerts() {
            return this.userProvidedCerts;
        }

        public boolean isVerifiedSignature() {
            return this.signVerified;
        }

        public boolean isValidSignature() {
            if (this.review != null) {
                return this.signVerified && this.review.isValidCertPath() && this.errors.isEmpty();
            }
            return false;
        }
    }
}

