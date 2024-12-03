/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1Encodable
 *  org.bouncycastle.asn1.ASN1Enumerated
 *  org.bouncycastle.asn1.ASN1IA5String
 *  org.bouncycastle.asn1.ASN1InputStream
 *  org.bouncycastle.asn1.ASN1Integer
 *  org.bouncycastle.asn1.ASN1ObjectIdentifier
 *  org.bouncycastle.asn1.ASN1OctetString
 *  org.bouncycastle.asn1.ASN1Primitive
 *  org.bouncycastle.asn1.ASN1Sequence
 *  org.bouncycastle.asn1.ASN1TaggedObject
 *  org.bouncycastle.asn1.DEROctetString
 *  org.bouncycastle.asn1.x509.AccessDescription
 *  org.bouncycastle.asn1.x509.AlgorithmIdentifier
 *  org.bouncycastle.asn1.x509.AuthorityInformationAccess
 *  org.bouncycastle.asn1.x509.AuthorityKeyIdentifier
 *  org.bouncycastle.asn1.x509.BasicConstraints
 *  org.bouncycastle.asn1.x509.CRLDistPoint
 *  org.bouncycastle.asn1.x509.DistributionPoint
 *  org.bouncycastle.asn1.x509.DistributionPointName
 *  org.bouncycastle.asn1.x509.Extension
 *  org.bouncycastle.asn1.x509.GeneralName
 *  org.bouncycastle.asn1.x509.GeneralNames
 *  org.bouncycastle.asn1.x509.GeneralSubtree
 *  org.bouncycastle.asn1.x509.IssuingDistributionPoint
 *  org.bouncycastle.asn1.x509.NameConstraints
 *  org.bouncycastle.asn1.x509.PolicyInformation
 *  org.bouncycastle.asn1.x509.qualified.Iso4217CurrencyCode
 *  org.bouncycastle.asn1.x509.qualified.MonetaryValue
 *  org.bouncycastle.asn1.x509.qualified.QCStatement
 *  org.bouncycastle.util.Integers
 *  org.bouncycastle.util.Objects
 */
package org.bouncycastle.pkix.jcajce;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.security.PublicKey;
import java.security.SignatureException;
import java.security.cert.CertPath;
import java.security.cert.CertPathValidatorException;
import java.security.cert.Certificate;
import java.security.cert.CertificateExpiredException;
import java.security.cert.CertificateFactory;
import java.security.cert.CertificateNotYetValidException;
import java.security.cert.PKIXCertPathChecker;
import java.security.cert.PKIXParameters;
import java.security.cert.PolicyNode;
import java.security.cert.TrustAnchor;
import java.security.cert.X509CRL;
import java.security.cert.X509CRLEntry;
import java.security.cert.X509CertSelector;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Vector;
import javax.security.auth.x500.X500Principal;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Enumerated;
import org.bouncycastle.asn1.ASN1IA5String;
import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.x509.AccessDescription;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.AuthorityInformationAccess;
import org.bouncycastle.asn1.x509.AuthorityKeyIdentifier;
import org.bouncycastle.asn1.x509.BasicConstraints;
import org.bouncycastle.asn1.x509.CRLDistPoint;
import org.bouncycastle.asn1.x509.DistributionPoint;
import org.bouncycastle.asn1.x509.DistributionPointName;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.GeneralName;
import org.bouncycastle.asn1.x509.GeneralNames;
import org.bouncycastle.asn1.x509.GeneralSubtree;
import org.bouncycastle.asn1.x509.IssuingDistributionPoint;
import org.bouncycastle.asn1.x509.NameConstraints;
import org.bouncycastle.asn1.x509.PolicyInformation;
import org.bouncycastle.asn1.x509.qualified.Iso4217CurrencyCode;
import org.bouncycastle.asn1.x509.qualified.MonetaryValue;
import org.bouncycastle.asn1.x509.qualified.QCStatement;
import org.bouncycastle.pkix.PKIXNameConstraintValidator;
import org.bouncycastle.pkix.PKIXNameConstraintValidatorException;
import org.bouncycastle.pkix.jcajce.AnnotatedException;
import org.bouncycastle.pkix.jcajce.CertPathReviewerException;
import org.bouncycastle.pkix.jcajce.CertPathValidatorUtilities;
import org.bouncycastle.pkix.jcajce.PKIXCRLUtil;
import org.bouncycastle.pkix.jcajce.PKIXPolicyNode;
import org.bouncycastle.pkix.jcajce.X509CRLStoreSelector;
import org.bouncycastle.pkix.util.ErrorBundle;
import org.bouncycastle.pkix.util.LocaleString;
import org.bouncycastle.pkix.util.filter.TrustedInput;
import org.bouncycastle.pkix.util.filter.UntrustedInput;
import org.bouncycastle.pkix.util.filter.UntrustedUrlInput;
import org.bouncycastle.util.Integers;
import org.bouncycastle.util.Objects;

