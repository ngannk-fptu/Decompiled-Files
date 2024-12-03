/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.x509;

import java.io.ByteArrayInputStream;
import java.io.IOException;
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
import java.security.cert.X509CRLSelector;
import java.security.cert.X509CertSelector;
import java.security.cert.X509Certificate;
import java.security.cert.X509Extension;
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
import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERIA5String;
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
import org.bouncycastle.i18n.ErrorBundle;
import org.bouncycastle.i18n.LocaleString;
import org.bouncycastle.i18n.LocalizedMessage;
import org.bouncycastle.i18n.filter.TrustedInput;
import org.bouncycastle.i18n.filter.UntrustedInput;
import org.bouncycastle.i18n.filter.UntrustedUrlInput;
import org.bouncycastle.jce.provider.AnnotatedException;
import org.bouncycastle.jce.provider.PKIXNameConstraintValidator;
import org.bouncycastle.jce.provider.PKIXNameConstraintValidatorException;
import org.bouncycastle.jce.provider.PKIXPolicyNode;
import org.bouncycastle.util.Integers;
import org.bouncycastle.util.Objects;
import org.bouncycastle.x509.CertPathReviewerException;
import org.bouncycastle.x509.CertPathValidatorUtilities;
import org.bouncycastle.x509.PKIXCRLUtil;
import org.bouncycastle.x509.X509CRLStoreSelector;

