/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.jce.provider;

import java.security.InvalidAlgorithmParameterException;
import java.security.PublicKey;
import java.security.cert.CertPath;
import java.security.cert.CertPathParameters;
import java.security.cert.CertPathValidatorException;
import java.security.cert.CertPathValidatorResult;
import java.security.cert.CertPathValidatorSpi;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.security.cert.PKIXCertPathChecker;
import java.security.cert.PKIXCertPathValidatorResult;
import java.security.cert.PKIXParameters;
import java.security.cert.PKIXRevocationChecker;
import java.security.cert.TrustAnchor;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.TBSCertificate;
import org.bouncycastle.jcajce.PKIXCertRevocationChecker;
import org.bouncycastle.jcajce.PKIXExtendedBuilderParameters;
import org.bouncycastle.jcajce.PKIXExtendedParameters;
import org.bouncycastle.jcajce.interfaces.BCX509Certificate;
import org.bouncycastle.jcajce.util.BCJcaJceHelper;
import org.bouncycastle.jcajce.util.JcaJceHelper;
import org.bouncycastle.jce.exception.ExtCertPathValidatorException;
import org.bouncycastle.jce.provider.AnnotatedException;
import org.bouncycastle.jce.provider.CertPathValidatorUtilities;
import org.bouncycastle.jce.provider.PKIXNameConstraintValidator;
import org.bouncycastle.jce.provider.PKIXPolicyNode;
import org.bouncycastle.jce.provider.PrincipalUtils;
import org.bouncycastle.jce.provider.ProvRevocationChecker;
import org.bouncycastle.jce.provider.RFC3280CertPathUtilities;
import org.bouncycastle.jce.provider.WrappedRevocationChecker;