public class PKIXCertPathReviewer
extends CertPathValidatorUtilities {
    private static final String QC_STATEMENT = Extension.qCStatements.getId();
    private static final String CRL_DIST_POINTS = Extension.cRLDistributionPoints.getId();
    private static final String AUTH_INFO_ACCESS = Extension.authorityInfoAccess.getId();
    private static final String RESOURCE_NAME = "org.bouncycastle.pkix.CertPathReviewerMessages";
    protected CertPath certPath;
    protected PKIXParameters pkixParams;
    protected Date currentDate;
    protected Date validDate;
    protected List certs;
    protected int n;
    protected List[] notifications;
    protected List[] errors;
    protected TrustAnchor trustAnchor;
    protected PublicKey subjectPublicKey;
    protected PolicyNode policyTree;
    private boolean initialized;

    public void init(CertPath certPath, PKIXParameters params) throws CertPathReviewerException {
        if (this.initialized) {
            throw new IllegalStateException("object is already initialized!");
        }
        this.initialized = true;
        if (certPath == null) {
            throw new NullPointerException("certPath was null");
        }
        List<? extends Certificate> cs = certPath.getCertificates();
        if (cs.size() != 1) {
            HashSet<X509Certificate> tas = new HashSet<X509Certificate>();
            for (TrustAnchor ta : params.getTrustAnchors()) {
                tas.add(ta.getTrustedCert());
            }
            ArrayList<Certificate> certs = new ArrayList<Certificate>();
            for (int i = 0; i != cs.size(); ++i) {
                if (tas.contains(cs.get(i))) continue;
                certs.add(cs.get(i));
            }
            try {
                CertificateFactory cf = CertificateFactory.getInstance("X.509", "BC");
                this.certPath = cf.generateCertPath(certs);
            }
            catch (GeneralSecurityException e) {
                throw new IllegalStateException("unable to rebuild certpath");
            }
            this.certs = certs;
        } else {
            this.certPath = certPath;
            this.certs = certPath.getCertificates();
        }
        this.n = this.certs.size();
        if (this.certs.isEmpty()) {
            throw new CertPathReviewerException(new ErrorBundle(RESOURCE_NAME, "CertPathReviewer.emptyCertPath"));
        }
        this.pkixParams = (PKIXParameters)params.clone();
        this.currentDate = new Date();
        this.validDate = PKIXCertPathReviewer.getValidityDate(this.pkixParams, this.currentDate);
        this.notifications = null;
        this.errors = null;
        this.trustAnchor = null;
        this.subjectPublicKey = null;
        this.policyTree = null;
    }

    public PKIXCertPathReviewer(CertPath certPath, PKIXParameters params) throws CertPathReviewerException {
        this.init(certPath, params);
    }

    public PKIXCertPathReviewer() {
    }

    public CertPath getCertPath() {
        return this.certPath;
    }

    public int getCertPathSize() {
        return this.n;
    }

    public List[] getErrors() {
        this.doChecks();
        return this.errors;
    }

    public List getErrors(int index) {
        this.doChecks();
        return this.errors[index + 1];
    }

    public List[] getNotifications() {
        this.doChecks();
        return this.notifications;
    }

    public List getNotifications(int index) {
        this.doChecks();
        return this.notifications[index + 1];
    }

    public PolicyNode getPolicyTree() {
        this.doChecks();
        return this.policyTree;
    }

    public PublicKey getSubjectPublicKey() {
        this.doChecks();
        return this.subjectPublicKey;
    }

    public TrustAnchor getTrustAnchor() {
        this.doChecks();
        return this.trustAnchor;
    }

    public boolean isValidCertPath() {
        this.doChecks();
        boolean valid = true;
        for (int i = 0; i < this.errors.length; ++i) {
            if (this.errors[i].isEmpty()) continue;
            valid = false;
            break;
        }
        return valid;
    }

    protected void addNotification(ErrorBundle msg) {
        this.notifications[0].add(msg);
    }

    protected void addNotification(ErrorBundle msg, int index) {
        if (index < -1 || index >= this.n) {
            throw new IndexOutOfBoundsException();
        }
        this.notifications[index + 1].add(msg);
    }

    protected void addError(ErrorBundle msg) {
        this.errors[0].add(msg);
    }

    protected void addError(ErrorBundle msg, int index) {
        if (index < -1 || index >= this.n) {
            throw new IndexOutOfBoundsException();
        }
        this.errors[index + 1].add(msg);
    }

    protected void doChecks() {
        if (!this.initialized) {
            throw new IllegalStateException("Object not initialized. Call init() first.");
        }
        if (this.notifications == null) {
            this.notifications = new List[this.n + 1];
            this.errors = new List[this.n + 1];
            for (int i = 0; i < this.notifications.length; ++i) {
                this.notifications[i] = new ArrayList();
                this.errors[i] = new ArrayList();
            }
            this.checkSignatures();
            this.checkNameConstraints();
            this.checkPathLength();
            this.checkPolicy();
            this.checkCriticalExtensions();
        }
    }

    private void checkNameConstraints() {
        X509Certificate cert = null;
        PKIXNameConstraintValidator nameConstraintValidator = new PKIXNameConstraintValidator();
        try {
            for (int index = this.certs.size() - 1; index > 0; --index) {
                GeneralSubtree[] excluded;
                ASN1Sequence ncSeq;
                int i = this.n - index;
                cert = (X509Certificate)this.certs.get(index);
                if (!PKIXCertPathReviewer.isSelfIssued(cert)) {
                    ASN1Sequence altName;
                    ASN1Sequence dns;
                    X500Principal principal = PKIXCertPathReviewer.getSubjectPrincipal(cert);
                    ASN1InputStream aIn = new ASN1InputStream((InputStream)new ByteArrayInputStream(principal.getEncoded()));
                    try {
                        dns = (ASN1Sequence)aIn.readObject();
                    }
                    catch (IOException e) {
                        ErrorBundle msg = new ErrorBundle(RESOURCE_NAME, "CertPathReviewer.ncSubjectNameError", new Object[]{new UntrustedInput(principal)});
                        throw new CertPathReviewerException(msg, (Throwable)e, this.certPath, index);
                    }
                    try {
                        nameConstraintValidator.checkPermittedDN(dns);
                    }
                    catch (PKIXNameConstraintValidatorException cpve) {
                        ErrorBundle msg = new ErrorBundle(RESOURCE_NAME, "CertPathReviewer.notPermittedDN", new Object[]{new UntrustedInput(principal.getName())});
                        throw new CertPathReviewerException(msg, (Throwable)cpve, this.certPath, index);
                    }
                    try {
                        nameConstraintValidator.checkExcludedDN(dns);
                    }
                    catch (PKIXNameConstraintValidatorException cpve) {
                        ErrorBundle msg = new ErrorBundle(RESOURCE_NAME, "CertPathReviewer.excludedDN", new Object[]{new UntrustedInput(principal.getName())});
                        throw new CertPathReviewerException(msg, (Throwable)cpve, this.certPath, index);
                    }
                    try {
                        altName = (ASN1Sequence)PKIXCertPathReviewer.getExtensionValue(cert, SUBJECT_ALTERNATIVE_NAME);
                    }
                    catch (AnnotatedException ae) {
                        ErrorBundle msg = new ErrorBundle(RESOURCE_NAME, "CertPathReviewer.subjAltNameExtError");
                        throw new CertPathReviewerException(msg, (Throwable)ae, this.certPath, index);
                    }
                    if (altName != null) {
                        for (int j = 0; j < altName.size(); ++j) {
                            GeneralName name = GeneralName.getInstance((Object)altName.getObjectAt(j));
                            try {
                                nameConstraintValidator.checkPermitted(name);
                                nameConstraintValidator.checkExcluded(name);
                                continue;
                            }
                            catch (PKIXNameConstraintValidatorException cpve) {
                                ErrorBundle msg = new ErrorBundle(RESOURCE_NAME, "CertPathReviewer.notPermittedEmail", new Object[]{new UntrustedInput(name)});
                                throw new CertPathReviewerException(msg, (Throwable)cpve, this.certPath, index);
                            }
                        }
                    }
                }
                try {
                    ncSeq = (ASN1Sequence)PKIXCertPathReviewer.getExtensionValue(cert, NAME_CONSTRAINTS);
                }
                catch (AnnotatedException ae) {
                    ErrorBundle msg = new ErrorBundle(RESOURCE_NAME, "CertPathReviewer.ncExtError");
                    throw new CertPathReviewerException(msg, (Throwable)ae, this.certPath, index);
                }
                if (ncSeq == null) continue;
                NameConstraints nc = NameConstraints.getInstance((Object)ncSeq);
                GeneralSubtree[] permitted = nc.getPermittedSubtrees();
                if (permitted != null) {
                    nameConstraintValidator.intersectPermittedSubtree(permitted);
                }
                if ((excluded = nc.getExcludedSubtrees()) == null) continue;
                for (int c = 0; c != excluded.length; ++c) {
                    nameConstraintValidator.addExcludedSubtree(excluded[c]);
                }
            }
        }
        catch (CertPathReviewerException cpre) {
            this.addError(cpre.getErrorMessage(), cpre.getIndex());
        }
    }

    private void checkPathLength() {
        int maxPathLength = this.n;
        int totalPathLength = 0;
        X509Certificate cert = null;
        for (int index = this.certs.size() - 1; index > 0; --index) {
            ASN1Integer pathLenConstraint;
            BasicConstraints bc;
            int i = this.n - index;
            cert = (X509Certificate)this.certs.get(index);
            if (!PKIXCertPathReviewer.isSelfIssued(cert)) {
                if (maxPathLength <= 0) {
                    ErrorBundle msg = new ErrorBundle(RESOURCE_NAME, "CertPathReviewer.pathLengthExtended");
                    this.addError(msg);
                }
                --maxPathLength;
                ++totalPathLength;
            }
            try {
                bc = BasicConstraints.getInstance((Object)PKIXCertPathReviewer.getExtensionValue(cert, BASIC_CONSTRAINTS));
            }
            catch (AnnotatedException ae) {
                ErrorBundle msg = new ErrorBundle(RESOURCE_NAME, "CertPathReviewer.processLengthConstError");
                this.addError(msg, index);
                bc = null;
            }
            if (bc == null || !bc.isCA() || (pathLenConstraint = bc.getPathLenConstraintInteger()) == null) continue;
            maxPathLength = Math.min(maxPathLength, pathLenConstraint.intPositiveValueExact());
        }
        ErrorBundle msg = new ErrorBundle(RESOURCE_NAME, "CertPathReviewer.totalPathLength", new Object[]{Integers.valueOf((int)totalPathLength)});
        this.addNotification(msg);
    }

    private void checkSignatures() {
        ErrorBundle msg;
        TrustAnchor trust = null;
        X500Principal trustPrincipal = null;
        ErrorBundle msg2 = new ErrorBundle(RESOURCE_NAME, "CertPathReviewer.certPathValidDate", new Object[]{new TrustedInput(this.validDate), new TrustedInput(this.currentDate)});
        this.addNotification(msg2);
        try {
            X509Certificate cert = (X509Certificate)this.certs.get(this.certs.size() - 1);
            Collection trustColl = this.getTrustAnchors(cert, this.pkixParams.getTrustAnchors());
            if (trustColl.size() > 1) {
                msg = new ErrorBundle(RESOURCE_NAME, "CertPathReviewer.conflictingTrustAnchors", new Object[]{Integers.valueOf((int)trustColl.size()), new UntrustedInput(cert.getIssuerX500Principal())});
                this.addError(msg);
            } else if (trustColl.isEmpty()) {
                msg = new ErrorBundle(RESOURCE_NAME, "CertPathReviewer.noTrustAnchorFound", new Object[]{new UntrustedInput(cert.getIssuerX500Principal()), Integers.valueOf((int)this.pkixParams.getTrustAnchors().size())});
                this.addError(msg);
            } else {
                trust = (TrustAnchor)trustColl.iterator().next();
                PublicKey trustPublicKey = trust.getTrustedCert() != null ? trust.getTrustedCert().getPublicKey() : trust.getCAPublicKey();
                try {
                    CertPathValidatorUtilities.verifyX509Certificate(cert, trustPublicKey, this.pkixParams.getSigProvider());
                }
                catch (SignatureException e) {
                    ErrorBundle msg3 = new ErrorBundle(RESOURCE_NAME, "CertPathReviewer.trustButInvalidCert");
                    this.addError(msg3);
                }
                catch (Exception e) {}
            }
        }
        catch (CertPathReviewerException cpre) {
            this.addError(cpre.getErrorMessage());
        }
        catch (Throwable t) {
            ErrorBundle msg4 = new ErrorBundle(RESOURCE_NAME, "CertPathReviewer.unknown", new Object[]{new UntrustedInput(t.getMessage()), new UntrustedInput(t)});
            this.addError(msg4);
        }
        if (trust != null) {
            boolean[] ku;
            X509Certificate sign = trust.getTrustedCert();
            try {
                trustPrincipal = sign != null ? PKIXCertPathReviewer.getSubjectPrincipal(sign) : new X500Principal(trust.getCAName());
            }
            catch (IllegalArgumentException ex) {
                msg = new ErrorBundle(RESOURCE_NAME, "CertPathReviewer.trustDNInvalid", new Object[]{new UntrustedInput(trust.getCAName())});
                this.addError(msg);
            }
            if (!(sign == null || (ku = sign.getKeyUsage()) == null || ku.length > 5 && ku[5])) {
                msg = new ErrorBundle(RESOURCE_NAME, "CertPathReviewer.trustKeyUsage");
                this.addNotification(msg);
            }
        }
        PublicKey workingPublicKey = null;
        X500Principal workingIssuerName = trustPrincipal;
        X509Certificate sign = null;
        AlgorithmIdentifier workingAlgId = null;
        ASN1ObjectIdentifier workingPublicKeyAlgorithm = null;
        ASN1Encodable workingPublicKeyParameters = null;
        if (trust != null) {
            sign = trust.getTrustedCert();
            workingPublicKey = sign != null ? sign.getPublicKey() : trust.getCAPublicKey();
            try {
                workingAlgId = PKIXCertPathReviewer.getAlgorithmIdentifier(workingPublicKey);
                workingPublicKeyAlgorithm = workingAlgId.getAlgorithm();
                workingPublicKeyParameters = workingAlgId.getParameters();
            }
            catch (CertPathValidatorException ex) {
                ErrorBundle msg5 = new ErrorBundle(RESOURCE_NAME, "CertPathReviewer.trustPubKeyError");
                this.addError(msg5);
                workingAlgId = null;
            }
        }
        X509Certificate cert = null;
        for (int index = this.certs.size() - 1; index >= 0; --index) {
            ErrorBundle msg6;
            ErrorBundle msg7;
            ErrorBundle msg8;
            int i = this.n - index;
            cert = (X509Certificate)this.certs.get(index);
            if (workingPublicKey != null) {
                try {
                    CertPathValidatorUtilities.verifyX509Certificate(cert, workingPublicKey, this.pkixParams.getSigProvider());
                }
                catch (GeneralSecurityException ex) {
                    msg8 = new ErrorBundle(RESOURCE_NAME, "CertPathReviewer.signatureNotVerified", new Object[]{ex.getMessage(), ex, ex.getClass().getName()});
                    this.addError(msg8, index);
                }
            } else if (PKIXCertPathReviewer.isSelfIssued(cert)) {
                try {
                    CertPathValidatorUtilities.verifyX509Certificate(cert, cert.getPublicKey(), this.pkixParams.getSigProvider());
                    msg7 = new ErrorBundle(RESOURCE_NAME, "CertPathReviewer.rootKeyIsValidButNotATrustAnchor");
                    this.addError(msg7, index);
                }
                catch (GeneralSecurityException ex) {
                    msg8 = new ErrorBundle(RESOURCE_NAME, "CertPathReviewer.signatureNotVerified", new Object[]{ex.getMessage(), ex, ex.getClass().getName()});
                    this.addError(msg8, index);
                }
            } else {
                AuthorityKeyIdentifier aki;
                GeneralNames issuerNames;
                msg7 = new ErrorBundle(RESOURCE_NAME, "CertPathReviewer.NoIssuerPublicKey");
                byte[] akiBytes = cert.getExtensionValue(Extension.authorityKeyIdentifier.getId());
                if (akiBytes != null && (issuerNames = (aki = AuthorityKeyIdentifier.getInstance((Object)DEROctetString.getInstance((Object)akiBytes).getOctets())).getAuthorityCertIssuer()) != null) {
                    GeneralName name = issuerNames.getNames()[0];
                    BigInteger serial = aki.getAuthorityCertSerialNumber();
                    if (serial != null) {
                        Object[] extraArgs = new Object[]{new LocaleString(RESOURCE_NAME, "missingIssuer"), " \"", name, "\" ", new LocaleString(RESOURCE_NAME, "missingSerial"), " ", serial};
                        msg7.setExtraArguments(extraArgs);
                    }
                }
                this.addError(msg7, index);
            }
            try {
                cert.checkValidity(this.validDate);
            }
            catch (CertificateNotYetValidException cnve) {
                msg8 = new ErrorBundle(RESOURCE_NAME, "CertPathReviewer.certificateNotYetValid", new Object[]{new TrustedInput(cert.getNotBefore())});
                this.addError(msg8, index);
            }
            catch (CertificateExpiredException cee) {
                msg8 = new ErrorBundle(RESOURCE_NAME, "CertPathReviewer.certificateExpired", new Object[]{new TrustedInput(cert.getNotAfter())});
                this.addError(msg8, index);
            }
            if (this.pkixParams.isRevocationEnabled()) {
                ErrorBundle msg9;
                CRLDistPoint crlDistPoints = null;
                try {
                    ASN1Primitive crl_dp = PKIXCertPathReviewer.getExtensionValue(cert, CRL_DIST_POINTS);
                    if (crl_dp != null) {
                        crlDistPoints = CRLDistPoint.getInstance((Object)crl_dp);
                    }
                }
                catch (AnnotatedException ae) {
                    msg6 = new ErrorBundle(RESOURCE_NAME, "CertPathReviewer.crlDistPtExtError");
                    this.addError(msg6, index);
                }
                AuthorityInformationAccess authInfoAcc = null;
                try {
                    ASN1Primitive auth_info_acc = PKIXCertPathReviewer.getExtensionValue(cert, AUTH_INFO_ACCESS);
                    if (auth_info_acc != null) {
                        authInfoAcc = AuthorityInformationAccess.getInstance((Object)auth_info_acc);
                    }
                }
                catch (AnnotatedException ae) {
                    ErrorBundle msg10 = new ErrorBundle(RESOURCE_NAME, "CertPathReviewer.crlAuthInfoAccError");
                    this.addError(msg10, index);
                }
                Vector crlDistPointUrls = this.getCRLDistUrls(crlDistPoints);
                Vector ocspUrls = this.getOCSPUrls(authInfoAcc);
                Iterator urlIt = crlDistPointUrls.iterator();
                while (urlIt.hasNext()) {
                    msg9 = new ErrorBundle(RESOURCE_NAME, "CertPathReviewer.crlDistPoint", new Object[]{new UntrustedUrlInput(urlIt.next())});
                    this.addNotification(msg9, index);
                }
                urlIt = ocspUrls.iterator();
                while (urlIt.hasNext()) {
                    msg9 = new ErrorBundle(RESOURCE_NAME, "CertPathReviewer.ocspLocation", new Object[]{new UntrustedUrlInput(urlIt.next())});
                    this.addNotification(msg9, index);
                }
                try {
                    this.checkRevocation(this.pkixParams, cert, this.validDate, sign, workingPublicKey, crlDistPointUrls, ocspUrls, index);
                }
                catch (CertPathReviewerException cpre) {
                    this.addError(cpre.getErrorMessage(), index);
                }
            }
            if (workingIssuerName != null && !cert.getIssuerX500Principal().equals(workingIssuerName)) {
                msg7 = new ErrorBundle(RESOURCE_NAME, "CertPathReviewer.certWrongIssuer", new Object[]{workingIssuerName.getName(), cert.getIssuerX500Principal().getName()});
                this.addError(msg7, index);
            }
            if (i != this.n) {
                if (cert != null && cert.getVersion() == 1) {
                    msg7 = new ErrorBundle(RESOURCE_NAME, "CertPathReviewer.noCACert");
                    this.addError(msg7, index);
                }
                try {
                    BasicConstraints bc = BasicConstraints.getInstance((Object)PKIXCertPathReviewer.getExtensionValue(cert, BASIC_CONSTRAINTS));
                    if (bc != null) {
                        if (!bc.isCA()) {
                            msg8 = new ErrorBundle(RESOURCE_NAME, "CertPathReviewer.noCACert");
                            this.addError(msg8, index);
                        }
                    } else {
                        msg8 = new ErrorBundle(RESOURCE_NAME, "CertPathReviewer.noBasicConstraints");
                        this.addError(msg8, index);
                    }
                }
                catch (AnnotatedException ae) {
                    msg6 = new ErrorBundle(RESOURCE_NAME, "CertPathReviewer.errorProcesingBC");
                    this.addError(msg6, index);
                }
                boolean[] keyUsage = cert.getKeyUsage();
                if (!(keyUsage == null || keyUsage.length > 5 && keyUsage[5])) {
                    msg6 = new ErrorBundle(RESOURCE_NAME, "CertPathReviewer.noCertSign");
                    this.addError(msg6, index);
                }
            }
            sign = cert;
            workingIssuerName = cert.getSubjectX500Principal();
            try {
                workingPublicKey = PKIXCertPathReviewer.getNextWorkingKey(this.certs, index);
                workingAlgId = PKIXCertPathReviewer.getAlgorithmIdentifier(workingPublicKey);
                workingPublicKeyAlgorithm = workingAlgId.getAlgorithm();
                workingPublicKeyParameters = workingAlgId.getParameters();
                continue;
            }
            catch (CertPathValidatorException ex) {
                msg8 = new ErrorBundle(RESOURCE_NAME, "CertPathReviewer.pubKeyError");
                this.addError(msg8, index);
                workingAlgId = null;
                workingPublicKeyAlgorithm = null;
                workingPublicKeyParameters = null;
            }
        }
        this.trustAnchor = trust;
        this.subjectPublicKey = workingPublicKey;
    }

    private void checkPolicy() {
        Set<String> userInitialPolicySet = this.pkixParams.getInitialPolicies();
        List[] policyNodes = new ArrayList[this.n + 1];
        for (int j = 0; j < policyNodes.length; ++j) {
            policyNodes[j] = new ArrayList();
        }
        HashSet<String> policySet = new HashSet<String>();
        policySet.add("2.5.29.32.0");
        PKIXPolicyNode validPolicyTree = new PKIXPolicyNode(new ArrayList(), 0, policySet, null, new HashSet(), "2.5.29.32.0", false);
        policyNodes[0].add(validPolicyTree);
        int explicitPolicy = this.pkixParams.isExplicitPolicyRequired() ? 0 : this.n + 1;
        int inhibitAnyPolicy = this.pkixParams.isAnyPolicyInhibited() ? 0 : this.n + 1;
        int policyMapping = this.pkixParams.isPolicyMappingInhibited() ? 0 : this.n + 1;
        HashSet<String> acceptablePolicies = null;
        X509Certificate cert = null;
        try {
            HashSet _validPolicyNodeSet;
            PKIXPolicyNode intersection;
            ErrorBundle msg;
            int index;
            for (index = this.certs.size() - 1; index >= 0; --index) {
                ASN1Sequence mappings;
                ASN1Primitive pm;
                ErrorBundle msg2;
                ASN1Sequence certPolicies;
                int i = this.n - index;
                cert = (X509Certificate)this.certs.get(index);
                try {
                    certPolicies = (ASN1Sequence)PKIXCertPathReviewer.getExtensionValue(cert, CERTIFICATE_POLICIES);
                }
                catch (AnnotatedException ae) {
                    ErrorBundle msg3 = new ErrorBundle(RESOURCE_NAME, "CertPathReviewer.policyExtError");
                    throw new CertPathReviewerException(msg3, (Throwable)ae, this.certPath, index);
                }
                if (certPolicies != null && validPolicyTree != null) {
                    PolicyInformation pInfo;
                    Enumeration e = certPolicies.getObjects();
                    HashSet<String> pols = new HashSet<String>();
                    while (e.hasMoreElements()) {
                        Set pq;
                        pInfo = PolicyInformation.getInstance(e.nextElement());
                        ASN1ObjectIdentifier pOid = pInfo.getPolicyIdentifier();
                        pols.add(pOid.getId());
                        if ("2.5.29.32.0".equals(pOid.getId())) continue;
                        try {
                            pq = PKIXCertPathReviewer.getQualifierSet(pInfo.getPolicyQualifiers());
                        }
                        catch (CertPathValidatorException cpve) {
                            msg2 = new ErrorBundle(RESOURCE_NAME, "CertPathReviewer.policyQualifierError");
                            throw new CertPathReviewerException(msg2, (Throwable)cpve, this.certPath, index);
                        }
                        boolean match = PKIXCertPathReviewer.processCertD1i(i, policyNodes, pOid, pq);
                        if (match) continue;
                        PKIXCertPathReviewer.processCertD1ii(i, policyNodes, pOid, pq);
                    }
                    if (acceptablePolicies == null || acceptablePolicies.contains("2.5.29.32.0")) {
                        acceptablePolicies = pols;
                    } else {
                        Iterator it = acceptablePolicies.iterator();
                        HashSet t1 = new HashSet();
                        while (it.hasNext()) {
                            Object o = it.next();
                            if (!pols.contains(o)) continue;
                            t1.add(o);
                        }
                        acceptablePolicies = t1;
                    }
                    if (inhibitAnyPolicy > 0 || i < this.n && PKIXCertPathReviewer.isSelfIssued(cert)) {
                        e = certPolicies.getObjects();
                        while (e.hasMoreElements()) {
                            Set _apq;
                            pInfo = PolicyInformation.getInstance(e.nextElement());
                            if (!"2.5.29.32.0".equals(pInfo.getPolicyIdentifier().getId())) continue;
                            try {
                                _apq = PKIXCertPathReviewer.getQualifierSet(pInfo.getPolicyQualifiers());
                            }
                            catch (CertPathValidatorException cpve) {
                                ErrorBundle msg4 = new ErrorBundle(RESOURCE_NAME, "CertPathReviewer.policyQualifierError");
                                throw new CertPathReviewerException(msg4, (Throwable)cpve, this.certPath, index);
                            }
                            List _nodes = policyNodes[i - 1];
                            for (int k = 0; k < _nodes.size(); ++k) {
                                PKIXPolicyNode _node = (PKIXPolicyNode)_nodes.get(k);
                                for (Object _tmp : _node.getExpectedPolicies()) {
                                    String _policy;
                                    if (_tmp instanceof String) {
                                        _policy = (String)_tmp;
                                    } else {
                                        if (!(_tmp instanceof ASN1ObjectIdentifier)) continue;
                                        _policy = ((ASN1ObjectIdentifier)_tmp).getId();
                                    }
                                    boolean _found = false;
                                    Iterator _childrenIter = _node.getChildren();
                                    while (_childrenIter.hasNext()) {
                                        PKIXPolicyNode _child = (PKIXPolicyNode)_childrenIter.next();
                                        if (!_policy.equals(_child.getValidPolicy())) continue;
                                        _found = true;
                                    }
                                    if (_found) continue;
                                    HashSet<String> _newChildExpectedPolicies = new HashSet<String>();
                                    _newChildExpectedPolicies.add(_policy);
                                    PKIXPolicyNode _newChild = new PKIXPolicyNode(new ArrayList(), i, _newChildExpectedPolicies, _node, _apq, _policy, false);
                                    _node.addChild(_newChild);
                                    policyNodes[i].add(_newChild);
                                }
                            }
                        }
                    }
                    for (int j = i - 1; j >= 0; --j) {
                        PKIXPolicyNode node;
                        List nodes = policyNodes[j];
                        for (int k = 0; k < nodes.size() && ((node = (PKIXPolicyNode)nodes.get(k)).hasChildren() || (validPolicyTree = PKIXCertPathReviewer.removePolicyNode(validPolicyTree, policyNodes, node)) != null); ++k) {
                        }
                    }
                    Set<String> criticalExtensionOids = cert.getCriticalExtensionOIDs();
                    if (criticalExtensionOids != null) {
                        boolean critical = criticalExtensionOids.contains(CERTIFICATE_POLICIES);
                        List nodes = policyNodes[i];
                        for (int j = 0; j < nodes.size(); ++j) {
                            PKIXPolicyNode node = (PKIXPolicyNode)nodes.get(j);
                            node.setCritical(critical);
                        }
                    }
                }
                if (certPolicies == null) {
                    validPolicyTree = null;
                }
                if (explicitPolicy <= 0 && validPolicyTree == null) {
                    msg = new ErrorBundle(RESOURCE_NAME, "CertPathReviewer.noValidPolicyTree");
                    throw new CertPathReviewerException(msg);
                }
                if (i == this.n) continue;
                try {
                    pm = PKIXCertPathReviewer.getExtensionValue(cert, POLICY_MAPPINGS);
                }
                catch (AnnotatedException ae) {
                    ErrorBundle msg5 = new ErrorBundle(RESOURCE_NAME, "CertPathReviewer.policyMapExtError");
                    throw new CertPathReviewerException(msg5, (Throwable)ae, this.certPath, index);
                }
                if (pm != null) {
                    mappings = (ASN1Sequence)pm;
                    for (int j = 0; j < mappings.size(); ++j) {
                        ASN1Sequence mapping = (ASN1Sequence)mappings.getObjectAt(j);
                        ASN1ObjectIdentifier ip_id = (ASN1ObjectIdentifier)mapping.getObjectAt(0);
                        ASN1ObjectIdentifier sp_id = (ASN1ObjectIdentifier)mapping.getObjectAt(1);
                        if ("2.5.29.32.0".equals(ip_id.getId())) {
                            msg2 = new ErrorBundle(RESOURCE_NAME, "CertPathReviewer.invalidPolicyMapping");
                            throw new CertPathReviewerException(msg2, this.certPath, index);
                        }
                        if (!"2.5.29.32.0".equals(sp_id.getId())) continue;
                        msg2 = new ErrorBundle(RESOURCE_NAME, "CertPathReviewer.invalidPolicyMapping");
                        throw new CertPathReviewerException(msg2, this.certPath, index);
                    }
                }
                if (pm != null) {
                    mappings = (ASN1Sequence)pm;
                    HashMap m_idp = new HashMap();
                    HashSet<String> s_idp = new HashSet<String>();
                    for (int j = 0; j < mappings.size(); ++j) {
                        Set<String> tmp;
                        ASN1Sequence mapping = (ASN1Sequence)mappings.getObjectAt(j);
                        String id_p = ((ASN1ObjectIdentifier)mapping.getObjectAt(0)).getId();
                        String sd_p = ((ASN1ObjectIdentifier)mapping.getObjectAt(1)).getId();
                        if (!m_idp.containsKey(id_p)) {
                            tmp = new HashSet<String>();
                            tmp.add(sd_p);
                            m_idp.put(id_p, tmp);
                            s_idp.add(id_p);
                            continue;
                        }
                        tmp = (Set)m_idp.get(id_p);
                        tmp.add(sd_p);
                    }
                    for (String id_p : s_idp) {
                        if (policyMapping > 0) {
                            ErrorBundle msg6;
                            try {
                                PKIXCertPathReviewer.prepareNextCertB1(i, policyNodes, id_p, m_idp, cert);
                                continue;
                            }
                            catch (AnnotatedException ae) {
                                msg6 = new ErrorBundle(RESOURCE_NAME, "CertPathReviewer.policyExtError");
                                throw new CertPathReviewerException(msg6, (Throwable)ae, this.certPath, index);
                            }
                            catch (CertPathValidatorException cpve) {
                                msg6 = new ErrorBundle(RESOURCE_NAME, "CertPathReviewer.policyQualifierError");
                                throw new CertPathReviewerException(msg6, (Throwable)cpve, this.certPath, index);
                            }
                        }
                        if (policyMapping > 0) continue;
                        validPolicyTree = PKIXCertPathReviewer.prepareNextCertB2(i, policyNodes, id_p, validPolicyTree);
                    }
                }
                if (!PKIXCertPathReviewer.isSelfIssued(cert)) {
                    if (explicitPolicy != 0) {
                        --explicitPolicy;
                    }
                    if (policyMapping != 0) {
                        --policyMapping;
                    }
                    if (inhibitAnyPolicy != 0) {
                        --inhibitAnyPolicy;
                    }
                }
                try {
                    ASN1Sequence pc = (ASN1Sequence)PKIXCertPathReviewer.getExtensionValue(cert, POLICY_CONSTRAINTS);
                    if (pc != null) {
                        Enumeration policyConstraints = pc.getObjects();
                        while (policyConstraints.hasMoreElements()) {
                            ASN1TaggedObject constraint = (ASN1TaggedObject)policyConstraints.nextElement();
                            switch (constraint.getTagNo()) {
                                case 0: {
                                    int tmpInt = ASN1Integer.getInstance((ASN1TaggedObject)constraint, (boolean)false).intValueExact();
                                    if (tmpInt >= explicitPolicy) break;
                                    explicitPolicy = tmpInt;
                                    break;
                                }
                                case 1: {
                                    int tmpInt = ASN1Integer.getInstance((ASN1TaggedObject)constraint, (boolean)false).intValueExact();
                                    if (tmpInt >= policyMapping) break;
                                    policyMapping = tmpInt;
                                }
                            }
                        }
                    }
                }
                catch (AnnotatedException ae) {
                    ErrorBundle msg7 = new ErrorBundle(RESOURCE_NAME, "CertPathReviewer.policyConstExtError");
                    throw new CertPathReviewerException(msg7, this.certPath, index);
                }
                try {
                    int _inhibitAnyPolicy;
                    ASN1Integer iap = (ASN1Integer)PKIXCertPathReviewer.getExtensionValue(cert, INHIBIT_ANY_POLICY);
                    if (iap == null || (_inhibitAnyPolicy = iap.intValueExact()) >= inhibitAnyPolicy) continue;
                    inhibitAnyPolicy = _inhibitAnyPolicy;
                    continue;
                }
                catch (AnnotatedException ae) {
                    ErrorBundle msg8 = new ErrorBundle(RESOURCE_NAME, "CertPathReviewer.policyInhibitExtError");
                    throw new CertPathReviewerException(msg8, this.certPath, index);
                }
            }
            if (!PKIXCertPathReviewer.isSelfIssued(cert) && explicitPolicy > 0) {
                --explicitPolicy;
            }
            try {
                ASN1Sequence pc = (ASN1Sequence)PKIXCertPathReviewer.getExtensionValue(cert, POLICY_CONSTRAINTS);
                if (pc != null) {
                    Enumeration policyConstraints = pc.getObjects();
                    while (policyConstraints.hasMoreElements()) {
                        ASN1TaggedObject constraint = (ASN1TaggedObject)policyConstraints.nextElement();
                        switch (constraint.getTagNo()) {
                            case 0: {
                                int tmpInt = ASN1Integer.getInstance((ASN1TaggedObject)constraint, (boolean)false).intValueExact();
                                if (tmpInt != 0) break;
                                explicitPolicy = 0;
                            }
                        }
                    }
                }
            }
            catch (AnnotatedException e) {
                msg = new ErrorBundle(RESOURCE_NAME, "CertPathReviewer.policyConstExtError");
                throw new CertPathReviewerException(msg, this.certPath, index);
            }
            if (validPolicyTree == null) {
                if (this.pkixParams.isExplicitPolicyRequired()) {
                    msg = new ErrorBundle(RESOURCE_NAME, "CertPathReviewer.explicitPolicy");
                    throw new CertPathReviewerException(msg, this.certPath, index);
                }
                intersection = null;
            } else if (PKIXCertPathReviewer.isAnyPolicy(userInitialPolicySet)) {
                if (this.pkixParams.isExplicitPolicyRequired()) {
                    if (acceptablePolicies.isEmpty()) {
                        msg = new ErrorBundle(RESOURCE_NAME, "CertPathReviewer.explicitPolicy");
                        throw new CertPathReviewerException(msg, this.certPath, index);
                    }
                    _validPolicyNodeSet = new HashSet();
                    for (int j = 0; j < policyNodes.length; ++j) {
                        ArrayList _nodeDepth = policyNodes[j];
                        for (int k = 0; k < _nodeDepth.size(); ++k) {
                            PKIXPolicyNode _node = (PKIXPolicyNode)_nodeDepth.get(k);
                            if (!"2.5.29.32.0".equals(_node.getValidPolicy())) continue;
                            Iterator _iter = _node.getChildren();
                            while (_iter.hasNext()) {
                                _validPolicyNodeSet.add(_iter.next());
                            }
                        }
                    }
                    for (PKIXPolicyNode _node : _validPolicyNodeSet) {
                        String _validPolicy = _node.getValidPolicy();
                        if (acceptablePolicies.contains(_validPolicy)) continue;
                    }
                    if (validPolicyTree != null) {
                        for (int j = this.n - 1; j >= 0; --j) {
                            List nodes = policyNodes[j];
                            for (int k = 0; k < nodes.size(); ++k) {
                                PKIXPolicyNode node = (PKIXPolicyNode)nodes.get(k);
                                if (node.hasChildren()) continue;
                                validPolicyTree = PKIXCertPathReviewer.removePolicyNode(validPolicyTree, policyNodes, node);
                            }
                        }
                    }
                }
                intersection = validPolicyTree;
            } else {
                _validPolicyNodeSet = new HashSet();
                for (int j = 0; j < policyNodes.length; ++j) {
                    ArrayList _nodeDepth = policyNodes[j];
                    for (int k = 0; k < _nodeDepth.size(); ++k) {
                        PKIXPolicyNode _node = (PKIXPolicyNode)_nodeDepth.get(k);
                        if (!"2.5.29.32.0".equals(_node.getValidPolicy())) continue;
                        Iterator _iter = _node.getChildren();
                        while (_iter.hasNext()) {
                            PKIXPolicyNode _c_node = (PKIXPolicyNode)_iter.next();
                            if ("2.5.29.32.0".equals(_c_node.getValidPolicy())) continue;
                            _validPolicyNodeSet.add(_c_node);
                        }
                    }
                }
                for (PKIXPolicyNode _node : _validPolicyNodeSet) {
                    String _validPolicy = _node.getValidPolicy();
                    if (userInitialPolicySet.contains(_validPolicy)) continue;
                    validPolicyTree = PKIXCertPathReviewer.removePolicyNode(validPolicyTree, policyNodes, _node);
                }
                if (validPolicyTree != null) {
                    for (int j = this.n - 1; j >= 0; --j) {
                        ArrayList nodes = policyNodes[j];
                        for (int k = 0; k < nodes.size(); ++k) {
                            PKIXPolicyNode node = (PKIXPolicyNode)nodes.get(k);
                            if (node.hasChildren()) continue;
                            validPolicyTree = PKIXCertPathReviewer.removePolicyNode(validPolicyTree, policyNodes, node);
                        }
                    }
                }
                intersection = validPolicyTree;
            }
            if (explicitPolicy <= 0 && intersection == null) {
                msg = new ErrorBundle(RESOURCE_NAME, "CertPathReviewer.invalidPolicy");
                throw new CertPathReviewerException(msg);
            }
            validPolicyTree = intersection;
        }
        catch (CertPathReviewerException cpre) {
            this.addError(cpre.getErrorMessage(), cpre.getIndex());
            validPolicyTree = null;
        }
    }

    private void checkCriticalExtensions() {
        List<PKIXCertPathChecker> pathCheckers = this.pkixParams.getCertPathCheckers();
        Iterator<PKIXCertPathChecker> certIter = pathCheckers.iterator();
        try {
            try {
                while (certIter.hasNext()) {
                    certIter.next().init(false);
                }
            }
            catch (CertPathValidatorException cpve) {
                ErrorBundle msg = new ErrorBundle(RESOURCE_NAME, "CertPathReviewer.certPathCheckerError", new Object[]{cpve.getMessage(), cpve, cpve.getClass().getName()});
                throw new CertPathReviewerException(msg, (Throwable)cpve);
            }
            X509Certificate cert = null;
            for (int index = this.certs.size() - 1; index >= 0; --index) {
                cert = (X509Certificate)this.certs.get(index);
                Set<String> criticalExtensions = cert.getCriticalExtensionOIDs();
                if (criticalExtensions == null || criticalExtensions.isEmpty()) continue;
                criticalExtensions.remove(KEY_USAGE);
                criticalExtensions.remove(CERTIFICATE_POLICIES);
                criticalExtensions.remove(POLICY_MAPPINGS);
                criticalExtensions.remove(INHIBIT_ANY_POLICY);
                criticalExtensions.remove(ISSUING_DISTRIBUTION_POINT);
                criticalExtensions.remove(DELTA_CRL_INDICATOR);
                criticalExtensions.remove(POLICY_CONSTRAINTS);
                criticalExtensions.remove(BASIC_CONSTRAINTS);
                criticalExtensions.remove(SUBJECT_ALTERNATIVE_NAME);
                criticalExtensions.remove(NAME_CONSTRAINTS);
                if (index == 0) {
                    criticalExtensions.remove(Extension.extendedKeyUsage.getId());
                }
                if (criticalExtensions.contains(QC_STATEMENT) && this.processQcStatements(cert, index)) {
                    criticalExtensions.remove(QC_STATEMENT);
                }
                Iterator<PKIXCertPathChecker> tmpIter = pathCheckers.iterator();
                while (tmpIter.hasNext()) {
                    try {
                        tmpIter.next().check(cert, criticalExtensions);
                    }
                    catch (CertPathValidatorException e) {
                        ErrorBundle msg = new ErrorBundle(RESOURCE_NAME, "CertPathReviewer.criticalExtensionError", new Object[]{e.getMessage(), e, e.getClass().getName()});
                        throw new CertPathReviewerException(msg, e.getCause(), this.certPath, index);
                    }
                }
                if (criticalExtensions.isEmpty()) continue;
                Iterator<String> it = criticalExtensions.iterator();
                while (it.hasNext()) {
                    ErrorBundle msg = new ErrorBundle(RESOURCE_NAME, "CertPathReviewer.unknownCriticalExt", new Object[]{new ASN1ObjectIdentifier(it.next())});
                    this.addError(msg, index);
                }
            }
        }
        catch (CertPathReviewerException cpre) {
            this.addError(cpre.getErrorMessage(), cpre.getIndex());
        }
    }

    private boolean processQcStatements(X509Certificate cert, int index) {
        try {
            boolean unknownStatement = false;
            ASN1Sequence qcSt = (ASN1Sequence)PKIXCertPathReviewer.getExtensionValue(cert, QC_STATEMENT);
            for (int j = 0; j < qcSt.size(); ++j) {
                ErrorBundle msg;
                QCStatement stmt = QCStatement.getInstance((Object)qcSt.getObjectAt(j));
                if (QCStatement.id_etsi_qcs_QcCompliance.equals((ASN1Primitive)stmt.getStatementId())) {
                    msg = new ErrorBundle(RESOURCE_NAME, "CertPathReviewer.QcEuCompliance");
                    this.addNotification(msg, index);
                    continue;
                }
                if (QCStatement.id_qcs_pkixQCSyntax_v1.equals((ASN1Primitive)stmt.getStatementId())) continue;
                if (QCStatement.id_etsi_qcs_QcSSCD.equals((ASN1Primitive)stmt.getStatementId())) {
                    msg = new ErrorBundle(RESOURCE_NAME, "CertPathReviewer.QcSSCD");
                    this.addNotification(msg, index);
                    continue;
                }
                if (QCStatement.id_etsi_qcs_LimiteValue.equals((ASN1Primitive)stmt.getStatementId())) {
                    MonetaryValue limit = MonetaryValue.getInstance((Object)stmt.getStatementInfo());
                    Iso4217CurrencyCode currency = limit.getCurrency();
                    double value = limit.getAmount().doubleValue() * Math.pow(10.0, limit.getExponent().doubleValue());
                    ErrorBundle msg2 = limit.getCurrency().isAlphabetic() ? new ErrorBundle(RESOURCE_NAME, "CertPathReviewer.QcLimitValueAlpha", new Object[]{limit.getCurrency().getAlphabetic(), new TrustedInput(new Double(value)), limit}) : new ErrorBundle(RESOURCE_NAME, "CertPathReviewer.QcLimitValueNum", new Object[]{Integers.valueOf((int)limit.getCurrency().getNumeric()), new TrustedInput(new Double(value)), limit});
                    this.addNotification(msg2, index);
                    continue;
                }
                msg = new ErrorBundle(RESOURCE_NAME, "CertPathReviewer.QcUnknownStatement", new Object[]{stmt.getStatementId(), new UntrustedInput(stmt)});
                this.addNotification(msg, index);
                unknownStatement = true;
            }
            return !unknownStatement;
        }
        catch (AnnotatedException ae) {
            ErrorBundle msg = new ErrorBundle(RESOURCE_NAME, "CertPathReviewer.QcStatementExtError");
            this.addError(msg, index);
            return false;
        }
    }

    private String IPtoString(byte[] ip) {
        String result;
        try {
            result = InetAddress.getByAddress(ip).getHostAddress();
        }
        catch (Exception e) {
            StringBuffer b = new StringBuffer();
            for (int i = 0; i != ip.length; ++i) {
                b.append(Integer.toHexString(ip[i] & 0xFF));
                b.append(' ');
            }
            result = b.toString();
        }
        return result;
    }

    protected void checkRevocation(PKIXParameters paramsPKIX, X509Certificate cert, Date validDate, X509Certificate sign, PublicKey workingPublicKey, Vector crlDistPointUrls, Vector ocspUrls, int index) throws CertPathReviewerException {
        this.checkCRLs(paramsPKIX, cert, validDate, sign, workingPublicKey, crlDistPointUrls, index);
    }

    protected void checkCRLs(PKIXParameters paramsPKIX, X509Certificate cert, Date validDate, X509Certificate sign, PublicKey workingPublicKey, Vector crlDistPointUrls, int index) throws CertPathReviewerException {
        ErrorBundle msg;
        ErrorBundle msg2;
        ErrorBundle msg3;
        ErrorBundle msg4;
        Iterator crl_iter;
        X509CRLStoreSelector crlselect = new X509CRLStoreSelector();
        try {
            crlselect.addIssuerName(PKIXCertPathReviewer.getEncodedIssuerPrincipal(cert).getEncoded());
        }
        catch (IOException e) {
            ErrorBundle msg5 = new ErrorBundle(RESOURCE_NAME, "CertPathReviewer.crlIssuerException");
            throw new CertPathReviewerException(msg5, (Throwable)e);
        }
        crlselect.setCertificateChecking(cert);
        try {
            Set crl_coll = PKIXCRLUtil.findCRLs(crlselect, paramsPKIX);
            crl_iter = crl_coll.iterator();
            if (crl_coll.isEmpty()) {
                crl_coll = PKIXCRLUtil.findCRLs(new X509CRLStoreSelector(), paramsPKIX);
                Iterator it = crl_coll.iterator();
                ArrayList<X500Principal> nonMatchingCrlNames = new ArrayList<X500Principal>();
                while (it.hasNext()) {
                    nonMatchingCrlNames.add(((X509CRL)it.next()).getIssuerX500Principal());
                }
                int numbOfCrls = nonMatchingCrlNames.size();
                msg4 = new ErrorBundle(RESOURCE_NAME, "CertPathReviewer.noCrlInCertstore", new Object[]{new UntrustedInput(crlselect.getIssuerNames()), new UntrustedInput(nonMatchingCrlNames), Integers.valueOf((int)numbOfCrls)});
                this.addNotification(msg4, index);
            }
        }
        catch (AnnotatedException ae) {
            ErrorBundle msg6 = new ErrorBundle(RESOURCE_NAME, "CertPathReviewer.crlExtractionError", new Object[]{ae.getCause().getMessage(), ae.getCause(), ae.getCause().getClass().getName()});
            this.addError(msg6, index);
            crl_iter = new ArrayList().iterator();
        }
        boolean validCrlFound = false;
        X509CRL crl = null;
        while (crl_iter.hasNext()) {
            crl = (X509CRL)crl_iter.next();
            Date thisUpdate = crl.getThisUpdate();
            Date nextUpdate = crl.getNextUpdate();
            Object[] arguments = new Object[]{new TrustedInput(thisUpdate), new TrustedInput(nextUpdate)};
            if (nextUpdate == null || validDate.before(nextUpdate)) {
                validCrlFound = true;
                msg3 = new ErrorBundle(RESOURCE_NAME, "CertPathReviewer.localValidCRL", arguments);
                this.addNotification(msg3, index);
                break;
            }
            msg3 = new ErrorBundle(RESOURCE_NAME, "CertPathReviewer.localInvalidCRL", arguments);
            this.addNotification(msg3, index);
        }
        if (!validCrlFound) {
            X500Principal certIssuer = cert.getIssuerX500Principal();
            X509CRL onlineCRL = null;
            Iterator urlIt = crlDistPointUrls.iterator();
            while (urlIt.hasNext()) {
                try {
                    String location = (String)urlIt.next();
                    onlineCRL = this.getCRL(location);
                    if (onlineCRL == null) continue;
                    X500Principal crlIssuer = onlineCRL.getIssuerX500Principal();
                    if (!certIssuer.equals(crlIssuer)) {
                        msg2 = new ErrorBundle(RESOURCE_NAME, "CertPathReviewer.onlineCRLWrongCA", new Object[]{new UntrustedInput(crlIssuer.getName()), new UntrustedInput(certIssuer.getName()), new UntrustedUrlInput(location)});
                        this.addNotification(msg2, index);
                        continue;
                    }
                    Date thisUpdate = onlineCRL.getThisUpdate();
                    Date nextUpdate = onlineCRL.getNextUpdate();
                    Object[] arguments = new Object[]{new TrustedInput(thisUpdate), new TrustedInput(nextUpdate), new UntrustedUrlInput(location)};
                    if (nextUpdate == null || validDate.before(nextUpdate)) {
                        validCrlFound = true;
                        msg = new ErrorBundle(RESOURCE_NAME, "CertPathReviewer.onlineValidCRL", arguments);
                        this.addNotification(msg, index);
                        crl = onlineCRL;
                        break;
                    }
                    msg = new ErrorBundle(RESOURCE_NAME, "CertPathReviewer.onlineInvalidCRL", arguments);
                    this.addNotification(msg, index);
                }
                catch (CertPathReviewerException cpre) {
                    this.addNotification(cpre.getErrorMessage(), index);
                }
            }
        }
        if (crl != null) {
            ErrorBundle msg7;
            ErrorBundle msg8;
            ASN1Primitive dci;
            ASN1Primitive idp;
            ErrorBundle msg9;
            boolean[] keyUsage;
            if (!(sign == null || (keyUsage = sign.getKeyUsage()) == null || keyUsage.length > 6 && keyUsage[6])) {
                msg4 = new ErrorBundle(RESOURCE_NAME, "CertPathReviewer.noCrlSigningPermited");
                throw new CertPathReviewerException(msg4);
            }
            if (workingPublicKey != null) {
                try {
                    crl.verify(workingPublicKey, "BC");
                }
                catch (Exception e) {
                    msg4 = new ErrorBundle(RESOURCE_NAME, "CertPathReviewer.crlVerifyFailed");
                    throw new CertPathReviewerException(msg4, (Throwable)e);
                }
            } else {
                ErrorBundle msg10 = new ErrorBundle(RESOURCE_NAME, "CertPathReviewer.crlNoIssuerPublicKey");
                throw new CertPathReviewerException(msg10);
            }
            X509CRLEntry crl_entry = crl.getRevokedCertificate(cert.getSerialNumber());
            if (crl_entry != null) {
                String reason = null;
                if (crl_entry.hasExtensions()) {
                    ASN1Enumerated reasonCode;
                    try {
                        reasonCode = ASN1Enumerated.getInstance((Object)PKIXCertPathReviewer.getExtensionValue(crl_entry, Extension.reasonCode.getId()));
                    }
                    catch (AnnotatedException ae) {
                        msg9 = new ErrorBundle(RESOURCE_NAME, "CertPathReviewer.crlReasonExtError");
                        throw new CertPathReviewerException(msg9, (Throwable)ae);
                    }
                    if (reasonCode != null) {
                        reason = crlReasons[reasonCode.intValueExact()];
                    }
                }
                if (reason == null) {
                    reason = crlReasons[7];
                }
                LocaleString ls = new LocaleString(RESOURCE_NAME, reason);
                if (!validDate.before(crl_entry.getRevocationDate())) {
                    msg3 = new ErrorBundle(RESOURCE_NAME, "CertPathReviewer.certRevoked", new Object[]{new TrustedInput(crl_entry.getRevocationDate()), ls});
                    throw new CertPathReviewerException(msg3);
                }
                msg3 = new ErrorBundle(RESOURCE_NAME, "CertPathReviewer.revokedAfterValidation", new Object[]{new TrustedInput(crl_entry.getRevocationDate()), ls});
                this.addNotification(msg3, index);
            } else {
                ErrorBundle msg11 = new ErrorBundle(RESOURCE_NAME, "CertPathReviewer.notRevoked");
                this.addNotification(msg11, index);
            }
            Date nextUpdate = crl.getNextUpdate();
            if (nextUpdate != null && !validDate.before(nextUpdate)) {
                msg4 = new ErrorBundle(RESOURCE_NAME, "CertPathReviewer.crlUpdateAvailable", new Object[]{new TrustedInput(nextUpdate)});
                this.addNotification(msg4, index);
            }
            try {
                idp = PKIXCertPathReviewer.getExtensionValue(crl, ISSUING_DISTRIBUTION_POINT);
            }
            catch (AnnotatedException ae) {
                msg9 = new ErrorBundle(RESOURCE_NAME, "CertPathReviewer.distrPtExtError");
                throw new CertPathReviewerException(msg9);
            }
            try {
                dci = PKIXCertPathReviewer.getExtensionValue(crl, DELTA_CRL_INDICATOR);
            }
            catch (AnnotatedException ae) {
                msg2 = new ErrorBundle(RESOURCE_NAME, "CertPathReviewer.deltaCrlExtError");
                throw new CertPathReviewerException(msg2);
            }
            if (dci != null) {
                Iterator it;
                X509CRLStoreSelector baseSelect = new X509CRLStoreSelector();
                try {
                    baseSelect.addIssuerName(PKIXCertPathReviewer.getIssuerPrincipal(crl).getEncoded());
                }
                catch (IOException e) {
                    msg8 = new ErrorBundle(RESOURCE_NAME, "CertPathReviewer.crlIssuerException");
                    throw new CertPathReviewerException(msg8, (Throwable)e);
                }
                baseSelect.setMinCRLNumber(((ASN1Integer)dci).getPositiveValue());
                try {
                    baseSelect.setMaxCRLNumber(((ASN1Integer)PKIXCertPathReviewer.getExtensionValue(crl, CRL_NUMBER)).getPositiveValue().subtract(BigInteger.valueOf(1L)));
                }
                catch (AnnotatedException ae) {
                    msg8 = new ErrorBundle(RESOURCE_NAME, "CertPathReviewer.crlNbrExtError");
                    throw new CertPathReviewerException(msg8, (Throwable)ae);
                }
                boolean foundBase = false;
                try {
                    it = PKIXCRLUtil.findCRLs(baseSelect, paramsPKIX).iterator();
                }
                catch (AnnotatedException ae) {
                    msg = new ErrorBundle(RESOURCE_NAME, "CertPathReviewer.crlExtractionError");
                    throw new CertPathReviewerException(msg, (Throwable)ae);
                }
                while (it.hasNext()) {
                    ASN1Primitive baseIdp;
                    X509CRL base = (X509CRL)it.next();
                    try {
                        baseIdp = PKIXCertPathReviewer.getExtensionValue(base, ISSUING_DISTRIBUTION_POINT);
                    }
                    catch (AnnotatedException ae) {
                        ErrorBundle msg12 = new ErrorBundle(RESOURCE_NAME, "CertPathReviewer.distrPtExtError");
                        throw new CertPathReviewerException(msg12, (Throwable)ae);
                    }
                    if (!Objects.areEqual((Object)idp, (Object)baseIdp)) continue;
                    foundBase = true;
                    break;
                }
                if (!foundBase) {
                    msg7 = new ErrorBundle(RESOURCE_NAME, "CertPathReviewer.noBaseCRL");
                    throw new CertPathReviewerException(msg7);
                }
            }
            if (idp != null) {
                IssuingDistributionPoint p = IssuingDistributionPoint.getInstance((Object)idp);
                BasicConstraints bc = null;
                try {
                    bc = BasicConstraints.getInstance((Object)PKIXCertPathReviewer.getExtensionValue(cert, BASIC_CONSTRAINTS));
                }
                catch (AnnotatedException ae) {
                    msg7 = new ErrorBundle(RESOURCE_NAME, "CertPathReviewer.crlBCExtError");
                    throw new CertPathReviewerException(msg7, (Throwable)ae);
                }
                if (p.onlyContainsUserCerts() && bc != null && bc.isCA()) {
                    msg8 = new ErrorBundle(RESOURCE_NAME, "CertPathReviewer.crlOnlyUserCert");
                    throw new CertPathReviewerException(msg8);
                }
                if (p.onlyContainsCACerts() && (bc == null || !bc.isCA())) {
                    msg8 = new ErrorBundle(RESOURCE_NAME, "CertPathReviewer.crlOnlyCaCert");
                    throw new CertPathReviewerException(msg8);
                }
                if (p.onlyContainsAttributeCerts()) {
                    msg8 = new ErrorBundle(RESOURCE_NAME, "CertPathReviewer.crlOnlyAttrCert");
                    throw new CertPathReviewerException(msg8);
                }
            }
        }
        if (!validCrlFound) {
            ErrorBundle msg13 = new ErrorBundle(RESOURCE_NAME, "CertPathReviewer.noValidCrlFound");
            throw new CertPathReviewerException(msg13);
        }
    }

    protected Vector getCRLDistUrls(CRLDistPoint crlDistPoints) {
        Vector<String> urls = new Vector<String>();
        if (crlDistPoints != null) {
            DistributionPoint[] distPoints = crlDistPoints.getDistributionPoints();
            for (int i = 0; i < distPoints.length; ++i) {
                DistributionPointName dp_name = distPoints[i].getDistributionPoint();
                if (dp_name.getType() != 0) continue;
                GeneralName[] generalNames = GeneralNames.getInstance((Object)dp_name.getName()).getNames();
                for (int j = 0; j < generalNames.length; ++j) {
                    if (generalNames[j].getTagNo() != 6) continue;
                    String url = ((ASN1IA5String)generalNames[j].getName()).getString();
                    urls.add(url);
                }
            }
        }
        return urls;
    }

    protected Vector getOCSPUrls(AuthorityInformationAccess authInfoAccess) {
        Vector<String> urls = new Vector<String>();
        if (authInfoAccess != null) {
            AccessDescription[] ads = authInfoAccess.getAccessDescriptions();
            for (int i = 0; i < ads.length; ++i) {
                GeneralName name;
                if (!ads[i].getAccessMethod().equals((ASN1Primitive)AccessDescription.id_ad_ocsp) || (name = ads[i].getAccessLocation()).getTagNo() != 6) continue;
                String url = ((ASN1IA5String)name.getName()).getString();
                urls.add(url);
            }
        }
        return urls;
    }

    private X509CRL getCRL(String location) throws CertPathReviewerException {
        X509CRL result;
        block3: {
            result = null;
            try {
                URL url = new URL(location);
                if (!url.getProtocol().equals("http") && !url.getProtocol().equals("https")) break block3;
                HttpURLConnection conn = (HttpURLConnection)url.openConnection();
                conn.setUseCaches(false);
                conn.setDoInput(true);
                conn.connect();
                if (conn.getResponseCode() == 200) {
                    CertificateFactory cf = CertificateFactory.getInstance("X.509", "BC");
                    result = (X509CRL)cf.generateCRL(conn.getInputStream());
                    break block3;
                }
                throw new Exception(conn.getResponseMessage());
            }
            catch (Exception e) {
                ErrorBundle msg = new ErrorBundle(RESOURCE_NAME, "CertPathReviewer.loadCrlDistPointError", new Object[]{new UntrustedInput(location), e.getMessage(), e, e.getClass().getName()});
                throw new CertPathReviewerException(msg);
            }
        }
        return result;
    }

    protected Collection getTrustAnchors(X509Certificate cert, Set trustanchors) throws CertPathReviewerException {
        ArrayList<TrustAnchor> trustColl = new ArrayList<TrustAnchor>();
        Iterator it = trustanchors.iterator();
        X509CertSelector certSelectX509 = new X509CertSelector();
        try {
            certSelectX509.setSubject(PKIXCertPathReviewer.getEncodedIssuerPrincipal(cert).getEncoded());
            byte[] ext = cert.getExtensionValue(Extension.authorityKeyIdentifier.getId());
            if (ext != null) {
                ASN1OctetString oct = (ASN1OctetString)ASN1Primitive.fromByteArray((byte[])ext);
                AuthorityKeyIdentifier authID = AuthorityKeyIdentifier.getInstance((Object)ASN1Primitive.fromByteArray((byte[])oct.getOctets()));
                BigInteger serial = authID.getAuthorityCertSerialNumber();
                if (serial != null) {
                    certSelectX509.setSerialNumber(authID.getAuthorityCertSerialNumber());
                } else {
                    byte[] keyID = authID.getKeyIdentifier();
                    if (keyID != null) {
                        certSelectX509.setSubjectKeyIdentifier(new DEROctetString(keyID).getEncoded());
                    }
                }
            }
        }
        catch (IOException ex) {
            ErrorBundle msg = new ErrorBundle(RESOURCE_NAME, "CertPathReviewer.trustAnchorIssuerError");
            throw new CertPathReviewerException(msg);
        }
        while (it.hasNext()) {
            X500Principal caName;
            X500Principal certIssuer;
            TrustAnchor trust = (TrustAnchor)it.next();
            if (trust.getTrustedCert() != null) {
                if (!certSelectX509.match(trust.getTrustedCert())) continue;
                trustColl.add(trust);
                continue;
            }
            if (trust.getCAName() == null || trust.getCAPublicKey() == null || !(certIssuer = PKIXCertPathReviewer.getEncodedIssuerPrincipal(cert)).equals(caName = new X500Principal(trust.getCAName()))) continue;
            trustColl.add(trust);
        }
        return trustColl;
    }
}