public class PKIXCertPathReviewer
extends CertPathValidatorUtilities {
    private static final String QC_STATEMENT = Extension.qCStatements.getId();
    private static final String CRL_DIST_POINTS = Extension.cRLDistributionPoints.getId();
    private static final String AUTH_INFO_ACCESS = Extension.authorityInfoAccess.getId();
    private static final String RESOURCE_NAME = "org.bouncycastle.x509.CertPathReviewerMessages";
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

    public void init(CertPath certPath, PKIXParameters pKIXParameters) throws CertPathReviewerException {
        if (this.initialized) {
            throw new IllegalStateException("object is already initialized!");
        }
        this.initialized = true;
        if (certPath == null) {
            throw new NullPointerException("certPath was null");
        }
        this.certPath = certPath;
        this.certs = certPath.getCertificates();
        this.n = this.certs.size();
        if (this.certs.isEmpty()) {
            throw new CertPathReviewerException(new ErrorBundle(RESOURCE_NAME, "CertPathReviewer.emptyCertPath"));
        }
        this.pkixParams = (PKIXParameters)pKIXParameters.clone();
        this.currentDate = new Date();
        this.validDate = PKIXCertPathReviewer.getValidityDate(this.pkixParams, this.currentDate);
        this.notifications = null;
        this.errors = null;
        this.trustAnchor = null;
        this.subjectPublicKey = null;
        this.policyTree = null;
    }

    public PKIXCertPathReviewer(CertPath certPath, PKIXParameters pKIXParameters) throws CertPathReviewerException {
        this.init(certPath, pKIXParameters);
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

    public List getErrors(int n) {
        this.doChecks();
        return this.errors[n + 1];
    }

    public List[] getNotifications() {
        this.doChecks();
        return this.notifications;
    }

    public List getNotifications(int n) {
        this.doChecks();
        return this.notifications[n + 1];
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
        boolean bl = true;
        for (int i = 0; i < this.errors.length; ++i) {
            if (this.errors[i].isEmpty()) continue;
            bl = false;
            break;
        }
        return bl;
    }

    protected void addNotification(ErrorBundle errorBundle) {
        this.notifications[0].add(errorBundle);
    }

    protected void addNotification(ErrorBundle errorBundle, int n) {
        if (n < -1 || n >= this.n) {
            throw new IndexOutOfBoundsException();
        }
        this.notifications[n + 1].add(errorBundle);
    }

    protected void addError(ErrorBundle errorBundle) {
        this.errors[0].add(errorBundle);
    }

    protected void addError(ErrorBundle errorBundle, int n) {
        if (n < -1 || n >= this.n) {
            throw new IndexOutOfBoundsException();
        }
        this.errors[n + 1].add(errorBundle);
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
        X509Certificate x509Certificate = null;
        PKIXNameConstraintValidator pKIXNameConstraintValidator = new PKIXNameConstraintValidator();
        try {
            for (int i = this.certs.size() - 1; i > 0; --i) {
                int n;
                Object object;
                GeneralSubtree[] generalSubtreeArray;
                Object object2;
                Object object3;
                int n2 = this.n - i;
                x509Certificate = (X509Certificate)this.certs.get(i);
                if (!PKIXCertPathReviewer.isSelfIssued(x509Certificate)) {
                    Object object4;
                    object3 = PKIXCertPathReviewer.getSubjectPrincipal(x509Certificate);
                    object2 = new ASN1InputStream(new ByteArrayInputStream(((X500Principal)object3).getEncoded()));
                    try {
                        generalSubtreeArray = (ASN1Sequence)((ASN1InputStream)object2).readObject();
                    }
                    catch (IOException iOException) {
                        ErrorBundle errorBundle = new ErrorBundle(RESOURCE_NAME, "CertPathReviewer.ncSubjectNameError", new Object[]{new UntrustedInput(object3)});
                        throw new CertPathReviewerException(errorBundle, (Throwable)iOException, this.certPath, i);
                    }
                    try {
                        pKIXNameConstraintValidator.checkPermittedDN((ASN1Sequence)generalSubtreeArray);
                    }
                    catch (PKIXNameConstraintValidatorException pKIXNameConstraintValidatorException) {
                        ErrorBundle errorBundle = new ErrorBundle(RESOURCE_NAME, "CertPathReviewer.notPermittedDN", new Object[]{new UntrustedInput(((X500Principal)object3).getName())});
                        throw new CertPathReviewerException(errorBundle, (Throwable)pKIXNameConstraintValidatorException, this.certPath, i);
                    }
                    try {
                        pKIXNameConstraintValidator.checkExcludedDN((ASN1Sequence)generalSubtreeArray);
                    }
                    catch (PKIXNameConstraintValidatorException pKIXNameConstraintValidatorException) {
                        ErrorBundle errorBundle = new ErrorBundle(RESOURCE_NAME, "CertPathReviewer.excludedDN", new Object[]{new UntrustedInput(((X500Principal)object3).getName())});
                        throw new CertPathReviewerException(errorBundle, (Throwable)pKIXNameConstraintValidatorException, this.certPath, i);
                    }
                    try {
                        object = (ASN1Sequence)PKIXCertPathReviewer.getExtensionValue(x509Certificate, SUBJECT_ALTERNATIVE_NAME);
                    }
                    catch (AnnotatedException annotatedException) {
                        object4 = new ErrorBundle(RESOURCE_NAME, "CertPathReviewer.subjAltNameExtError");
                        throw new CertPathReviewerException((ErrorBundle)object4, (Throwable)annotatedException, this.certPath, i);
                    }
                    if (object != null) {
                        for (n = 0; n < ((ASN1Sequence)object).size(); ++n) {
                            object4 = GeneralName.getInstance(((ASN1Sequence)object).getObjectAt(n));
                            try {
                                pKIXNameConstraintValidator.checkPermitted((GeneralName)object4);
                                pKIXNameConstraintValidator.checkExcluded((GeneralName)object4);
                                continue;
                            }
                            catch (PKIXNameConstraintValidatorException pKIXNameConstraintValidatorException) {
                                ErrorBundle errorBundle = new ErrorBundle(RESOURCE_NAME, "CertPathReviewer.notPermittedEmail", new Object[]{new UntrustedInput(object4)});
                                throw new CertPathReviewerException(errorBundle, (Throwable)pKIXNameConstraintValidatorException, this.certPath, i);
                            }
                        }
                    }
                }
                try {
                    object3 = (ASN1Sequence)PKIXCertPathReviewer.getExtensionValue(x509Certificate, NAME_CONSTRAINTS);
                }
                catch (AnnotatedException annotatedException) {
                    generalSubtreeArray = new ErrorBundle(RESOURCE_NAME, "CertPathReviewer.ncExtError");
                    throw new CertPathReviewerException((ErrorBundle)generalSubtreeArray, (Throwable)annotatedException, this.certPath, i);
                }
                if (object3 == null) continue;
                object2 = NameConstraints.getInstance(object3);
                generalSubtreeArray = ((NameConstraints)object2).getPermittedSubtrees();
                if (generalSubtreeArray != null) {
                    pKIXNameConstraintValidator.intersectPermittedSubtree(generalSubtreeArray);
                }
                if ((object = ((NameConstraints)object2).getExcludedSubtrees()) == null) continue;
                for (n = 0; n != ((GeneralSubtree[])object).length; ++n) {
                    pKIXNameConstraintValidator.addExcludedSubtree(object[n]);
                }
            }
        }
        catch (CertPathReviewerException certPathReviewerException) {
            this.addError(certPathReviewerException.getErrorMessage(), certPathReviewerException.getIndex());
        }
    }

    private void checkPathLength() {
        int n = this.n;
        int n2 = 0;
        X509Certificate x509Certificate = null;
        for (int i = this.certs.size() - 1; i > 0; --i) {
            int n3;
            BigInteger bigInteger;
            Object object;
            int n4 = this.n - i;
            x509Certificate = (X509Certificate)this.certs.get(i);
            if (!PKIXCertPathReviewer.isSelfIssued(x509Certificate)) {
                if (n <= 0) {
                    object = new ErrorBundle(RESOURCE_NAME, "CertPathReviewer.pathLengthExtended");
                    this.addError((ErrorBundle)object);
                }
                --n;
                ++n2;
            }
            try {
                object = BasicConstraints.getInstance(PKIXCertPathReviewer.getExtensionValue(x509Certificate, BASIC_CONSTRAINTS));
            }
            catch (AnnotatedException annotatedException) {
                ErrorBundle errorBundle = new ErrorBundle(RESOURCE_NAME, "CertPathReviewer.processLengthConstError");
                this.addError(errorBundle, i);
                object = null;
            }
            if (object == null || (bigInteger = ((BasicConstraints)object).getPathLenConstraint()) == null || (n3 = bigInteger.intValue()) >= n) continue;
            n = n3;
        }
        ErrorBundle errorBundle = new ErrorBundle(RESOURCE_NAME, "CertPathReviewer.totalPathLength", new Object[]{Integers.valueOf(n2)});
        this.addNotification(errorBundle);
    }

    private void checkSignatures() {
        Object object;
        Object object2;
        Object object3;
        TrustAnchor trustAnchor = null;
        Object object4 = null;
        Object object5 = new ErrorBundle(RESOURCE_NAME, "CertPathReviewer.certPathValidDate", new Object[]{new TrustedInput(this.validDate), new TrustedInput(this.currentDate)});
        this.addNotification((ErrorBundle)object5);
        try {
            object5 = (X509Certificate)this.certs.get(this.certs.size() - 1);
            object3 = this.getTrustAnchors((X509Certificate)object5, this.pkixParams.getTrustAnchors());
            if (object3.size() > 1) {
                object2 = new ErrorBundle(RESOURCE_NAME, "CertPathReviewer.conflictingTrustAnchors", new Object[]{Integers.valueOf(object3.size()), new UntrustedInput(((X509Certificate)object5).getIssuerX500Principal())});
                this.addError((ErrorBundle)object2);
            } else if (object3.isEmpty()) {
                object2 = new ErrorBundle(RESOURCE_NAME, "CertPathReviewer.noTrustAnchorFound", new Object[]{new UntrustedInput(((X509Certificate)object5).getIssuerX500Principal()), Integers.valueOf(this.pkixParams.getTrustAnchors().size())});
                this.addError((ErrorBundle)object2);
            } else {
                trustAnchor = (TrustAnchor)object3.iterator().next();
                object2 = trustAnchor.getTrustedCert() != null ? trustAnchor.getTrustedCert().getPublicKey() : trustAnchor.getCAPublicKey();
                try {
                    CertPathValidatorUtilities.verifyX509Certificate((X509Certificate)object5, (PublicKey)object2, this.pkixParams.getSigProvider());
                }
                catch (SignatureException signatureException) {
                    object = new ErrorBundle(RESOURCE_NAME, "CertPathReviewer.trustButInvalidCert");
                    this.addError((ErrorBundle)object);
                }
                catch (Exception exception) {}
            }
        }
        catch (CertPathReviewerException certPathReviewerException) {
            this.addError(certPathReviewerException.getErrorMessage());
        }
        catch (Throwable throwable) {
            object3 = new ErrorBundle(RESOURCE_NAME, "CertPathReviewer.unknown", new Object[]{new UntrustedInput(throwable.getMessage()), new UntrustedInput(throwable)});
            this.addError((ErrorBundle)object3);
        }
        if (trustAnchor != null) {
            object5 = trustAnchor.getTrustedCert();
            try {
                object4 = object5 != null ? PKIXCertPathReviewer.getSubjectPrincipal((X509Certificate)object5) : new X500Principal(trustAnchor.getCAName());
            }
            catch (IllegalArgumentException illegalArgumentException) {
                object2 = new ErrorBundle(RESOURCE_NAME, "CertPathReviewer.trustDNInvalid", new Object[]{new UntrustedInput(trustAnchor.getCAName())});
                this.addError((ErrorBundle)object2);
            }
            if (object5 != null && (object3 = (Object)((X509Certificate)object5).getKeyUsage()) != null && (((Object)object3).length <= 5 || object3[5] == false)) {
                object2 = new ErrorBundle(RESOURCE_NAME, "CertPathReviewer.trustKeyUsage");
                this.addNotification((ErrorBundle)object2);
            }
        }
        object5 = null;
        object3 = object4;
        object2 = null;
        AlgorithmIdentifier algorithmIdentifier = null;
        object = null;
        ASN1Encodable aSN1Encodable = null;
        if (trustAnchor != null) {
            object2 = trustAnchor.getTrustedCert();
            object5 = object2 != null ? ((Certificate)object2).getPublicKey() : trustAnchor.getCAPublicKey();
            try {
                algorithmIdentifier = PKIXCertPathReviewer.getAlgorithmIdentifier((PublicKey)object5);
                object = algorithmIdentifier.getAlgorithm();
                aSN1Encodable = algorithmIdentifier.getParameters();
            }
            catch (CertPathValidatorException certPathValidatorException) {
                ErrorBundle errorBundle = new ErrorBundle(RESOURCE_NAME, "CertPathReviewer.trustPubKeyError");
                this.addError(errorBundle);
                algorithmIdentifier = null;
            }
        }
        X509Certificate x509Certificate = null;
        for (int i = this.certs.size() - 1; i >= 0; --i) {
            Object object6;
            Iterator iterator;
            Object object7;
            Object object8;
            Object object9;
            Object object10;
            int n = this.n - i;
            x509Certificate = (X509Certificate)this.certs.get(i);
            if (object5 != null) {
                try {
                    CertPathValidatorUtilities.verifyX509Certificate(x509Certificate, (PublicKey)object5, this.pkixParams.getSigProvider());
                }
                catch (GeneralSecurityException generalSecurityException) {
                    object10 = new ErrorBundle(RESOURCE_NAME, "CertPathReviewer.signatureNotVerified", new Object[]{generalSecurityException.getMessage(), generalSecurityException, generalSecurityException.getClass().getName()});
                    this.addError((ErrorBundle)object10, i);
                }
            } else if (PKIXCertPathReviewer.isSelfIssued(x509Certificate)) {
                try {
                    CertPathValidatorUtilities.verifyX509Certificate(x509Certificate, x509Certificate.getPublicKey(), this.pkixParams.getSigProvider());
                    object9 = new ErrorBundle(RESOURCE_NAME, "CertPathReviewer.rootKeyIsValidButNotATrustAnchor");
                    this.addError((ErrorBundle)object9, i);
                }
                catch (GeneralSecurityException generalSecurityException) {
                    object10 = new ErrorBundle(RESOURCE_NAME, "CertPathReviewer.signatureNotVerified", new Object[]{generalSecurityException.getMessage(), generalSecurityException, generalSecurityException.getClass().getName()});
                    this.addError((ErrorBundle)object10, i);
                }
            } else {
                object9 = new ErrorBundle(RESOURCE_NAME, "CertPathReviewer.NoIssuerPublicKey");
                object10 = x509Certificate.getExtensionValue(Extension.authorityKeyIdentifier.getId());
                if (object10 != null && (object8 = ((AuthorityKeyIdentifier)(object7 = AuthorityKeyIdentifier.getInstance(DEROctetString.getInstance(object10).getOctets()))).getAuthorityCertIssuer()) != null) {
                    iterator = ((GeneralNames)object8).getNames()[0];
                    object6 = ((AuthorityKeyIdentifier)object7).getAuthorityCertSerialNumber();
                    if (object6 != null) {
                        Object[] objectArray = new Object[]{new LocaleString(RESOURCE_NAME, "missingIssuer"), " \"", iterator, "\" ", new LocaleString(RESOURCE_NAME, "missingSerial"), " ", object6};
                        ((LocalizedMessage)object9).setExtraArguments(objectArray);
                    }
                }
                this.addError((ErrorBundle)object9, i);
            }
            try {
                x509Certificate.checkValidity(this.validDate);
            }
            catch (CertificateNotYetValidException certificateNotYetValidException) {
                object10 = new ErrorBundle(RESOURCE_NAME, "CertPathReviewer.certificateNotYetValid", new Object[]{new TrustedInput(x509Certificate.getNotBefore())});
                this.addError((ErrorBundle)object10, i);
            }
            catch (CertificateExpiredException certificateExpiredException) {
                object10 = new ErrorBundle(RESOURCE_NAME, "CertPathReviewer.certificateExpired", new Object[]{new TrustedInput(x509Certificate.getNotAfter())});
                this.addError((ErrorBundle)object10, i);
            }
            if (this.pkixParams.isRevocationEnabled()) {
                object9 = null;
                try {
                    object10 = PKIXCertPathReviewer.getExtensionValue(x509Certificate, CRL_DIST_POINTS);
                    if (object10 != null) {
                        object9 = CRLDistPoint.getInstance(object10);
                    }
                }
                catch (AnnotatedException annotatedException) {
                    object7 = new ErrorBundle(RESOURCE_NAME, "CertPathReviewer.crlDistPtExtError");
                    this.addError((ErrorBundle)object7, i);
                }
                object10 = null;
                try {
                    object7 = PKIXCertPathReviewer.getExtensionValue(x509Certificate, AUTH_INFO_ACCESS);
                    if (object7 != null) {
                        object10 = AuthorityInformationAccess.getInstance(object7);
                    }
                }
                catch (AnnotatedException annotatedException) {
                    object8 = new ErrorBundle(RESOURCE_NAME, "CertPathReviewer.crlAuthInfoAccError");
                    this.addError((ErrorBundle)object8, i);
                }
                object7 = this.getCRLDistUrls((CRLDistPoint)object9);
                object8 = this.getOCSPUrls((AuthorityInformationAccess)object10);
                iterator = ((Vector)object7).iterator();
                while (iterator.hasNext()) {
                    object6 = new ErrorBundle(RESOURCE_NAME, "CertPathReviewer.crlDistPoint", new Object[]{new UntrustedUrlInput(iterator.next())});
                    this.addNotification((ErrorBundle)object6, i);
                }
                iterator = ((Vector)object8).iterator();
                while (iterator.hasNext()) {
                    object6 = new ErrorBundle(RESOURCE_NAME, "CertPathReviewer.ocspLocation", new Object[]{new UntrustedUrlInput(iterator.next())});
                    this.addNotification((ErrorBundle)object6, i);
                }
                try {
                    this.checkRevocation(this.pkixParams, x509Certificate, this.validDate, (X509Certificate)object2, (PublicKey)object5, (Vector)object7, (Vector)object8, i);
                }
                catch (CertPathReviewerException certPathReviewerException) {
                    this.addError(certPathReviewerException.getErrorMessage(), i);
                }
            }
            if (object3 != null && !x509Certificate.getIssuerX500Principal().equals(object3)) {
                object9 = new ErrorBundle(RESOURCE_NAME, "CertPathReviewer.certWrongIssuer", new Object[]{((X500Principal)object3).getName(), x509Certificate.getIssuerX500Principal().getName()});
                this.addError((ErrorBundle)object9, i);
            }
            if (n != this.n) {
                if (x509Certificate != null && x509Certificate.getVersion() == 1) {
                    object9 = new ErrorBundle(RESOURCE_NAME, "CertPathReviewer.noCACert");
                    this.addError((ErrorBundle)object9, i);
                }
                try {
                    object9 = BasicConstraints.getInstance(PKIXCertPathReviewer.getExtensionValue(x509Certificate, BASIC_CONSTRAINTS));
                    if (object9 != null) {
                        if (!((BasicConstraints)object9).isCA()) {
                            object10 = new ErrorBundle(RESOURCE_NAME, "CertPathReviewer.noCACert");
                            this.addError((ErrorBundle)object10, i);
                        }
                    } else {
                        object10 = new ErrorBundle(RESOURCE_NAME, "CertPathReviewer.noBasicConstraints");
                        this.addError((ErrorBundle)object10, i);
                    }
                }
                catch (AnnotatedException annotatedException) {
                    object7 = new ErrorBundle(RESOURCE_NAME, "CertPathReviewer.errorProcesingBC");
                    this.addError((ErrorBundle)object7, i);
                }
                object10 = x509Certificate.getKeyUsage();
                if (object10 != null && (((byte[])object10).length <= 5 || object10[5] == 0)) {
                    object7 = new ErrorBundle(RESOURCE_NAME, "CertPathReviewer.noCertSign");
                    this.addError((ErrorBundle)object7, i);
                }
            }
            object2 = x509Certificate;
            object3 = x509Certificate.getSubjectX500Principal();
            try {
                object5 = PKIXCertPathReviewer.getNextWorkingKey(this.certs, i);
                algorithmIdentifier = PKIXCertPathReviewer.getAlgorithmIdentifier((PublicKey)object5);
                object = algorithmIdentifier.getAlgorithm();
                aSN1Encodable = algorithmIdentifier.getParameters();
                continue;
            }
            catch (CertPathValidatorException certPathValidatorException) {
                object10 = new ErrorBundle(RESOURCE_NAME, "CertPathReviewer.pubKeyError");
                this.addError((ErrorBundle)object10, i);
                algorithmIdentifier = null;
                object = null;
                aSN1Encodable = null;
            }
        }
        this.trustAnchor = trustAnchor;
        this.subjectPublicKey = object5;
    }

    /*
     * WARNING - void declaration
     */
    private void checkPolicy() {
        Set<String> set = this.pkixParams.getInitialPolicies();
        List[] listArray = new ArrayList[this.n + 1];
        for (int i = 0; i < listArray.length; ++i) {
            listArray[i] = new ArrayList();
        }
        HashSet<String> hashSet = new HashSet<String>();
        hashSet.add("2.5.29.32.0");
        Object object = new PKIXPolicyNode(new ArrayList(), 0, hashSet, null, new HashSet(), "2.5.29.32.0", false);
        listArray[0].add(object);
        int n = this.pkixParams.isExplicitPolicyRequired() ? 0 : this.n + 1;
        int n2 = this.pkixParams.isAnyPolicyInhibited() ? 0 : this.n + 1;
        int n3 = this.pkixParams.isPolicyMappingInhibited() ? 0 : this.n + 1;
        Object object2 = null;
        X509Certificate x509Certificate = null;
        try {
            Object certPathValidatorException;
            Object object4;
            Object object5;
            Object object6;
            int n4;
            for (n4 = this.certs.size() - 1; n4 >= 0; --n4) {
                int n5 = this.n - n4;
                x509Certificate = (X509Certificate)this.certs.get(n4);
                try {
                    object6 = (ASN1Sequence)PKIXCertPathReviewer.getExtensionValue(x509Certificate, CERTIFICATE_POLICIES);
                }
                catch (AnnotatedException annotatedException) {
                    object5 = new ErrorBundle(RESOURCE_NAME, "CertPathReviewer.policyExtError");
                    throw new CertPathReviewerException((ErrorBundle)object5, (Throwable)annotatedException, this.certPath, n4);
                }
                if (object6 != null && object != null) {
                    boolean n6;
                    Object object7;
                    Object object8;
                    Object object9;
                    object4 = ((ASN1Sequence)object6).getObjects();
                    object5 = new HashSet();
                    while (object4.hasMoreElements()) {
                        object9 = PolicyInformation.getInstance(object4.nextElement());
                        object8 = ((PolicyInformation)object9).getPolicyIdentifier();
                        object5.add(((ASN1ObjectIdentifier)object8).getId());
                        if ("2.5.29.32.0".equals(((ASN1ObjectIdentifier)object8).getId())) continue;
                        try {
                            object7 = PKIXCertPathReviewer.getQualifierSet(((PolicyInformation)object9).getPolicyQualifiers());
                        }
                        catch (CertPathValidatorException certPathValidatorException2) {
                            certPathValidatorException = new ErrorBundle(RESOURCE_NAME, "CertPathReviewer.policyQualifierError");
                            throw new CertPathReviewerException((ErrorBundle)certPathValidatorException, (Throwable)certPathValidatorException2, this.certPath, n4);
                        }
                        n6 = PKIXCertPathReviewer.processCertD1i(n5, listArray, (ASN1ObjectIdentifier)object8, object7);
                        if (n6) continue;
                        PKIXCertPathReviewer.processCertD1ii(n5, listArray, (ASN1ObjectIdentifier)object8, object7);
                    }
                    if (object2 == null || object2.contains("2.5.29.32.0")) {
                        object2 = object5;
                    } else {
                        object9 = object2.iterator();
                        object8 = new HashSet();
                        while (object9.hasNext()) {
                            object7 = object9.next();
                            if (!object5.contains(object7)) continue;
                            object8.add(object7);
                        }
                        object2 = object8;
                    }
                    if (n2 > 0 || n5 < this.n && PKIXCertPathReviewer.isSelfIssued(x509Certificate)) {
                        object4 = ((ASN1Sequence)object6).getObjects();
                        while (object4.hasMoreElements()) {
                            void pKIXPolicyNode;
                            object9 = PolicyInformation.getInstance(object4.nextElement());
                            if (!"2.5.29.32.0".equals(((PolicyInformation)object9).getPolicyIdentifier().getId())) continue;
                            try {
                                object8 = PKIXCertPathReviewer.getQualifierSet(((PolicyInformation)object9).getPolicyQualifiers());
                            }
                            catch (CertPathValidatorException certPathValidatorException3) {
                                ErrorBundle errorBundle = new ErrorBundle(RESOURCE_NAME, "CertPathReviewer.policyQualifierError");
                                throw new CertPathReviewerException(errorBundle, (Throwable)certPathValidatorException3, this.certPath, n4);
                            }
                            object7 = listArray[n5 - 1];
                            n6 = false;
                            while (pKIXPolicyNode < object7.size()) {
                                certPathValidatorException = (PKIXPolicyNode)object7.get((int)pKIXPolicyNode);
                                for (Set set2 : ((PKIXPolicyNode)certPathValidatorException).getExpectedPolicies()) {
                                    Object object3;
                                    String string;
                                    if (set2 instanceof String) {
                                        string = (String)((Object)set2);
                                    } else {
                                        if (!(set2 instanceof ASN1ObjectIdentifier)) continue;
                                        string = ((ASN1ObjectIdentifier)((Object)set2)).getId();
                                    }
                                    boolean bl = false;
                                    Iterator iterator = ((PKIXPolicyNode)certPathValidatorException).getChildren();
                                    while (iterator.hasNext()) {
                                        object3 = (PKIXPolicyNode)iterator.next();
                                        if (!string.equals(((PKIXPolicyNode)object3).getValidPolicy())) continue;
                                        bl = true;
                                    }
                                    if (bl) continue;
                                    object3 = new HashSet();
                                    object3.add(string);
                                    PKIXPolicyNode pKIXPolicyNode2 = new PKIXPolicyNode(new ArrayList(), n5, (Set)object3, (PolicyNode)certPathValidatorException, (Set)object8, string, false);
                                    ((PKIXPolicyNode)certPathValidatorException).addChild(pKIXPolicyNode2);
                                    listArray[n5].add(pKIXPolicyNode2);
                                }
                                ++pKIXPolicyNode;
                            }
                            break block30;
                        }
                    }
                    for (int i = n5 - 1; i >= 0; --i) {
                        object8 = listArray[i];
                        for (int j = 0; j < object8.size() && ((i = (PKIXPolicyNode)object8.get(j)).hasChildren() || (object = PKIXCertPathReviewer.removePolicyNode((PKIXPolicyNode)object, listArray, i)) != null); ++j) {
                        }
                    }
                    Set<String> set3 = x509Certificate.getCriticalExtensionOIDs();
                    if (set3 != null) {
                        void aSN1Sequence;
                        boolean bl = set3.contains(CERTIFICATE_POLICIES);
                        List list = listArray[n5];
                        boolean aSN1ObjectIdentifier2 = false;
                        while (aSN1Sequence < list.size()) {
                            certPathValidatorException = (PKIXPolicyNode)list.get((int)aSN1Sequence);
                            ((PKIXPolicyNode)certPathValidatorException).setCritical(bl);
                            ++aSN1Sequence;
                        }
                    }
                }
                if (object6 == null) {
                    object = null;
                }
                if (n <= 0 && object == null) {
                    object4 = new ErrorBundle(RESOURCE_NAME, "CertPathReviewer.noValidPolicyTree");
                    throw new CertPathReviewerException((ErrorBundle)object4);
                }
                if (n5 == this.n) continue;
                try {
                    object4 = PKIXCertPathReviewer.getExtensionValue(x509Certificate, POLICY_MAPPINGS);
                }
                catch (AnnotatedException annotatedException) {
                    ErrorBundle errorBundle = new ErrorBundle(RESOURCE_NAME, "CertPathReviewer.policyMapExtError");
                    throw new CertPathReviewerException(errorBundle, (Throwable)annotatedException, this.certPath, n4);
                }
                if (object4 != null) {
                    object5 = (ASN1Sequence)object4;
                    for (int i = 0; i < ((ASN1Sequence)object5).size(); ++i) {
                        ASN1Sequence aSN1Sequence = (ASN1Sequence)((ASN1Sequence)object5).getObjectAt(i);
                        ASN1ObjectIdentifier aSN1ObjectIdentifier = (ASN1ObjectIdentifier)aSN1Sequence.getObjectAt(0);
                        ASN1ObjectIdentifier string = (ASN1ObjectIdentifier)aSN1Sequence.getObjectAt(1);
                        if ("2.5.29.32.0".equals(aSN1ObjectIdentifier.getId())) {
                            certPathValidatorException = new ErrorBundle(RESOURCE_NAME, "CertPathReviewer.invalidPolicyMapping");
                            throw new CertPathReviewerException((ErrorBundle)certPathValidatorException, this.certPath, n4);
                        }
                        if (!"2.5.29.32.0".equals(string.getId())) continue;
                        certPathValidatorException = new ErrorBundle(RESOURCE_NAME, "CertPathReviewer.invalidPolicyMapping");
                        throw new CertPathReviewerException((ErrorBundle)certPathValidatorException, this.certPath, n4);
                    }
                }
                if (object4 != null) {
                    Iterator iterator;
                    object5 = (ASN1Sequence)object4;
                    HashMap hashMap = new HashMap();
                    HashSet<Object> hashSet2 = new HashSet<Object>();
                    for (int i = 0; i < ((ASN1Sequence)object5).size(); ++i) {
                        Set set2;
                        ASN1Sequence iterator2 = (ASN1Sequence)((ASN1Sequence)object5).getObjectAt(i);
                        certPathValidatorException = ((ASN1ObjectIdentifier)iterator2.getObjectAt(0)).getId();
                        iterator = ((ASN1ObjectIdentifier)iterator2.getObjectAt(1)).getId();
                        if (!hashMap.containsKey(certPathValidatorException)) {
                            set2 = new HashSet();
                            set2.add(iterator);
                            hashMap.put(certPathValidatorException, set2);
                            hashSet2.add(certPathValidatorException);
                            continue;
                        }
                        set2 = (Set)hashMap.get(certPathValidatorException);
                        set2.add(iterator);
                    }
                    for (String pKIXPolicyNode : hashSet2) {
                        if (n3 > 0) {
                            try {
                                PKIXCertPathReviewer.prepareNextCertB1(n5, listArray, pKIXPolicyNode, hashMap, x509Certificate);
                                continue;
                            }
                            catch (AnnotatedException annotatedException) {
                                iterator = new ErrorBundle(RESOURCE_NAME, "CertPathReviewer.policyExtError");
                                throw new CertPathReviewerException((ErrorBundle)((Object)iterator), (Throwable)annotatedException, this.certPath, n4);
                            }
                            catch (CertPathValidatorException certPathValidatorException4) {
                                iterator = new ErrorBundle(RESOURCE_NAME, "CertPathReviewer.policyQualifierError");
                                throw new CertPathReviewerException((ErrorBundle)((Object)iterator), (Throwable)certPathValidatorException4, this.certPath, n4);
                            }
                        }
                        if (n3 > 0) continue;
                        object = PKIXCertPathReviewer.prepareNextCertB2(n5, listArray, pKIXPolicyNode, (PKIXPolicyNode)object);
                    }
                }
                if (!PKIXCertPathReviewer.isSelfIssued(x509Certificate)) {
                    if (n != 0) {
                        --n;
                    }
                    if (n3 != 0) {
                        --n3;
                    }
                    if (n2 != 0) {
                        --n2;
                    }
                }
                try {
                    object5 = (ASN1Sequence)PKIXCertPathReviewer.getExtensionValue(x509Certificate, POLICY_CONSTRAINTS);
                    if (object5 != null) {
                        Enumeration enumeration = ((ASN1Sequence)object5).getObjects();
                        while (enumeration.hasMoreElements()) {
                            ASN1TaggedObject aSN1TaggedObject = (ASN1TaggedObject)enumeration.nextElement();
                            switch (aSN1TaggedObject.getTagNo()) {
                                case 0: {
                                    int n7 = ASN1Integer.getInstance(aSN1TaggedObject, false).intValueExact();
                                    if (n7 >= n) break;
                                    n = n7;
                                    break;
                                }
                                case 1: {
                                    int n7 = ASN1Integer.getInstance(aSN1TaggedObject, false).intValueExact();
                                    if (n7 >= n3) break;
                                    n3 = n7;
                                }
                            }
                        }
                    }
                }
                catch (AnnotatedException annotatedException) {
                    ErrorBundle errorBundle = new ErrorBundle(RESOURCE_NAME, "CertPathReviewer.policyConstExtError");
                    throw new CertPathReviewerException(errorBundle, this.certPath, n4);
                }
                try {
                    int n8;
                    object5 = (ASN1Integer)PKIXCertPathReviewer.getExtensionValue(x509Certificate, INHIBIT_ANY_POLICY);
                    if (object5 == null || (n8 = ((ASN1Integer)object5).intValueExact()) >= n2) continue;
                    n2 = n8;
                    continue;
                }
                catch (AnnotatedException annotatedException) {
                    ErrorBundle errorBundle = new ErrorBundle(RESOURCE_NAME, "CertPathReviewer.policyInhibitExtError");
                    throw new CertPathReviewerException(errorBundle, this.certPath, n4);
                }
            }
            if (!PKIXCertPathReviewer.isSelfIssued(x509Certificate) && n > 0) {
                --n;
            }
            try {
                object6 = (ASN1Sequence)PKIXCertPathReviewer.getExtensionValue(x509Certificate, POLICY_CONSTRAINTS);
                if (object6 != null) {
                    object4 = ((ASN1Sequence)object6).getObjects();
                    while (object4.hasMoreElements()) {
                        object5 = (ASN1TaggedObject)object4.nextElement();
                        switch (((ASN1TaggedObject)object5).getTagNo()) {
                            case 0: {
                                int n9 = ASN1Integer.getInstance((ASN1TaggedObject)object5, false).intValueExact();
                                if (n9 != 0) break;
                                n = 0;
                            }
                        }
                    }
                }
            }
            catch (AnnotatedException annotatedException) {
                object4 = new ErrorBundle(RESOURCE_NAME, "CertPathReviewer.policyConstExtError");
                throw new CertPathReviewerException((ErrorBundle)object4, this.certPath, n4);
            }
            if (object == null) {
                if (this.pkixParams.isExplicitPolicyRequired()) {
                    object4 = new ErrorBundle(RESOURCE_NAME, "CertPathReviewer.explicitPolicy");
                    throw new CertPathReviewerException((ErrorBundle)object4, this.certPath, n4);
                }
                object6 = null;
            } else if (PKIXCertPathReviewer.isAnyPolicy(set)) {
                if (this.pkixParams.isExplicitPolicyRequired()) {
                    if (object2.isEmpty()) {
                        object4 = new ErrorBundle(RESOURCE_NAME, "CertPathReviewer.explicitPolicy");
                        throw new CertPathReviewerException((ErrorBundle)object4, this.certPath, n4);
                    }
                    object4 = new HashSet();
                    for (int i = 0; i < listArray.length; ++i) {
                        ArrayList arrayList = listArray[i];
                        for (int j = 0; j < arrayList.size(); ++j) {
                            PKIXPolicyNode pKIXPolicyNode = (PKIXPolicyNode)arrayList.get(j);
                            if (!"2.5.29.32.0".equals(pKIXPolicyNode.getValidPolicy())) continue;
                            Iterator iterator = pKIXPolicyNode.getChildren();
                            while (iterator.hasNext()) {
                                object4.add(iterator.next());
                            }
                        }
                    }
                    Iterator iterator = object4.iterator();
                    while (iterator.hasNext()) {
                        PKIXPolicyNode pKIXPolicyNode = (PKIXPolicyNode)iterator.next();
                        String string = pKIXPolicyNode.getValidPolicy();
                        if (object2.contains(string)) continue;
                    }
                    if (object != null) {
                        for (int i = this.n - 1; i >= 0; --i) {
                            List list = listArray[i];
                            for (int j = 0; j < list.size(); ++j) {
                                PKIXPolicyNode pKIXPolicyNode = (PKIXPolicyNode)list.get(j);
                                if (pKIXPolicyNode.hasChildren()) continue;
                                object = PKIXCertPathReviewer.removePolicyNode((PKIXPolicyNode)object, listArray, pKIXPolicyNode);
                            }
                        }
                    }
                }
                object6 = object;
            } else {
                object4 = new HashSet();
                for (int i = 0; i < listArray.length; ++i) {
                    ArrayList arrayList = listArray[i];
                    for (int j = 0; j < arrayList.size(); ++j) {
                        PKIXPolicyNode pKIXPolicyNode = (PKIXPolicyNode)arrayList.get(j);
                        if (!"2.5.29.32.0".equals(pKIXPolicyNode.getValidPolicy())) continue;
                        Iterator iterator = pKIXPolicyNode.getChildren();
                        while (iterator.hasNext()) {
                            certPathValidatorException = (PKIXPolicyNode)iterator.next();
                            if ("2.5.29.32.0".equals(((PKIXPolicyNode)certPathValidatorException).getValidPolicy())) continue;
                            object4.add(certPathValidatorException);
                        }
                    }
                }
                Iterator iterator = object4.iterator();
                while (iterator.hasNext()) {
                    PKIXPolicyNode pKIXPolicyNode = (PKIXPolicyNode)iterator.next();
                    String string = pKIXPolicyNode.getValidPolicy();
                    if (set.contains(string)) continue;
                    object = PKIXCertPathReviewer.removePolicyNode((PKIXPolicyNode)object, listArray, pKIXPolicyNode);
                }
                if (object != null) {
                    for (int i = this.n - 1; i >= 0; --i) {
                        ArrayList arrayList = listArray[i];
                        for (int j = 0; j < arrayList.size(); ++j) {
                            PKIXPolicyNode pKIXPolicyNode = (PKIXPolicyNode)arrayList.get(j);
                            if (pKIXPolicyNode.hasChildren()) continue;
                            object = PKIXCertPathReviewer.removePolicyNode((PKIXPolicyNode)object, listArray, pKIXPolicyNode);
                        }
                    }
                }
                object6 = object;
            }
            if (n <= 0 && object6 == null) {
                object4 = new ErrorBundle(RESOURCE_NAME, "CertPathReviewer.invalidPolicy");
                throw new CertPathReviewerException((ErrorBundle)object4);
            }
            object = object6;
        }
        catch (CertPathReviewerException certPathReviewerException) {
            this.addError(certPathReviewerException.getErrorMessage(), certPathReviewerException.getIndex());
            object = null;
        }
    }

    private void checkCriticalExtensions() {
        List<PKIXCertPathChecker> list = this.pkixParams.getCertPathCheckers();
        Iterator<PKIXCertPathChecker> iterator = list.iterator();
        try {
            try {
                while (iterator.hasNext()) {
                    iterator.next().init(false);
                }
            }
            catch (CertPathValidatorException certPathValidatorException) {
                ErrorBundle errorBundle = new ErrorBundle(RESOURCE_NAME, "CertPathReviewer.certPathCheckerError", new Object[]{certPathValidatorException.getMessage(), certPathValidatorException, certPathValidatorException.getClass().getName()});
                throw new CertPathReviewerException(errorBundle, (Throwable)certPathValidatorException);
            }
            X509Certificate x509Certificate = null;
            for (int i = this.certs.size() - 1; i >= 0; --i) {
                Object object;
                x509Certificate = (X509Certificate)this.certs.get(i);
                Set<String> set = x509Certificate.getCriticalExtensionOIDs();
                if (set == null || set.isEmpty()) continue;
                set.remove(KEY_USAGE);
                set.remove(CERTIFICATE_POLICIES);
                set.remove(POLICY_MAPPINGS);
                set.remove(INHIBIT_ANY_POLICY);
                set.remove(ISSUING_DISTRIBUTION_POINT);
                set.remove(DELTA_CRL_INDICATOR);
                set.remove(POLICY_CONSTRAINTS);
                set.remove(BASIC_CONSTRAINTS);
                set.remove(SUBJECT_ALTERNATIVE_NAME);
                set.remove(NAME_CONSTRAINTS);
                if (set.contains(QC_STATEMENT) && this.processQcStatements(x509Certificate, i)) {
                    set.remove(QC_STATEMENT);
                }
                Iterator<PKIXCertPathChecker> iterator2 = list.iterator();
                while (iterator2.hasNext()) {
                    try {
                        iterator2.next().check(x509Certificate, set);
                    }
                    catch (CertPathValidatorException certPathValidatorException) {
                        object = new ErrorBundle(RESOURCE_NAME, "CertPathReviewer.criticalExtensionError", new Object[]{certPathValidatorException.getMessage(), certPathValidatorException, certPathValidatorException.getClass().getName()});
                        throw new CertPathReviewerException((ErrorBundle)object, certPathValidatorException.getCause(), this.certPath, i);
                    }
                }
                if (set.isEmpty()) continue;
                object = set.iterator();
                while (object.hasNext()) {
                    ErrorBundle errorBundle = new ErrorBundle(RESOURCE_NAME, "CertPathReviewer.unknownCriticalExt", new Object[]{new ASN1ObjectIdentifier(object.next())});
                    this.addError(errorBundle, i);
                }
            }
        }
        catch (CertPathReviewerException certPathReviewerException) {
            this.addError(certPathReviewerException.getErrorMessage(), certPathReviewerException.getIndex());
        }
    }

    private boolean processQcStatements(X509Certificate x509Certificate, int n) {
        try {
            boolean bl = false;
            ASN1Sequence aSN1Sequence = (ASN1Sequence)PKIXCertPathReviewer.getExtensionValue(x509Certificate, QC_STATEMENT);
            for (int i = 0; i < aSN1Sequence.size(); ++i) {
                Object object;
                QCStatement qCStatement = QCStatement.getInstance(aSN1Sequence.getObjectAt(i));
                if (QCStatement.id_etsi_qcs_QcCompliance.equals(qCStatement.getStatementId())) {
                    object = new ErrorBundle(RESOURCE_NAME, "CertPathReviewer.QcEuCompliance");
                    this.addNotification((ErrorBundle)object, n);
                    continue;
                }
                if (QCStatement.id_qcs_pkixQCSyntax_v1.equals(qCStatement.getStatementId())) continue;
                if (QCStatement.id_etsi_qcs_QcSSCD.equals(qCStatement.getStatementId())) {
                    object = new ErrorBundle(RESOURCE_NAME, "CertPathReviewer.QcSSCD");
                    this.addNotification((ErrorBundle)object, n);
                    continue;
                }
                if (QCStatement.id_etsi_qcs_LimiteValue.equals(qCStatement.getStatementId())) {
                    object = MonetaryValue.getInstance(qCStatement.getStatementInfo());
                    Iso4217CurrencyCode iso4217CurrencyCode = ((MonetaryValue)object).getCurrency();
                    double d = ((MonetaryValue)object).getAmount().doubleValue() * Math.pow(10.0, ((MonetaryValue)object).getExponent().doubleValue());
                    ErrorBundle errorBundle = ((MonetaryValue)object).getCurrency().isAlphabetic() ? new ErrorBundle(RESOURCE_NAME, "CertPathReviewer.QcLimitValueAlpha", new Object[]{((MonetaryValue)object).getCurrency().getAlphabetic(), new TrustedInput(new Double(d)), object}) : new ErrorBundle(RESOURCE_NAME, "CertPathReviewer.QcLimitValueNum", new Object[]{Integers.valueOf(((MonetaryValue)object).getCurrency().getNumeric()), new TrustedInput(new Double(d)), object});
                    this.addNotification(errorBundle, n);
                    continue;
                }
                object = new ErrorBundle(RESOURCE_NAME, "CertPathReviewer.QcUnknownStatement", new Object[]{qCStatement.getStatementId(), new UntrustedInput(qCStatement)});
                this.addNotification((ErrorBundle)object, n);
                bl = true;
            }
            return !bl;
        }
        catch (AnnotatedException annotatedException) {
            ErrorBundle errorBundle = new ErrorBundle(RESOURCE_NAME, "CertPathReviewer.QcStatementExtError");
            this.addError(errorBundle, n);
            return false;
        }
    }

    private String IPtoString(byte[] byArray) {
        String string;
        try {
            string = InetAddress.getByAddress(byArray).getHostAddress();
        }
        catch (Exception exception) {
            StringBuffer stringBuffer = new StringBuffer();
            for (int i = 0; i != byArray.length; ++i) {
                stringBuffer.append(Integer.toHexString(byArray[i] & 0xFF));
                stringBuffer.append(' ');
            }
            string = stringBuffer.toString();
        }
        return string;
    }

    protected void checkRevocation(PKIXParameters pKIXParameters, X509Certificate x509Certificate, Date date, X509Certificate x509Certificate2, PublicKey publicKey, Vector vector, Vector vector2, int n) throws CertPathReviewerException {
        this.checkCRLs(pKIXParameters, x509Certificate, date, x509Certificate2, publicKey, vector, n);
    }

    protected void checkCRLs(PKIXParameters pKIXParameters, X509Certificate x509Certificate, Date date, X509Certificate x509Certificate2, PublicKey publicKey, Vector vector, int n) throws CertPathReviewerException {
        Object object;
        Object object2;
        Iterator iterator;
        Object object3;
        Object object4;
        Object object5;
        Object object6;
        Object object7;
        Object object8;
        Iterator iterator2;
        X509CRLStoreSelector x509CRLStoreSelector = new X509CRLStoreSelector();
        try {
            x509CRLStoreSelector.addIssuerName(PKIXCertPathReviewer.getEncodedIssuerPrincipal(x509Certificate).getEncoded());
        }
        catch (IOException iOException) {
            ErrorBundle errorBundle = new ErrorBundle(RESOURCE_NAME, "CertPathReviewer.crlIssuerException");
            throw new CertPathReviewerException(errorBundle, (Throwable)iOException);
        }
        x509CRLStoreSelector.setCertificateChecking(x509Certificate);
        try {
            Set set = PKIXCRLUtil.findCRLs(x509CRLStoreSelector, pKIXParameters);
            iterator2 = set.iterator();
            if (set.isEmpty()) {
                set = PKIXCRLUtil.findCRLs(new X509CRLStoreSelector(), pKIXParameters);
                object8 = set.iterator();
                object7 = new ArrayList();
                while (object8.hasNext()) {
                    object7.add(((X509CRL)object8.next()).getIssuerX500Principal());
                }
                int n2 = object7.size();
                object6 = new ErrorBundle(RESOURCE_NAME, "CertPathReviewer.noCrlInCertstore", new Object[]{new UntrustedInput(x509CRLStoreSelector.getIssuerNames()), new UntrustedInput(object7), Integers.valueOf(n2)});
                this.addNotification((ErrorBundle)object6, n);
            }
        }
        catch (AnnotatedException annotatedException) {
            object8 = new ErrorBundle(RESOURCE_NAME, "CertPathReviewer.crlExtractionError", new Object[]{annotatedException.getCause().getMessage(), annotatedException.getCause(), annotatedException.getCause().getClass().getName()});
            this.addError((ErrorBundle)object8, n);
            iterator2 = new ArrayList().iterator();
        }
        boolean bl = false;
        object8 = null;
        while (iterator2.hasNext()) {
            object8 = (X509CRL)iterator2.next();
            object7 = ((X509CRL)object8).getThisUpdate();
            Date date2 = ((X509CRL)object8).getNextUpdate();
            object6 = new Object[]{new TrustedInput(object7), new TrustedInput(date2)};
            if (date2 == null || date.before(((X509CRL)object8).getNextUpdate())) {
                bl = true;
                object5 = new ErrorBundle(RESOURCE_NAME, "CertPathReviewer.localValidCRL", (Object[])object6);
                this.addNotification((ErrorBundle)object5, n);
                break;
            }
            object5 = new ErrorBundle(RESOURCE_NAME, "CertPathReviewer.localInvalidCRL", (Object[])object6);
            this.addNotification((ErrorBundle)object5, n);
        }
        if (!bl) {
            object7 = x509Certificate.getIssuerX500Principal();
            X509CRL x509CRL = null;
            object6 = vector.iterator();
            while (object6.hasNext()) {
                try {
                    object5 = (String)object6.next();
                    x509CRL = this.getCRL((String)object5);
                    if (x509CRL == null) continue;
                    object4 = x509CRL.getIssuerX500Principal();
                    if (!((X500Principal)object7).equals(object4)) {
                        object3 = new ErrorBundle(RESOURCE_NAME, "CertPathReviewer.onlineCRLWrongCA", new Object[]{new UntrustedInput(((X500Principal)object4).getName()), new UntrustedInput(((X500Principal)object7).getName()), new UntrustedUrlInput(object5)});
                        this.addNotification((ErrorBundle)object3, n);
                        continue;
                    }
                    object3 = x509CRL.getThisUpdate();
                    iterator = x509CRL.getNextUpdate();
                    object2 = new Object[]{new TrustedInput(object3), new TrustedInput(iterator), new UntrustedUrlInput(object5)};
                    if (iterator == null || date.before((Date)((Object)iterator))) {
                        bl = true;
                        object = new ErrorBundle(RESOURCE_NAME, "CertPathReviewer.onlineValidCRL", (Object[])object2);
                        this.addNotification((ErrorBundle)object, n);
                        object8 = x509CRL;
                        break;
                    }
                    object = new ErrorBundle(RESOURCE_NAME, "CertPathReviewer.onlineInvalidCRL", (Object[])object2);
                    this.addNotification((ErrorBundle)object, n);
                }
                catch (CertPathReviewerException certPathReviewerException) {
                    this.addNotification(certPathReviewerException.getErrorMessage(), n);
                }
            }
        }
        if (object8 != null) {
            Object object9;
            boolean[] blArray;
            if (!(x509Certificate2 == null || (blArray = x509Certificate2.getKeyUsage()) == null || blArray.length > 6 && blArray[6])) {
                object6 = new ErrorBundle(RESOURCE_NAME, "CertPathReviewer.noCrlSigningPermited");
                throw new CertPathReviewerException((ErrorBundle)object6);
            }
            if (publicKey != null) {
                try {
                    ((X509CRL)object8).verify(publicKey, "BC");
                }
                catch (Exception exception) {
                    object6 = new ErrorBundle(RESOURCE_NAME, "CertPathReviewer.crlVerifyFailed");
                    throw new CertPathReviewerException((ErrorBundle)object6, (Throwable)exception);
                }
            } else {
                ErrorBundle errorBundle = new ErrorBundle(RESOURCE_NAME, "CertPathReviewer.crlNoIssuerPublicKey");
                throw new CertPathReviewerException(errorBundle);
            }
            object7 = ((X509CRL)object8).getRevokedCertificate(x509Certificate.getSerialNumber());
            if (object7 != null) {
                object9 = null;
                if (((X509CRLEntry)object7).hasExtensions()) {
                    try {
                        object6 = ASN1Enumerated.getInstance(PKIXCertPathReviewer.getExtensionValue((X509Extension)object7, Extension.reasonCode.getId()));
                    }
                    catch (AnnotatedException annotatedException) {
                        object4 = new ErrorBundle(RESOURCE_NAME, "CertPathReviewer.crlReasonExtError");
                        throw new CertPathReviewerException((ErrorBundle)object4, (Throwable)annotatedException);
                    }
                    if (object6 != null) {
                        object9 = crlReasons[object6.intValueExact()];
                    }
                }
                if (object9 == null) {
                    object9 = crlReasons[7];
                }
                object6 = new LocaleString(RESOURCE_NAME, (String)object9);
                if (!date.before(((X509CRLEntry)object7).getRevocationDate())) {
                    object5 = new ErrorBundle(RESOURCE_NAME, "CertPathReviewer.certRevoked", new Object[]{new TrustedInput(((X509CRLEntry)object7).getRevocationDate()), object6});
                    throw new CertPathReviewerException((ErrorBundle)object5);
                }
                object5 = new ErrorBundle(RESOURCE_NAME, "CertPathReviewer.revokedAfterValidation", new Object[]{new TrustedInput(((X509CRLEntry)object7).getRevocationDate()), object6});
                this.addNotification((ErrorBundle)object5, n);
            } else {
                object9 = new ErrorBundle(RESOURCE_NAME, "CertPathReviewer.notRevoked");
                this.addNotification((ErrorBundle)object9, n);
            }
            object9 = ((X509CRL)object8).getNextUpdate();
            if (object9 != null && !date.before((Date)object9)) {
                object6 = new ErrorBundle(RESOURCE_NAME, "CertPathReviewer.crlUpdateAvailable", new Object[]{new TrustedInput(object9)});
                this.addNotification((ErrorBundle)object6, n);
            }
            try {
                object6 = PKIXCertPathReviewer.getExtensionValue((X509Extension)object8, ISSUING_DISTRIBUTION_POINT);
            }
            catch (AnnotatedException annotatedException) {
                object4 = new ErrorBundle(RESOURCE_NAME, "CertPathReviewer.distrPtExtError");
                throw new CertPathReviewerException((ErrorBundle)object4);
            }
            try {
                object5 = PKIXCertPathReviewer.getExtensionValue((X509Extension)object8, DELTA_CRL_INDICATOR);
            }
            catch (AnnotatedException annotatedException) {
                object3 = new ErrorBundle(RESOURCE_NAME, "CertPathReviewer.deltaCrlExtError");
                throw new CertPathReviewerException((ErrorBundle)object3);
            }
            if (object5 != null) {
                object4 = new X509CRLStoreSelector();
                try {
                    ((X509CRLSelector)object4).addIssuerName(PKIXCertPathReviewer.getIssuerPrincipal((X509CRL)object8).getEncoded());
                }
                catch (IOException iOException) {
                    iterator = new ErrorBundle(RESOURCE_NAME, "CertPathReviewer.crlIssuerException");
                    throw new CertPathReviewerException((ErrorBundle)((Object)iterator), (Throwable)iOException);
                }
                ((X509CRLSelector)object4).setMinCRLNumber(((ASN1Integer)object5).getPositiveValue());
                try {
                    ((X509CRLSelector)object4).setMaxCRLNumber(((ASN1Integer)PKIXCertPathReviewer.getExtensionValue((X509Extension)object8, CRL_NUMBER)).getPositiveValue().subtract(BigInteger.valueOf(1L)));
                }
                catch (AnnotatedException annotatedException) {
                    iterator = new ErrorBundle(RESOURCE_NAME, "CertPathReviewer.crlNbrExtError");
                    throw new CertPathReviewerException((ErrorBundle)((Object)iterator), (Throwable)annotatedException);
                }
                boolean bl2 = false;
                try {
                    iterator = PKIXCRLUtil.findCRLs((X509CRLStoreSelector)object4, pKIXParameters).iterator();
                }
                catch (AnnotatedException annotatedException) {
                    object = new ErrorBundle(RESOURCE_NAME, "CertPathReviewer.crlExtractionError");
                    throw new CertPathReviewerException((ErrorBundle)object, (Throwable)annotatedException);
                }
                while (iterator.hasNext()) {
                    object2 = (X509CRL)iterator.next();
                    try {
                        object = PKIXCertPathReviewer.getExtensionValue((X509Extension)object2, ISSUING_DISTRIBUTION_POINT);
                    }
                    catch (AnnotatedException annotatedException) {
                        ErrorBundle errorBundle = new ErrorBundle(RESOURCE_NAME, "CertPathReviewer.distrPtExtError");
                        throw new CertPathReviewerException(errorBundle, (Throwable)annotatedException);
                    }
                    if (!Objects.areEqual(object6, object)) continue;
                    bl2 = true;
                    break;
                }
                if (!bl2) {
                    object2 = new ErrorBundle(RESOURCE_NAME, "CertPathReviewer.noBaseCRL");
                    throw new CertPathReviewerException((ErrorBundle)object2);
                }
            }
            if (object6 != null) {
                object4 = IssuingDistributionPoint.getInstance(object6);
                BasicConstraints basicConstraints = null;
                try {
                    basicConstraints = BasicConstraints.getInstance(PKIXCertPathReviewer.getExtensionValue(x509Certificate, BASIC_CONSTRAINTS));
                }
                catch (AnnotatedException annotatedException) {
                    object2 = new ErrorBundle(RESOURCE_NAME, "CertPathReviewer.crlBCExtError");
                    throw new CertPathReviewerException((ErrorBundle)object2, (Throwable)annotatedException);
                }
                if (((IssuingDistributionPoint)object4).onlyContainsUserCerts() && basicConstraints != null && basicConstraints.isCA()) {
                    iterator = new ErrorBundle(RESOURCE_NAME, "CertPathReviewer.crlOnlyUserCert");
                    throw new CertPathReviewerException((ErrorBundle)((Object)iterator));
                }
                if (((IssuingDistributionPoint)object4).onlyContainsCACerts() && (basicConstraints == null || !basicConstraints.isCA())) {
                    iterator = new ErrorBundle(RESOURCE_NAME, "CertPathReviewer.crlOnlyCaCert");
                    throw new CertPathReviewerException((ErrorBundle)((Object)iterator));
                }
                if (((IssuingDistributionPoint)object4).onlyContainsAttributeCerts()) {
                    iterator = new ErrorBundle(RESOURCE_NAME, "CertPathReviewer.crlOnlyAttrCert");
                    throw new CertPathReviewerException((ErrorBundle)((Object)iterator));
                }
            }
        }
        if (!bl) {
            ErrorBundle errorBundle = new ErrorBundle(RESOURCE_NAME, "CertPathReviewer.noValidCrlFound");
            throw new CertPathReviewerException(errorBundle);
        }
    }

    protected Vector getCRLDistUrls(CRLDistPoint cRLDistPoint) {
        Vector<String> vector = new Vector<String>();
        if (cRLDistPoint != null) {
            DistributionPoint[] distributionPointArray = cRLDistPoint.getDistributionPoints();
            for (int i = 0; i < distributionPointArray.length; ++i) {
                DistributionPointName distributionPointName = distributionPointArray[i].getDistributionPoint();
                if (distributionPointName.getType() != 0) continue;
                GeneralName[] generalNameArray = GeneralNames.getInstance(distributionPointName.getName()).getNames();
                for (int j = 0; j < generalNameArray.length; ++j) {
                    if (generalNameArray[j].getTagNo() != 6) continue;
                    String string = ((DERIA5String)generalNameArray[j].getName()).getString();
                    vector.add(string);
                }
            }
        }
        return vector;
    }

    protected Vector getOCSPUrls(AuthorityInformationAccess authorityInformationAccess) {
        Vector<String> vector = new Vector<String>();
        if (authorityInformationAccess != null) {
            AccessDescription[] accessDescriptionArray = authorityInformationAccess.getAccessDescriptions();
            for (int i = 0; i < accessDescriptionArray.length; ++i) {
                GeneralName generalName;
                if (!accessDescriptionArray[i].getAccessMethod().equals(AccessDescription.id_ad_ocsp) || (generalName = accessDescriptionArray[i].getAccessLocation()).getTagNo() != 6) continue;
                String string = ((DERIA5String)generalName.getName()).getString();
                vector.add(string);
            }
        }
        return vector;
    }

    private X509CRL getCRL(String string) throws CertPathReviewerException {
        X509CRL x509CRL;
        block3: {
            x509CRL = null;
            try {
                URL uRL = new URL(string);
                if (!uRL.getProtocol().equals("http") && !uRL.getProtocol().equals("https")) break block3;
                HttpURLConnection httpURLConnection = (HttpURLConnection)uRL.openConnection();
                httpURLConnection.setUseCaches(false);
                httpURLConnection.setDoInput(true);
                httpURLConnection.connect();
                if (httpURLConnection.getResponseCode() == 200) {
                    CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509", "BC");
                    x509CRL = (X509CRL)certificateFactory.generateCRL(httpURLConnection.getInputStream());
                    break block3;
                }
                throw new Exception(httpURLConnection.getResponseMessage());
            }
            catch (Exception exception) {
                ErrorBundle errorBundle = new ErrorBundle(RESOURCE_NAME, "CertPathReviewer.loadCrlDistPointError", new Object[]{new UntrustedInput(string), exception.getMessage(), exception, exception.getClass().getName()});
                throw new CertPathReviewerException(errorBundle);
            }
        }
        return x509CRL;
    }

    protected Collection getTrustAnchors(X509Certificate x509Certificate, Set set) throws CertPathReviewerException {
        Object object;
        Object object2;
        Object object3;
        ArrayList<Object> arrayList = new ArrayList<Object>();
        Iterator iterator = set.iterator();
        X509CertSelector x509CertSelector = new X509CertSelector();
        try {
            x509CertSelector.setSubject(PKIXCertPathReviewer.getEncodedIssuerPrincipal(x509Certificate).getEncoded());
            object3 = x509Certificate.getExtensionValue(Extension.authorityKeyIdentifier.getId());
            if (object3 != null) {
                object2 = (ASN1OctetString)ASN1Primitive.fromByteArray((byte[])object3);
                object = AuthorityKeyIdentifier.getInstance(ASN1Primitive.fromByteArray(((ASN1OctetString)object2).getOctets()));
                x509CertSelector.setSerialNumber(((AuthorityKeyIdentifier)object).getAuthorityCertSerialNumber());
                byte[] byArray = ((AuthorityKeyIdentifier)object).getKeyIdentifier();
                if (byArray != null) {
                    x509CertSelector.setSubjectKeyIdentifier(new DEROctetString(byArray).getEncoded());
                }
            }
        }
        catch (IOException iOException) {
            ErrorBundle errorBundle = new ErrorBundle(RESOURCE_NAME, "CertPathReviewer.trustAnchorIssuerError");
            throw new CertPathReviewerException(errorBundle);
        }
        while (iterator.hasNext()) {
            object3 = (TrustAnchor)iterator.next();
            if (((TrustAnchor)object3).getTrustedCert() != null) {
                if (!x509CertSelector.match(((TrustAnchor)object3).getTrustedCert())) continue;
                arrayList.add(object3);
                continue;
            }
            if (((TrustAnchor)object3).getCAName() == null || ((TrustAnchor)object3).getCAPublicKey() == null || !((X500Principal)(object2 = PKIXCertPathReviewer.getEncodedIssuerPrincipal(x509Certificate))).equals(object = new X500Principal(((TrustAnchor)object3).getCAName()))) continue;
            arrayList.add(object3);
        }
        return arrayList;
    }
}