public class PKIXCertPathValidatorSpi_8
extends CertPathValidatorSpi {
    private final JcaJceHelper helper = new BCJcaJceHelper();
    private final boolean isForCRLCheck;

    public PKIXCertPathValidatorSpi_8() {
        this(false);
    }

    public PKIXCertPathValidatorSpi_8(boolean isForCRLCheck) {
        this.isForCRLCheck = isForCRLCheck;
    }

    @Override
    public PKIXCertPathChecker engineGetRevocationChecker() {
        return new ProvRevocationChecker(this.helper);
    }

    @Override
    public CertPathValidatorResult engineValidate(CertPath certPath, CertPathParameters params) throws CertPathValidatorException, InvalidAlgorithmParameterException {
        PublicKey workingPublicKey;
        X500Name workingIssuerName;
        TrustAnchor trust;
        PKIXExtendedParameters paramsPKIX;
        if (params instanceof PKIXParameters) {
            PKIXExtendedParameters.Builder paramsPKIXBldr = new PKIXExtendedParameters.Builder((PKIXParameters)params);
            paramsPKIX = paramsPKIXBldr.build();
        } else if (params instanceof PKIXExtendedBuilderParameters) {
            paramsPKIX = ((PKIXExtendedBuilderParameters)params).getBaseParameters();
        } else if (params instanceof PKIXExtendedParameters) {
            paramsPKIX = (PKIXExtendedParameters)params;
        } else {
            throw new InvalidAlgorithmParameterException("Parameters must be a " + PKIXParameters.class.getName() + " instance.");
        }
        if (paramsPKIX.getTrustAnchors() == null) {
            throw new InvalidAlgorithmParameterException("trustAnchors is null, this is not allowed for certification path validation.");
        }
        List<? extends Certificate> certs = certPath.getCertificates();
        int n = certs.size();
        if (certs.isEmpty()) {
            throw new CertPathValidatorException("Certification path is empty.", null, certPath, -1);
        }
        Date currentDate = new Date();
        Date validityDate = CertPathValidatorUtilities.getValidityDate(paramsPKIX, currentDate);
        Set userInitialPolicySet = paramsPKIX.getInitialPolicies();
        try {
            trust = CertPathValidatorUtilities.findTrustAnchor((X509Certificate)certs.get(certs.size() - 1), paramsPKIX.getTrustAnchors(), paramsPKIX.getSigProvider());
            if (trust == null) {
                throw new CertPathValidatorException("Trust anchor for certification path not found.", null, certPath, -1);
            }
            PKIXCertPathValidatorSpi_8.checkCertificate(trust.getTrustedCert());
        }
        catch (AnnotatedException e) {
            throw new CertPathValidatorException(e.getMessage(), e.getUnderlyingException(), certPath, certs.size() - 1);
        }
        paramsPKIX = new PKIXExtendedParameters.Builder(paramsPKIX).setTrustAnchor(trust).build();
        PKIXCertRevocationChecker revocationChecker = null;
        ArrayList<PKIXCertPathChecker> pathCheckers = new ArrayList<PKIXCertPathChecker>();
        for (PKIXCertPathChecker checker : paramsPKIX.getCertPathCheckers()) {
            checker.init(false);
            if (checker instanceof PKIXRevocationChecker) {
                if (revocationChecker != null) {
                    throw new CertPathValidatorException("only one PKIXRevocationChecker allowed");
                }
                revocationChecker = checker instanceof PKIXCertRevocationChecker ? (PKIXCertRevocationChecker)((Object)checker) : new WrappedRevocationChecker(checker);
                continue;
            }
            pathCheckers.add(checker);
        }
        if (paramsPKIX.isRevocationEnabled() && revocationChecker == null) {
            revocationChecker = new ProvRevocationChecker(this.helper);
        }
        int index = 0;
        List[] policyNodes = new ArrayList[n + 1];
        for (int j = 0; j < policyNodes.length; ++j) {
            policyNodes[j] = new ArrayList();
        }
        HashSet<String> policySet = new HashSet<String>();
        policySet.add("2.5.29.32.0");
        PKIXPolicyNode validPolicyTree = new PKIXPolicyNode(new ArrayList(), 0, policySet, null, new HashSet(), "2.5.29.32.0", false);
        policyNodes[0].add(validPolicyTree);
        PKIXNameConstraintValidator nameConstraintValidator = new PKIXNameConstraintValidator();
        HashSet acceptablePolicies = new HashSet();
        int explicitPolicy = paramsPKIX.isExplicitPolicyRequired() ? 0 : n + 1;
        int inhibitAnyPolicy = paramsPKIX.isAnyPolicyInhibited() ? 0 : n + 1;
        int policyMapping = paramsPKIX.isPolicyMappingInhibited() ? 0 : n + 1;
        X509Certificate sign = trust.getTrustedCert();
        try {
            if (sign != null) {
                workingIssuerName = PrincipalUtils.getSubjectPrincipal(sign);
                workingPublicKey = sign.getPublicKey();
            } else {
                workingIssuerName = PrincipalUtils.getCA(trust);
                workingPublicKey = trust.getCAPublicKey();
            }
        }
        catch (RuntimeException ex) {
            throw new ExtCertPathValidatorException("Subject of trust anchor could not be (re)encoded.", (Throwable)ex, certPath, -1);
        }
        AlgorithmIdentifier workingAlgId = null;
        try {
            workingAlgId = CertPathValidatorUtilities.getAlgorithmIdentifier(workingPublicKey);
        }
        catch (CertPathValidatorException e) {
            throw new ExtCertPathValidatorException("Algorithm identifier of public key of trust anchor could not be read.", (Throwable)e, certPath, -1);
        }
        ASN1ObjectIdentifier workingPublicKeyAlgorithm = workingAlgId.getAlgorithm();
        ASN1Encodable workingPublicKeyParameters = workingAlgId.getParameters();
        int maxPathLength = n;
        if (paramsPKIX.getTargetConstraints() != null && !paramsPKIX.getTargetConstraints().match((X509Certificate)certs.get(0))) {
            throw new ExtCertPathValidatorException("Target certificate in certification path does not match targetConstraints.", null, certPath, 0);
        }
        X509Certificate cert = null;
        for (index = certs.size() - 1; index >= 0; --index) {
            int i = n - index;
            cert = (X509Certificate)certs.get(index);
            boolean verificationAlreadyPerformed = index == certs.size() - 1;
            try {
                PKIXCertPathValidatorSpi_8.checkCertificate(cert);
            }
            catch (AnnotatedException e) {
                throw new CertPathValidatorException(e.getMessage(), e.getUnderlyingException(), certPath, index);
            }
            RFC3280CertPathUtilities.processCertA(certPath, paramsPKIX, validityDate, revocationChecker, index, workingPublicKey, verificationAlreadyPerformed, workingIssuerName, sign);
            RFC3280CertPathUtilities.processCertBC(certPath, index, nameConstraintValidator, this.isForCRLCheck);
            validPolicyTree = RFC3280CertPathUtilities.processCertD(certPath, index, acceptablePolicies, validPolicyTree, policyNodes, inhibitAnyPolicy, this.isForCRLCheck);
            validPolicyTree = RFC3280CertPathUtilities.processCertE(certPath, index, validPolicyTree);
            RFC3280CertPathUtilities.processCertF(certPath, index, validPolicyTree, explicitPolicy);
            if (i == n) continue;
            if (cert != null && cert.getVersion() == 1) {
                if (i == 1 && cert.equals(trust.getTrustedCert())) continue;
                throw new CertPathValidatorException("Version 1 certificates can't be used as CA ones.", null, certPath, index);
            }
            RFC3280CertPathUtilities.prepareNextCertA(certPath, index);
            validPolicyTree = RFC3280CertPathUtilities.prepareCertB(certPath, index, policyNodes, validPolicyTree, policyMapping);
            RFC3280CertPathUtilities.prepareNextCertG(certPath, index, nameConstraintValidator);
            explicitPolicy = RFC3280CertPathUtilities.prepareNextCertH1(certPath, index, explicitPolicy);
            policyMapping = RFC3280CertPathUtilities.prepareNextCertH2(certPath, index, policyMapping);
            inhibitAnyPolicy = RFC3280CertPathUtilities.prepareNextCertH3(certPath, index, inhibitAnyPolicy);
            explicitPolicy = RFC3280CertPathUtilities.prepareNextCertI1(certPath, index, explicitPolicy);
            policyMapping = RFC3280CertPathUtilities.prepareNextCertI2(certPath, index, policyMapping);
            inhibitAnyPolicy = RFC3280CertPathUtilities.prepareNextCertJ(certPath, index, inhibitAnyPolicy);
            RFC3280CertPathUtilities.prepareNextCertK(certPath, index);
            maxPathLength = RFC3280CertPathUtilities.prepareNextCertL(certPath, index, maxPathLength);
            maxPathLength = RFC3280CertPathUtilities.prepareNextCertM(certPath, index, maxPathLength);
            RFC3280CertPathUtilities.prepareNextCertN(certPath, index);
            Set<String> criticalExtensions = cert.getCriticalExtensionOIDs();
            if (criticalExtensions != null) {
                criticalExtensions = new HashSet<String>(criticalExtensions);
                criticalExtensions.remove(RFC3280CertPathUtilities.KEY_USAGE);
                criticalExtensions.remove(RFC3280CertPathUtilities.CERTIFICATE_POLICIES);
                criticalExtensions.remove(RFC3280CertPathUtilities.POLICY_MAPPINGS);
                criticalExtensions.remove(RFC3280CertPathUtilities.INHIBIT_ANY_POLICY);
                criticalExtensions.remove(RFC3280CertPathUtilities.ISSUING_DISTRIBUTION_POINT);
                criticalExtensions.remove(RFC3280CertPathUtilities.DELTA_CRL_INDICATOR);
                criticalExtensions.remove(RFC3280CertPathUtilities.POLICY_CONSTRAINTS);
                criticalExtensions.remove(RFC3280CertPathUtilities.BASIC_CONSTRAINTS);
                criticalExtensions.remove(RFC3280CertPathUtilities.SUBJECT_ALTERNATIVE_NAME);
                criticalExtensions.remove(RFC3280CertPathUtilities.NAME_CONSTRAINTS);
            } else {
                criticalExtensions = new HashSet<String>();
            }
            RFC3280CertPathUtilities.prepareNextCertO(certPath, index, criticalExtensions, pathCheckers);
            sign = cert;
            workingIssuerName = PrincipalUtils.getSubjectPrincipal(sign);
            try {
                workingPublicKey = CertPathValidatorUtilities.getNextWorkingKey(certPath.getCertificates(), index, this.helper);
            }
            catch (CertPathValidatorException e) {
                throw new CertPathValidatorException("Next working key could not be retrieved.", (Throwable)e, certPath, index);
            }
            workingAlgId = CertPathValidatorUtilities.getAlgorithmIdentifier(workingPublicKey);
            workingPublicKeyAlgorithm = workingAlgId.getAlgorithm();
            workingPublicKeyParameters = workingAlgId.getParameters();
        }
        explicitPolicy = RFC3280CertPathUtilities.wrapupCertA(explicitPolicy, cert);
        explicitPolicy = RFC3280CertPathUtilities.wrapupCertB(certPath, index + 1, explicitPolicy);
        Set<String> criticalExtensions = cert.getCriticalExtensionOIDs();
        if (criticalExtensions != null) {
            criticalExtensions = new HashSet<String>(criticalExtensions);
            criticalExtensions.remove(RFC3280CertPathUtilities.KEY_USAGE);
            criticalExtensions.remove(RFC3280CertPathUtilities.CERTIFICATE_POLICIES);
            criticalExtensions.remove(RFC3280CertPathUtilities.POLICY_MAPPINGS);
            criticalExtensions.remove(RFC3280CertPathUtilities.INHIBIT_ANY_POLICY);
            criticalExtensions.remove(RFC3280CertPathUtilities.ISSUING_DISTRIBUTION_POINT);
            criticalExtensions.remove(RFC3280CertPathUtilities.DELTA_CRL_INDICATOR);
            criticalExtensions.remove(RFC3280CertPathUtilities.POLICY_CONSTRAINTS);
            criticalExtensions.remove(RFC3280CertPathUtilities.BASIC_CONSTRAINTS);
            criticalExtensions.remove(RFC3280CertPathUtilities.SUBJECT_ALTERNATIVE_NAME);
            criticalExtensions.remove(RFC3280CertPathUtilities.NAME_CONSTRAINTS);
            criticalExtensions.remove(RFC3280CertPathUtilities.CRL_DISTRIBUTION_POINTS);
            criticalExtensions.remove(Extension.extendedKeyUsage.getId());
        } else {
            criticalExtensions = new HashSet<String>();
        }
        RFC3280CertPathUtilities.wrapupCertF(certPath, index + 1, pathCheckers, criticalExtensions);
        PKIXPolicyNode intersection = RFC3280CertPathUtilities.wrapupCertG(certPath, paramsPKIX, userInitialPolicySet, index + 1, policyNodes, validPolicyTree, acceptablePolicies);
        if (explicitPolicy > 0 || intersection != null) {
            return new PKIXCertPathValidatorResult(trust, intersection, cert.getPublicKey());
        }
        throw new CertPathValidatorException("Path processing failed on policy.", null, certPath, index);
    }

    static void checkCertificate(X509Certificate cert) throws AnnotatedException {
        if (cert instanceof BCX509Certificate) {
            RuntimeException cause = null;
            try {
                if (null != ((BCX509Certificate)((Object)cert)).getTBSCertificateNative()) {
                    return;
                }
            }
            catch (RuntimeException e) {
                cause = e;
            }
            throw new AnnotatedException("unable to process TBSCertificate", cause);
        }
        try {
            TBSCertificate.getInstance(cert.getTBSCertificate());
        }
        catch (CertificateEncodingException e) {
            throw new AnnotatedException("unable to process TBSCertificate", e);
        }
        catch (IllegalArgumentException e) {
            throw new AnnotatedException(e.getMessage());
        }
    }
}

